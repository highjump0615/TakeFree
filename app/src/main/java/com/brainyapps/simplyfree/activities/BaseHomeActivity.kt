package com.brainyapps.simplyfree.activities

import android.os.Handler
import android.widget.Toast

/**
 * Created by Administrator on 6/20/18.
 */
open class BaseHomeActivity : BaseActivity() {

    private var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please tap BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }
}