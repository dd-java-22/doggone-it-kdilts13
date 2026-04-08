package edu.cnm.deepdive.doggoneit.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.doggoneit.model.entity.UserProfile;
import edu.cnm.deepdive.doggoneit.service.repository.ScanRepository;
import edu.cnm.deepdive.doggoneit.service.repository.UserSessionRepository;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

@HiltViewModel
public class ScansGalleryViewModel extends ViewModel {

  private final ScanRepository scanRepository;
  private final LiveData<List<ScanGalleryItem>> galleryItems;
  private final MutableLiveData<List<ScanGalleryItem>> emptyGalleryItems;

  @Inject
  public ScansGalleryViewModel(ScanRepository scanRepository,
      UserSessionRepository userSessionRepository) {
    this.scanRepository = scanRepository;
    emptyGalleryItems = new MutableLiveData<>();
    emptyGalleryItems.setValue(Collections.emptyList());
    galleryItems = Transformations.switchMap(
        userSessionRepository.getCurrentUser(),
        this::resolveGalleryItems
    );
  }

  public LiveData<List<ScanGalleryItem>> getGalleryItems() {
    return galleryItems;
  }

  private LiveData<List<ScanGalleryItem>> resolveGalleryItems(UserProfile userProfile) {
    if (userProfile == null || userProfile.getId() <= 0) {
      return emptyGalleryItems;
    }
    return scanRepository.getGalleryItemsByUserProfileId(userProfile.getId());
  }
}
