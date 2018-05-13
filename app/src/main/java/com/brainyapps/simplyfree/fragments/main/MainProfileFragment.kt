package com.brainyapps.simplyfree.fragments.main

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.*

import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.main.ProfileEditActivity
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_main_profile.*
import kotlinx.android.synthetic.main.fragment_main_profile.view.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MainProfileFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MainProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainProfileFragment : MainBaseFragment(), View.OnClickListener {

    private var mListener: OnFragmentInteractionListener? = null

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    var aryFragment = ArrayList<MainProfileItemFragment>()

    companion object {
        const val ITEM_AVAILABLE = 0
        const val ITEM_TAKEN = 1
    }

    private var currentTab = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewMain = inflater.inflate(R.layout.fragment_main_profile, container, false)

        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return viewMain
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager)

        // init tab views
        this.aryFragment.add(MainProfileItemFragment.newInstance(ITEM_AVAILABLE))
        this.aryFragment.add(MainProfileItemFragment.newInstance(ITEM_TAKEN))

        // Set up the ViewPager with the sections adapter.
        pager_item.adapter = mSectionsPagerAdapter

        pager_item.addOnPageChangeListener(PageChangeListener())
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(pager_item))
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater!!.inflate(R.menu.edit, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            // edit profile
            R.id.menu_edit -> {
                Utils.moveNextActivity(activity!!, ProfileEditActivity::class.java)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onResume() {
        super.onResume()

        //
        // fill user info
        //
        text_name.text = User.currentUser?.userFullName()

        Glide.with(this)
                .load(User.currentUser?.photoUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.user_default).fitCenter())
                .into(imgview_photo)
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return this@MainProfileFragment.aryFragment[position]
        }

        override fun getCount(): Int {
            return 2
        }
    }

    interface OnFragmentInteractionListener : MainBaseFragment.OnFragmentInteractionListener {
    }

    inner class PageChangeListener: TabLayout.TabLayoutOnPageChangeListener(tabs) {

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)

            if (this@MainProfileFragment.currentTab != position) {
                this@MainProfileFragment.currentTab = position

                initItemList()
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
        }
    }

    /**
     * get item list in fragment
     *
     * @param reload force reload item data
     */
    fun initItemList(reload: Boolean = false) {
        if (this.currentTab < 0) {
            return
        }

        // update list
        val fragment = this@MainProfileFragment.mSectionsPagerAdapter!!.getItem(this.currentTab) as MainProfileItemFragment
        if (reload || !fragment.isInitialized) {
            fragment.getItems(false, false)
        }
    }

    override fun getInteractionListener() = mListener

}// Required empty public constructor
