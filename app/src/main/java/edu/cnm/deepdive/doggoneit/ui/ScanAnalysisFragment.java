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

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.doggoneit.ml.DogBreedInference;
import edu.cnm.deepdive.doggoneit.ml.DogBreedInferenceResult;
import edu.cnm.deepdive.doggoneit.R;
import edu.cnm.deepdive.doggoneit.databinding.FragmentScanAnalysisBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@AndroidEntryPoint
public class ScanAnalysisFragment extends Fragment {

  private FragmentScanAnalysisBinding binding;
  private final ExecutorService inferenceExecutor = Executors.newSingleThreadExecutor();
  private final Handler mainHandler = new Handler(Looper.getMainLooper());
  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentScanAnalysisBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    String imageUri = ScanAnalysisFragmentArgs.fromBundle(getArguments()).getImageUri();
    if (imageUri != null) {
      Uri parsedUri = Uri.parse(imageUri);
      binding.capturedImage.setImageURI(parsedUri);
      binding.capturedImage.setVisibility(View.VISIBLE);
      runInference(parsedUri);
    } else {
      binding.capturedImage.setVisibility(View.GONE);
      binding.analysisOutputText.setText(R.string.scan_analysis_missing_image);
    }
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  @Override
  public void onDestroy() {
    inferenceExecutor.shutdownNow();
    super.onDestroy();
  }

  private void runInference(Uri imageUri) {
    binding.analysisOutputText.setText(R.string.scan_analysis_loading);
    inferenceExecutor.execute(() -> {
      try {
        DogBreedInferenceResult result = DogBreedInference.run(requireContext(), imageUri);
        String json = gson.toJson(result);
        mainHandler.post(() -> {
          if (binding != null) {
            binding.analysisOutputText.setText(json);
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
            binding.analysisOutputText.setText(errorText);
          }
        });
      }
    });
  }

}
