package edu.cnm.deepdive.doggoneit.service.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import edu.cnm.deepdive.doggoneit.model.dao.BreedFactDao;
import edu.cnm.deepdive.doggoneit.model.entity.BreedFact;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BreedFactRepositoryImpl implements BreedFactRepository {

  private final BreedFactDao breedFactDao;

  @Inject
  public BreedFactRepositoryImpl(BreedFactDao breedFactDao) {
    this.breedFactDao = breedFactDao;
  }

  @Override
  public LiveData<BreedFact> getById(long breedFactId) {
    return wrapLiveData(() -> breedFactDao.findById(breedFactId));
  }

  @Override
  public LiveData<BreedFact> getByDogFactsApiId(long dogFactsApiId) {
    return wrapLiveData(() -> breedFactDao.findByDogFactsApiId(dogFactsApiId));
  }

  @Override
  public LiveData<List<BreedFact>> getByNameFragment(String nameFragment) {
    return wrapLiveData(() -> breedFactDao.findByNameFragment(nameFragment));
  }

  @Override
  public LiveData<List<BreedFact>> getAll() {
    return wrapLiveData(breedFactDao::findAll);
  }

  @Override
  public CompletableFuture<BreedFact> save(BreedFact breedFact) {
    return CompletableFuture.supplyAsync(() -> {
      long id = breedFactDao.insert(breedFact);
      breedFact.setId(id);
      return breedFact;
    });
  }

  @Override
  public CompletableFuture<Integer> update(BreedFact breedFact) {
    return CompletableFuture.supplyAsync(() -> breedFactDao.update(breedFact));
  }

  private <T> LiveData<T> wrapLiveData(Supplier<T> supplier) {
    MutableLiveData<T> liveData = new MutableLiveData<>();
    CompletableFuture.supplyAsync(supplier).thenAccept(liveData::postValue);
    return liveData;
  }

}
