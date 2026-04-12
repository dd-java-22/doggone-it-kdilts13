package edu.cnm.deepdive.doggoneit.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.doggoneit.model.entity.BreedInfo;
import edu.cnm.deepdive.doggoneit.model.entity.BreedMapping;
import edu.cnm.deepdive.doggoneit.model.entity.BreedPrediction;
import edu.cnm.deepdive.doggoneit.model.entity.Scan;
import edu.cnm.deepdive.doggoneit.model.entity.ScanWithPredictions;
import edu.cnm.deepdive.doggoneit.service.dogapi.BreedDetailsMapper;
import edu.cnm.deepdive.doggoneit.service.dogapi.DogApiService;
import edu.cnm.deepdive.doggoneit.service.dogapi.dto.BreedDetailsDto;
import edu.cnm.deepdive.doggoneit.service.repository.BreedInfoRepository;
import edu.cnm.deepdive.doggoneit.service.repository.BreedMappingRepository;
import edu.cnm.deepdive.doggoneit.service.repository.ScanRepository;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import retrofit2.Response;
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

  public enum FactsStatus {
    IDLE,
    LOADING,
    LOADED,
    SELECTED_BREED_MISSING,
    BREED_MAPPING_MISSING,
    FETCH_FAILED,
    NO_FACTS_AVAILABLE
  }

  private final ScanRepository scanRepository;
  private final BreedMappingRepository breedMappingRepository;
  private final BreedInfoRepository breedInfoRepository;
  private final DogApiService dogApiService;
  private final MediatorLiveData<UiState> uiState;
  private final MutableLiveData<ContentTab> selectedTab;
  private final MutableLiveData<FactsState> factsState;
  private final MutableLiveData<Boolean> favoriteSaving;
  private final MutableLiveData<String> savedNote;
  private final MutableLiveData<String> noteDraft;
  private final MutableLiveData<Boolean> noteSaving;
  private final MutableLiveData<NotesMode> notesMode;
  private final MutableLiveData<UiMessage> messageEvent;
  private LiveData<ScanWithPredictions> scanSource;
  private long scanId;
  private String factsSelectedBreedLabel;
  private int factsRequestCounter;
  private int favoriteRequestCounter;
  private int noteSaveRequestCounter;
  private long messageCounter;

  @Inject
  public ScanDisplayViewModel(ScanRepository scanRepository,
      BreedMappingRepository breedMappingRepository,
      BreedInfoRepository breedInfoRepository,
      DogApiService dogApiService) {
    this.scanRepository = scanRepository;
    this.breedMappingRepository = breedMappingRepository;
    this.breedInfoRepository = breedInfoRepository;
    this.dogApiService = dogApiService;
    uiState = new MediatorLiveData<>();
    selectedTab = new MutableLiveData<>(ContentTab.FACTS);
    factsState = new MutableLiveData<>(new FactsState(FactsStatus.IDLE, null, null));
    favoriteSaving = new MutableLiveData<>(false);
    savedNote = new MutableLiveData<>("");
    noteDraft = new MutableLiveData<>("");
    noteSaving = new MutableLiveData<>(false);
    notesMode = new MutableLiveData<>(NotesMode.VIEW);
    messageEvent = new MutableLiveData<>();
    uiState.addSource(selectedTab, value -> emitUiState());
    uiState.addSource(factsState, value -> emitUiState());
    uiState.addSource(favoriteSaving, value -> emitUiState());
    uiState.addSource(savedNote, value -> emitUiState());
    uiState.addSource(noteDraft, value -> emitUiState());
    uiState.addSource(noteSaving, value -> emitUiState());
    uiState.addSource(notesMode, value -> emitUiState());
    emitUiState();
  }

  public LiveData<UiState> getUiState() {
    return uiState;
  }

  public LiveData<UiMessage> getMessageEvent() {
    return messageEvent;
  }

  public void loadScan(long scanId) {
    if (scanId <= 0 || scanId == this.scanId) {
      if (scanId <= 0) {
        this.scanId = 0;
        if (scanSource != null) {
          uiState.removeSource(scanSource);
          scanSource = null;
        }
        factsSelectedBreedLabel = null;
        factsRequestCounter++;
        factsState.setValue(new FactsState(FactsStatus.IDLE, null, null));
        favoriteSaving.setValue(false);
        savedNote.setValue("");
        noteDraft.setValue("");
        noteSaving.setValue(false);
        notesMode.setValue(NotesMode.VIEW);
        emitUiState();
      }
      return;
    }
    this.scanId = scanId;
    if (scanSource != null) {
      uiState.removeSource(scanSource);
    }
    scanSource = scanRepository.getWithPredictionsById(scanId);
    uiState.addSource(scanSource, value -> {
      onScanChanged(value);
      emitUiState();
    });
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
    if (Boolean.TRUE.equals(favoriteSaving.getValue())) {
      return;
    }
    Scan scan = currentState.scan;
    boolean previousFavorite = scan.isFavorite();
    if (previousFavorite == favorite) {
      return;
    }
    final int requestId = ++favoriteRequestCounter;
    favoriteSaving.setValue(true);
    scan.setFavorite(favorite);
    emitUiState();
    scanRepository.update(scan)
        .thenAccept(rowsUpdated -> {
          if (requestId != favoriteRequestCounter) {
            return;
          }
          favoriteSaving.postValue(false);
          if (rowsUpdated == null || rowsUpdated <= 0) {
            scan.setFavorite(previousFavorite);
            emitUiState();
            postMessage("Unable to update favorite right now.");
          }
        })
        .exceptionally(throwable -> {
          if (requestId == favoriteRequestCounter) {
            favoriteSaving.postValue(false);
            scan.setFavorite(previousFavorite);
            emitUiState();
            postMessage("Unable to update favorite right now.");
          }
          return null;
        });
  }

  public void beginEditNote() {
    noteDraft.setValue((savedNote.getValue() != null) ? savedNote.getValue() : "");
    notesMode.setValue(NotesMode.EDIT);
  }

  public void updateNoteDraft(String value) {
    noteDraft.setValue((value != null) ? value : "");
  }

  public void cancelEditNote() {
    noteDraft.setValue((savedNote.getValue() != null) ? savedNote.getValue() : "");
    notesMode.setValue(NotesMode.VIEW);
    noteSaving.setValue(false);
  }

  public void saveNote() {
    UiState currentState = uiState.getValue();
    if (currentState == null || currentState.scan == null) {
      return;
    }
    if (Boolean.TRUE.equals(noteSaving.getValue())) {
      return;
    }
    Scan scan = currentState.scan;
    String previous = (savedNote.getValue() != null) ? savedNote.getValue() : "";
    String target = normalizeNote(noteDraft.getValue());
    if (previous.equals(target)) {
      notesMode.setValue(NotesMode.VIEW);
      return;
    }
    final int requestId = ++noteSaveRequestCounter;
    noteSaving.setValue(true);
    scan.setNote(target);
    scanRepository.update(scan)
        .thenAccept(rowsUpdated -> {
          if (requestId != noteSaveRequestCounter) {
            return;
          }
          noteSaving.postValue(false);
          if (rowsUpdated == null || rowsUpdated <= 0) {
            scan.setNote(previous);
            noteDraft.postValue(previous);
            postMessage("Unable to save note right now.");
            return;
          }
          savedNote.postValue(target);
          noteDraft.postValue(target);
          notesMode.postValue(NotesMode.VIEW);
        })
        .exceptionally(throwable -> {
          if (requestId == noteSaveRequestCounter) {
            scan.setNote(previous);
            noteSaving.postValue(false);
            noteDraft.postValue(previous);
            postMessage("Unable to save note right now.");
          }
          return null;
        });
  }

  private void emitUiState() {
    ScanWithPredictions scanWithPredictions = (scanSource != null) ? scanSource.getValue() : null;
    Scan scan = (scanWithPredictions != null) ? scanWithPredictions.getScan() : null;
    String selectedBreedLabel = resolveSelectedBreedLabel(scanWithPredictions);
    Integer selectedConfidencePercent = resolveSelectedConfidencePercent(scanWithPredictions);
    ContentTab tab = (selectedTab.getValue() != null) ? selectedTab.getValue() : ContentTab.FACTS;
    FactsState currentFactsState = (factsState.getValue() != null)
        ? factsState.getValue()
        : new FactsState(FactsStatus.IDLE, null, null);
    NotesMode currentNotesMode =
        (notesMode.getValue() != null) ? notesMode.getValue() : NotesMode.VIEW;
    String currentSavedNote = (savedNote.getValue() != null) ? savedNote.getValue() : "";
    String currentNoteDraft = (noteDraft.getValue() != null) ? noteDraft.getValue() : "";
    boolean isNoteSaving = Boolean.TRUE.equals(noteSaving.getValue());
    boolean isFavoriteSaving = Boolean.TRUE.equals(favoriteSaving.getValue());
    uiState.setValue(new UiState(
        scanId,
        scan,
        tab,
        currentFactsState,
        currentNotesMode,
        currentSavedNote,
        currentNoteDraft,
        isNoteSaving,
        isFavoriteSaving,
        formatBreedLabel(selectedBreedLabel),
        selectedConfidencePercent
    ));
  }

  private void onScanChanged(ScanWithPredictions scanWithPredictions) {
    Scan scan = (scanWithPredictions != null) ? scanWithPredictions.getScan() : null;
    syncNoteFromScan(scan);
    String selectedBreedLabel = getPersistedSelectedBreedLabel(scan);
    if (selectedBreedLabel == null) {
      factsSelectedBreedLabel = null;
      factsRequestCounter++;
      factsState.setValue(new FactsState(FactsStatus.SELECTED_BREED_MISSING, null, null));
      return;
    }
    if (selectedBreedLabel.equals(factsSelectedBreedLabel)) {
      return;
    }
    factsSelectedBreedLabel = selectedBreedLabel;
    loadFacts(selectedBreedLabel);
  }

  private void loadFacts(String selectedBreedLabel) {
    final int requestId = ++factsRequestCounter;
    factsState.setValue(new FactsState(FactsStatus.LOADING, null, null));
    CompletableFuture
        .supplyAsync(() -> resolveFacts(selectedBreedLabel))
        .thenAccept(state -> postFactsStateIfCurrent(requestId, state))
        .exceptionally(throwable -> {
          postFactsStateIfCurrent(requestId, new FactsState(
              FactsStatus.FETCH_FAILED,
              null,
              throwable.getMessage()
          ));
          return null;
        });
  }

  private FactsState resolveFacts(String selectedBreedLabel) {
    try {
      BreedMapping mapping = breedMappingRepository.getByModelLabelNow(selectedBreedLabel).join();
      if (mapping == null || mapping.getDogApiBreedId() <= 0) {
        return new FactsState(FactsStatus.BREED_MAPPING_MISSING, null, null);
      }
      long dogApiBreedId = mapping.getDogApiBreedId();
      BreedInfo cached = breedInfoRepository.getByDogApiBreedIdNow(dogApiBreedId).join();
      if (cached != null && hasDisplayableFacts(cached)) {
        return new FactsState(FactsStatus.LOADED, cached, null);
      }
      BreedInfo fetched = fetchBreedInfoFromApi(dogApiBreedId);
      if (fetched == null) {
        return new FactsState(FactsStatus.NO_FACTS_AVAILABLE, null, null);
      }
      BreedInfo saved = breedInfoRepository.saveOrUpdate(fetched).join();
      if (saved == null || !hasDisplayableFacts(saved)) {
        return new FactsState(FactsStatus.NO_FACTS_AVAILABLE, null, null);
      }
      return new FactsState(FactsStatus.LOADED, saved, null);
    } catch (Exception e) {
      return new FactsState(FactsStatus.FETCH_FAILED, null, e.getMessage());
    }
  }

  private BreedInfo fetchBreedInfoFromApi(long dogApiBreedId) throws IOException {
    Response<BreedDetailsDto> response = dogApiService.getBreedDetails(dogApiBreedId).execute();
    if (!response.isSuccessful()) {
      throw new IOException("Dog API returned " + response.code());
    }
    BreedDetailsDto body = response.body();
    if (body == null) {
      return null;
    }
    return BreedDetailsMapper.toBreedInfo(body);
  }

  private void postFactsStateIfCurrent(int requestId, FactsState state) {
    if (requestId != factsRequestCounter) {
      return;
    }
    factsState.postValue(state);
  }

  private String getPersistedSelectedBreedLabel(Scan scan) {
    if (scan == null) {
      return null;
    }
    String selectedBreedLabel = scan.getSelectedBreedLabel();
    if (selectedBreedLabel == null || selectedBreedLabel.isBlank()) {
      return null;
    }
    return selectedBreedLabel.trim();
  }

  private void syncNoteFromScan(Scan scan) {
    if (notesMode.getValue() == NotesMode.EDIT) {
      return;
    }
    if (scan == null) {
      savedNote.setValue("");
      noteDraft.setValue("");
      return;
    }
    String persisted = normalizeNote(scan.getNote());
    savedNote.setValue(persisted);
    noteDraft.setValue(persisted);
  }

  private String normalizeNote(String value) {
    return (value == null) ? "" : value.trim();
  }

  private void postMessage(String message) {
    messageEvent.postValue(new UiMessage(++messageCounter, message));
  }

  private boolean hasDisplayableFacts(BreedInfo breedInfo) {
    if (breedInfo == null) {
      return false;
    }
    return hasValue(breedInfo.getName())
        || hasValue(breedInfo.getBreedGroup())
        || hasValue(breedInfo.getBredFor())
        || hasValue(breedInfo.getLifeSpan())
        || hasValue(breedInfo.getTemperament())
        || hasValue(breedInfo.getOrigin())
        || hasValue(breedInfo.getWeightMetric())
        || hasValue(breedInfo.getWeightImperial())
        || hasValue(breedInfo.getHeightMetric())
        || hasValue(breedInfo.getHeightImperial());
  }

  private boolean hasValue(String value) {
    return value != null && !value.isBlank();
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
    public final FactsState factsState;
    public final NotesMode notesMode;
    public final String savedNote;
    public final String noteDraft;
    public final boolean noteSaving;
    public final boolean favoriteSaving;
    public final String selectedBreedLabel;
    public final Integer selectedConfidencePercent;

    UiState(
        long scanId,
        Scan scan,
        @NonNull ContentTab selectedTab,
        @NonNull FactsState factsState,
        @NonNull NotesMode notesMode,
        @NonNull String savedNote,
        @NonNull String noteDraft,
        boolean noteSaving,
        boolean favoriteSaving,
        String selectedBreedLabel,
        Integer selectedConfidencePercent
    ) {
      this.scanId = scanId;
      this.scan = scan;
      this.selectedTab = selectedTab;
      this.factsState = factsState;
      this.notesMode = notesMode;
      this.savedNote = savedNote;
      this.noteDraft = noteDraft;
      this.noteSaving = noteSaving;
      this.favoriteSaving = favoriteSaving;
      this.selectedBreedLabel = selectedBreedLabel;
      this.selectedConfidencePercent = selectedConfidencePercent;
    }
  }

  public static class FactsState {

    public final FactsStatus status;
    public final BreedInfo breedInfo;
    public final String errorMessage;

    FactsState(@NonNull FactsStatus status, BreedInfo breedInfo, String errorMessage) {
      this.status = status;
      this.breedInfo = breedInfo;
      this.errorMessage = errorMessage;
    }
  }

  public static class UiMessage {

    public final long id;
    public final String message;

    UiMessage(long id, @NonNull String message) {
      this.id = id;
      this.message = message;
    }
  }

}
