package edu.cnm.deepdive.doggoneit.service.repository;

import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.doggoneit.model.entity.BreedInfo;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BreedInfoRepository {

  LiveData<BreedInfo> getById(long breedInfoId);

  LiveData<BreedInfo> getByDogApiBreedId(long dogApiBreedId);

  LiveData<List<BreedInfo>> getByNameFragment(String nameFragment);

  LiveData<List<BreedInfo>> getAll();

  CompletableFuture<BreedInfo> save(BreedInfo breedInfo);

  CompletableFuture<Integer> update(BreedInfo breedInfo);

}
