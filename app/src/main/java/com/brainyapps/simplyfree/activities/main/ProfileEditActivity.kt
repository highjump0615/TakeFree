package com.brainyapps.simplyfree.activities.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.utils.Utils
import kotlinx.android.synthetic.main.activity_signup_profile.*

class ProfileEditActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        setNavbar("Edit Profile", true)

        this.but_done.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.but_done -> {
                finish()
            }
        }
    }
}
