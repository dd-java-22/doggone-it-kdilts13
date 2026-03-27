package edu.cnm.deepdive.doggoneit.service.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import edu.cnm.deepdive.doggoneit.model.dao.UserProfileDao;
import edu.cnm.deepdive.doggoneit.model.entity.UserProfile;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserProfileRepositoryImpl implements UserProfileRepository {

  private final UserProfileDao userProfileDao;

  @Inject
  public UserProfileRepositoryImpl(UserProfileDao userProfileDao) {
    this.userProfileDao = userProfileDao;
  }

  @Override
  public LiveData<UserProfile> getById(long userProfileId) {
    return wrapLiveData(() -> userProfileDao.findById(userProfileId));
  }

  @Override
  public LiveData<UserProfile> getByEmail(String email) {
    return wrapLiveData(() -> userProfileDao.findByEmail(email));
  }

  @Override
  public LiveData<List<UserProfile>> getAll() {
    return wrapLiveData(userProfileDao::findAll);
  }

  @Override
  public CompletableFuture<UserProfile> save(UserProfile userProfile) {
    return CompletableFuture.supplyAsync(() -> {
      long id = userProfileDao.insert(userProfile);
      userProfile.setId(id);
      return userProfile;
    });
  }

  @Override
  public CompletableFuture<Integer> update(UserProfile userProfile) {
    return CompletableFuture.supplyAsync(() -> userProfileDao.update(userProfile));
  }

  private <T> LiveData<T> wrapLiveData(Supplier<T> supplier) {
    MutableLiveData<T> liveData = new MutableLiveData<>();
    CompletableFuture.supplyAsync(supplier).thenAccept(liveData::postValue);
    return liveData;
  }

}
