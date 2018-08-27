package com.brainyapps.simplyfree.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.utils.Utils

/**
 * Created by Administrator on 6/11/18.
 */
open class BaseSettingActivity : BaseActivity(), View.OnClickListener {

    override fun onClick(view: View?) {
        when (view?.id) {
            // log out
            R.id.but_logout -> {
                // show confirm dialog
                AlertDialog.Builder(this)
                        .setTitle("Are you sure to log out?")
                        .setMessage("After log out, you can log in with other account")
                        .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                            signOutClear()
                            Utils.moveNextActivity(this, LandingActivity::class.java, true, true)
                        })
                        .setNegativeButton(android.R.string.no, DialogInterface.OnClickListener { dialog, which ->
                        })
                        .create()
                        .show()
            }
        }
    }
}