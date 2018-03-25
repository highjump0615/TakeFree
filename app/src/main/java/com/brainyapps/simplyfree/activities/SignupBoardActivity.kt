package com.brainyapps.simplyfree.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.fragments.SignupOnboardFragment
import com.brainyapps.simplyfree.utils.Utils
import kotlinx.android.synthetic.main.activity_board.*

class SignupBoardActivity : BaseActivity(), View.OnClickListener {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var currentIndex = 0
    private val MAX_PAGE = 4

    var aryFragment = ArrayList<SignupOnboardFragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        setNavbar("", true)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // init tab views
        for (i in 0 until MAX_PAGE) {
            this.aryFragment.add(SignupOnboardFragment.newInstance(i))
        }

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter
        container.addOnPageChangeListener(PageChangeListener())

        this.indicator.setViewPager(container)
        this.but_continue.setOnClickListener(this)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return this@SignupBoardActivity.aryFragment[position]
        }

        override fun getCount(): Int {
            return 4
        }
    }

    inner class PageChangeListener: ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            currentIndex = position
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.but_continue -> {
                if (currentIndex < MAX_PAGE - 1) {
                    container.setCurrentItem(currentIndex + 1, true)
                }
                else {
                }
            }
        }
    }
}
