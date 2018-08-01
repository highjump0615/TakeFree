package com.brainyapps.simplyfree.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.admin.AdminHomeActivity
import com.brainyapps.simplyfree.activities.main.HomeActivity
import com.brainyapps.simplyfree.api.APIManager
import com.brainyapps.simplyfree.models.Notification
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.FirebaseManager
import com.brainyapps.simplyfree.utils.Globals
import com.brainyapps.simplyfree.utils.Utils
import com.facebook.login.LoginManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

/**
 * Created by Administrator on 2/14/18.
 */
open class BaseActivity : AppCompatActivity() {

    companion object {
        const val KEY_NOTIFICATION = "notification"
    }

    var notificationPending: Notification? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get notification pending
        val bundle = intent.extras
        if (bundle != null) {
            notificationPending = bundle.getParcelable(KEY_NOTIFICATION)
        }
    }

    override fun onResume() {
        super.onResume()

        if (Globals.isBackToRoot) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // close progress dialog
        Utils.closeProgressDialog()
    }

    fun setTitle(title: String?) {
        if (!TextUtils.isEmpty(title)) {
            val textTitle = findViewById<View>(R.id.text_toolbar_title) as TextView
            textTitle.text = title
        }
    }

    fun setNavbar(title: String? = null, withBackButton: Boolean = false, hideDefaultTitle: Boolean = true) {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        if (hideDefaultTitle) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        // set title
        setTitle(title)

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
                // intent to home
                val intent = Intent(this, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                notificationPending?.let {
                    intent.putExtra(BaseActivity.KEY_NOTIFICATION, it)
                }

                startActivity(intent)
                finish()
            }
        }
    }

    /**
     * Send push notification
     */
    fun sendPushNotification(token: String?, type: Int, itemId: String = "", message: String = "") {
        val keyServer = resources.getString(R.string.server_key)

        if (TextUtils.isEmpty(token)) {
            return
        }

        // param
        val jsonData = JSONObject()
        jsonData.put(Notification.FIELD_TYPE, type.toString())
        jsonData.put(Notification.FIELD_USER, User.currentUser!!.id)
        jsonData.put(Notification.FIELD_MSG, message)
        jsonData.put(Notification.FIELD_ITEM, itemId)

        val jsonParam = JSONObject()
        jsonParam.put("to", token)
        jsonParam.put("data", jsonData)
        jsonParam.put("priority", "high")

        APIManager.sendFcmMessage(jsonParam.toString(),
                "key=$keyServer",
                object : Callback {
                    override fun onFailure(call: Call?, e: IOException?) {
                    }

                    override fun onResponse(call: Call?, response: Response?) {
                    }

                })
    }

    /**
     * sign out & clear data
     */
    fun signOutClear() {
        FirebaseManager.mAuth.signOut()
        LoginManager.getInstance().logOut()

        // clear token
        User.currentUser?.saveToDatabaseChild(User.FIELD_TOKEN, "")
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