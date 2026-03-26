package edu.cnm.deepdive.doggoneit.viewmodel;

import android.app.Activity;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.doggoneit.service.repository.GoogleAuthRepository;
import java.util.function.BiConsumer;
import javax.inject.Inject;

@HiltViewModel
public class LoginViewModel extends ViewModel {

  private static final String TAG = "LoginViewModel";

  private final GoogleAuthRepository repository;
  private final MutableLiveData<GoogleIdTokenCredential> credential;
  private final MutableLiveData<Throwable> throwable;
  private final BiConsumer<GoogleIdTokenCredential, Throwable> signInConsumer;

  @Inject
  public LoginViewModel(GoogleAuthRepository repository) {
    this.repository = repository;
    this.credential = new MutableLiveData<>();
    this.throwable = new MutableLiveData<>();
    this.signInConsumer = (result, ex) -> {
      if (ex != null) {
        Log.e(TAG, "Sign in failure", ex);
        throwable.postValue(ex);
      } else {
        credential.postValue(result);
      }
    };
  }

  public LiveData<GoogleIdTokenCredential> getCredential() {
    return credential;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void signInQuickly(Activity activity) {
    throwable.setValue(null);
    repository.SignInQuickly(activity)
      .whenComplete(signInConsumer);
  }

  public void signIn(Activity activity) {
    throwable.setValue(null);
    repository.SignIn(activity)
      .whenComplete(signInConsumer);
  }

  public void refreshToken(Activity activity, GoogleIdTokenCredential credential) {
    throwable.setValue(null);
    repository.refreshToken(activity, credential)
      .whenComplete(signInConsumer);
  }

  public void signOut() {
    throwable.setValue(null);
    repository.SignOut()
      .whenComplete((result, ex) -> {
        if (ex != null) {
          Log.e(TAG, "signOut failed", ex);
          throwable.postValue(ex);
        } else {
          credential.postValue(null);
        }
      });
  }

}