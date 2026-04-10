package edu.cnm.deepdive.doggoneit.service.repository;

import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.doggoneit.model.dao.BreedInfoDao;
import edu.cnm.deepdive.doggoneit.model.entity.BreedInfo;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BreedInfoRepositoryImpl implements BreedInfoRepository {

  private final BreedInfoDao breedInfoDao;

  @Inject
  BreedInfoRepositoryImpl(BreedInfoDao breedInfoDao) {
    this.breedInfoDao = breedInfoDao;
  }

  @Override
  public LiveData<BreedInfo> getById(long breedInfoId) {
    return breedInfoDao.findById(breedInfoId);
  }

  @Override
  public LiveData<BreedInfo> getByDogApiBreedId(long dogApiBreedId) {
    return breedInfoDao.findByDogApiBreedId(dogApiBreedId);
  }

  @Override
  public LiveData<List<BreedInfo>> getByNameFragment(String nameFragment) {
    return breedInfoDao.findByNameFragment(nameFragment);
  }

  @Override
  public LiveData<List<BreedInfo>> getAll() {
    return breedInfoDao.findAll();
  }

  @Override
  public CompletableFuture<BreedInfo> save(BreedInfo breedInfo) {
    return CompletableFuture.supplyAsync(() -> {
      long id = breedInfoDao.insert(breedInfo);
      breedInfo.setId(id);
      return breedInfo;
    });
  }

  @Override
  public CompletableFuture<Integer> update(BreedInfo breedInfo) {
    return CompletableFuture.supplyAsync(() -> breedInfoDao.update(breedInfo));
  }
}
