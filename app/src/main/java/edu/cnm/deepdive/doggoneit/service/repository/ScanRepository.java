package edu.cnm.deepdive.doggoneit.service.repository;

import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.doggoneit.model.entity.BreedPrediction;
import edu.cnm.deepdive.doggoneit.model.entity.Scan;
import edu.cnm.deepdive.doggoneit.model.entity.ScanWithPredictions;
import edu.cnm.deepdive.doggoneit.viewmodel.ScanGalleryItem;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Repository abstraction for saved scans, predictions, and gallery projections.
 */
public interface ScanRepository {

  LiveData<Scan> getById(long scanId);

  LiveData<ScanWithPredictions> getWithPredictionsById(long scanId);

  LiveData<List<Scan>> getByUserProfileId(long userProfileId);

  LiveData<List<ScanGalleryItem>> getGalleryItemsByUserProfileId(long userProfileId);

  LiveData<List<Scan>> getFavoritesByUserProfileId(long userProfileId);

  LiveData<List<BreedPrediction>> getPredictionsByScanId(long scanId);

  CompletableFuture<Scan> save(Scan scan);

  /**
   * Saves a scan and top predictions with optional user-selected breed override.
   *
   * @param scan Scan metadata to persist.
   * @param predictions Prediction rows to persist with the scan.
   * @param selectedBreedLabel User-selected breed label, if any.
   * @param selectedBreedConfidence User-selected confidence score, if any.
   * @return Future with combined scan/predictions result.
   */
  CompletableFuture<ScanWithPredictions> saveWithPredictions(
      Scan scan,
      List<BreedPrediction> predictions,
      String selectedBreedLabel,
      Double selectedBreedConfidence
  );

  CompletableFuture<Integer> update(Scan scan);

  CompletableFuture<Integer> delete(Scan scan);

}
