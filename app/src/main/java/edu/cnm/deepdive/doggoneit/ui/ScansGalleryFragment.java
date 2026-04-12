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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.doggoneit.MainActivity;
import edu.cnm.deepdive.doggoneit.R;
import edu.cnm.deepdive.doggoneit.databinding.FragmentScansGalleryBinding;
import edu.cnm.deepdive.doggoneit.viewmodel.ScansGalleryViewModel;

@AndroidEntryPoint
public class ScansGalleryFragment extends Fragment {

  private FragmentScansGalleryBinding binding;
  private SavedScanGridAdapter adapter;
  private ScansGalleryViewModel viewModel;
  private SharedPreferences preferences;
  private GridLayoutManager layoutManager;
  private TextWatcher filterWatcher;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentScansGalleryBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(this).get(ScansGalleryViewModel.class);
    preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
    adapter = new SavedScanGridAdapter(this::openScan);
    setupControls();
    layoutManager = new GridLayoutManager(requireContext(), 3);
    binding.savedScansGrid.setLayoutManager(layoutManager);
    binding.savedScansGrid.setAdapter(adapter);
    applySettingsDefaults();
    viewModel.getGalleryItems().observe(getViewLifecycleOwner(), items -> {
      adapter.submitList(items);
      boolean isEmpty = items == null || items.isEmpty();
      binding.savedScansGrid.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
      Integer emptyMessage = viewModel.getEmptyMessageResId().getValue();
      boolean showEmptyMessage = isEmpty && emptyMessage != null && emptyMessage != 0;
      binding.emptyGalleryMessage.setVisibility(showEmptyMessage ? View.VISIBLE : View.GONE);
    });
    viewModel.getEmptyMessageResId().observe(getViewLifecycleOwner(), messageResId -> {
      if (messageResId != null && messageResId != 0) {
        binding.emptyGalleryMessage.setText(messageResId);
      }
    });
    viewModel.getSortField().observe(getViewLifecycleOwner(), sortField -> {
      int position = (sortField == ScansGalleryViewModel.SortField.BREED) ? 1 : 0;
      if (binding.sortFieldSpinner.getSelectedItemPosition() != position) {
        binding.sortFieldSpinner.setSelection(position);
      }
    });
    viewModel.getSortDirection().observe(getViewLifecycleOwner(), direction -> {
      int position = (direction == ScansGalleryViewModel.SortDirection.ASCENDING) ? 0 : 1;
      if (binding.sortDirectionSpinner.getSelectedItemPosition() != position) {
        binding.sortDirectionSpinner.setSelection(position);
      }
    });
    viewModel.getFilterText().observe(getViewLifecycleOwner(), text -> {
      String current = binding.filterInput.getText().toString();
      String target = (text != null) ? text : "";
      if (!current.equals(target)) {
        binding.filterInput.setText(target);
        binding.filterInput.setSelection(target.length());
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();
    applySettingsDefaults();
  }

  @Override
  public void onDestroyView() {
    binding.filterInput.removeTextChangedListener(filterWatcher);
    binding.savedScansGrid.setAdapter(null);
    layoutManager = null;
    adapter = null;
    filterWatcher = null;
    binding = null;
    super.onDestroyView();
  }

  private void openScan(long scanId) {
    if (scanId <= 0) {
      return;
    }
    ScansGalleryFragmentDirections.ActionScansGalleryFragmentToScanDisplayFragment action =
        ScansGalleryFragmentDirections.actionScansGalleryFragmentToScanDisplayFragment();
    action.setScanId(scanId);
    action.setSource(MainActivity.SCAN_DISPLAY_SOURCE_SAVED_GALLERY);
    NavHostFragment.findNavController(this).navigate(action);
  }

  private void setupControls() {
    ArrayAdapter<CharSequence> sortFieldAdapter = ArrayAdapter.createFromResource(
        requireContext(),
        R.array.scans_gallery_sort_fields,
        android.R.layout.simple_spinner_item
    );
    sortFieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    binding.sortFieldSpinner.setAdapter(sortFieldAdapter);
    binding.sortFieldSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener(position -> {
      viewModel.setSortField(
          (position == 1) ? ScansGalleryViewModel.SortField.BREED
              : ScansGalleryViewModel.SortField.DATE
      );
    }));

    ArrayAdapter<CharSequence> sortDirectionAdapter = ArrayAdapter.createFromResource(
        requireContext(),
        R.array.scans_gallery_sort_directions,
        android.R.layout.simple_spinner_item
    );
    sortDirectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    binding.sortDirectionSpinner.setAdapter(sortDirectionAdapter);
    binding.sortDirectionSpinner.setOnItemSelectedListener(
        new SimpleItemSelectedListener(position -> {
          viewModel.setSortDirection(
              (position == 0) ? ScansGalleryViewModel.SortDirection.ASCENDING
                  : ScansGalleryViewModel.SortDirection.DESCENDING
          );
        })
    );

    filterWatcher = new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        viewModel.setFilterText((s != null) ? s.toString() : "");
      }
    };
    binding.filterInput.addTextChangedListener(filterWatcher);
  }

  private void applySettingsDefaults() {
    if (preferences == null || binding == null) {
      return;
    }

    String sortFieldValue = preferences.getString(
        SettingsFragment.PREF_KEY_SORT_FIELD,
        SettingsFragment.DEFAULT_SORT_FIELD
    );
    ScansGalleryViewModel.SortField sortField = SettingsFragment.SORT_FIELD_BREED_NAME.equals(
        sortFieldValue
    )
        ? ScansGalleryViewModel.SortField.BREED
        : ScansGalleryViewModel.SortField.DATE;
    if (!SettingsFragment.SORT_FIELD_DATE_CREATED.equals(sortFieldValue)
        && !SettingsFragment.SORT_FIELD_BREED_NAME.equals(sortFieldValue)) {
      preferences.edit()
          .putString(SettingsFragment.PREF_KEY_SORT_FIELD, SettingsFragment.DEFAULT_SORT_FIELD)
          .apply();
    }
    viewModel.setSortField(sortField);

    String sortDirectionValue = preferences.getString(
        SettingsFragment.PREF_KEY_SORT_DIRECTION,
        SettingsFragment.DEFAULT_SORT_DIRECTION
    );
    ScansGalleryViewModel.SortDirection direction =
        SettingsFragment.SORT_DIRECTION_ASCENDING.equals(sortDirectionValue)
            ? ScansGalleryViewModel.SortDirection.ASCENDING
            : ScansGalleryViewModel.SortDirection.DESCENDING;
    if (!SettingsFragment.SORT_DIRECTION_DESCENDING.equals(sortDirectionValue)
        && !SettingsFragment.SORT_DIRECTION_ASCENDING.equals(sortDirectionValue)) {
      preferences.edit().putString(
          SettingsFragment.PREF_KEY_SORT_DIRECTION,
          SettingsFragment.DEFAULT_SORT_DIRECTION
      ).apply();
    }
    viewModel.setSortDirection(direction);

    String columnsValue = preferences.getString(
        SettingsFragment.PREF_KEY_GALLERY_COLUMNS,
        SettingsFragment.DEFAULT_GALLERY_COLUMNS
    );
    int columns;
    if (SettingsFragment.GALLERY_COLUMNS_2.equals(columnsValue)) {
      columns = 2;
    } else if (SettingsFragment.GALLERY_COLUMNS_4.equals(columnsValue)) {
      columns = 4;
    } else {
      columns = 3;
      if (!SettingsFragment.GALLERY_COLUMNS_3.equals(columnsValue)) {
        preferences.edit().putString(
            SettingsFragment.PREF_KEY_GALLERY_COLUMNS,
            SettingsFragment.DEFAULT_GALLERY_COLUMNS
        ).apply();
      }
    }
    if (layoutManager != null && layoutManager.getSpanCount() != columns) {
      layoutManager.setSpanCount(columns);
    }
  }

}
