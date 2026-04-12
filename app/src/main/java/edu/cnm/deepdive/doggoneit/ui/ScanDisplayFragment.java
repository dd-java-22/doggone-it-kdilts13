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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.doggoneit.MainActivity;
import edu.cnm.deepdive.doggoneit.R;
import edu.cnm.deepdive.doggoneit.databinding.FragmentScanDisplayBinding;
import edu.cnm.deepdive.doggoneit.model.entity.BreedInfo;
import edu.cnm.deepdive.doggoneit.model.entity.Scan;
import edu.cnm.deepdive.doggoneit.viewmodel.ScanDisplayViewModel;
import java.util.ArrayList;
import java.util.List;

@AndroidEntryPoint
public class ScanDisplayFragment extends Fragment {

  private FragmentScanDisplayBinding binding;
  private ScanDisplayFragmentArgs navArgs;
  private ScanDisplayViewModel viewModel;
  private boolean updatingFavoriteControl;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentScanDisplayBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(this).get(ScanDisplayViewModel.class);
    Bundle args = getArguments();
    if (args == null) {
      viewModel.loadScan(0);
    } else {
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
      viewModel.loadScan(navArgs.getScanId());
    }
    setupControls();
    viewModel.getUiState().observe(getViewLifecycleOwner(), this::renderState);
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
    binding.summaryStatusText.setVisibility(View.VISIBLE);
    binding.summaryStatusText.setText(R.string.scan_display_missing_image);
    binding.breedNameText.setText(R.string.scan_display_unknown_breed);
    binding.breedConfidenceText.setVisibility(View.GONE);
    binding.favoriteToggle.setEnabled(false);
  }

  private void showMissingScan() {
    binding.savedImage.setVisibility(View.GONE);
    binding.summaryStatusText.setVisibility(View.VISIBLE);
    binding.summaryStatusText.setText(R.string.scan_display_missing_scan);
    binding.breedNameText.setText(R.string.scan_display_unknown_breed);
    binding.breedConfidenceText.setVisibility(View.GONE);
    binding.favoriteToggle.setEnabled(false);
  }

  private void setupControls() {
    binding.favoriteToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
      if (updatingFavoriteControl) {
        return;
      }
      viewModel.toggleFavorite(isChecked);
    });
    binding.contentTabGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
      if (!isChecked) {
        return;
      }
      if (checkedId == R.id.tab_facts) {
        viewModel.setSelectedTab(ScanDisplayViewModel.ContentTab.FACTS);
      } else if (checkedId == R.id.tab_notes) {
        viewModel.setSelectedTab(ScanDisplayViewModel.ContentTab.NOTES);
      }
    });
  }

  private void renderState(ScanDisplayViewModel.UiState state) {
    if (binding == null || state == null) {
      return;
    }
    if (state.scanId <= 0) {
      showMissingScan();
      return;
    }
    Scan scan = state.scan;
    if (scan == null) {
      showMissingScan();
      return;
    }
    String imagePath = (scan != null) ? scan.getImagePath() : null;
    if (imagePath == null || imagePath.isBlank()) {
      showMissingImage();
      return;
    }
    Uri parsedUri = Uri.parse(imagePath);
    binding.savedImage.setImageURI(parsedUri);
    binding.savedImage.setVisibility(View.VISIBLE);
    binding.summaryStatusText.setVisibility(View.GONE);

    if (state.selectedBreedLabel == null || state.selectedBreedLabel.isBlank()) {
      binding.breedNameText.setText(R.string.scan_display_unknown_breed);
    } else {
      binding.breedNameText.setText(state.selectedBreedLabel);
    }
    if (state.selectedConfidencePercent == null) {
      binding.breedConfidenceText.setVisibility(View.GONE);
    } else {
      binding.breedConfidenceText.setVisibility(View.VISIBLE);
      binding.breedConfidenceText.setText(
          getString(R.string.scan_display_confidence_percent, state.selectedConfidencePercent));
    }

    updatingFavoriteControl = true;
    binding.favoriteToggle.setEnabled(true);
    binding.favoriteToggle.setChecked(scan.isFavorite());
    updatingFavoriteControl = false;

    if (state.selectedTab == ScanDisplayViewModel.ContentTab.NOTES) {
      if (binding.contentTabGroup.getCheckedButtonId() != R.id.tab_notes) {
        binding.contentTabGroup.check(R.id.tab_notes);
      }
      binding.factsLoadingIndicator.setVisibility(View.GONE);
      binding.factsPlaceholderText.setVisibility(View.GONE);
      binding.factsDetailsText.setVisibility(View.GONE);
      binding.notesPlaceholderText.setVisibility(View.VISIBLE);
    } else {
      if (binding.contentTabGroup.getCheckedButtonId() != R.id.tab_facts) {
        binding.contentTabGroup.check(R.id.tab_facts);
      }
      renderFactsState(state.factsState);
      binding.notesPlaceholderText.setVisibility(View.GONE);
    }
    if (state.notesMode == ScanDisplayViewModel.NotesMode.EDIT) {
      binding.notesPlaceholderText.setText(R.string.scan_display_notes_edit_placeholder);
    } else {
      binding.notesPlaceholderText.setText(R.string.scan_display_notes_placeholder);
    }
  }

  private void renderFactsState(ScanDisplayViewModel.FactsState factsState) {
    if (factsState == null) {
      binding.factsLoadingIndicator.setVisibility(View.GONE);
      binding.factsDetailsText.setVisibility(View.GONE);
      binding.factsPlaceholderText.setVisibility(View.VISIBLE);
      binding.factsPlaceholderText.setText(R.string.scan_display_facts_placeholder);
      return;
    }
    switch (factsState.status) {
      case LOADING -> {
        binding.factsLoadingIndicator.setVisibility(View.VISIBLE);
        binding.factsDetailsText.setVisibility(View.GONE);
        binding.factsPlaceholderText.setVisibility(View.GONE);
      }
      case LOADED -> {
        binding.factsLoadingIndicator.setVisibility(View.GONE);
        String detailsText = buildFactsDetails(factsState.breedInfo);
        if (detailsText.isBlank()) {
          binding.factsDetailsText.setVisibility(View.GONE);
          binding.factsPlaceholderText.setVisibility(View.VISIBLE);
          binding.factsPlaceholderText.setText(R.string.scan_display_facts_no_data);
        } else {
          binding.factsPlaceholderText.setVisibility(View.GONE);
          binding.factsDetailsText.setVisibility(View.VISIBLE);
          binding.factsDetailsText.setText(detailsText);
        }
      }
      case SELECTED_BREED_MISSING -> {
        showFactsMessage(R.string.scan_display_facts_selected_breed_missing);
      }
      case BREED_MAPPING_MISSING -> {
        showFactsMessage(R.string.scan_display_facts_mapping_missing);
      }
      case FETCH_FAILED -> {
        showFactsMessage(R.string.scan_display_facts_fetch_failed);
      }
      case NO_FACTS_AVAILABLE -> {
        showFactsMessage(R.string.scan_display_facts_no_data);
      }
      case IDLE -> {
        showFactsMessage(R.string.scan_display_facts_placeholder);
      }
    }
  }

  private void showFactsMessage(int messageResId) {
    binding.factsLoadingIndicator.setVisibility(View.GONE);
    binding.factsDetailsText.setVisibility(View.GONE);
    binding.factsPlaceholderText.setVisibility(View.VISIBLE);
    binding.factsPlaceholderText.setText(messageResId);
  }

  private String buildFactsDetails(BreedInfo breedInfo) {
    if (breedInfo == null) {
      return "";
    }
    List<String> lines = new ArrayList<>();
    appendFactLine(lines, getString(R.string.scan_display_facts_name), breedInfo.getName());
    appendFactLine(lines, getString(R.string.scan_display_facts_group), breedInfo.getBreedGroup());
    appendFactLine(lines, getString(R.string.scan_display_facts_bred_for), breedInfo.getBredFor());
    appendFactLine(lines, getString(R.string.scan_display_facts_life_span), breedInfo.getLifeSpan());
    appendFactLine(lines, getString(R.string.scan_display_facts_temperament),
        breedInfo.getTemperament());
    appendFactLine(lines, getString(R.string.scan_display_facts_origin), breedInfo.getOrigin());
    appendFactLine(lines, getString(R.string.scan_display_facts_weight), formatMeasurement(
        breedInfo.getWeightMetric(),
        getString(R.string.scan_display_measurement_metric_kg),
        breedInfo.getWeightImperial(),
        getString(R.string.scan_display_measurement_imperial_lb)
    ));
    appendFactLine(lines, getString(R.string.scan_display_facts_height), formatMeasurement(
        breedInfo.getHeightMetric(),
        getString(R.string.scan_display_measurement_metric_cm),
        breedInfo.getHeightImperial(),
        getString(R.string.scan_display_measurement_imperial_in)
    ));
    return TextUtils.join("\n", lines);
  }

  private void appendFactLine(List<String> lines, String label, String value) {
    if (value == null || value.isBlank()) {
      return;
    }
    lines.add(getString(R.string.scan_display_facts_line_format, label, value.trim()));
  }

  private String formatMeasurement(String metric, String metricUnit, String imperial,
      String imperialUnit) {
    boolean hasMetric = metric != null && !metric.isBlank();
    boolean hasImperial = imperial != null && !imperial.isBlank();
    if (!hasMetric && !hasImperial) {
      return null;
    }
    if (hasMetric && hasImperial) {
      return getString(
          R.string.scan_display_measurement_both,
          metric.trim(),
          metricUnit,
          imperial.trim(),
          imperialUnit
      );
    }
    if (hasMetric) {
      return getString(R.string.scan_display_measurement_single, metric.trim(), metricUnit);
    }
    return getString(R.string.scan_display_measurement_single, imperial.trim(), imperialUnit);
  }

}
