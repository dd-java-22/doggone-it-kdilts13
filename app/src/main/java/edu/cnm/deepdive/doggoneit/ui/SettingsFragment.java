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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;
import com.google.android.material.button.MaterialButtonToggleGroup;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.doggoneit.R;
import edu.cnm.deepdive.doggoneit.databinding.FragmentSettingsBinding;
import edu.cnm.deepdive.doggoneit.viewmodel.LoginViewModel;

@AndroidEntryPoint
/**
 * Settings screen for authentication actions and default gallery presentation preferences.
 */
public class SettingsFragment extends Fragment {

  public static final String PREF_KEY_SORT_FIELD = "pref_default_sort_field";
  public static final String PREF_KEY_SORT_DIRECTION = "pref_default_sort_direction";
  public static final String PREF_KEY_GALLERY_COLUMNS = "pref_gallery_column_count";
  public static final String SORT_FIELD_DATE_CREATED = "date_created";
  public static final String SORT_FIELD_BREED_NAME = "breed_name";
  public static final String SORT_DIRECTION_DESCENDING = "descending";
  public static final String SORT_DIRECTION_ASCENDING = "ascending";
  public static final String GALLERY_COLUMNS_2 = "2";
  public static final String GALLERY_COLUMNS_3 = "3";
  public static final String GALLERY_COLUMNS_4 = "4";
  public static final String DEFAULT_SORT_FIELD = SORT_FIELD_DATE_CREATED;
  public static final String DEFAULT_SORT_DIRECTION = SORT_DIRECTION_DESCENDING;
  public static final String DEFAULT_GALLERY_COLUMNS = GALLERY_COLUMNS_3;

  private FragmentSettingsBinding binding;
  private LoginViewModel viewModel;
  private SharedPreferences preferences;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentSettingsBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

    binding.logoutButton.setOnClickListener(v -> viewModel.signOut());

    MaterialButtonToggleGroup sortByGroup = binding.sortByGroup;
    MaterialButtonToggleGroup sortDirectionGroup = binding.sortDirectionGroup;
    MaterialButtonToggleGroup galleryColumnsGroup = binding.galleryColumnsGroup;

    configureSortByGroup(sortByGroup);
    configureSortDirectionGroup(sortDirectionGroup);
    configureGalleryColumnsGroup(galleryColumnsGroup);
    applySavedSelections(sortByGroup, sortDirectionGroup, galleryColumnsGroup);

    NavController navController = NavHostFragment.findNavController(this);
    viewModel.getCredential()
      .observe(getViewLifecycleOwner(), credential -> {
        if (credential == null) {
          NavOptions options = new NavOptions.Builder()
              .setPopUpTo(R.id.loginFragment, true)
              .setLaunchSingleTop(true)
              .build();
          navController.navigate(R.id.loginFragment, null, options);
        }
      });
    viewModel.getThrowable()
      .observe(getViewLifecycleOwner(), throwable -> {
        if (throwable != null) {
          // TODO: 3/26/2026 show a snackbar
        }
      });
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  private void configureSortByGroup(MaterialButtonToggleGroup group) {
    group.addOnButtonCheckedListener((toggleGroup, checkedId, isChecked) -> {
      if (!isChecked) {
        return;
      }
      String value;
      if (checkedId == R.id.sort_by_breed_button) {
        value = SORT_FIELD_BREED_NAME;
      } else if (checkedId == R.id.sort_by_date_button) {
        value = SORT_FIELD_DATE_CREATED;
      } else {
        value = DEFAULT_SORT_FIELD;
      }
      preferences.edit().putString(PREF_KEY_SORT_FIELD, value).apply();
    });
  }

  private void configureSortDirectionGroup(MaterialButtonToggleGroup group) {
    group.addOnButtonCheckedListener((toggleGroup, checkedId, isChecked) -> {
      if (!isChecked) {
        return;
      }
      String value;
      if (checkedId == R.id.sort_direction_asc_button) {
        value = SORT_DIRECTION_ASCENDING;
      } else if (checkedId == R.id.sort_direction_desc_button) {
        value = SORT_DIRECTION_DESCENDING;
      } else {
        value = DEFAULT_SORT_DIRECTION;
      }
      preferences.edit().putString(PREF_KEY_SORT_DIRECTION, value).apply();
    });
  }

  private void configureGalleryColumnsGroup(MaterialButtonToggleGroup group) {
    group.addOnButtonCheckedListener((toggleGroup, checkedId, isChecked) -> {
      if (!isChecked) {
        return;
      }
      String value;
      if (checkedId == R.id.gallery_columns_2_button) {
        value = GALLERY_COLUMNS_2;
      } else if (checkedId == R.id.gallery_columns_4_button) {
        value = GALLERY_COLUMNS_4;
      } else if (checkedId == R.id.gallery_columns_3_button) {
        value = GALLERY_COLUMNS_3;
      } else {
        value = DEFAULT_GALLERY_COLUMNS;
      }
      preferences.edit().putString(PREF_KEY_GALLERY_COLUMNS, value).apply();
    });
  }

  private void applySavedSelections(MaterialButtonToggleGroup sortByGroup,
      MaterialButtonToggleGroup sortDirectionGroup, MaterialButtonToggleGroup galleryColumnsGroup) {
    String sortField = preferences.getString(PREF_KEY_SORT_FIELD, DEFAULT_SORT_FIELD);
    if (!SORT_FIELD_DATE_CREATED.equals(sortField) && !SORT_FIELD_BREED_NAME.equals(sortField)) {
      sortField = DEFAULT_SORT_FIELD;
      preferences.edit().putString(PREF_KEY_SORT_FIELD, sortField).apply();
    }
    sortByGroup.check(
        SORT_FIELD_BREED_NAME.equals(sortField)
            ? R.id.sort_by_breed_button
            : R.id.sort_by_date_button
    );

    String sortDirection = preferences.getString(PREF_KEY_SORT_DIRECTION, DEFAULT_SORT_DIRECTION);
    if (!SORT_DIRECTION_DESCENDING.equals(sortDirection)
        && !SORT_DIRECTION_ASCENDING.equals(sortDirection)) {
      sortDirection = DEFAULT_SORT_DIRECTION;
      preferences.edit().putString(PREF_KEY_SORT_DIRECTION, sortDirection).apply();
    }
    sortDirectionGroup.check(
        SORT_DIRECTION_ASCENDING.equals(sortDirection)
            ? R.id.sort_direction_asc_button
            : R.id.sort_direction_desc_button
    );

    String columns = preferences.getString(PREF_KEY_GALLERY_COLUMNS, DEFAULT_GALLERY_COLUMNS);
    if (!GALLERY_COLUMNS_2.equals(columns) && !GALLERY_COLUMNS_3.equals(columns)
        && !GALLERY_COLUMNS_4.equals(columns)) {
      columns = DEFAULT_GALLERY_COLUMNS;
      preferences.edit().putString(PREF_KEY_GALLERY_COLUMNS, columns).apply();
    }
    int checkedColumnsId;
    if (GALLERY_COLUMNS_2.equals(columns)) {
      checkedColumnsId = R.id.gallery_columns_2_button;
    } else if (GALLERY_COLUMNS_4.equals(columns)) {
      checkedColumnsId = R.id.gallery_columns_4_button;
    } else {
      checkedColumnsId = R.id.gallery_columns_3_button;
    }
    galleryColumnsGroup.check(checkedColumnsId);
  }

}
