package edu.cnm.deepdive.doggoneit.viewmodel;

import android.app.Activity;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.doggoneit.service.repository.GoogleAuthRepository;
import edu.cnm.deepdive.doggoneit.service.repository.UserSessionRepository;
import java.util.function.BiConsumer;
import javax.inject.Inject;

/**
 * Coordinates Google sign-in flows and local session state for login/logout UI.
 */
@HiltViewModel
public class LoginViewModel extends ViewModel {

  private static final String TAG = "LoginViewModel";

  private final GoogleAuthRepository repository;
  private final UserSessionRepository sessionRepository;
  private final MutableLiveData<GoogleIdTokenCredential> credential;
  private final MutableLiveData<Throwable> throwable;
  private final BiConsumer<GoogleIdTokenCredential, Throwable> signInConsumer;

  @Inject
  public LoginViewModel(GoogleAuthRepository repository, UserSessionRepository sessionRepository) {
    this.repository = repository;
    this.sessionRepository = sessionRepository;
    this.credential = new MutableLiveData<>();
    this.throwable = new MutableLiveData<>();
    this.signInConsumer = (result, ex) -> {
      if (ex != null) {
        Log.e(TAG, "Sign in failure", ex);
        throwable.postValue(ex);
      } else {
        sessionRepository.ensureSignedIn(result)
            .whenComplete((profile, profileEx) -> {
              if (profileEx != null) {
                Log.e(TAG, "Local profile creation failure", profileEx);
                throwable.postValue(profileEx);
              } else {
                credential.postValue(result);
              }
            });
      }
    };
  }

  /**
   * @return Latest successful Google credential, or {@code null}.
   */
  public LiveData<GoogleIdTokenCredential> getCredential() {
    return credential;
  }

  /**
   * @return Latest sign-in/sign-out error, or {@code null}.
   */
  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  /**
   * Attempts one-tap sign-in without forcing account selection.
   *
   * @param activity Host activity for credential flow.
   */
  public void signInQuickly(Activity activity) {
    throwable.setValue(null);
    repository.SignInQuickly(activity)
      .whenComplete(signInConsumer);
  }

  /**
   * Starts explicit Google sign-in flow.
   *
   * @param activity Host activity for credential flow.
   */
  public void signIn(Activity activity) {
    throwable.setValue(null);
    repository.SignIn(activity)
      .whenComplete(signInConsumer);
  }

  /**
   * Refreshes credential state for an existing Google account.
   *
   * @param activity Host activity for credential flow.
   * @param credential Existing credential to refresh.
   */
  public void refreshToken(Activity activity, GoogleIdTokenCredential credential) {
    throwable.setValue(null);
    repository.refreshToken(activity, credential)
      .whenComplete(signInConsumer);
  }

  /**
   * Signs out and clears locally tracked current-user session state.
   */
  public void signOut() {
    throwable.setValue(null);
    repository.SignOut()
      .whenComplete((result, ex) -> {
        if (ex != null) {
          Log.e(TAG, "signOut failed", ex);
          throwable.postValue(ex);
        } else {
          sessionRepository.clearCurrentUser();
          credential.postValue(null);
        }
      });
  }

}
