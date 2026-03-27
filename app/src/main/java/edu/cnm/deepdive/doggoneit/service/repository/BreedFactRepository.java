package edu.cnm.deepdive.doggoneit.service.repository;

import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.doggoneit.model.entity.BreedFact;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BreedFactRepository {

  LiveData<BreedFact> getById(long breedFactId);

  LiveData<BreedFact> getByDogFactsApiId(long dogFactsApiId);

  LiveData<List<BreedFact>> getByNameFragment(String nameFragment);

  LiveData<List<BreedFact>> getAll();

  CompletableFuture<BreedFact> save(BreedFact breedFact);

  CompletableFuture<Integer> update(BreedFact breedFact);

}
