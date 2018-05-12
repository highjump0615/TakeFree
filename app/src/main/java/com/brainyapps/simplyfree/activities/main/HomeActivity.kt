package com.brainyapps.simplyfree.activities.main

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.brainyapps.simplyfree.R
import kotlinx.android.synthetic.main.activity_home.*
import android.graphics.Typeface
import android.location.Location
import android.net.Uri
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.fragments.main.MainHomeFragment
import com.brainyapps.simplyfree.fragments.main.MainMessageFragment
import com.brainyapps.simplyfree.fragments.main.MainNotificationFragment
import com.brainyapps.simplyfree.fragments.main.MainProfileFragment
import com.brainyapps.simplyfree.utils.Globals
import com.brainyapps.simplyfree.utils.Utils
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import kotlinx.android.synthetic.main.layout_main_app_bar.*


class HomeActivity : BaseActivity(),
        MainHomeFragment.OnFragmentInteractionListener,
        MainNotificationFragment.OnFragmentInteractionListener,
        MainMessageFragment.OnFragmentInteractionListener {

    private val TAG = HomeActivity::class.java.getSimpleName()
    val FRAG_HOME = "home_frag"
    val FRAG_PROFILE = "profile_frag"
    val FRAG_NOTIFICATION = "notification_frag"
    val FRAG_MESSAGE = "message_frag"

    var fragCurrent: Fragment? = null

    private var fusedLocationClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        navigation.enableShiftingMode(false)
        navigation.setTextVisibility(false)

        // set home tab as default
        loadFragByTag(FRAG_HOME)
        navigation.menu.getItem(2).setChecked(true)

        setNavbar()

        // init location
        initLocation()
    }

    @SuppressLint("MissingPermission")
    private fun initLocation() {
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
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                    fusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
                        // Got last known location. In some rare situations this can be null.
                        gotNewLocation(location)
                    }
                }
                .onDenied {
                }
                .start()
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

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(mLocationCallback)
    }

    override fun onResume() {
        super.onResume()
        if (Globals.mLocation == null) {
            startLocationUpdates()
        }
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
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
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
        }

        fragCurrent = frag
    }

    /**
     * home fragment
     */
    override fun onHomeClickMap() {
        // go to map page
        Utils.moveNextActivity(this, HomeMapActivity::class.java)
    }
}
