package com.brainyapps.simplyfree.activities

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.utils.FirebaseManager
import com.brainyapps.simplyfree.utils.Utils
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.activity_forget.*

class ForgetActivity : BaseActivity(), View.OnClickListener {

    private val TAG = ForgetActivity::class.java.getSimpleName()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget)

        setNavbar("Reset Password", true)

        this.but_done.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.but_done -> {
                onButDone()
            }
        }
    }

    private fun onButDone() {
        val strEmail = edit_email.text.toString()

        //
        // check if input is valid
        //
        if (TextUtils.isEmpty(strEmail)) {
            Toast.makeText(this, "Please input email address", Toast.LENGTH_SHORT).show()
            return
        }
        if (!Utils.isValidEmail(strEmail)) {
            Utils.createErrorAlertDialog(this, "Invalid Email", "Please input valid email address").show()
            edit_email.requestFocus()
            return
        }

        Utils.createProgressDialog(this, "Submitting...", "Sending password reset email")

        FirebaseManager.mAuth.sendPasswordResetEmail(strEmail)
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "sendPasswordResetEmail:failure", task.exception)
                        Utils.createErrorAlertDialog(this, "Failed Sending", task.exception?.localizedMessage!!).show()
                    }
                    else {
                        Utils.createErrorAlertDialog(this,
                                "Email Sent",
                                "We’ve sent a password reset link to your email",
                                DialogInterface.OnClickListener { dialog, which ->
                                    this@ForgetActivity.finish()
                                })
                                .show()
                    }

                    Utils.closeProgressDialog()
                })

    }
}
