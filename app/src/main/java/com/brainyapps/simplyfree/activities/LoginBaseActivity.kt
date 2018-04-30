package com.brainyapps.simplyfree.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.FirebaseManager
import com.brainyapps.simplyfree.utils.Utils
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

/**
 * Created by Administrator on 4/30/18.
 */
open class LoginBaseActivity : BaseActivity(), GoogleApiClient.OnConnectionFailedListener {

    companion object {
        val KEY_LOGIN_TYPE = "loginType"

        val LOGIN_TYPE_EMAIL = 0
        val LOGIN_TYPE_FACEBOOK = 1
        val LOGIN_TYPE_GOOGLE = 2

        val KEY_PAYMENT_TYPE = "paymentType"

        val PAYMENT_TYPE_NOT_DETERMINED = -1
        val PAYMENT_TYPE_NONE = 0
        val PAYMENT_TYPE_PAY = 1
    }

    private val TAG = LoginBaseActivity ::class.java.getSimpleName()

    protected var loginType: Int = 0
    protected var paymentType: Int = 0

    private var googleApiClient: GoogleApiClient? = null
    private var RC_SIGN_IN = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        this.googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account);
            }
            catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    /**
     * google log in
     */
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id);

        Utils.createProgressDialog(this, "Logging in...", "Submitting user credentials")

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null);
        FirebaseManager.mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    if (!task.isSuccessful) {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Utils.createErrorAlertDialog(this, "Authentication failed.", task.exception?.localizedMessage!!).show()
                        Utils.closeProgressDialog()

                        return@OnCompleteListener
                    }

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val onFetchFailed: () -> Unit = {
                    }
                    val onFetchCompleted: () -> Unit = {
                        Utils.closeProgressDialog()
                    }

                    fetchUserInfo(task.result.user, onFetchFailed, onFetchCompleted)
                })
    }

    protected fun fetchUserInfo(userInfo: FirebaseUser? = null, onFailed:() -> Unit = {}, onCompleted:() -> Unit = {}) {
        val userId = FirebaseManager.mAuth.currentUser!!.uid

        User.readFromDatabase(userId, object: User.FetchDatabaseListener {
            override fun onFetchedReviews() {
            }

            override fun onFetchedUser(user: User?, success: Boolean) {
                User.currentUser = user

                if (!success) {
                    signOutClear()

                    onFailed()
                }
                else {
                    if (User.currentUser == null) {
                        // get user info, from facebook account info
                        if (userInfo != null) {
                            val newUser = User(userId)
                            if (!TextUtils.isEmpty(userInfo.displayName)) {
                                val names = userInfo.displayName!!.split(" ")
                                newUser.firstName = names[0]

                                if (names.size > 1) {
                                    newUser.lastName = names[1]
                                }
                            }

                            newUser.email = userInfo.email!!
                            newUser.photoUrl = userInfo.photoUrl.toString()

                            User.currentUser = newUser
                        }

                        // social login, go to user type page
                        var intent = Intent(this@LoginBaseActivity, SignupLandingActivity::class.java)
                        if (paymentType > PAYMENT_TYPE_NOT_DETERMINED) {
                            // already selected payment, go to profile page
                            intent = Intent(this@LoginBaseActivity, SignupProfileActivity::class.java)
                        }

                        intent.putExtra(LoginBaseActivity.KEY_LOGIN_TYPE, this@LoginBaseActivity.loginType)
                        startActivity(intent)
                    }
                    else {
                        goToMain()
                    }
                }

                onCompleted()
            }
        })
    }

    protected fun onButGPlus() {
        this.loginType = LoginBaseActivity.LOGIN_TYPE_GOOGLE
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(this.googleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /**
     * GoogleApiClient.OnConnectionFailedListener
     */
    override fun onConnectionFailed(p0: ConnectionResult) {
    }
}