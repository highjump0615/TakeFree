package com.brainyapps.simplyfree.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.FirebaseManager
import com.brainyapps.simplyfree.utils.Utils
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
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

    private var butFbLogin: LoginButton? = null
    private var mFbCallbackManager: CallbackManager? = null

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

    /**
     * initialize facebook button
     */
    protected fun initFbButton() {
        //
        // Initialize Facebook Login button
        //
        butFbLogin = findViewById<LoginButton>(R.id.but_fb_login)
        mFbCallbackManager = CallbackManager.Factory.create()
        butFbLogin?.setReadPermissions("email", "public_profile");
        butFbLogin?.registerCallback(mFbCallbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                Log.d(TAG, "facebook:onSuccess: $result")
                handleFacebookAccessToken(result?.accessToken!!);
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException?) {
                Log.d(TAG, "facebook:onError", error)
            }

        })
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

        // Pass the activity result back to the Facebook SDK
        mFbCallbackManager!!.onActivityResult(requestCode, resultCode, data);
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

    /**
     * facebook log in
     */
    fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        Utils.createProgressDialog(this, "Loggin in...", "Submitting user credentials")

        val credential = FacebookAuthProvider.getCredential(token.getToken());
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

                    fetchUserInfo(task.result.user)
                })
    }

    protected fun fetchUserInfo(userInfo: FirebaseUser? = null, onFailed:() -> Unit = {}, onCompleted:() -> Unit = {}) {
        val userId = FirebaseManager.mAuth.currentUser!!.uid

        User.readFromDatabase(userId, object: User.FetchDatabaseListener {
            override fun onFetchedNotifications() {
            }

            override fun onFetchedItems() {
            }

            override fun onFetchedUser(u: User?, success: Boolean) {
                User.currentUser = u

                if (!success) {
                    signOutClear()

                    onFailed()
                }
                else {
                    if (User.currentUser == null) {
                        // get u info, from facebook account info
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

                        // social login, go to u type page
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

    /**
     * google signup button
     */
    protected fun onButGPlus() {
        this.loginType = LoginBaseActivity.LOGIN_TYPE_GOOGLE
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(this.googleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /**
     * facebook button
     */
    protected fun onButFacebook() {
        this.loginType = LoginBaseActivity.LOGIN_TYPE_FACEBOOK
        butFbLogin?.performClick()
    }

    /**
     * GoogleApiClient.OnConnectionFailedListener
     */
    override fun onConnectionFailed(p0: ConnectionResult) {
    }
}