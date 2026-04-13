package edu.cnm.deepdive.doggoneit.service.repository;

import androidx.lifecycle.LiveData;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import edu.cnm.deepdive.doggoneit.model.entity.UserProfile;
import java.util.concurrent.CompletableFuture;

/**
 * Tracks authenticated session state and current local user profile.
 */
public interface UserSessionRepository {

  /**
   * Ensures the signed-in credential has a corresponding local profile and sets it as current.
   *
   * @param credential Google credential from sign-in flow.
   * @return Future with resolved current user profile.
   */
  CompletableFuture<UserProfile> ensureSignedIn(GoogleIdTokenCredential credential);

  LiveData<UserProfile> getCurrentUser();

  long getCurrentUserId();

  /**
   * Clears in-memory current user state after sign-out.
   */
  void clearCurrentUser();
}
