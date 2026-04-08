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
import android.net.Uri;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.fragment.NavHostFragment;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.doggoneit.CameraCaptureHelper;
import edu.cnm.deepdive.doggoneit.databinding.ActivityMainBinding;
import java.io.File;
import java.io.IOException;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

  public static final String SCAN_DISPLAY_SOURCE_ARG = "source";
  public static final String SCAN_DISPLAY_SOURCE_ANALYSIS = "ANALYSIS";
  public static final String SCAN_DISPLAY_SOURCE_SAVED_GALLERY = "SAVED_GALLERY";
  private static final int TOP_LEVEL_ROOT_ID = R.id.homeFragment;

  private ActivityMainBinding binding;
  private AppBarConfiguration appBarConfiguration;
  private NavController navController;
  private ActivityResultLauncher<Uri> takePictureLauncher;
  private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;
  private Uri pendingPhotoUri;
  private File pendingPhotoFile;
  private int lastSelectedItemId;
  private boolean restoringSelection;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    binding = ActivityMainBinding.inflate(getLayoutInflater());

    setupUI();

    setContentView(binding.getRoot());
    setSupportActionBar(binding.topAppBar);

    NavHostFragment navHostFragment =
        (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
    if (navHostFragment == null) {
      throw new IllegalStateException("NavHostFragment not found");
    }
    navController = navHostFragment.getNavController();
    appBarConfiguration = new AppBarConfiguration.Builder(
        R.id.homeFragment,
        R.id.scansGalleryFragment,
        R.id.settingsFragment
    ).build();
    NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(),
        this::handleTakePictureResult);
    pickMediaLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(),
        this::handlePickMediaResult);
    binding.bottomNav.setOnItemSelectedListener(item -> {
      if (restoringSelection) {
        return true;
      }
      if (item.getItemId() == R.id.cameraAction) {
        restoreBottomNavSelection();
        launchCamera();
        return false;
      }
      if (item.getItemId() == R.id.galleryAction) {
        restoreBottomNavSelection();
        launchGalleryPicker();
        return false;
      }
      if (item.getItemId() == R.id.homeFragment
          || item.getItemId() == R.id.scansGalleryFragment
          || item.getItemId() == R.id.settingsFragment) {
        return navigateToTopLevelDestination(item.getItemId());
      }
      boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
      return handled;
    });
    binding.bottomNav.setOnItemReselectedListener(item -> {
      if (item.getItemId() == R.id.cameraAction) {
        restoreBottomNavSelection();
        launchCamera();
      } else if (item.getItemId() == R.id.galleryAction) {
        restoreBottomNavSelection();
        launchGalleryPicker();
      } else if (isTopLevelDestination(item.getItemId())) {
        navigateToTopLevelDestination(item.getItemId());
      }
    });
    navController.addOnDestinationChangedListener(
        (controller, destination, arguments) -> toggleBottomNav(destination, arguments));
  }

  @Override
  public boolean onSupportNavigateUp() {
    NavDestination currentDestination = navController.getCurrentDestination();
    if (currentDestination != null && currentDestination.getId() == R.id.scanDisplayFragment) {
      Bundle arguments = navController.getCurrentBackStackEntry() != null
          ? navController.getCurrentBackStackEntry().getArguments() : null;
      if (handleScanDisplayReturn(arguments)) {
        return true;
      }
    }
    return NavigationUI.navigateUp(navController, appBarConfiguration)
        || super.onSupportNavigateUp();
  }

  private void toggleBottomNav(NavDestination destination, Bundle arguments) {
    int visibility =
        (destination.getId() == R.id.loginFragment) ? View.GONE : View.VISIBLE;
    binding.bottomNav.setVisibility(visibility);
    updateSelectedBottomNavItem(destination, arguments);
  }

  private void setupUI() {
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

  private void restoreBottomNavSelection() {
    if (lastSelectedItemId != 0
        && lastSelectedItemId != R.id.cameraAction
        && lastSelectedItemId != R.id.galleryAction) {
      restoringSelection = true;
      binding.bottomNav.setSelectedItemId(lastSelectedItemId);
      restoringSelection = false;
    }
  }

  private void updateSelectedBottomNavItem(NavDestination destination, Bundle arguments) {
    int selectedItemId = resolveSelectedItemId(destination, arguments);
    if (selectedItemId == 0) {
      return;
    }
    lastSelectedItemId = selectedItemId;
    if (binding.bottomNav.getSelectedItemId() != selectedItemId) {
      restoringSelection = true;
      binding.bottomNav.setSelectedItemId(selectedItemId);
      restoringSelection = false;
    }
  }

  private boolean navigateToTopLevelDestination(int destinationId) {
    if (!isTopLevelDestination(destinationId)) {
      return false;
    }
    NavOptions options = new NavOptions.Builder()
        .setLaunchSingleTop(true)
        .setPopUpTo(TOP_LEVEL_ROOT_ID, false)
        .build();
    navController.navigate(destinationId, null, options);
    return true;
  }

  private boolean isTopLevelDestination(int destinationId) {
    return destinationId == R.id.homeFragment
        || destinationId == R.id.scansGalleryFragment
        || destinationId == R.id.settingsFragment;
  }

  private int resolveSelectedItemId(NavDestination destination, Bundle arguments) {
    int destinationId = destination.getId();
    if (isTopLevelDestination(destinationId)) {
      return destinationId;
    }
    if (destinationId == R.id.scanDisplayFragment) {
      String source = (arguments != null) ? arguments.getString(SCAN_DISPLAY_SOURCE_ARG) : null;
      if (SCAN_DISPLAY_SOURCE_SAVED_GALLERY.equals(source)) {
        return R.id.scansGalleryFragment;
      }
      return R.id.homeFragment;
    }
    return lastSelectedItemId;
  }

  private void launchCamera() {
    try {
      pendingPhotoFile = CameraCaptureHelper.createTempImageFile(this);
      pendingPhotoUri = FileProvider.getUriForFile(this,
          getPackageName() + ".fileprovider", pendingPhotoFile);
      takePictureLauncher.launch(pendingPhotoUri);
    } catch (IOException e) {
      pendingPhotoFile = null;
      pendingPhotoUri = null;
    }
  }

  private void handleTakePictureResult(boolean success) {
    if (success && pendingPhotoUri != null) {
      Bundle args = new Bundle();
      args.putString("imageUri", pendingPhotoUri.toString());
      navController.navigate(R.id.scanAnalysisFragment, args);
    } else {
      if (pendingPhotoFile != null && pendingPhotoFile.exists()) {
        pendingPhotoFile.delete();
      }
    }
    pendingPhotoFile = null;
    pendingPhotoUri = null;
  }

  private void launchGalleryPicker() {
    pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
        .build());
  }

  private void handlePickMediaResult(Uri selectedUri) {
    if (selectedUri != null) {
      Bundle args = new Bundle();
      args.putString("imageUri", selectedUri.toString());
      navController.navigate(R.id.scanAnalysisFragment, args);
    }
  }

  public boolean handleScanDisplayReturn(Bundle arguments) {
    String source = (arguments != null) ? arguments.getString(SCAN_DISPLAY_SOURCE_ARG) : null;
    int destinationId = SCAN_DISPLAY_SOURCE_SAVED_GALLERY.equals(source)
        ? R.id.scansGalleryFragment
        : R.id.homeFragment;
    return navigateToTopLevelDestination(destinationId);
  }

}
