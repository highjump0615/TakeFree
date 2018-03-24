package com.brainyapps.simplyfree.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.brainyapps.simplyfree.R
import kotlinx.android.synthetic.main.activity_signup_profile.*

class SignupProfileActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_profile)

        setNavbar("Profile Set Up", true)

        this.but_done.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.but_done -> {
            }
        }
    }
}
