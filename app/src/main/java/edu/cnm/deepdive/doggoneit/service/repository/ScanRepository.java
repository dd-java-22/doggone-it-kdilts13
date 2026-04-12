package edu.cnm.deepdive.doggoneit.service.repository;

import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.doggoneit.model.entity.BreedPrediction;
import edu.cnm.deepdive.doggoneit.model.entity.Scan;
import edu.cnm.deepdive.doggoneit.model.entity.ScanWithPredictions;
import edu.cnm.deepdive.doggoneit.viewmodel.ScanGalleryItem;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ScanRepository {

  LiveData<Scan> getById(long scanId);

  LiveData<ScanWithPredictions> getWithPredictionsById(long scanId);

  LiveData<List<Scan>> getByUserProfileId(long userProfileId);

  LiveData<List<ScanGalleryItem>> getGalleryItemsByUserProfileId(long userProfileId);

  LiveData<List<Scan>> getFavoritesByUserProfileId(long userProfileId);

  LiveData<List<BreedPrediction>> getPredictionsByScanId(long scanId);

  CompletableFuture<Scan> save(Scan scan);

  CompletableFuture<ScanWithPredictions> saveWithPredictions(
      Scan scan,
      List<BreedPrediction> predictions,
      String selectedBreedLabel,
      Double selectedBreedConfidence
  );

  CompletableFuture<Integer> update(Scan scan);

  CompletableFuture<Integer> delete(Scan scan);

}
