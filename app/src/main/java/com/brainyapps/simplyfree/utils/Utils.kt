package com.brainyapps.simplyfree.utils

import android.app.Activity
import android.content.Intent

/**
 * Created by Administrator on 3/24/18.
 */
class Utils {

    companion object {

        /**
         * Move to destination activity class with animate transition.
         */
        fun moveNextActivity(source: Activity, destinationClass: Class<*>, removeSource: Boolean = false, removeAll: Boolean = false) {
            val intent = Intent(source, destinationClass)

            if (removeAll) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            source.startActivity(intent)

            if (removeSource) {
                source.finish()
            }
        }
    }
}