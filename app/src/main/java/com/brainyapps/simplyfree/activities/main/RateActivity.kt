package com.brainyapps.simplyfree.activities.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity

class RateActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate)

        setNavbar("Fernando Gimenez", true)
    }
}
