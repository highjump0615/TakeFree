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
import android.view.*

import com.brainyapps.simplyfree.R
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
class MainProfileFragment : MainBaseFragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private var mListener: OnFragmentInteractionListener? = null

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    var aryFragment = ArrayList<MainProfileItemFragment>()

    companion object {
        val ITEM_AVAILABLE = 0
        val ITEM_TAKEN = 1
    }

    private var currentTab = -1

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val viewMain = inflater!!.inflate(R.layout.fragment_main_profile, container, false)

        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return viewMain
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
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

    override fun onRefresh() {
        getItems(true, false)
    }

    /**
     * get User data
     */
    private fun getItems(bRefresh: Boolean, bAnimation: Boolean) {
//        if (bAnimation) {
//            if (!this.swiperefresh.isRefreshing) {
//                this.swiperefresh.isRefreshing = true
//            }
//        }
//
//        // clear
//        if (bAnimation) {
//            this@MainProfileFragment.adapter!!.notifyItemRangeRemoved(0, aryCategory.count())
//        }
//        aryCategory.clear()
//
//
//        // add
//        for (i in 0..10) {
//            aryCategory.add(Category())
//        }
//
//        updateList(bAnimation)
//
//        if (aryCategory.isEmpty()) {
//            this@MainProfileFragment.text_empty_notice.visibility = View.VISIBLE
//        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
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
