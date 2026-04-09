/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.doggoneit.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.doggoneit.MainActivity;
import edu.cnm.deepdive.doggoneit.R;
import edu.cnm.deepdive.doggoneit.databinding.FragmentScanAnalysisBinding;
import edu.cnm.deepdive.doggoneit.ml.DogBreedInference;
import edu.cnm.deepdive.doggoneit.ml.DogBreedInferenceResult;
import edu.cnm.deepdive.doggoneit.ml.DogBreedPrediction;
import edu.cnm.deepdive.doggoneit.model.entity.BreedPrediction;
import edu.cnm.deepdive.doggoneit.model.entity.Scan;
import edu.cnm.deepdive.doggoneit.model.entity.ScanWithPredictions;
import edu.cnm.deepdive.doggoneit.service.dogapi.DogApiService;
import edu.cnm.deepdive.doggoneit.service.dogapi.dto.BreedFactDto;
import edu.cnm.deepdive.doggoneit.service.dogapi.dto.BreedSearchResultDto;
import edu.cnm.deepdive.doggoneit.service.repository.ScanRepository;
import edu.cnm.deepdive.doggoneit.service.repository.UserSessionRepository;
import edu.cnm.deepdive.doggoneit.storage.ImageStorage;
import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import retrofit2.Response;

@AndroidEntryPoint
public class ScanAnalysisFragment extends Fragment {

  private static final String TAG = ScanAnalysisFragment.class.getSimpleName();
  private static final String TEST_BREED_QUERY = "beagle";

  private enum AnalysisState {
    IDLE,
    ANALYZING,
    ANALYSIS_READY,
    ANALYSIS_FAILED
  }

  private FragmentScanAnalysisBinding binding;
  private final ExecutorService inferenceExecutor = Executors.newSingleThreadExecutor();
  private final ExecutorService dogApiExecutor = Executors.newSingleThreadExecutor();
  private final ExecutorService saveExecutor = Executors.newSingleThreadExecutor();
  private final Handler mainHandler = new Handler(Looper.getMainLooper());
  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private Context appContext;
  private Uri currentImageUri;
  private DogBreedInferenceResult currentResult;
  private AnalysisState analysisState = AnalysisState.IDLE;
  @Inject
  ScanRepository scanRepository;
  @Inject
  UserSessionRepository userSessionRepository;
  @Inject
  DogApiService dogApiService;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentScanAnalysisBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    binding.saveImageButton.setOnClickListener(v -> onSaveClicked());
    binding.testDogApiButton.setOnClickListener(v -> onTestDogApiClicked());
    Bundle args = getArguments();
    if (args == null) {
      binding.capturedImage.setVisibility(View.GONE);
      binding.analysisOutputText.setText(R.string.scan_analysis_missing_image);
      clearAnalysisState();
      updateSaveButtonState();
      return;
    }

    String imageUri = ScanAnalysisFragmentArgs.fromBundle(args).getImageUri();
    if (imageUri == null || imageUri.isBlank()) {
      binding.capturedImage.setVisibility(View.GONE);
      binding.analysisOutputText.setText(R.string.scan_analysis_missing_image);
      clearAnalysisState();
      updateSaveButtonState();
      return;
    }

    Uri parsedUri = Uri.parse(imageUri);
    appContext = requireContext().getApplicationContext();
    currentImageUri = parsedUri;
    currentResult = null;
    analysisState = AnalysisState.IDLE;
    binding.capturedImage.setImageURI(parsedUri);
    binding.capturedImage.setVisibility(View.VISIBLE);
    updateSaveButtonState();
    runInference(appContext, parsedUri);
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  @Override
  public void onDestroy() {
    inferenceExecutor.shutdownNow();
    dogApiExecutor.shutdownNow();
    saveExecutor.shutdownNow();
    super.onDestroy();
  }

  private void onTestDogApiClicked() {
    Log.d(TAG, "Starting temporary Dog API test for query: " + TEST_BREED_QUERY);
    binding.analysisOutputText.setText(R.string.test_dog_api_loading);
    dogApiExecutor.execute(() -> {
      try {
        Response<List<BreedSearchResultDto>> searchResponse =
            dogApiService.searchBreeds(TEST_BREED_QUERY).execute();
        if (!searchResponse.isSuccessful()) {
          throw new IllegalStateException(
              "Breed search failed with code " + searchResponse.code());
        }
        List<BreedSearchResultDto> breeds = searchResponse.body();
        if (breeds == null || breeds.isEmpty()) {
          postAnalysisOutput(getString(R.string.test_dog_api_no_breed_found, TEST_BREED_QUERY));
          Log.d(TAG, "No breed found for query: " + TEST_BREED_QUERY);
          return;
        }

        BreedSearchResultDto breed = breeds.get(0);
        String breedName = breed.getName();
        Log.d(TAG, "Dog API breed search returned id=" + breed.getId() + ", name=" + breedName);

        Response<List<BreedFactDto>> factResponse = dogApiService.getBreedFacts(breed.getId(), 1)
            .execute();
        if (!factResponse.isSuccessful()) {
          throw new IllegalStateException(
              "Breed facts failed with code " + factResponse.code());
        }
        List<BreedFactDto> facts = factResponse.body();
        if (facts == null || facts.isEmpty() || facts.get(0).getFact() == null
            || facts.get(0).getFact().isBlank()) {
          postAnalysisOutput(getString(R.string.test_dog_api_no_facts_returned, breedName));
          Log.d(TAG, "No facts returned for breed: " + breedName);
          return;
        }

        String fact = facts.get(0).getFact();
        Log.d(TAG, "Dog API fact received for breed: " + breedName);
        postAnalysisOutput(getString(R.string.test_dog_api_result, breedName, fact));
      } catch (Exception e) {
        Log.e(TAG, "Dog API test request failed", e);
        String message = e.getMessage();
        if (message == null || message.isBlank()) {
          message = e.getClass().getSimpleName();
        }
        postAnalysisOutput(getString(R.string.test_dog_api_error, message));
      }
    });
  }

  private void runInference(Context appContext, Uri imageUri) {
    binding.analysisOutputText.setText(R.string.scan_analysis_loading);
    analysisState = AnalysisState.ANALYZING;
    currentResult = null;
    updateSaveButtonState();
    inferenceExecutor.execute(() -> {
      try {
        DogBreedInferenceResult result = DogBreedInference.run(appContext, imageUri);
        String json = gson.toJson(result);
        mainHandler.post(() -> {
          if (binding != null) {
            currentResult = result;
            analysisState = AnalysisState.ANALYSIS_READY;
            binding.analysisOutputText.setText(json);
            updateSaveButtonState();
          }
        });
      } catch (Exception e) {
        String message = e.getMessage();
        if (message == null || message.isBlank()) {
          message = e.getClass().getSimpleName();
        }
        String errorText = getString(R.string.scan_analysis_error, message);
        mainHandler.post(() -> {
          if (binding != null) {
            currentResult = null;
            analysisState = AnalysisState.ANALYSIS_FAILED;
            binding.analysisOutputText.setText(errorText);
            updateSaveButtonState();
          }
        });
      }
    });
  }

  private void onSaveClicked() {
    if (!canSave()) {
      showSaveMissing();
      return;
    }
    Toast.makeText(requireContext(), R.string.save_image_saving, Toast.LENGTH_SHORT).show();
    saveExecutor.execute(() -> {
      Uri savedUri = null;
      try {
        savedUri = ImageStorage.saveImage(appContext, currentImageUri);
        Scan scan = new Scan();
        long userProfileId = userSessionRepository.getCurrentUserId();
        if (userProfileId <= 0) {
          throw new IllegalStateException("No signed-in user profile.");
        }
        scan.setUserProfileId(userProfileId);
        scan.setImagePath(savedUri.toString());
        scan.setTimestamp(Instant.now());
        List<BreedPrediction> predictions = toBreedPredictions(currentResult);
        ScanWithPredictions saved = scanRepository.saveWithPredictions(scan, predictions).join();
        mainHandler.post(() -> {
          if (!isAdded()) {
            return;
          }
          long scanId = (saved != null && saved.getScan() != null) ? saved.getScan().getId() : 0;
          if (scanId <= 0) {
            String errorText = getString(R.string.save_image_failed, "Missing scan ID");
            Toast.makeText(requireContext(), errorText, Toast.LENGTH_LONG).show();
            return;
          }
          ScanAnalysisFragmentDirections.ActionScanAnalysisFragmentToScanDisplayFragment action =
              ScanAnalysisFragmentDirections.actionScanAnalysisFragmentToScanDisplayFragment();
          action.setScanId(scanId);
          action.setSource(MainActivity.SCAN_DISPLAY_SOURCE_ANALYSIS);
          NavHostFragment.findNavController(this).navigate(action);
        });
      } catch (Exception e) {
        bestEffortDelete(savedUri);
        String message = e.getMessage();
        if (message == null || message.isBlank()) {
          message = e.getClass().getSimpleName();
        }
        String errorText = getString(R.string.save_image_failed, message);
        mainHandler.post(() -> {
          if (binding != null) {
            Log.e(">>>", errorText);
            Toast.makeText(requireContext(), errorText, Toast.LENGTH_LONG).show();
          }
        });
      }
    });
  }

  private void showSaveMissing() {
    Toast.makeText(requireContext(), R.string.save_image_missing, Toast.LENGTH_SHORT).show();
  }

  private void postAnalysisOutput(String text) {
    mainHandler.post(() -> {
      if (binding != null) {
        binding.analysisOutputText.setText(text);
      }
    });
  }

  private void clearAnalysisState() {
    currentImageUri = null;
    currentResult = null;
    analysisState = AnalysisState.IDLE;
  }

  private void updateSaveButtonState() {
    if (binding != null) {
      binding.saveImageButton.setEnabled(canSave());
    }
  }

  private boolean canSave() {
    return analysisState == AnalysisState.ANALYSIS_READY
        && currentResult != null
        && currentImageUri != null
        && appContext != null;
  }

  private List<BreedPrediction> toBreedPredictions(DogBreedInferenceResult result) {
    List<BreedPrediction> predictions = new ArrayList<>();
    if (result == null || result.topPredictions() == null) {
      return predictions;
    }
    for (DogBreedPrediction prediction : result.topPredictions()) {
      BreedPrediction entity = new BreedPrediction();
      entity.setName(prediction.label());
      entity.setProbability(prediction.score());
      predictions.add(entity);
    }
    return predictions;
  }

  private void bestEffortDelete(Uri savedUri) {
    if (savedUri == null) {
      return;
    }
    String path = savedUri.getPath();
    if (path == null || path.isBlank()) {
      return;
    }
    File file = new File(path);
    if (file.exists() && !file.delete()) {
      // Best-effort cleanup; ignore failure.
    }
  }

}
