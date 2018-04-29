package com.brainyapps.simplyfree.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        setNavbar("Sign Up", true)

        this.but_next.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.but_next -> {
                onButSignup()
            }
        }
    }

    private fun onButSignup() {
        val strEmail = edit_email.text.toString()
        val strPassword = edit_password.text.toString()
        val strCPassword = edit_cpassword.text.toString()

        //
        // check if input is valid
        //

        // email
        if (!Utils.isValidEmail(strEmail)) {
            Utils.createErrorAlertDialog(this, "Invalid Email", "Please input valid email address").show()
            return
        }

        // password
        if (TextUtils.isEmpty(strPassword)) {
            Utils.createErrorAlertDialog(this, "Input Password", "Password cannot be empty").show()
            return
        }
        if (strPassword.length < 6) {
            Utils.createErrorAlertDialog(this, "Invalid Password", "Password should be at least 6 characters").show()
            return
        }
        if (!strPassword.equals(strCPassword)) {
            Utils.createErrorAlertDialog(this, "Password Mismatch", "Please confirm your password").show()
            return
        }

        // check existence
        checkUsedEmail(strEmail)
    }

    /**
     * check if email has been used
     */
    private fun checkUsedEmail(email: String) {
        val database = FirebaseDatabase.getInstance().reference
        val query = database.child(User.TABLE_NAME)

        Toast.makeText(this, "Checking if email address exists...", Toast.LENGTH_SHORT).show()

        // Read from the database
        query.orderByChild(User.FIELD_EMAIL)
                .equalTo(email)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            gotoNext()
                            return
                        }

                        Utils.createErrorAlertDialog(this@SignupActivity, "Invalid Email", "Email address is existing").show()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
    }

    /**
     * go to next page
     */
    private fun gotoNext() {
        // go to signup profile page
        val intent = Intent(this, SignupProfileActivity::class.java)
        intent.putExtra(SignupProfileActivity.KEY_EMAIL, edit_email.text.toString())
        intent.putExtra(SignupProfileActivity.KEY_PASSWORD, edit_password.text.toString())
        startActivity(intent)
    }
}
