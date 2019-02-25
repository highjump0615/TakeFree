package com.brainyapps.billingmodule.billing

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.SignupLandingActivity
import java.io.IOException
import java.util.ArrayList

/**
 * Handles all the interactions with Play Store (via Billing library), maintains connection to
 * it through BillingClient and caches temporary states/data if needed
 */
class BillingManager(activity: Activity, updatesListener: BillingUpdatesListener): PurchasesUpdatedListener {

    /**
     * Listener to the updates that happen when purchases list was updated or consumption of the
     * item was finished
     */
    interface BillingUpdatesListener {
        fun onBillingClientSetupFinished()
        fun onConsumeFinished(token: String, @BillingClient.BillingResponse result: Int)
        fun onPurchasesUpdated(purchases: List<Purchase>)
    }

    /* BASE_64_ENCODED_PUBLIC_KEY should be YOUR APPLICATION'S PUBLIC KEY
     * (that you got from the Google Play developer console). This is not your
     * developer public key, it's the *app-specific* public key.
     *
     * Instead of just storing the entire literal string here embedded in the
     * program,  construct the key at runtime from pieces or
     * use bit manipulation (for example, XOR with some other string) to hide
     * the actual key.  The key itself is not secret information, but we don't
     * want to make it easy for an attacker to replace the public key with one
     * of their own and then fake messages from the server.
     */
    private var BASE_64_ENCODED_PUBLIC_KEY = "CONSTRUCT_YOUR_KEY_AND_PLACE_IT_HERE"

    // Default value of mBillingClientResponseCode until BillingManager was not yeat initialized
    val BILLING_MANAGER_NOT_INITIALIZED = -1

    private val TAG = BillingManager::class.java.simpleName

    /** A reference to BillingClient  */
    private var mBillingClient: BillingClient

    /**
     * True if billing service is connected now.
     */
    private var mIsServiceConnected: Boolean = false

    private val mActivity: Activity
    private val mBillingUpdatesListener: BillingUpdatesListener

    private val mPurchases = ArrayList<Purchase>()

    private var mBillingClientResponseCode = BILLING_MANAGER_NOT_INITIALIZED

    init {
        BASE_64_ENCODED_PUBLIC_KEY = activity.resources.getString(R.string.base64_public_key)

        Log.d(TAG, "Creating Billing client.")
        mActivity = activity
        mBillingUpdatesListener = updatesListener
        mBillingClient = BillingClient.newBuilder(mActivity).setListener(this).build()

        Log.d(TAG, "Starting setup.")

        // Start setup. This is asynchronous and the specified listener will be called
        // once setup completes.
        // It also starts to report all the new purchases through onPurchasesUpdated() callback.
        startServiceConnection(Runnable {
            // Notifying the listener that billing client is ready
            mBillingUpdatesListener.onBillingClientSetupFinished()
            // IAB is fully set up. Now, let's get an inventory of stuff we own.
            Log.d(TAG, "Setup successful. Querying inventory.")
            queryPurchases()
        })
    }

    fun startServiceConnection(executeOnSuccess: Runnable?) {
        mBillingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                Log.d(TAG, "Setup finished. Response code: $billingResponseCode")

                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    mIsServiceConnected = true
                    executeOnSuccess?.run()
                }
                mBillingClientResponseCode = billingResponseCode
            }

            override fun onBillingServiceDisconnected() {
                mIsServiceConnected = false
            }
        })
    }

    /**
     * Returns the value Billing client response code or BILLING_MANAGER_NOT_INITIALIZED if the
     * clien connection response was not received yet.
     */
    fun getBillingClientResponseCode(): Int {
        return mBillingClientResponseCode
    }

    /**
     * Checks if subscriptions are supported for current client
     *
     * Note: This method does not automatically retry for RESULT_SERVICE_DISCONNECTED.
     * It is only used in unit tests and after queryPurchases execution, which already has
     * a retry-mechanism implemented.
     *
     */
    fun areSubscriptionsSupported(): Boolean {
        val responseCode = mBillingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        if (responseCode != BillingClient.BillingResponse.OK) {
            Log.w(TAG, "areSubscriptionsSupported() got an error response: $responseCode")
        }
        return responseCode == BillingClient.BillingResponse.OK
    }

    /**
     * Handle a result from querying of purchases and report an updated list to the listener
     */
    private fun onQueryPurchasesFinished(result: Purchase.PurchasesResult) {
        // Have we been disposed of in the meantime? If so, or bad result code, then quit
        if (result.responseCode != BillingClient.BillingResponse.OK) {
            Log.w(TAG, "Billing client was null or result code (" + result.responseCode
                    + ") was bad - quitting")
            return
        }

        Log.d(TAG, "Query inventory was successful.")

        // Update the UI and purchases inventory with new list of purchases
        mPurchases.clear()
        onPurchasesUpdated(BillingClient.BillingResponse.OK, result.purchasesList)
    }

    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     *
     * Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     *
     */
    private fun verifyValidSignature(signedData: String, signature: String): Boolean {
        // Some sanity checks to see if the developer (that's you!) really followed the
        // instructions to run this sample (don't put these checks on your app!)
        if (BASE_64_ENCODED_PUBLIC_KEY.contains("CONSTRUCT_YOUR")) {
            throw RuntimeException("Please update your app's public key at: " + "BASE_64_ENCODED_PUBLIC_KEY")
        }

        try {
            return Security.verifyPurchase(BASE_64_ENCODED_PUBLIC_KEY, signedData, signature)
        }
        catch (e: IOException) {
            Log.e(TAG, "Got an exception trying to validate a purchase: $e")
            return false
        }

    }

    /**
     * Handles the purchase
     *
     * Note: Notice that for each purchase, we check if signature is valid on the client.
     * It's recommended to move this check into your backend.
     * See [Security.verifyPurchase]
     *
     * @param purchase Purchase to be handled
     */
    private fun handlePurchase(purchase: Purchase) {
        if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
            Log.i(TAG, "Got a purchase: $purchase; but signature is bad. Skipping...")
            return
        }

        Log.d(TAG, "Got a verified purchase: $purchase")

        mPurchases.add(purchase)
    }

    private fun executeServiceRequest(runnable: Runnable) {
        if (mIsServiceConnected) {
            runnable.run()
        } else {
            // If billing service was disconnected, we try to reconnect 1 time.
            // (feel free to introduce your retry policy here).
            startServiceConnection(runnable)
        }
    }

    /**
     * Query purchases across various use cases and deliver the result in a formalized way through
     * a listener
     */
    fun queryPurchases() {
        val queryToExecute = Runnable {
            val time = System.currentTimeMillis()
            val purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP)
            Log.i(TAG, "Querying purchases elapsed time: " + (System.currentTimeMillis() - time)
                    + "ms")
            // If there are subscriptions supported, we add subscription rows as well
            if (areSubscriptionsSupported()) {
                val subscriptionResult = mBillingClient.queryPurchases(BillingClient.SkuType.SUBS)
                Log.i(TAG, "Querying purchases and subscriptions elapsed time: "
                        + (System.currentTimeMillis() - time) + "ms")
                Log.i(TAG, "Querying subscriptions result code: "
                        + subscriptionResult.responseCode
                        + " res: " + subscriptionResult.purchasesList.size)

                if (subscriptionResult.responseCode == BillingClient.BillingResponse.OK) {
                    purchasesResult.purchasesList.addAll(
                            subscriptionResult.purchasesList)
                }
                else {
                    Log.e(TAG, "Got an error response trying to query subscription purchases")
                }
            }
            else if (purchasesResult.responseCode == BillingClient.BillingResponse.OK) {
                Log.i(TAG, "Skipped subscription purchases query since they are not supported")
            }
            else {
                Log.w(TAG, "queryPurchases() got an error response code: " + purchasesResult.responseCode)
            }
            onQueryPurchasesFinished(purchasesResult)
        }

        executeServiceRequest(queryToExecute)
    }

    /**
     * Start a purchase flow
     */
    fun initiatePurchaseFlow(skuId: String, @BillingClient.SkuType billingType: String) {
        initiatePurchaseFlow(skuId, null, billingType)
    }

    /**
     * Start a purchase or subscription replace flow
     */
    fun initiatePurchaseFlow(skuId: String, oldSkus: ArrayList<String>?, @BillingClient.SkuType billingType: String) {
        val purchaseFlowRequest = Runnable {
            Log.d(TAG, "Launching in-app purchase flow. Replace old SKU? " + (oldSkus != null))
            val purchaseParams = BillingFlowParams.newBuilder()
                    .setSku(skuId).setType(billingType).setOldSkus(oldSkus).build()
            mBillingClient.launchBillingFlow(mActivity, purchaseParams)
        }

        executeServiceRequest(purchaseFlowRequest)
    }

    /**
     * Clear the resources
     */
    fun destroy() {
        Log.d(TAG, "Destroying the manager.")

        if (mBillingClient.isReady) {
            mBillingClient.endConnection()
        }
    }


    //
    // PurchasesUpdatedListener
    //

    /**
     * Handle a callback that purchases were updated from the Billing library
     */
    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        if (responseCode == BillingClient.BillingResponse.OK) {
            purchases?.let {
                for (purchase in it) {
                    handlePurchase(purchase)
                }
                mBillingUpdatesListener.onPurchasesUpdated(mPurchases)
            }
        }
        else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            Log.i(TAG, "onPurchasesUpdated() - user cancelled the purchase flow - skipping")
        }
        else {
            Log.w(TAG, "onPurchasesUpdated() got unknown resultCode: $responseCode")
        }
    }

}