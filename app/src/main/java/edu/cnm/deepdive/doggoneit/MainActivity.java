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
package edu.cnm.deepdive.doggoneit;

import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.fragment.NavHostFragment;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.doggoneit.databinding.ActivityMainBinding;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

  private ActivityMainBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = ActivityMainBinding.inflate(getLayoutInflater());

    setupUI();

    setContentView(binding.getRoot());

    NavHostFragment navHostFragment =
        (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
    if (navHostFragment == null) {
      throw new IllegalStateException("NavHostFragment not found");
    }
    NavController navController = navHostFragment.getNavController();
    NavigationUI.setupWithNavController(binding.bottomNav, navController);
    binding.bottomNav.setOnItemReselectedListener(item -> {
      // No-op to avoid reselect actions creating duplicate destinations.
    });
    navController.addOnDestinationChangedListener(
        (controller, destination, arguments) -> toggleBottomNav(destination));
  }

  private void toggleBottomNav(NavDestination destination) {
    int visibility =
        (destination.getId() == R.id.loginFragment) ? View.GONE : View.VISIBLE;
    binding.bottomNav.setVisibility(visibility);
  }

  private void setupUI() {
    setContentView(binding.getRoot());
    EdgeToEdge.enable(this);
    View root = binding.getRoot();
    int initialLeft = root.getPaddingLeft();
    int initialTop = root.getPaddingTop();
    int initialRight = root.getPaddingRight();
    int initialBottom = root.getPaddingBottom();
    ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
      Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      view.setPadding(
        initialLeft + systemBars.left,
        initialTop + systemBars.top,
        initialRight + systemBars.right,
        initialBottom + systemBars.bottom
      );
      return WindowInsetsCompat.CONSUMED;
    });
  }

}
