package com.brainyapps.simplyfree.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.utils.Utils
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        setNavbar("Sign Up", true)

        this.but_next.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.but_next -> {
                Utils.moveNextActivity(this, SignupProfileActivity::class.java)
            }
        }
    }
}
