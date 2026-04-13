package edu.cnm.deepdive.doggoneit.service.repository;

import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.doggoneit.model.entity.BreedMapping;
import java.util.concurrent.CompletableFuture;

/**
 * Repository abstraction for mapping model labels to Dog API breed ids.
 */
public interface BreedMappingRepository {

  LiveData<BreedMapping> getByModelLabel(String modelLabel);

  CompletableFuture<BreedMapping> getByModelLabelNow(String modelLabel);

  CompletableFuture<BreedMapping> save(BreedMapping mapping);

  CompletableFuture<int[]> saveAll(BreedMapping... mappings);

  /**
   * Seeds mapping data from assets when no rows exist yet.
   *
   * @return Future with inserted row count.
   */
  CompletableFuture<Integer> ensureBreedMappingsSeeded();
}
