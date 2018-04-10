package com.brainyapps.simplyfree.activities.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.brainyapps.simplyfree.R
import kotlinx.android.synthetic.main.activity_home.*
import android.graphics.Typeface
import android.net.Uri
import android.util.Log
import com.brainyapps.simplyfree.fragments.main.MainHomeFragment
import com.brainyapps.simplyfree.utils.Utils


class HomeActivity : AppCompatActivity(), MainHomeFragment.OnFragmentInteractionListener {

    private val TAG = HomeActivity::class.java.getSimpleName()
    val FRAG_HOME = "home_frag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        navigation.enableShiftingMode(false)
        navigation.setTextVisibility(false)

        // set home tab as default
        loadFragByTag(FRAG_HOME)
        navigation.menu.getItem(2).setChecked(true)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_notification -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_message -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_home -> {
                loadFragByTag(FRAG_HOME)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_setting -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun loadFragByTag(tag: String) {
        var frag = supportFragmentManager.findFragmentByTag(tag)
        if (frag == null) {
            Log.d(TAG, "$tag not found, creating a new one.")

            when (tag) {
                FRAG_HOME -> {
                    frag = MainHomeFragment()
                }
            }
        }
        else {
            Log.d(TAG, "$tag found")
        }

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.layout_main, frag, tag)
                .commit()
    }

    /**
     * home fragment
     */
    override fun onHomeClickMap() {
        // go to map page
        Utils.moveNextActivity(this, HomeMapActivity::class.java)
    }
}
