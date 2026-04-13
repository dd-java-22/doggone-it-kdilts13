package edu.cnm.deepdive.doggoneit.service.repository;

import android.util.Base64;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import edu.cnm.deepdive.doggoneit.model.dao.UserProfileDao;
import edu.cnm.deepdive.doggoneit.model.entity.UserProfile;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.json.JSONObject;

@Singleton
/**
 * In-memory session manager backed by {@link UserProfileDao} for profile lookup/creation.
 */
public class UserSessionRepositoryImpl implements UserSessionRepository {

  private final UserProfileDao userProfileDao;
  private final MutableLiveData<UserProfile> currentUser;
  private final AtomicReference<UserProfile> currentUserRef;

  @Inject
  UserSessionRepositoryImpl(UserProfileDao userProfileDao) {
    this.userProfileDao = userProfileDao;
    this.currentUser = new MutableLiveData<>();
    this.currentUserRef = new AtomicReference<>();
  }

  @Override
  public CompletableFuture<UserProfile> ensureSignedIn(GoogleIdTokenCredential credential) {
    return CompletableFuture.supplyAsync(() -> {
      if (credential == null) {
        throw new IllegalStateException("Missing Google credential.");
      }
      String idToken = credential.getIdToken();
      if (idToken == null || idToken.isBlank()) {
        throw new IllegalStateException("Signed-in user token is unavailable.");
      }
      JSONObject claims = parseClaims(idToken);
      String email = getClaimString(claims, "email");
      if (email == null || email.isBlank()) {
        throw new IllegalStateException("Signed-in user email is unavailable.");
      }
      String displayName = credential.getDisplayName();
      if (displayName == null || displayName.isBlank()) {
        String givenName = credential.getGivenName();
        String familyName = credential.getFamilyName();
        if ((givenName == null || givenName.isBlank())
            && (familyName == null || familyName.isBlank())) {
          givenName = getClaimString(claims, "given_name");
          familyName = getClaimString(claims, "family_name");
        }
        displayName = buildDisplayName(givenName, familyName);
      }
      if (displayName == null || displayName.isBlank()) {
        displayName = getClaimString(claims, "name");
      }
      if (displayName == null || displayName.isBlank()) {
        throw new IllegalStateException("Signed-in user name is unavailable.");
      }
      UserProfile profile = userProfileDao.findByEmailSync(email);
      if (profile == null) {
        UserProfile created = new UserProfile();
        created.setEmail(email);
        created.setName(displayName);
        long id = userProfileDao.insertOrIgnore(created);
        if (id > 0) {
          created.setId(id);
          profile = created;
        } else {
          profile = userProfileDao.findByEmailSync(email);
        }
      }
      if (profile == null || profile.getId() <= 0) {
        throw new IllegalStateException("Unable to create local user profile.");
      }
      currentUserRef.set(profile);
      currentUser.postValue(profile);
      return profile;
    });
  }

  @Override
  public LiveData<UserProfile> getCurrentUser() {
    return currentUser;
  }

  @Override
  public long getCurrentUserId() {
    UserProfile profile = currentUserRef.get();
    return (profile != null) ? profile.getId() : 0;
  }

  @Override
  public void clearCurrentUser() {
    currentUserRef.set(null);
    currentUser.postValue(null);
  }

  private String buildDisplayName(String givenName, String familyName) {
    String given = (givenName != null) ? givenName.trim() : "";
    String family = (familyName != null) ? familyName.trim() : "";
    if (!given.isBlank() && !family.isBlank()) {
      return given + " " + family;
    }
    if (!given.isBlank()) {
      return given;
    }
    if (!family.isBlank()) {
      return family;
    }
    return null;
  }

  private JSONObject parseClaims(String idToken) {
    String[] parts = idToken.split("\\.");
    if (parts.length < 2) {
      throw new IllegalStateException("Signed-in user token is invalid.");
    }
    String payload = new String(Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_WRAP));
    try {
      return new JSONObject(payload);
    } catch (Exception e) {
      throw new IllegalStateException("Signed-in user token is invalid.", e);
    }
  }

  private String getClaimString(JSONObject claims, String claim) {
    if (claims.has(claim)) {
      String value = claims.optString(claim, null);
      return (value == null || value.isBlank()) ? null : value;
    }
    return null;
  }
}
