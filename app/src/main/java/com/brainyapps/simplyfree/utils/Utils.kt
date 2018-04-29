package com.brainyapps.simplyfree.utils

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.text.TextUtils

/**
 * Created by Administrator on 3/24/18.
 */
class Utils {

    companion object {

        val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 9009

        var progressDlg: ProgressDialog? = null

        var ServerOffset = 0.0

        fun isValidEmail(target: String): Boolean {
            return if (TextUtils.isEmpty(target)) {
                false
            } else {
                android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                        .matches()
            }
        }

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

        /**
         * Show progress dialog
         */
        fun createProgressDialog(context: Context, title:String, message: String): ProgressDialog? {
            progressDlg = ProgressDialog.show(context, title, message);

            return progressDlg
        }

        fun closeProgressDialog() {
            progressDlg?.dismiss()
        }

        /**
         * Create error AlertDialog.
         */
        fun createErrorAlertDialog(context: Context, title: String, message: String, listener: DialogInterface.OnClickListener? = null): Dialog {
            return AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, listener).create()
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        fun checkStoragePermission(context: Context): Boolean {
            val currentAPIVersion = Build.VERSION.SDK_INT
            if (currentAPIVersion >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                return false
            } else {
                return true
            }
        }
    }
}