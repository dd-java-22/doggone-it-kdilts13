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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.doggoneit.MainActivity;
import edu.cnm.deepdive.doggoneit.R;
import edu.cnm.deepdive.doggoneit.databinding.FragmentScanDisplayBinding;
import edu.cnm.deepdive.doggoneit.model.entity.BreedPrediction;
import edu.cnm.deepdive.doggoneit.model.entity.Scan;
import edu.cnm.deepdive.doggoneit.model.entity.ScanWithPredictions;
import edu.cnm.deepdive.doggoneit.service.repository.ScanRepository;
import java.util.List;
import javax.inject.Inject;

@AndroidEntryPoint
public class ScanDisplayFragment extends Fragment {

  private FragmentScanDisplayBinding binding;
  private ScanDisplayFragmentArgs navArgs;
  @Inject
  ScanRepository scanRepository;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentScanDisplayBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Bundle args = getArguments();
    if (args == null) {
      showMissingImage();
      return;
    }
    navArgs = ScanDisplayFragmentArgs.fromBundle(args);
    requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
        new OnBackPressedCallback(true) {
          @Override
          public void handleOnBackPressed() {
            if (!handleReturnToParentContext()) {
              NavHostFragment.findNavController(ScanDisplayFragment.this).navigateUp();
            }
          }
        });
    long scanId = navArgs.getScanId();
    if (scanId <= 0) {
      showMissingScan();
      return;
    }
    scanRepository.getWithPredictionsById(scanId).observe(getViewLifecycleOwner(),
        this::renderScan);
  }

  @Override
  public void onDestroyView() {
    navArgs = null;
    binding = null;
    super.onDestroyView();
  }

  private boolean handleReturnToParentContext() {
    if (requireActivity() instanceof MainActivity mainActivity) {
      return mainActivity.handleScanDisplayReturn(getSourceArguments());
    }
    NavController navController = NavHostFragment.findNavController(this);
    return navController.navigateUp();
  }

  private Bundle getSourceArguments() {
    Bundle arguments = new Bundle();
    String source = (navArgs != null) ? navArgs.getSource() : MainActivity.SCAN_DISPLAY_SOURCE_ANALYSIS;
    arguments.putString(MainActivity.SCAN_DISPLAY_SOURCE_ARG, source);
    return arguments;
  }

  private void showMissingImage() {
    binding.savedImage.setVisibility(View.GONE);
    binding.scanDisplayStatus.setVisibility(View.VISIBLE);
    binding.scanDisplayStatus.setText(R.string.scan_display_missing_image);
    binding.scanPredictions.setVisibility(View.GONE);
  }

  private void showMissingScan() {
    binding.savedImage.setVisibility(View.GONE);
    binding.scanDisplayStatus.setVisibility(View.VISIBLE);
    binding.scanDisplayStatus.setText(R.string.scan_display_missing_scan);
    binding.scanPredictions.setVisibility(View.GONE);
  }

  private void renderScan(ScanWithPredictions scanWithPredictions) {
    if (binding == null) {
      return;
    }
    if (scanWithPredictions == null || scanWithPredictions.getScan() == null) {
      showMissingScan();
      return;
    }
    Scan scan = scanWithPredictions.getScan();
    String imagePath = (scan != null) ? scan.getImagePath() : null;
    if (imagePath == null || imagePath.isBlank()) {
      showMissingImage();
      return;
    }
    Uri parsedUri = Uri.parse(imagePath);
    binding.savedImage.setImageURI(parsedUri);
    binding.savedImage.setVisibility(View.VISIBLE);
    binding.scanDisplayStatus.setVisibility(View.GONE);
    renderPredictions(scanWithPredictions.getPredictions());
  }

  private void renderPredictions(List<BreedPrediction> predictions) {
    if (predictions == null || predictions.isEmpty()) {
      binding.scanPredictions.setText(R.string.scan_display_missing_predictions);
      binding.scanPredictions.setVisibility(View.VISIBLE);
      return;
    }
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < predictions.size(); i++) {
      BreedPrediction prediction = predictions.get(i);
      if (prediction == null) {
        continue;
      }
      builder.append(i + 1)
          .append(". ")
          .append(prediction.getName())
          .append(" - ")
          .append(prediction.getProbability());
      if (i < predictions.size() - 1) {
        builder.append('\n');
      }
    }
    if (builder.length() == 0) {
      binding.scanPredictions.setText(R.string.scan_display_missing_predictions);
    } else {
      binding.scanPredictions.setText(builder.toString());
    }
    binding.scanPredictions.setVisibility(View.VISIBLE);
  }

}
