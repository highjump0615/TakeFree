package com.brainyapps.simplyfree.activities.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.activities.LandingActivity
import com.brainyapps.simplyfree.utils.Utils
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        setNavbar("Settings", true)

        layout_about.setOnClickListener(this)
        but_logout.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.layout_about -> {
            }

            // log out
            R.id.but_logout -> {
                signOutClear()
                Utils.moveNextActivity(this, LandingActivity::class.java, true, true)
            }
        }
    }
}
