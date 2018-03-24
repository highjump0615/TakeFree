package com.brainyapps.simplyfree.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.brainyapps.simplyfree.R
import kotlinx.android.synthetic.main.activity_landing.*

class LandingActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        this.but_login.setOnClickListener(this)
        this.but_signup.setOnClickListener(this)
        this.but_forget.setOnClickListener(this)
        this.layout_but_fb.setOnClickListener(this)
        this.layout_but_gplus.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
    }
}
