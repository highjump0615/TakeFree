package com.brainyapps.simplyfree.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.utils.FontManager
import kotlinx.android.synthetic.main.activity_signup_landing.*

class SignupLandingActivity : AppCompatActivity(), View.OnClickListener {

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
        }
    }
}
