package edu.cnm.deepdive.doggoneit.service.repository;

import androidx.lifecycle.LiveData;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import edu.cnm.deepdive.doggoneit.model.entity.UserProfile;
import java.util.concurrent.CompletableFuture;

public interface UserSessionRepository {

  CompletableFuture<UserProfile> ensureSignedIn(GoogleIdTokenCredential credential);

  LiveData<UserProfile> getCurrentUser();

  long getCurrentUserId();

  void clearCurrentUser();
}
