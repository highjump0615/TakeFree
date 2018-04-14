package com.brainyapps.simplyfree.activities.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.brainyapps.simplyfree.R
import kotlinx.android.synthetic.main.activity_home.*
import android.graphics.Typeface
import android.net.Uri
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.fragments.main.MainHomeFragment
import com.brainyapps.simplyfree.fragments.main.MainMessageFragment
import com.brainyapps.simplyfree.fragments.main.MainNotificationFragment
import com.brainyapps.simplyfree.fragments.main.MainProfileFragment
import com.brainyapps.simplyfree.utils.Utils
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
