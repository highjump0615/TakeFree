package com.brainyapps.simplyfree.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.brainyapps.billingmodule.billing.BillingManager
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.billing.BillingConstants
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.FontManager
import com.brainyapps.simplyfree.utils.Utils
import kotlinx.android.synthetic.main.activity_signup_landing.*

class SignupLandingActivity : LoginBaseActivity(), View.OnClickListener {

    private val TAG = SignupLandingActivity ::class.java.simpleName

    private lateinit var mBillingManager: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_landing)

        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = BillingManager(this, object: BillingManager.BillingUpdatesListener {
            override fun onBillingClientSetupFinished() {
                Log.d(TAG, "BillingManager -- onBillingClientSetupFinished");
            }

            override fun onConsumeFinished(token: String, result: Int) {
                Log.d(TAG, "BillingManager -- onConsumeFinished");
            }

            override fun onPurchasesUpdated(purchases: List<Purchase>) {
                Log.d(TAG, "BillingManager -- onPurchasesUpdated");

                for (purchase in purchases) {
                    Log.d(TAG, "onPurchasesUpdated ${purchase.sku}")

                    when (purchase.sku) {
                        BillingConstants.SKU_PREMIUM -> {
                            // payment successful
                            Log.d(TAG, "You are Premium! Congratulations!!!")

                            this@SignupLandingActivity.onPaymentSuccess()
                        }
                    }
                }
            }

        })

        // get payment type from intent
        val bundle = intent.extras
        if (bundle != null) {
            loginType = bundle.getInt(KEY_LOGIN_TYPE)
        }

        // font
        this.text_premium.typeface = FontManager.getTypeface(this, FontManager.ENCHANTING)
        this.text_free.typeface = FontManager.getTypeface(this, FontManager.ENCHANTING)

        // buttons
        this.layout_but_premium.setOnClickListener(this)
        this.layout_but_free.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()

        // Note: We query purchases in onResume() to handle purchases completed while the activity
        // is inactive. For example, this can happen if the activity is destroyed during the
        // purchase flow. This ensures that when the activity is resumed it reflects the user's
        // current purchases.
        if (mBillingManager.getBillingClientResponseCode() == BillingClient.BillingResponse.OK) {
            mBillingManager.queryPurchases()
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "Destroying helper.")
        mBillingManager.destroy()

        super.onDestroy()
    }

    private fun getNextIntent(): Intent {
        var intent = Intent(this@SignupLandingActivity, SignupActivity::class.java)

        // if social login, go to sign up info page directly
        if (this.loginType > LOGIN_TYPE_EMAIL) {
            intent = Intent(this@SignupLandingActivity, SignupProfileActivity::class.java)
        }

        return intent
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.layout_but_premium, R.id.layout_but_free -> {
                if (view.id == R.id.layout_but_premium) {
                    mBillingManager.initiatePurchaseFlow(BillingConstants.SKU_PREMIUM, BillingClient.SkuType.INAPP)
                }
                else {
                    val intent = getNextIntent()

                    // if it is prompt, finish it
                    User.currentUser?.let {
                        finish()
                        return
                    }

                    intent.putExtra(LoginBaseActivity.KEY_PAYMENT_TYPE, User.PAYMENT_TYPE_NONE)
                    startActivity(intent)
                }
            }
        }
    }

    fun onPaymentSuccess() {
        // if it is prompt, finish it
        User.currentUser?.let {
            it.paymentType = User.PAYMENT_TYPE_PAY
            it.saveToDatabaseChild(User.FIELD_PAYMENTTYPE, User.PAYMENT_TYPE_PAY)

            finish()
            return
        }

        // if it is signup, continue
        val intent = getNextIntent()

        intent.putExtra(LoginBaseActivity.KEY_PAYMENT_TYPE, User.PAYMENT_TYPE_PAY)
        startActivity(intent)
    }
}
