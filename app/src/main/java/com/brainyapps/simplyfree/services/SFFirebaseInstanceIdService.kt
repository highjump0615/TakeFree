package com.brainyapps.simplyfree.services

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

/**
 * Created by Administrator on 7/5/18.
 */
class SFFirebaseInstanceIdService : FirebaseInstanceIdService() {

    private val TAG = SFFirebaseInstanceIdService::class.java.getSimpleName()

    override fun onTokenRefresh() {
        super.onTokenRefresh()

        // Getting registration token
        val refreshedToken = FirebaseInstanceId.getInstance().token

        // Displaying token on logcat
        Log.d(TAG, "Refreshed token: $refreshedToken")
    }
}