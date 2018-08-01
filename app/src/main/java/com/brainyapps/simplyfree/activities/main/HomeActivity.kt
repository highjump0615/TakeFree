package com.brainyapps.simplyfree.activities.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import com.brainyapps.simplyfree.R
import kotlinx.android.synthetic.main.activity_home.*
import android.location.Location
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.brainyapps.simplyfree.activities.BaseHomeActivity
import com.brainyapps.simplyfree.activities.LandingActivity
import com.brainyapps.simplyfree.fragments.main.MainHomeFragment
import com.brainyapps.simplyfree.fragments.main.MainMessageFragment
import com.brainyapps.simplyfree.fragments.main.MainNotificationFragment
import com.brainyapps.simplyfree.fragments.main.MainProfileFragment
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Globals
import com.brainyapps.simplyfree.utils.Utils
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.firebase.iid.FirebaseInstanceId
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import kotlinx.android.synthetic.main.layout_main_app_bar.*
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView


class HomeActivity : BaseHomeActivity(),
        MainHomeFragment.OnFragmentInteractionListener,
        MainNotificationFragment.OnFragmentInteractionListener,
        MainMessageFragment.OnFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private val TAG = HomeActivity::class.java.getSimpleName()
    private val FRAG_HOME = "home_frag"
    private val FRAG_PROFILE = "profile_frag"
    private val FRAG_NOTIFICATION = "notification_frag"
    private val FRAG_MESSAGE = "message_frag"

    private var fragCurrent: Fragment? = null
    lateinit var googleApiClient: GoogleApiClient

    private var fusedLocationClient: FusedLocationProviderClient? = null

    lateinit var mBadgeNotification: Badge
    lateinit var mBadgeMessage: Badge

    // admob
    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        navigation.onNavigationItemSelectedListener = mOnNavigationItemSelectedListener

        navigation.enableShiftingMode(false)
        navigation.setTextVisibility(false)

        // set home tab as default
        loadFragByTag(FRAG_HOME)
        navigation.menu.getItem(2).isChecked = true

        setNavbar()

        // init location
        initLocation()

        // init admob
        MobileAds.initialize(this, resources.getString(R.string.admob_id))

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = resources.getString(R.string.admob_unit_id)
        mInterstitialAd.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            override fun onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                if (fragCurrent is MainHomeFragment) {
                    // post new item
                    (fragCurrent as MainHomeFragment).showPostItem()
                }
            }
        }

        // get device token
        val userCurrent = User.currentUser!!
        val strToken = FirebaseInstanceId.getInstance().token

        strToken?.let {
            userCurrent.token = it
            userCurrent.saveToDatabaseChild(User.FIELD_TOKEN, it)
        }

        // add badge
        mBadgeNotification = addBadgeAt(0)
        mBadgeMessage = addBadgeAt(1);

        //
        // check pending notification
        //
        notificationPending?.let {
            // intent to notification specific page
            val intent = it.getIntentForDetail(this)
            startActivity(intent)
        }
    }

    private fun signOutAndExit() {
        signOutClear()
        Utils.moveNextActivity(this, LandingActivity::class.java, true, true)
    }

    private fun initLocation() {
        googleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
    }

    fun showBadge() {
        mBadgeMessage.badgeNumber = if (Globals.hasNewMessage) -1 else 0
        mBadgeNotification.badgeNumber = if (Globals.hasNewNotification) -1 else 0
    }

    private fun gotNewLocation(location: Location?) {
        Globals.mLocation = location
        Log.w(TAG, "Location: ${location?.latitude}, ${location?.longitude}")
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_notification -> {
                loadFragByTag(FRAG_NOTIFICATION)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_message -> {
                loadFragByTag(FRAG_MESSAGE)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_home -> {
                loadFragByTag(FRAG_HOME)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                loadFragByTag(FRAG_PROFILE)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_setting -> {
                Utils.moveNextActivity(this, SettingActivity::class.java)
            }
        }
        false
    }

    override fun onStart() {
        super.onStart()

        // Initiating the connection
        googleApiClient.connect()

        mInterstitialAd.loadAd(AdRequest.Builder().build())

        Globals.activityMain = this
    }

    override fun onStop() {
        super.onStop()

        // Disconnecting the connection
        googleApiClient.disconnect()

        Globals.activityMain = null
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(mLocationCallback)
    }

    override fun onResume() {
        val bToProfile = Globals.isBackToRoot

        // already backed, restore flag
        Globals.isBackToRoot = false

        super.onResume()
        if (Globals.mLocation == null) {
            startLocationUpdates()
        }

        if (bToProfile) {
            // change to profile tab
            loadFragByTag(FRAG_PROFILE)
            navigation.menu.getItem(3).isChecked = true
        }

        // show badge
        showBadge()

        // check user availability
        User.currentUser?.readFromDatabaseChild(User.FIELD_BANNED, {
            it?.let {
                val banned = it as Boolean
                User.currentUser?.banned = banned

                if (banned) {
                    // Force log out
                    Utils.createErrorAlertDialog(this,
                            "Cannot log in",
                            "You are banned, contact to Administrator to continue using",
                            DialogInterface.OnClickListener { _, _ ->
                                signOutAndExit()
                            },
                            DialogInterface.OnCancelListener{ _ ->
                                signOutAndExit()
                            })
                            .show()
                }
            }
        })
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return

            gotNewLocation(locationResult.locations[0])
        }
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient?.requestLocationUpdates(locationRequest,
                    mLocationCallback,
                    null /* Looper */)
        }
    }

    private fun loadFragByTag(tag: String) {
        var frag = supportFragmentManager.findFragmentByTag(tag)
        val transaction = supportFragmentManager.beginTransaction()

        // hide current fragment
        fragCurrent?.let {
            transaction.hide(fragCurrent)
        }

        if (frag == null) {
            Log.d(TAG, "$tag not found, creating a new one.")

            when (tag) {
                FRAG_HOME -> {
                    frag = MainHomeFragment()
                }
                FRAG_PROFILE -> {
                    frag = MainProfileFragment()
                }
                FRAG_NOTIFICATION -> {
                    frag = MainNotificationFragment()
                }
                FRAG_MESSAGE -> {
                    frag = MainMessageFragment()
                }
            }

            // add new fragment
            transaction.add(R.id.layout_main, frag, tag)
        }
        else {
            Log.d(TAG, "$tag found")

            // show fragments
            transaction.show(frag)
        }

        transaction.commit()

        // show activity tool bar in profile page only
        this.toolbar.visibility = View.GONE
        if (tag == FRAG_PROFILE) {
            this.toolbar.visibility = View.VISIBLE

            // update user info
            frag.onResume()
        }

        fragCurrent = frag
    }

    private fun addBadgeAt(position: Int) : Badge {
        return QBadgeView(this)
                .setBadgeNumber(-1)
                .setGravityOffset(16.0F, 4.0F, true)
                .bindTarget(navigation.getBottomNavigationItemView(position))
    }

    fun showAds() {
        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.")
        }
    }

    /**
     * home fragment
     */
    override fun onHomeClickMap() {
        // go to map page
        Utils.moveNextActivity(this, HomeMapActivity::class.java)
    }

    /**
     * GoogleApiClient.ConnectionCallbacks
     */
    @SuppressLint("MissingPermission")
    override fun onConnected(bundle: Bundle?) {
        // check permission
        // android.permission.ACCESS_COARSE_LOCATION
        AndPermission.with(this)
                .permission(
                        Permission.Group.LOCATION
                )
                .rationale { context, permissions, executor ->
                    // show confirm dialog
                    AlertDialog.Builder(context)
                            .setTitle("Will you open location features?")
                            .setMessage("Location is needed when posting an item and getting items")
                            .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                                executor.execute()
                            })
                            .setNegativeButton(android.R.string.no, DialogInterface.OnClickListener { dialog, which ->
                                executor.cancel()
                            })
                            .create()
                }
                .onGranted {
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@HomeActivity)

                    fusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
                        // Got last known location. In some rare situations this can be null.
                        gotNewLocation(location)
                    }
                }
                .onDenied {
                }
                .start()
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
    }
}
