package com.brainyapps.simplyfree.activities.admin

import android.os.Bundle
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.utils.Utils
import kotlinx.android.synthetic.main.activity_admin_home.*

class AdminHomeActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        setNavbar("Main Menu")

        this.layout_users.setOnClickListener(this)
        this.layout_reported_users.setOnClickListener(this)
        this.layout_settings.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.layout_users -> {
                Utils.moveNextActivity(this, AdminUserActivity::class.java)
            }

            R.id.layout_reported_users -> {
                Utils.moveNextActivity(this, AdminReportedUserActivity::class.java)
            }

            R.id.layout_settings -> {
                Utils.moveNextActivity(this, AdminSettingsActivity::class.java)
            }
        }
    }
}
