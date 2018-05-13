package com.brainyapps.simplyfree.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.FirebaseManager
import com.brainyapps.simplyfree.utils.SFUpdateImageListener
import com.brainyapps.simplyfree.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_signup_profile.*

class SignupProfileActivity : BaseActivity(), View.OnClickListener, SFUpdateImageListener {

    private val TAG = SignupProfileActivity::class.java.getSimpleName()

    var helper: PhotoActivityHelper? = null

    companion object {
        const val KEY_EMAIL = "email"
        const val KEY_PASSWORD = "password"
    }

    var email: String? = null
    var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_profile)

        setNavbar("Profile Set Up", true)

        // get user info from intent
        val bundle = intent.extras
        if (bundle != null) {
            email = bundle.getString(KEY_EMAIL)
            password = bundle.getString(KEY_PASSWORD)
        }

        this.layout_photo.setOnClickListener(this)
        this.but_done.setOnClickListener(this)

        this.helper = PhotoActivityHelper(this)

        //
        // fill user info
        //
        this.edit_firstname.setText(User.currentUser?.firstName)
        this.edit_lastname.setText(User.currentUser?.lastName)

        if (!TextUtils.isEmpty(User.currentUser?.photoUrl)) {
            Glide.with(this)
                    .load(User.currentUser?.photoUrl)
                    .apply(RequestOptions.placeholderOf(R.drawable.user_default).fitCenter())
                    .into(this.imgview_photo)
            imgview_photo.visibility = View.VISIBLE
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            // Photo
            R.id.layout_photo -> {
                this.helper!!.showMenuDialog()
            }
            R.id.but_done -> {
                onButDone()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        helper!!.onActivityResult(requestCode, resultCode, data)
    }

    private fun onButDone() {
        val strFirstName = edit_firstname.text.toString()
        val strLastName = edit_lastname.text.toString()

        //
        // check if input is valid
        //

        // first name
        if (TextUtils.isEmpty(strFirstName)) {
            Utils.createErrorAlertDialog(this, "Invalid Name", "First name cannot be empty").show()
            return
        }

        // last name
        if (TextUtils.isEmpty(strLastName)) {
            Utils.createErrorAlertDialog(this, "Invalid Name", "Last name cannot be empty").show()
            return
        }

        // social log in, make it success directly
        User.currentUser?.let {
            saveUserData(it)
            return
        }

        Utils.createProgressDialog(this, "Signing up...", "Submitting user credentials")

        // create new user
        FirebaseManager.mAuth.createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    if (!task.isSuccessful) {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Utils.createErrorAlertDialog(this, "Signup failed.", task.exception?.localizedMessage!!).show()

                        Utils.closeProgressDialog()

                        return@OnCompleteListener
                    }

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")

                    val user = initUser()
                    user.id = FirebaseManager.mAuth.currentUser!!.uid

                    // save photo image
                    user.updateProfilePhoto(this, this.helper!!.byteData, {
                        saveUserData(user)
                    })
                })
    }

    /**
     * initialize user info
     */
    private fun initUser(): User {
        val newUser = User.currentUser ?: User()

        this.email?.let { email -> newUser.email = email }

        newUser.firstName = this.edit_firstname.text.toString()
        newUser.lastName = this.edit_lastname.text.toString()

        newUser.type = User.USER_TYPE_CUSTOMER

        User.currentUser = newUser

        return newUser
    }

    /**
     * Save user data & go to board page
     */
    private fun saveUserData(user: User) {
        user.saveToDatabase(user.id)

        Utils.moveNextActivity(this, SignupBoardActivity::class.java, true, true)
    }

    /**
     * SFUpdateImageListener
     */
    override fun getActivity(): Activity {
        return this
    }

    override fun updatePhotoImageView(byteData: ByteArray) {
        Glide.with(this).load(byteData).into(this.imgview_photo)
        this.imgview_photo.visibility = View.VISIBLE
    }
}
