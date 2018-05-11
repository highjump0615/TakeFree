package com.brainyapps.simplyfree.activities.main

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.activities.PhotoActivityHelper
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Globals
import com.brainyapps.simplyfree.utils.SFUpdateImageListener
import com.brainyapps.simplyfree.utils.Utils
import com.bumptech.glide.Glide
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_item_post.*

class ItemPostActivity : BaseActivity(), View.OnClickListener, SFUpdateImageListener {

    private val TAG = ItemPostActivity::class.java.getSimpleName()

    var helper: PhotoActivityHelper? = null

    var progressDlg: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_post)

        setNavbar("Post New Item", true)

        // init category spinner
        val adapterCategory = ArrayAdapter.createFromResource(this, R.array.item_category_array, android.R.layout.simple_spinner_item)
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.spinner_category.adapter = adapterCategory

        // init condition spinner
        val adapterCondition = ArrayAdapter.createFromResource(this, R.array.item_condition_array, android.R.layout.simple_spinner_item)
        adapterCondition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.spinner_condition.adapter = adapterCondition

        this.layout_photo.setOnClickListener(this)
        this.but_post.setOnClickListener(this)

        this.helper = PhotoActivityHelper(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        helper!!.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            // Photo
            R.id.layout_photo -> {
                this.helper!!.showMenuDialog()
            }

            R.id.but_post -> {
                onButPost()
            }
        }
    }

    /**
     * Post an item
     */
    private fun onButPost() {
        val strName = edit_name.text.toString()

        //
        // check input validate
        //

        // photo
        if (helper!!.byteData == null) {
            Utils.createErrorAlertDialog(this, "Invalid Photo", "Photo cannot be empty").show()
            return
        }

        // name
        if (TextUtils.isEmpty(strName)) {
            Utils.createErrorAlertDialog(this, "Invalid Name", "Name cannot be empty").show()
            edit_name.requestFocus()
            return
        }

        // generate id
        val database = FirebaseDatabase.getInstance().reference
        val strKey = database.child(Item.TABLE_NAME).push().getKey();

        showSaveProgress()

        val metadata = StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build()
        val storageReference = FirebaseStorage.getInstance().getReference(Item.TABLE_NAME).child("$strKey.jpg")
        val uploadTask = storageReference.putBytes(helper!!.byteData!!, metadata)
        uploadTask.addOnSuccessListener(this, OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
            savePost(strKey, taskSnapshot.downloadUrl.toString())
        }).addOnFailureListener(this, OnFailureListener {
            Log.d(TAG, it.toString())

            Toast.makeText(this, "Failed save photo data", Toast.LENGTH_SHORT).show()
        })
    }

    private fun savePost(withId: String, urlPic: String) {
        val strName = edit_name.text.toString()
        val strDesc = edit_desc.text.toString()

        val newItem = Item()

        newItem.name = strName
        newItem.description = strDesc
        newItem.photoUrl = urlPic
        newItem.category = spinner_category.selectedItemPosition
        newItem.condition = spinner_condition.selectedItemPosition

        // geofire
        val geoFire = GeoFire(FirebaseDatabase.getInstance().getReference(Item.TABLE_NAME))
        if (Globals.mLocation != null) {

            showSaveProgress()

            this.but_post.isEnabled = false

            geoFire.setLocation(withId, GeoLocation(Globals.mLocation!!.latitude, Globals.mLocation!!.longitude)) { key, error ->
                if (error != null) {
                    Log.w(TAG, "setLocation:failure", error.toException())

                    this.but_post.isEnabled = true
                }
                else {
                    newItem.saveToDatabase(withId)
                    User.currentUser!!.posts.add(0, newItem)

                    // go to posted job page
                    finish()
                }

                // close progress view
                closeSaveProgress()
            }
        }
        else {
            Toast.makeText(this, "Cannot get current location", Toast.LENGTH_SHORT).show()

            closeSaveProgress()
        }
    }

    private fun showSaveProgress() {
        if (progressDlg == null) {
            progressDlg = Utils.createProgressDialog(this, "Submitting Item", "Your item will is being posted right now")
        }
    }

    private fun closeSaveProgress() {
        Utils.closeProgressDialog()
        progressDlg = null
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
