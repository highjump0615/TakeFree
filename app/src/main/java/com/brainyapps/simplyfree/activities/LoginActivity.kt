package com.brainyapps.simplyfree.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.FirebaseManager
import com.brainyapps.simplyfree.utils.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : LoginBaseActivity(), View.OnClickListener {

    private val TAG = LoginActivity::class.java.getSimpleName()
    var progressDlg: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setNavbar("Sign In", true)

        this.but_forget.setOnClickListener(this)
        this.but_signin.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            // Log in button
            R.id.but_signin -> {
                loginType = LoginBaseActivity.LOGIN_TYPE_EMAIL
                onButSignin()
            }
            // Forget password button
            R.id.but_forget -> {
                Utils.moveNextActivity(this, ForgetActivity::class.java)
            }
        }
    }

    /**
     * Log in
     */
    private fun onButSignin() {
        val strEmail = edit_email.text.toString()
        val strPassword = edit_password.text.toString()

        //
        // check if input is valid
        //

        // email
        if (!Utils.isValidEmail(strEmail)) {
            Utils.createErrorAlertDialog(this, "Invalid Email", "Please input valid email address").show()
            edit_email.requestFocus()
            return
        }

        // password
        if (TextUtils.isEmpty(strPassword)) {
            Utils.createErrorAlertDialog(this, "Input Password", "Password cannot be empty").show()
            edit_password.requestFocus()
            return
        }

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        if (progressDlg != null) {
            // already processing, exit
            return
        }

        progressDlg = Utils.createProgressDialog(this, "Logging in...", "Submitting user credentials")
        but_signin.isEnabled = false

        FirebaseManager.mAuth.signInWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    if (!task.isSuccessful) {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Utils.createErrorAlertDialog(this, "Authentication failed.", task.exception?.localizedMessage!!).show()

                        closeProgressDialog()
                        this.but_signin.isEnabled = true

                        return@OnCompleteListener
                    }

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")

                    val onFetchFailed: () -> Unit = {
                        this@LoginActivity.but_signin.isEnabled = true
                    }
                    val onFetchCompleted: () -> Unit = {
                        closeProgressDialog()
                    }

                    fetchUserInfo(null, onFetchFailed, onFetchCompleted)
                })
    }

    private fun closeProgressDialog() {
        Utils.closeProgressDialog()
        progressDlg = null
    }
}
