package edu.cnm.deepdive.doggoneit.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.doggoneit.model.entity.BreedPrediction;
import edu.cnm.deepdive.doggoneit.model.entity.Scan;
import edu.cnm.deepdive.doggoneit.model.entity.ScanWithPredictions;
import edu.cnm.deepdive.doggoneit.service.repository.ScanRepository;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;

@HiltViewModel
public class ScanDisplayViewModel extends ViewModel {

  public enum ContentTab {
    FACTS,
    NOTES
  }

  public enum NotesMode {
    VIEW,
    EDIT
  }

  private final ScanRepository scanRepository;
  private final MediatorLiveData<UiState> uiState;
  private final MutableLiveData<ContentTab> selectedTab;
  private final MutableLiveData<Boolean> factsLoading;
  private final MutableLiveData<NotesMode> notesMode;
  private LiveData<ScanWithPredictions> scanSource;
  private long scanId;

  @Inject
  public ScanDisplayViewModel(ScanRepository scanRepository) {
    this.scanRepository = scanRepository;
    uiState = new MediatorLiveData<>();
    selectedTab = new MutableLiveData<>(ContentTab.FACTS);
    factsLoading = new MutableLiveData<>(false);
    notesMode = new MutableLiveData<>(NotesMode.VIEW);
    uiState.addSource(selectedTab, value -> emitUiState());
    uiState.addSource(factsLoading, value -> emitUiState());
    uiState.addSource(notesMode, value -> emitUiState());
    emitUiState();
  }

  public LiveData<UiState> getUiState() {
    return uiState;
  }

  public void loadScan(long scanId) {
    if (scanId <= 0 || scanId == this.scanId) {
      if (scanId <= 0) {
        this.scanId = 0;
        if (scanSource != null) {
          uiState.removeSource(scanSource);
          scanSource = null;
        }
        emitUiState();
      }
      return;
    }
    this.scanId = scanId;
    if (scanSource != null) {
      uiState.removeSource(scanSource);
    }
    scanSource = scanRepository.getWithPredictionsById(scanId);
    uiState.addSource(scanSource, value -> emitUiState());
    emitUiState();
  }

  public void setSelectedTab(ContentTab tab) {
    selectedTab.setValue((tab != null) ? tab : ContentTab.FACTS);
  }

  public void toggleFavorite(boolean favorite) {
    UiState currentState = uiState.getValue();
    if (currentState == null || currentState.scan == null) {
      return;
    }
    Scan scan = currentState.scan;
    if (scan.isFavorite() == favorite) {
      return;
    }
    scan.setFavorite(favorite);
    emitUiState();
    scanRepository.update(scan);
  }

  private void emitUiState() {
    ScanWithPredictions scanWithPredictions = (scanSource != null) ? scanSource.getValue() : null;
    Scan scan = (scanWithPredictions != null) ? scanWithPredictions.getScan() : null;
    String selectedBreedLabel = resolveSelectedBreedLabel(scanWithPredictions);
    Integer selectedConfidencePercent = resolveSelectedConfidencePercent(scanWithPredictions);
    ContentTab tab = (selectedTab.getValue() != null) ? selectedTab.getValue() : ContentTab.FACTS;
    boolean isFactsLoading = Boolean.TRUE.equals(factsLoading.getValue());
    NotesMode currentNotesMode =
        (notesMode.getValue() != null) ? notesMode.getValue() : NotesMode.VIEW;
    uiState.setValue(new UiState(
        scanId,
        scan,
        tab,
        isFactsLoading,
        currentNotesMode,
        formatBreedLabel(selectedBreedLabel),
        selectedConfidencePercent
    ));
  }

  private String resolveSelectedBreedLabel(ScanWithPredictions scanWithPredictions) {
    if (scanWithPredictions == null || scanWithPredictions.getScan() == null) {
      return null;
    }
    Scan scan = scanWithPredictions.getScan();
    String selectedLabel = scan.getSelectedBreedLabel();
    if (selectedLabel != null && !selectedLabel.isBlank()) {
      return selectedLabel;
    }
    List<BreedPrediction> predictions = scanWithPredictions.getPredictions();
    if (predictions == null || predictions.isEmpty()) {
      return null;
    }
    BreedPrediction topPrediction = predictions.get(0);
    return (topPrediction != null) ? topPrediction.getName() : null;
  }

  private Integer resolveSelectedConfidencePercent(ScanWithPredictions scanWithPredictions) {
    if (scanWithPredictions == null || scanWithPredictions.getScan() == null) {
      return null;
    }
    Scan scan = scanWithPredictions.getScan();
    Double selectedConfidence = scan.getSelectedBreedConfidence();
    if (selectedConfidence != null) {
      return (int) Math.round(selectedConfidence * 100.0);
    }
    List<BreedPrediction> predictions = scanWithPredictions.getPredictions();
    if (predictions == null || predictions.isEmpty()) {
      return null;
    }
    BreedPrediction topPrediction = predictions.get(0);
    if (topPrediction == null) {
      return null;
    }
    return (int) Math.round(topPrediction.getProbability() * 100.0);
  }

  private String formatBreedLabel(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    String[] words = value.trim().replace('-', ' ').replace('_', ' ').split("\\s+");
    StringBuilder builder = new StringBuilder();
    for (String word : words) {
      if (word.isBlank()) {
        continue;
      }
      if (builder.length() > 0) {
        builder.append(' ');
      }
      if (word.length() == 1) {
        builder.append(word.toUpperCase(Locale.US));
      } else {
        builder.append(word.substring(0, 1).toUpperCase(Locale.US))
            .append(word.substring(1).toLowerCase(Locale.US));
      }
    }
    return (builder.length() > 0) ? builder.toString() : null;
  }

  public static class UiState {

    public final long scanId;
    public final Scan scan;
    public final ContentTab selectedTab;
    public final boolean factsLoading;
    public final NotesMode notesMode;
    public final String selectedBreedLabel;
    public final Integer selectedConfidencePercent;

    UiState(
        long scanId,
        Scan scan,
        @NonNull ContentTab selectedTab,
        boolean factsLoading,
        @NonNull NotesMode notesMode,
        String selectedBreedLabel,
        Integer selectedConfidencePercent
    ) {
      this.scanId = scanId;
      this.scan = scan;
      this.selectedTab = selectedTab;
      this.factsLoading = factsLoading;
      this.notesMode = notesMode;
      this.selectedBreedLabel = selectedBreedLabel;
      this.selectedConfidencePercent = selectedConfidencePercent;
    }
  }

}
