package com.brainyapps.simplyfree.activities.main

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.activities.BaseSettingActivity
import com.brainyapps.simplyfree.activities.LandingActivity
import com.brainyapps.simplyfree.utils.Utils
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseSettingActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        setNavbar("Settings", true)

        layout_about.setOnClickListener(this)
        layout_privacy.setOnClickListener(this)
        layout_term.setOnClickListener(this)
        layout_report.setOnClickListener(this)
        but_logout.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        super.onClick(view)

        when (view?.id) {
            R.id.layout_about -> {
                Utils.moveNextActivity(this, AboutActivity::class.java)
            }

            // privacy policy
            R.id.layout_privacy -> {
                val intent = Intent(this, TermActivity::class.java)
                intent.putExtra(TermActivity.KEY_TERM_TYPE, TermActivity.TERM_POLICY)
                startActivity(intent)
            }

            // terms
            R.id.layout_term -> {
                val intent = Intent(this, TermActivity::class.java)
                intent.putExtra(TermActivity.KEY_TERM_TYPE, TermActivity.TERM_SERVICE)
                startActivity(intent)
            }

            // report a problem
            R.id.layout_report -> {
                val emailintent = Intent(Intent.ACTION_SENDTO)
                val uriText = "mailto:admin@simplyfreeonline.com" +
                        "?subject=" + Uri.encode("SimplyFree Report a Problem") +
                        "&body=" + Uri.encode("SimplyFree is not working properly for me.\n" +
                        "Here is a brief description of what's going on:\n\n" + "Sent from my Android")

                emailintent.data = Uri.parse(uriText)
                startActivity(emailintent)
            }
        }
    }
}
