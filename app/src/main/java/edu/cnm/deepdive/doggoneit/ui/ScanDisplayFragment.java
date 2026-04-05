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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.doggoneit.R;
import edu.cnm.deepdive.doggoneit.databinding.FragmentScanDisplayBinding;

@AndroidEntryPoint
public class ScanDisplayFragment extends Fragment {

  private FragmentScanDisplayBinding binding;

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
    String savedImageUri = ScanDisplayFragmentArgs.fromBundle(args).getSavedImageUri();
    if (savedImageUri == null || savedImageUri.isBlank()) {
      showMissingImage();
      return;
    }
    Uri parsedUri = Uri.parse(savedImageUri);
    binding.savedImage.setImageURI(parsedUri);
    binding.savedImage.setVisibility(View.VISIBLE);
    binding.scanDisplayStatus.setVisibility(View.GONE);
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  private void showMissingImage() {
    binding.savedImage.setVisibility(View.GONE);
    binding.scanDisplayStatus.setVisibility(View.VISIBLE);
    binding.scanDisplayStatus.setText(R.string.scan_display_missing_image);
  }

}
