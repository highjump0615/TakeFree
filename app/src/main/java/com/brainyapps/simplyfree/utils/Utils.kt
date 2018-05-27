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
import android.net.ConnectivityManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import com.brainyapps.simplyfree.R
import kotlinx.android.synthetic.main.layout_dialog_edit.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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

        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
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

        fun createEditDialog(context: Context,
                             title: String,
                             message: String,
                             listener: DialogInterface.OnClickListener? = null): Dialog {
            val builder = AlertDialog.Builder(context)
            val inflater = (context as Activity).layoutInflater
            val view = inflater.inflate(R.layout.layout_dialog_edit, null)

            // placeholder
            view.edit_content.hint = message

            val dialog = builder.setTitle(title)
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, listener)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setCancelable(true)
                    .create()

            return dialog
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

        fun getServerLongTime(): Long {
            val estimatedServerTimeMs = System.currentTimeMillis() + Utils.ServerOffset
            return estimatedServerTimeMs.toLong()
        }

        private fun getServerTime(): Date {
            val estimatedServerTimeMs = System.currentTimeMillis() + Utils.ServerOffset
            return Date(estimatedServerTimeMs.toLong())
        }

        /**
         * format date time
         * - Message list view
         */
        fun getFormattedDateTime(date: Date): String {
            val period = Utils.getServerTime().time - date.time
            val value = TimeUnit.MINUTES.convert(period, TimeUnit.MILLISECONDS)
            if (value == 0L) {
                return "0 min ago"
            } else if (value < 60) {
                return value.toString() + " mins ago"
            } else if (value == 60L) {
                return "1 hour ago"
            } else if (value < 120) {
                return "1 hour " + (value - 60) + " mins ago"
            } else if (value < 720) {
                return "" + (value / 60).toString() + " hours ago"
            } else if (value < 1440) {
                return "Today " + SimpleDateFormat("HH:mm").format(date)
            } else if (value < 2880) {
                return "Yesterday " + SimpleDateFormat("HH:mm").format(date)
            }

            return SimpleDateFormat("MM/dd, yyyy").format(date)
        }

        fun getFormattedDate(date: Date): String {
            val dateFormat = SimpleDateFormat("MM/dd/yyyy")
            var result = dateFormat.format(date)

            val period = Utils.getServerTime().time - date.time
            val diffDay = TimeUnit.DAYS.convert(period, TimeUnit.MILLISECONDS)

            if (diffDay == 0L) {
                result = "Today"
            }
            else if (diffDay == 1L) {
                result = "Yesterday"
            }

            return result
        }

        fun hideKeyboard(context: Context) {
            val view = (context as Activity).getCurrentFocus()
            view?.let {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(it.getWindowToken(), 0)
            }
        }
    }
}