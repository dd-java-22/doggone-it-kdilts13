package edu.cnm.deepdive.doggoneit.service.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.doggoneit.model.dao.BreedMappingDao;
import edu.cnm.deepdive.doggoneit.model.entity.BreedMapping;
import edu.cnm.deepdive.doggoneit.service.seed.BreedMappingSeedLoader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BreedMappingRepositoryImpl implements BreedMappingRepository {

  private static final String TAG = BreedMappingRepositoryImpl.class.getSimpleName();

  private final BreedMappingDao breedMappingDao;
  private final BreedMappingSeedLoader seedLoader;

  @Inject
  BreedMappingRepositoryImpl(BreedMappingDao breedMappingDao, BreedMappingSeedLoader seedLoader) {
    this.breedMappingDao = breedMappingDao;
    this.seedLoader = seedLoader;
  }

  @Override
  public LiveData<BreedMapping> getByModelLabel(String modelLabel) {
    return breedMappingDao.findByModelLabel(modelLabel);
  }

  @Override
  public CompletableFuture<BreedMapping> getByModelLabelNow(String modelLabel) {
    return CompletableFuture.supplyAsync(() -> breedMappingDao.findByModelLabelNow(modelLabel));
  }

  @Override
  public CompletableFuture<BreedMapping> save(BreedMapping mapping) {
    return CompletableFuture.supplyAsync(() -> {
      breedMappingDao.insert(mapping);
      return mapping;
    });
  }

  @Override
  public CompletableFuture<int[]> saveAll(BreedMapping... mappings) {
    return CompletableFuture.supplyAsync(() -> {
      List<Long> ids = breedMappingDao.insert(mappings);
      int[] results = new int[ids.size()];
      for (int i = 0; i < ids.size(); i++) {
        results[i] = (ids.get(i) > 0) ? 1 : 0;
      }
      return results;
    });
  }

  @Override
  public CompletableFuture<Integer> ensureBreedMappingsSeeded() {
    return CompletableFuture.supplyAsync(() -> {
      if (breedMappingDao.count() > 0) {
        return 0;
      }
      List<BreedMapping> mappings = seedLoader.loadMappings();
      if (mappings.isEmpty()) {
        Log.e(TAG, "Breed mapping seed failed or returned no rows.");
        return 0;
      }
      return breedMappingDao.insertAll(mappings).size();
    });
  }
}
