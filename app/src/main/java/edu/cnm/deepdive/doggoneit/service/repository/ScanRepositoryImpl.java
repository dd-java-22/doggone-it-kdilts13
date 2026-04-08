package edu.cnm.deepdive.doggoneit.service.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import edu.cnm.deepdive.doggoneit.model.dao.BreedPredictionDao;
import edu.cnm.deepdive.doggoneit.model.dao.ScanDao;
import edu.cnm.deepdive.doggoneit.model.entity.BreedPrediction;
import edu.cnm.deepdive.doggoneit.model.entity.Scan;
import edu.cnm.deepdive.doggoneit.model.entity.ScanWithPredictions;
import edu.cnm.deepdive.doggoneit.viewmodel.ScanGalleryItem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ScanRepositoryImpl implements ScanRepository {

  private final ScanDao scanDao;
  private final BreedPredictionDao breedPredictionDao;

  @Inject
  ScanRepositoryImpl(ScanDao scanDao, BreedPredictionDao breedPredictionDao) {
    this.scanDao = scanDao;
    this.breedPredictionDao = breedPredictionDao;
  }

  @Override
  public LiveData<Scan> getById(long scanId) {
    return scanDao.findById(scanId);
  }

  @Override
  public LiveData<ScanWithPredictions> getWithPredictionsById(long scanId) {
    return Transformations.map(
        scanDao.findWithPredictionsById(scanId),
        this::sortPredictions
    );
  }

  @Override
  public LiveData<List<Scan>> getByUserProfileId(long userProfileId) {
    return scanDao.findByUserProfileId(userProfileId);
  }

  @Override
  public LiveData<List<ScanGalleryItem>> getGalleryItemsByUserProfileId(long userProfileId) {
    return scanDao.findGalleryItemsByUserProfileId(userProfileId);
  }

  @Override
  public LiveData<List<Scan>> getFavoritesByUserProfileId(long userProfileId) {
    return scanDao.findFavoritesByUserProfileId(userProfileId);
  }

  @Override
  public LiveData<List<BreedPrediction>> getPredictionsByScanId(long scanId) {
    return breedPredictionDao.findByScanId(scanId);
  }

  @Override
  public CompletableFuture<Scan> save(Scan scan) {
    return CompletableFuture.supplyAsync(() -> {
      long id = scanDao.insert(scan);
      scan.setId(id);
      return scan;
    });
  }

  @Override
  public CompletableFuture<ScanWithPredictions> saveWithPredictions(Scan scan,
      List<BreedPrediction> predictions) {
    return CompletableFuture.supplyAsync(() -> {
      requireUserProfile(scan);
      List<BreedPrediction> prepared = prepareTopPredictions(predictions);
      long id = scanDao.insertWithPredictions(scan, prepared);
      scan.setId(id);
      ScanWithPredictions result = new ScanWithPredictions();
      result.setScan(scan);
      result.setPredictions(prepared);
      return result;
    });
  }

  @Override
  public CompletableFuture<Integer> update(Scan scan) {
    return CompletableFuture.supplyAsync(() -> scanDao.update(scan));
  }

  @Override
  public CompletableFuture<Integer> delete(Scan scan) {
    return CompletableFuture.supplyAsync(() -> scanDao.delete(scan));
  }

  private ScanWithPredictions sortPredictions(ScanWithPredictions scanWithPredictions) {
    if (scanWithPredictions == null || scanWithPredictions.getPredictions() == null) {
      return scanWithPredictions;
    }
    List<BreedPrediction> sorted = new ArrayList<>(scanWithPredictions.getPredictions());
    sorted.sort(Comparator
        .comparingInt(BreedPrediction::getRank)
        .thenComparing(Comparator.comparingDouble(BreedPrediction::getProbability).reversed()));
    scanWithPredictions.setPredictions(sorted);
    return scanWithPredictions;
  }

  private List<BreedPrediction> prepareTopPredictions(List<BreedPrediction> predictions) {
    List<BreedPrediction> prepared = new ArrayList<>();
    if (predictions != null && !predictions.isEmpty()) {
      prepared.addAll(predictions);
      prepared.sort(Comparator.comparingDouble(BreedPrediction::getProbability).reversed());
      if (prepared.size() > 5) {
        prepared = new ArrayList<>(prepared.subList(0, 5));
      }
      for (int i = 0; i < prepared.size(); i++) {
        prepared.get(i).setRank(i);
      }
    }
    return prepared;
  }

  private void requireUserProfile(Scan scan) {
    if (scan == null || scan.getUserProfileId() <= 0) {
      throw new IllegalStateException("Scan save requires a valid user profile id.");
    }
  }
}
