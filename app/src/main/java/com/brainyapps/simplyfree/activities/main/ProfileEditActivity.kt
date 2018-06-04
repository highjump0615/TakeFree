package com.brainyapps.simplyfree.activities.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.helpers.PhotoActivityHelper
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.SFUpdateImageListener
import com.brainyapps.simplyfree.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_signup_profile.*

class ProfileEditActivity : BaseActivity(), View.OnClickListener, SFUpdateImageListener {

    var helper: PhotoActivityHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        setNavbar("Edit Profile", true)

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

        this.but_done.setOnClickListener(this)
        this.layout_photo.setOnClickListener(this)
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
        if (Utils.isStringEmpty(strFirstName)) {
            Utils.createErrorAlertDialog(this, "Invalid Name", "First name cannot be empty").show()
            return
        }

        // last name
        if (Utils.isStringEmpty(strLastName)) {
            Utils.createErrorAlertDialog(this, "Invalid Name", "Last name cannot be empty").show()
            return
        }

        val user = User.currentUser!!
        user.firstName = strFirstName
        user.lastName = strLastName

        // save photo image
        if (this.helper!!.byteData != null) {
            Utils.createProgressDialog(this, "Saving profile...", "Uploading profile photo")
        }

        user.updateProfilePhoto(this, this.helper!!.byteData, {
            saveUserData(user)
        })
    }

    /**
     * Save user data & go back
     */
    private fun saveUserData(user: User) {
        user.saveToDatabase(user.id)
        finish()
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
