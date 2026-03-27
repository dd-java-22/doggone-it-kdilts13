package edu.cnm.deepdive.doggoneit.service.repository;

import androidx.lifecycle.LiveData;
import edu.cnm.deepdive.doggoneit.model.entity.UserProfile;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserProfileRepository {

  LiveData<UserProfile> getById(long userProfileId);

  LiveData<UserProfile> getByEmail(String email);

  LiveData<List<UserProfile>> getAll();

  CompletableFuture<UserProfile> save(UserProfile userProfile);

  CompletableFuture<Integer> update(UserProfile userProfile);

}
