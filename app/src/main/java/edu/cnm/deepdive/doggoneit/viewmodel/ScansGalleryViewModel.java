package edu.cnm.deepdive.doggoneit.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.doggoneit.R;
import edu.cnm.deepdive.doggoneit.model.entity.UserProfile;
import edu.cnm.deepdive.doggoneit.service.repository.ScanRepository;
import edu.cnm.deepdive.doggoneit.service.repository.UserSessionRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;

@HiltViewModel
public class ScansGalleryViewModel extends ViewModel {

  public enum SortField {
    DATE,
    BREED
  }

  public enum SortDirection {
    ASCENDING,
    DESCENDING
  }

  private final ScanRepository scanRepository;
  private final LiveData<List<ScanGalleryItem>> sourceGalleryItems;
  private final MediatorLiveData<List<ScanGalleryItem>> galleryItems;
  private final MediatorLiveData<Integer> emptyMessageResId;
  private final MutableLiveData<List<ScanGalleryItem>> emptyGalleryItems;
  private final MutableLiveData<SortField> sortField;
  private final MutableLiveData<SortDirection> sortDirection;
  private final MutableLiveData<String> filterText;

  @Inject
  public ScansGalleryViewModel(ScanRepository scanRepository,
      UserSessionRepository userSessionRepository) {
    this.scanRepository = scanRepository;
    emptyGalleryItems = new MutableLiveData<>();
    emptyGalleryItems.setValue(Collections.emptyList());
    sortField = new MutableLiveData<>(SortField.DATE);
    sortDirection = new MutableLiveData<>(SortDirection.DESCENDING);
    filterText = new MutableLiveData<>("");
    sourceGalleryItems = Transformations.switchMap(
        userSessionRepository.getCurrentUser(),
        this::resolveGalleryItems
    );
    galleryItems = new MediatorLiveData<>();
    emptyMessageResId = new MediatorLiveData<>();
    galleryItems.addSource(sourceGalleryItems, items -> updateGalleryItems());
    galleryItems.addSource(sortField, value -> updateGalleryItems());
    galleryItems.addSource(sortDirection, value -> updateGalleryItems());
    galleryItems.addSource(filterText, value -> updateGalleryItems());
    emptyMessageResId.addSource(sourceGalleryItems, items -> updateGalleryItems());
    emptyMessageResId.addSource(sortField, value -> updateGalleryItems());
    emptyMessageResId.addSource(sortDirection, value -> updateGalleryItems());
    emptyMessageResId.addSource(filterText, value -> updateGalleryItems());
    updateGalleryItems();
  }

  public LiveData<List<ScanGalleryItem>> getGalleryItems() {
    return galleryItems;
  }

  public LiveData<Integer> getEmptyMessageResId() {
    return emptyMessageResId;
  }

  public LiveData<SortField> getSortField() {
    return sortField;
  }

  public LiveData<SortDirection> getSortDirection() {
    return sortDirection;
  }

  public LiveData<String> getFilterText() {
    return filterText;
  }

  public void setSortField(SortField value) {
    sortField.setValue((value != null) ? value : SortField.DATE);
  }

  public void setSortDirection(SortDirection value) {
    sortDirection.setValue((value != null) ? value : SortDirection.DESCENDING);
  }

  public void setFilterText(String value) {
    filterText.setValue((value != null) ? value : "");
  }

  private LiveData<List<ScanGalleryItem>> resolveGalleryItems(UserProfile userProfile) {
    if (userProfile == null || userProfile.getId() <= 0) {
      return emptyGalleryItems;
    }
    return scanRepository.getGalleryItemsByUserProfileId(userProfile.getId());
  }

  private void updateGalleryItems() {
    List<ScanGalleryItem> source = sourceGalleryItems.getValue();
    List<ScanGalleryItem> filteredAndSorted = applyFilterAndSort(source);
    galleryItems.setValue(filteredAndSorted);
    if (source == null || source.isEmpty()) {
      emptyMessageResId.setValue(R.string.scans_gallery_empty);
    } else if (filteredAndSorted.isEmpty()) {
      emptyMessageResId.setValue(R.string.scans_gallery_no_filter_matches);
    } else {
      emptyMessageResId.setValue(0);
    }
  }

  private List<ScanGalleryItem> applyFilterAndSort(List<ScanGalleryItem> source) {
    if (source == null || source.isEmpty()) {
      return Collections.emptyList();
    }
    String query = normalize(filterText.getValue());
    List<ScanGalleryItem> result = new ArrayList<>();
    for (ScanGalleryItem item : source) {
      if (item != null && matchesFilter(item, query)) {
        result.add(item);
      }
    }
    result.sort(getComparator());
    return result;
  }

  private boolean matchesFilter(ScanGalleryItem item, String query) {
    if (query.isEmpty()) {
      return true;
    }
    return normalize(item.getTopBreedLabel()).contains(query);
  }

  private Comparator<ScanGalleryItem> getComparator() {
    Comparator<ScanGalleryItem> comparator;
    if (sortField.getValue() == SortField.BREED) {
      comparator = Comparator
          .comparing(this::breedValue, String.CASE_INSENSITIVE_ORDER)
          .thenComparing(this::timestampValue)
          .thenComparingLong(ScanGalleryItem::getScanId);
    } else {
      comparator = Comparator
          .comparing(this::timestampValue)
          .thenComparing(this::breedValue, String.CASE_INSENSITIVE_ORDER)
          .thenComparingLong(ScanGalleryItem::getScanId);
    }
    if (sortDirection.getValue() == SortDirection.DESCENDING) {
      comparator = comparator.reversed();
    }
    return comparator;
  }

  private Instant timestampValue(ScanGalleryItem item) {
    return (item != null && item.getTimestamp() != null) ? item.getTimestamp() : Instant.EPOCH;
  }

  private String breedValue(ScanGalleryItem item) {
    return normalize((item != null) ? item.getTopBreedLabel() : "");
  }

  private String normalize(String value) {
    return (value != null) ? value.trim().toLowerCase(Locale.US) : "";
  }
}
