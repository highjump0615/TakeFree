package com.brainyapps.simplyfree.activities

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.FirebaseManager
import com.brainyapps.simplyfree.utils.Globals
import com.brainyapps.simplyfree.utils.Utils

class SplashActivity : BaseActivity() {

    private val TAG = SplashActivity::class.java.getSimpleName()

    private val SPLASH_TIME_OUT = 3000
    private var bTimeUp = false
    private var bFetchedUser = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // check login state
        val userId = FirebaseManager.mAuth.currentUser?.uid
        if (TextUtils.isEmpty(userId) || User.currentUser != null) {
            Log.d(TAG, "fetched user")
            bFetchedUser = true
        }
        else {
            User.readFromDatabase(userId!!, object: User.FetchDatabaseListener {
                override fun onFetchedReviews() {
                }

                override fun onFetchedItems() {
                }

                override fun onFetchedUser(u: User?, success: Boolean) {
                    User.currentUser = u

                    Log.d(TAG, "fetched u")
                    bFetchedUser = true

                    // do action only when time is up
                    if (bTimeUp) {
                        goToMainPage()
                    }
                }
            })
        }

        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity

            Log.d(TAG, "time up")
            bTimeUp = true

            // do action only when user is determined
            if (bFetchedUser) {
                goToMainPage()
            }

        }, SPLASH_TIME_OUT.toLong())
    }

    /**
     * Go to landing page according to user log in status
     */
    fun goToMainPage() {
        if (User.currentUser != null) {
            goToMain()
        }
        else {
            Utils.moveNextActivity(this, LandingActivity::class.java, true)
        }
    }
}
