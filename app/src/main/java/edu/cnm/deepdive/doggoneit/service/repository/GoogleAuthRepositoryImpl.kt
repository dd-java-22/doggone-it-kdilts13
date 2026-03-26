package edu.cnm.deepdive.doggoneit.service.repository

import android.app.Activity
import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.cnm.deepdive.doggoneit.R
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.future.future
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

@Singleton
class GoogleAuthRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
) : GoogleAuthRepository {

    private val credentialManager = CredentialManager.create(context)
    private val clientId = context.getString(R.string.client_id)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun SignInQuickly(activity: Activity): CompletableFuture<GoogleIdTokenCredential> =
        scope.future {
            attemptSignIn(activity, true, false)
        }

    override fun SignIn(activity: Activity): CompletableFuture<GoogleIdTokenCredential> =
        scope.future {
            attemptSignIn(activity, false, false)
        }

    override fun refreshToken(
        activity: Activity,
        credential: GoogleIdTokenCredential
    ): CompletableFuture<GoogleIdTokenCredential> =
        if (!isTokenExpired(credential.idToken)) {
            CompletableFuture.completedFuture(credential)
        } else {
            scope.future {
                attemptSignIn(activity, true, true)
            }
        }

    override fun SignOut(): CompletableFuture<Void?> =
        scope.future {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            null
        }

    private suspend fun attemptSignIn(
        activity: Activity,
        filter: Boolean,
        autoSelect: Boolean
    ): GoogleIdTokenCredential {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filter)
            .setAutoSelectEnabled(autoSelect)
            .setServerClientId(clientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(activity, request)

            if (result.credential is CustomCredential && result.credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val credential = GoogleIdTokenCredential.createFrom(result.credential.data)

                Log.d(TAG, "Token = " + credential.idToken) // FIXME: remove this after no longer essential

                credential
            } else {
                throw IllegalStateException("Credential is not a Google ID credential")
            }
        } catch (e: Exception) {
            throw GoogleAuthRepository.SignInRequiredException("Sign-in Required", e)
        }
    }

    private fun isTokenExpired(idToken: String): Boolean {
        return try {
            val parts = idToken.split(".")

            if (parts.size < 2) {
                true
            } else {
                val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP))

                val expiration = JSONObject(payload).getLong("exp")

                expiration < System.currentTimeMillis() / 1000 + 300
            }
        } catch (e: Exception) {
            true
        }
    }

    companion object {

        private val TAG = GoogleAuthRepositoryImpl::class.java.simpleName
    }
}