package edu.cnm.deepdive.doggoneit.service.repository

import android.app.Activity
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.util.concurrent.CompletableFuture

interface GoogleAuthRepository {

    fun SignInQuickly(activity: Activity): CompletableFuture<GoogleIdTokenCredential>

    fun SignIn(activity: Activity): CompletableFuture<GoogleIdTokenCredential>

    fun refreshToken(
        activity: Activity,
        credential: GoogleIdTokenCredential
    ): CompletableFuture<GoogleIdTokenCredential>

    fun SignOut(): CompletableFuture<Void?>

    class SignInRequiredException(message: String, cause: Throwable) :
        RuntimeException(message, cause)

}