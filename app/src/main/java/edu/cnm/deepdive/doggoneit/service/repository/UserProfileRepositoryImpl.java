package edu.cnm.deepdive.doggoneit.service.repository;

import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.doggoneit.model.dao.UserProfileDao;
import edu.cnm.deepdive.doggoneit.model.entity.UserProfile;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
/**
 * Room-backed implementation of {@link UserProfileRepository}.
 */
public class UserProfileRepositoryImpl implements UserProfileRepository {

  private final UserProfileDao userProfileDao;

  @Inject
  UserProfileRepositoryImpl(UserProfileDao userProfileDao) {
    this.userProfileDao = userProfileDao;
  }

  @Override
  public LiveData<UserProfile> getById(long userProfileId) {
    return userProfileDao.findById(userProfileId);
  }

  @Override
  public LiveData<UserProfile> getByEmail(String email) {
    return userProfileDao.findByEmail(email);
  }

  @Override
  public LiveData<List<UserProfile>> getAll() {
    return userProfileDao.findAll();
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
}
