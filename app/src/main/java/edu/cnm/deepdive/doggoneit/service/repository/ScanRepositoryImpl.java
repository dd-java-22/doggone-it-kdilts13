package edu.cnm.deepdive.doggoneit.service.repository;

import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.doggoneit.model.dao.BreedPredictionDao;
import edu.cnm.deepdive.doggoneit.model.dao.ScanDao;
import edu.cnm.deepdive.doggoneit.model.entity.BreedPrediction;
import edu.cnm.deepdive.doggoneit.model.entity.Scan;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ScanRepositoryImpl implements ScanRepository {

  private final ScanDao scanDao;
  private final BreedPredictionDao breedPredictionDao;

  @Inject
  public ScanRepositoryImpl(ScanDao scanDao, BreedPredictionDao breedPredictionDao) {
    this.scanDao = scanDao;
    this.breedPredictionDao = breedPredictionDao;
  }

  @Override
  public LiveData<Scan> getById(long scanId) {
    return scanDao.findById(scanId);
  }

  @Override
  public LiveData<List<Scan>> getByUserProfileId(long userProfileId) {
    return scanDao.findByUserProfileId(userProfileId);
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
  public CompletableFuture<Integer> update(Scan scan) {
    return CompletableFuture.supplyAsync(() -> scanDao.update(scan));
  }

  @Override
  public CompletableFuture<Integer> delete(Scan scan) {
    return CompletableFuture.supplyAsync(() -> scanDao.delete(scan));
  }
}
