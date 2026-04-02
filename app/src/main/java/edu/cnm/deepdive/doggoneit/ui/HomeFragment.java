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
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.doggoneit.databinding.FragmentHomeBinding;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

  private FragmentHomeBinding binding;
  private ActivityResultLauncher<Uri> takePictureLauncher;
  private Uri pendingPhotoUri;
  private File pendingPhotoFile;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentHomeBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(),
        this::handleTakePictureResult);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    binding.takePhotoButton.setOnClickListener(v -> launchCamera());
    binding.analyzeFromGalleryButton.setOnClickListener(
        v -> NavHostFragment.findNavController(this)
            .navigate(R.id.action_homeFragment_to_cameraGalleryFragment));
    binding.viewSavedButton.setOnClickListener(
        v -> NavHostFragment.findNavController(this)
            .navigate(R.id.action_homeFragment_to_scansGalleryFragment));
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  private void launchCamera() {
    try {
      pendingPhotoFile = createTempImageFile();
      pendingPhotoUri = FileProvider.getUriForFile(requireContext(),
          requireContext().getPackageName() + ".fileprovider", pendingPhotoFile);
      takePictureLauncher.launch(pendingPhotoUri);
    } catch (IOException e) {
      pendingPhotoFile = null;
      pendingPhotoUri = null;
    }
  }

  private void handleTakePictureResult(boolean success) {
    if (success && pendingPhotoUri != null) {
      NavHostFragment.findNavController(this)
          .navigate(HomeFragmentDirections.actionHomeFragmentToScanAnalysisFragment(
              pendingPhotoUri.toString()));
    } else {
      if (pendingPhotoFile != null && pendingPhotoFile.exists()) {
        pendingPhotoFile.delete();
      }
    }
    pendingPhotoFile = null;
    pendingPhotoUri = null;
  }

  private File createTempImageFile() throws IOException {
    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
    File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    return File.createTempFile("photo_" + timestamp + "_", ".jpg", storageDir);
  }

}
