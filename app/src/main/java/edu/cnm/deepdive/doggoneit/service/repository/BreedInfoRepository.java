package edu.cnm.deepdive.doggoneit.service.repository;

import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.doggoneit.model.entity.BreedInfo;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Repository abstraction for app-level breed facts lookup and persistence.
 */
public interface BreedInfoRepository {

  /**
   * Observes breed details by local id.
   *
   * @param breedInfoId Local breed-info id.
   * @return Live breed-info stream.
   */
  LiveData<BreedInfo> getById(long breedInfoId);

  LiveData<BreedInfo> getByDogApiBreedId(long dogApiBreedId);

  CompletableFuture<BreedInfo> getByDogApiBreedIdNow(long dogApiBreedId);

  LiveData<List<BreedInfo>> getByNameFragment(String nameFragment);

  LiveData<List<BreedInfo>> getAll();

  CompletableFuture<BreedInfo> save(BreedInfo breedInfo);

  CompletableFuture<Integer> update(BreedInfo breedInfo);

  /**
   * Fetches or creates a breed-info row for the provided entity key.
   *
   * @param breedInfo Breed entity to save or update.
   * @return Future completing with persisted entity.
   */
  CompletableFuture<BreedInfo> saveOrUpdate(BreedInfo breedInfo);

}
