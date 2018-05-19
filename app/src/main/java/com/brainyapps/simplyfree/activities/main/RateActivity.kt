package com.brainyapps.simplyfree.activities.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.activities.UserDetailHelper
import com.brainyapps.simplyfree.models.User

class RateActivity : BaseActivity() {

    lateinit var user: User
    lateinit var helperUser: UserDetailHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate)

        // get user from intent
        val bundle = intent.extras
        user = bundle?.getParcelable<User>(UserDetailHelper.KEY_USER)!!

        setNavbar(user.userFullName(), true)
    }
}
