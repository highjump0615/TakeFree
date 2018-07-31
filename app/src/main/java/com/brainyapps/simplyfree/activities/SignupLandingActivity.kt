package com.brainyapps.simplyfree.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.FontManager
import com.brainyapps.simplyfree.utils.Utils
import kotlinx.android.synthetic.main.activity_signup_landing.*

class SignupLandingActivity : LoginBaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_landing)

        // get payment type from intent
        val bundle = intent.extras
        if (bundle != null) {
            loginType = bundle.getInt(KEY_LOGIN_TYPE)
        }

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
                var intent = Intent(this@SignupLandingActivity, SignupActivity::class.java)

                // if social login, go to sign up info page directly
                if (this.loginType > LOGIN_TYPE_EMAIL) {
                    intent = Intent(this@SignupLandingActivity, SignupProfileActivity::class.java)
                }

                if (view.id == R.id.layout_but_premium) {
                    intent.putExtra(LoginBaseActivity.KEY_PAYMENT_TYPE, User.PAYMENT_TYPE_PAY)
                }
                else {
                    intent.putExtra(LoginBaseActivity.KEY_PAYMENT_TYPE, User.PAYMENT_TYPE_NONE)
                }

                startActivity(intent)
            }
        }
    }
}
