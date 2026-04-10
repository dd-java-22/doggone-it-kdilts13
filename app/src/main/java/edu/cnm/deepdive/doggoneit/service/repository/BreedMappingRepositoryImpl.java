package edu.cnm.deepdive.doggoneit.service.repository;

import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.doggoneit.model.dao.BreedMappingDao;
import edu.cnm.deepdive.doggoneit.model.entity.BreedMapping;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BreedMappingRepositoryImpl implements BreedMappingRepository {

  private final BreedMappingDao breedMappingDao;

  @Inject
  BreedMappingRepositoryImpl(BreedMappingDao breedMappingDao) {
    this.breedMappingDao = breedMappingDao;
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
}
