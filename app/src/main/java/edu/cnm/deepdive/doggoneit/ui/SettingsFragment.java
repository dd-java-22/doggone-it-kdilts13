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

import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.doggoneit.R;
import edu.cnm.deepdive.doggoneit.viewmodel.LoginViewModel;

@AndroidEntryPoint
public class SettingsFragment extends PreferenceFragmentCompat {

  private LoginViewModel viewModel;

  @Override
  public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
    setPreferencesFromResource(R.xml.preferences, rootKey);
    viewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    Preference logoutPreference = findPreference("logout");
    if (logoutPreference != null) {
      logoutPreference.setOnPreferenceClickListener(preference -> {
        viewModel.signOut();
        return true;
      });
    }
  }

  @Override
  public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
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

}
