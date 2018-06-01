package com.brainyapps.simplyfree.activities.admin

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.fragments.admin.AdminUserFragment
import com.brainyapps.simplyfree.utils.FontManager
import kotlinx.android.synthetic.main.activity_admin_users.*

class AdminUserActivity : BaseActivity(), View.OnClickListener {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    var aryFragment = ArrayList<AdminUserFragment>()

    companion object {
        const val USER_ALL = 0
        const val USER_BANNED = 1
    }

    private var currentTab = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_users)

        setNavbar("Users", true)

        // set search font
        val iconFont = FontManager.getTypeface(this, FontManager.FONTAWESOME)
        FontManager.markAsIconContainer(this.text_search_mark, iconFont)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // init tab views
        this.aryFragment.add(AdminUserFragment.newInstance(USER_ALL))
        this.aryFragment.add(AdminUserFragment.newInstance(USER_BANNED))

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(PageChangeListener())
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        // search keyword
        edit_search.addTextChangedListener(mTextWatcher)
    }

    override fun onResume() {
        super.onResume()

        initUserList(true)
    }

    /**
     * get user list in fragment
     *
     * @param reload force reload user data
     */
    fun initUserList(reload: Boolean = false) {
        if (this.currentTab < 0) {
            return
        }

        // update list
        val fragment = this@AdminUserActivity.mSectionsPagerAdapter!!.getItem(this.currentTab) as AdminUserFragment
        if (reload || !fragment.isInitialized) {
            fragment.getUsers(false, !fragment.isInitialized)
        }
        else {
            fragment.updateList()
        }
    }

    fun updateUserList() {
        if (this.currentTab < 0) {
            return
        }

        // update list
        val fragment = this@AdminUserActivity.mSectionsPagerAdapter!!.getItem(this.currentTab) as AdminUserFragment
        fragment.updateList()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
        }
    }

    private val mTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override fun afterTextChanged(editable: Editable) {
            updateUserList()
        }
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return this@AdminUserActivity.aryFragment[position]
        }

        override fun getCount(): Int {
            return 2
        }
    }

    inner class PageChangeListener: TabLayout.TabLayoutOnPageChangeListener(tabs) {

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)

            if (this@AdminUserActivity.currentTab != position) {
                this@AdminUserActivity.currentTab = position

                initUserList()
            }
        }
    }
}
