package com.brainyapps.simplyfree.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Utils

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity
            goToMainPage()

        }, SPLASH_TIME_OUT.toLong())
    }

    /**
     * Go to landing page according to user log in status
     */
    fun goToMainPage() {
        if (User.currentUser != null) {
        }
        else {
            Utils.moveNextActivity(this, LandingActivity::class.java, true)
        }
    }
}
