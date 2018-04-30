package com.brainyapps.simplyfree.activities.main

import android.os.Bundle
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_term.*

class TermActivity : BaseActivity() {

    companion object {
        val KEY_TERM_TYPE = "term_type"

        val TERM_SERVICE = 0
        val TERM_POLICY = 1
    }

    private var type = TERM_SERVICE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_term)

        var strTitle = "Terms and Conditions"

        // init webview
        var strUrl = "https://termsfeed.com/terms-conditions/f9652d9b206800684d310081f9179206"

        // get type from intent
        val bundle = intent.extras
        this.type = bundle?.getInt(KEY_TERM_TYPE)!!

        if (this.type == TERM_POLICY) {
            strTitle = "Privacy Policy"
            strUrl = "https://termsfeed.com/privacy-policy/2d0905ce662a7f916977aa3cfc55b735\n"
        }

        setNavbar(strTitle, true)

        this.webview.settings.javaScriptEnabled = true
        this.webview.settings.setSupportZoom(true)
        this.webview.settings.builtInZoomControls = true
        this.webview.loadUrl(strUrl)
    }
}
