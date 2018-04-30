package com.brainyapps.simplyfree

import android.os.StrictMode
import android.support.multidex.MultiDexApplication
import android.widget.Toast
import com.brainyapps.simplyfree.utils.PrefUtils
import com.brainyapps.simplyfree.utils.Utils
import com.google.firebase.FirebaseApp

/**
 * Created by Administrator on 3/7/18.
 */

class MainApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        PrefUtils.init(this)

        if (!Utils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Please check the network connection", Toast.LENGTH_SHORT).show()
        }

        FirebaseApp.initializeApp(this)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }
}
