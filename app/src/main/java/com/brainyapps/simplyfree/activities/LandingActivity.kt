package com.brainyapps.simplyfree.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.utils.FirebaseManager
import com.brainyapps.simplyfree.utils.Utils
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_landing.*

class LandingActivity : LoginBaseActivity(), View.OnClickListener {

    private val TAG = LandingActivity::class.java.getSimpleName()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        this.but_login.setOnClickListener(this)
        this.but_signup.setOnClickListener(this)
        this.but_forget.setOnClickListener(this)
        this.layout_but_fb.setOnClickListener(this)
        this.layout_but_gplus.setOnClickListener(this)

        initFbButton()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.but_login -> {
                Utils.moveNextActivity(this, LoginActivity::class.java)
            }
            R.id.but_signup -> {
                Utils.moveNextActivity(this, SignupLandingActivity::class.java)
            }
            R.id.but_forget -> {
                Utils.moveNextActivity(this, ForgetActivity::class.java)
            }

            // facebook
            R.id.layout_but_fb -> {
                onButFacebook()
            }

            // Google login
            R.id.layout_but_gplus -> {
                onButGPlus()
            }
        }
    }
}
