package com.brainyapps.simplyfree.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.admin.AdminHomeActivity
import com.brainyapps.simplyfree.activities.main.HomeActivity
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.FirebaseManager
import com.brainyapps.simplyfree.utils.Utils

/**
 * Created by Administrator on 2/14/18.
 */
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()

        // close progress dialog
        Utils.closeProgressDialog()
    }

    fun setNavbar(title: String? = null, withBackButton: Boolean = false, hideDefaultTitle: Boolean = true) {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        if (hideDefaultTitle) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        // set title
        if (!TextUtils.isEmpty(title)) {
            val textTitle = findViewById<View>(R.id.text_toolbar_title) as TextView
            textTitle.text = title
        }

        // back button
        if (withBackButton) {
            // back icon
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)

            // back icon event
            toolbar.setNavigationOnClickListener { onBackPressed() }
        }
    }

    /**
     * go to main page according to user type
     */
    fun goToMain() {
        when (User.currentUser!!.type) {
            User.USER_TYPE_ADMIN -> {
                Utils.moveNextActivity(this, AdminHomeActivity::class.java, true, true)
            }
            User.USER_TYPE_CUSTOMER -> {
                Utils.moveNextActivity(this, HomeActivity::class.java, true, true)
            }
        }
    }

    /**
     * sign out & clear data
     */
    fun signOutClear() {
        FirebaseManager.mAuth.signOut()
        User.currentUser = null
    }

    fun enableButton(button: View, enable: Boolean) {
        button.isEnabled = enable

        if (enable) {
            button.alpha = 1.0f
        }
        else {
            button.alpha = 0.6f
        }
    }
}