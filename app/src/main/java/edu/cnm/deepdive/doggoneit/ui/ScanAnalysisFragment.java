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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.navigation.fragment.NavHostFragment;
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
import edu.cnm.deepdive.doggoneit.service.repository.ScanRepository;
import edu.cnm.deepdive.doggoneit.service.repository.UserSessionRepository;
import edu.cnm.deepdive.doggoneit.storage.ImageStorage;
import edu.cnm.deepdive.doggoneit.ui.ScanPredictionAdapter.ScanPredictionItem;
import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;

@AndroidEntryPoint
public class ScanAnalysisFragment extends Fragment {

  private enum AnalysisState {
    IDLE,
    ANALYZING,
    ANALYSIS_READY,
    ANALYSIS_FAILED,
    ANALYSIS_EMPTY
  }

  private FragmentScanAnalysisBinding binding;
  private final ExecutorService inferenceExecutor = Executors.newSingleThreadExecutor();
  private final ExecutorService saveExecutor = Executors.newSingleThreadExecutor();
  private final Handler mainHandler = new Handler(Looper.getMainLooper());
  private final ScanPredictionAdapter predictionAdapter =
      new ScanPredictionAdapter(item -> updateSaveButtonState());
  private Context appContext;
  private Uri currentImageUri;
  private DogBreedInferenceResult currentResult;
  private AnalysisState analysisState = AnalysisState.IDLE;
  @Inject
  ScanRepository scanRepository;
  @Inject
  UserSessionRepository userSessionRepository;

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
    binding.predictionsList.setLayoutManager(new LinearLayoutManager(requireContext()));
    binding.predictionsList.setAdapter(predictionAdapter);
    Bundle args = getArguments();
    if (args == null) {
      binding.capturedImage.setVisibility(View.GONE);
      clearAnalysisState();
      showStatus(getString(R.string.scan_analysis_missing_image), false);
      updateSaveButtonState();
      return;
    }

    String imageUri = ScanAnalysisFragmentArgs.fromBundle(args).getImageUri();
    if (imageUri == null || imageUri.isBlank()) {
      binding.capturedImage.setVisibility(View.GONE);
      clearAnalysisState();
      showStatus(getString(R.string.scan_analysis_missing_image), false);
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
    saveExecutor.shutdownNow();
    super.onDestroy();
  }

  private void runInference(Context appContext, Uri imageUri) {
    analysisState = AnalysisState.ANALYZING;
    currentResult = null;
    predictionAdapter.submitItems(List.of(), 0);
    showStatus(getString(R.string.scan_analysis_loading), true);
    updateSaveButtonState();
    inferenceExecutor.execute(() -> {
      try {
        DogBreedInferenceResult result = DogBreedInference.run(appContext, imageUri);
        List<ScanPredictionItem> topPredictions = toDisplayPredictions(result);
        mainHandler.post(() -> {
          if (binding != null) {
            if (topPredictions.isEmpty()) {
              currentResult = null;
              analysisState = AnalysisState.ANALYSIS_EMPTY;
              predictionAdapter.submitItems(List.of(), 0);
              showStatus(getString(R.string.scan_analysis_empty), false);
            } else {
              currentResult = result;
              analysisState = AnalysisState.ANALYSIS_READY;
              predictionAdapter.submitItems(topPredictions, 0);
              hideStatus();
              binding.predictionsList.setVisibility(View.VISIBLE);
            }
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
            predictionAdapter.submitItems(List.of(), 0);
            showStatus(errorText, false);
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
        ScanPredictionItem selected = predictionAdapter.getSelectedItem();
        String selectedBreedLabel = (selected != null) ? selected.rawLabel() : null;
        Double selectedBreedConfidence =
            (selected != null) ? (double) selected.score() : null;
        ScanWithPredictions saved = scanRepository.saveWithPredictions(
            scan,
            predictions,
            selectedBreedLabel,
            selectedBreedConfidence
        ).join();
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
            Toast.makeText(requireContext(), errorText, Toast.LENGTH_LONG).show();
          }
        });
      }
    });
  }

  private void showSaveMissing() {
    Toast.makeText(requireContext(), R.string.save_image_missing, Toast.LENGTH_SHORT).show();
  }

  private void clearAnalysisState() {
    currentImageUri = null;
    currentResult = null;
    analysisState = AnalysisState.IDLE;
    predictionAdapter.submitItems(List.of(), 0);
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
        && appContext != null
        && predictionAdapter.getSelectedItem() != null;
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

  private List<ScanPredictionItem> toDisplayPredictions(DogBreedInferenceResult result) {
    List<ScanPredictionItem> displayItems = new ArrayList<>();
    if (result == null || result.topPredictions() == null) {
      return displayItems;
    }
    for (DogBreedPrediction prediction : result.topPredictions()) {
      if (prediction == null || prediction.label() == null || prediction.label().isBlank()) {
        continue;
      }
      float score = prediction.score();
      int percent = Math.round(score * 100f);
      displayItems.add(new ScanPredictionItem(
          prediction.label(),
          toFriendlyBreedName(prediction.label()),
          score,
          Math.max(0, Math.min(percent, 100))
      ));
    }
    displayItems.sort(Comparator.comparingDouble(ScanPredictionItem::score).reversed());
    if (displayItems.size() > 5) {
      return new ArrayList<>(displayItems.subList(0, 5));
    }
    return displayItems;
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

  private void showStatus(String message, boolean loading) {
    if (binding == null) {
      return;
    }
    binding.analysisLoadingContainer.setVisibility(View.VISIBLE);
    binding.analysisProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
    binding.analysisStatusText.setText(message);
    binding.predictionsList.setVisibility(View.GONE);
  }

  private void hideStatus() {
    if (binding == null) {
      return;
    }
    binding.analysisLoadingContainer.setVisibility(View.GONE);
    binding.analysisProgress.setVisibility(View.GONE);
    binding.analysisStatusText.setText("");
  }

  private String toFriendlyBreedName(String rawLabel) {
    if (rawLabel == null) {
      return "";
    }
    String cleaned = rawLabel.trim().replace('_', ' ').replace('-', ' ');
    if (cleaned.isBlank()) {
      return rawLabel;
    }
    String[] words = cleaned.split("\\s+");
    StringBuilder builder = new StringBuilder();
    for (String word : words) {
      if (word.isBlank()) {
        continue;
      }
      if (builder.length() > 0) {
        builder.append(' ');
      }
      builder.append(word.substring(0, 1).toUpperCase(Locale.US));
      if (word.length() > 1) {
        builder.append(word.substring(1).toLowerCase(Locale.US));
      }
    }
    return (builder.length() > 0) ? builder.toString() : rawLabel;
  }

}
