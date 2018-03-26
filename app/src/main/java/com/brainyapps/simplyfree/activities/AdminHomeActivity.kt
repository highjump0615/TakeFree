package com.brainyapps.simplyfree.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.brainyapps.simplyfree.R
import kotlinx.android.synthetic.main.activity_admin_home.*

class AdminHomeActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        setNavbar("Main Menu", true)

        this.layout_users.setOnClickListener(this)
        this.layout_reported_users.setOnClickListener(this)
        this.layout_settings.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
        }
    }
}
