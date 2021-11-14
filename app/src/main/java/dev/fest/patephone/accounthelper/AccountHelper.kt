package dev.fest.patephone.accounthelper

import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import dev.fest.patephone.activity.MainActivity
import dev.fest.patephone.R
import dev.fest.patephone.constants.FirebaseAuthConst
import java.lang.Exception


class AccountHelper(activity: MainActivity)  {

    private val activityAccountHelper = activity
    private lateinit var signInClient: GoogleSignInClient

    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            activityAccountHelper.mAuth.currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    activityAccountHelper.mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                signUpWithEmailSuccessful(task.result.user!!)
                            } else {
                                signUpWithEmailExceptions(task.exception!!, email, password)
                            }
                        }
                }
            }

        }
    }

    private fun signUpWithEmailSuccessful(user: FirebaseUser) {
        sendEmailVerification(user)
        activityAccountHelper.uiUpdate(user)
    }

    private fun signUpWithEmailExceptions(e: Exception, email: String, password: String) {
        if (e is FirebaseAuthUserCollisionException) {
            if (e.errorCode == FirebaseAuthConst.ERROR_EMAIL_ALREADY_IN_USE) {
                linkEmailToGoogle(email, password)
            }
        } else if (e is FirebaseAuthInvalidCredentialsException) {
            if (e.errorCode == FirebaseAuthConst.ERROR_INVALID_EMAIL) {
                Toast.makeText(
                    activityAccountHelper,
                    FirebaseAuthConst.ERROR_INVALID_EMAIL,
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
        if (e is FirebaseAuthWeakPasswordException) {
            if (e.errorCode == FirebaseAuthConst.ERROR_WEAK_PASSWORD) {
                Toast.makeText(
                    activityAccountHelper,
                    FirebaseAuthConst.ERROR_WEAK_PASSWORD,
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            activityAccountHelper.mAuth.currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    activityAccountHelper.mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    activityAccountHelper,
                                    "Sign in done",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                activityAccountHelper.uiUpdate(task.result?.user)
                            } else {
                                signInWithEmailExceptions(task.exception!!)
                            }
                        }
                }
            }
        }
    }

    private fun signInWithEmailExceptions(e: Exception) {
//        Log.d("MyLog", "error ${e}")
        if (e is FirebaseAuthInvalidCredentialsException) {
            if (e.errorCode == FirebaseAuthConst.ERROR_INVALID_EMAIL) {
                Toast.makeText(
                    activityAccountHelper,
                    FirebaseAuthConst.ERROR_INVALID_EMAIL,
                    Toast.LENGTH_LONG
                )
                    .show()
            } else if (e.errorCode == FirebaseAuthConst.ERROR_WRONG_PASSWORD) {
                Toast.makeText(
                    activityAccountHelper,
                    FirebaseAuthConst.ERROR_WRONG_PASSWORD,
                    Toast.LENGTH_LONG
                ).show()
            }
        } else if (e is FirebaseAuthInvalidUserException) {
//            Log.d("MyLog", "error ${e}")
            if (e.errorCode == FirebaseAuthConst.ERROR_USER_NOT_FOUND) {
                Toast.makeText(
                    activityAccountHelper,
                    FirebaseAuthConst.ERROR_USER_NOT_FOUND,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    activityAccountHelper,
                    R.string.send_verification_done,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    activityAccountHelper,
                    R.string.send_verification_error,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activityAccountHelper.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activityAccountHelper, gso)
    }

    fun signInClientWithGoogle() {
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        activityAccountHelper.googleSignInLauncher.launch(intent)
    }

    fun signOutClientWithGoogle() {
        getSignInClient().signOut()
    }

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        activityAccountHelper.mAuth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                activityAccountHelper.mAuth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(activityAccountHelper, "Sign in done", Toast.LENGTH_LONG)
                                .show()
                            activityAccountHelper.uiUpdate(task.result?.user)
                        } else {
                            Toast.makeText(
                                activityAccountHelper,
                                "Sign in false",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }

                    }
            }
        }

    }

    private fun linkEmailToGoogle(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        if (activityAccountHelper.mAuth.currentUser != null) {
            activityAccountHelper.mAuth.currentUser?.linkWithCredential(credential)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            activityAccountHelper,
                            activityAccountHelper.resources.getString(R.string.link_done),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                }
        } else {
            Toast.makeText(
                activityAccountHelper,
                R.string.entre_to_google_account,
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    fun signInAnonymously(listener: Listener) {
        activityAccountHelper.mAuth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                listener.onComplete()
                Toast.makeText(
                    activityAccountHelper,
                    R.string.entre_anonym_done,
                    Toast.LENGTH_LONG
                )
                    .show()
            } else {
                Log.d("AccountHelper", "anonym: ${task.exception}")
                Toast.makeText(
                    activityAccountHelper,
                    R.string.entre_anonym_error,
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    interface Listener {
        fun onComplete()
    }
}
