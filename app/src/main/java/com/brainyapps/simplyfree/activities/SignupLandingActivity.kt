package com.brainyapps.simplyfree.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.utils.FontManager
import com.brainyapps.simplyfree.utils.Utils
import kotlinx.android.synthetic.main.activity_signup_landing.*

class SignupLandingActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        val KEY_LOGIN_TYPE = "loginType"

        val LOGIN_TYPE_EMAIL = 0
        val LOGIN_TYPE_FACEBOOK = 1
        val LOGIN_TYPE_GOOGLE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_landing)

        // font
        this.text_premium.typeface = FontManager.getTypeface(this, FontManager.ENCHANTING)
        this.text_free.typeface = FontManager.getTypeface(this, FontManager.ENCHANTING)

        // buttons
        this.layout_but_premium.setOnClickListener(this)
        this.layout_but_free.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.layout_but_premium, R.id.layout_but_free -> {
                Utils.moveNextActivity(this, SignupActivity::class.java)
            }
        }
    }
}
