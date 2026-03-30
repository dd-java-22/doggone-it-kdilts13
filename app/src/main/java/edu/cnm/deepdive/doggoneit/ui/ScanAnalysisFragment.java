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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.doggoneit.R;
import edu.cnm.deepdive.doggoneit.databinding.FragmentScanAnalysisBinding;
import edu.cnm.deepdive.doggoneit.ml.DogBreedModelPoc;

@AndroidEntryPoint
public class ScanAnalysisFragment extends Fragment {

  private FragmentScanAnalysisBinding binding;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentScanAnalysisBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    binding.viewResultsButton.setOnClickListener(
        v -> NavHostFragment.findNavController(this)
            .navigate(R.id.action_scanAnalysisFragment_to_scanDisplayFragment));
    binding.initModelPocButton.setOnClickListener(v -> {
      binding.pocStatusText.setText(R.string.init_model_poc_status_running);
      DogBreedModelPoc.runOnceAndLog(requireContext(), R.drawable.dog);
      binding.pocStatusText.setText(R.string.init_model_poc_status_done);
    });
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

}
