package com.brainyapps.simplyfree.fragments.main

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.main.ItemPostActivity
import com.brainyapps.simplyfree.adapters.main.HomeCategoryAdapter
import com.brainyapps.simplyfree.adapters.main.NotificationAdapter
import com.brainyapps.simplyfree.models.Category
import com.brainyapps.simplyfree.models.Notification
import com.brainyapps.simplyfree.utils.FontManager
import com.brainyapps.simplyfree.utils.Utils
import kotlinx.android.synthetic.main.fragment_main_home.*
import kotlinx.android.synthetic.main.fragment_main_home.view.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MainNotificationFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MainNotificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainNotificationFragment : MainBaseFragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private val TAG = MainNotificationFragment::class.java.getSimpleName()

    var aryNotification = ArrayList<Notification>()
    var adapter: NotificationAdapter? = null

    var mListener: OnFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val viewMain = inflater!!.inflate(R.layout.fragment_main_notification, container, false)

        // init list
        val layoutManager = LinearLayoutManager(activity)
        viewMain.list.setLayoutManager(layoutManager)

        viewMain.list.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        this.adapter = NotificationAdapter(activity, this.aryNotification)
        viewMain.list.setAdapter(this.adapter)
        viewMain.list.setItemAnimator(DefaultItemAnimator())

        viewMain.swiperefresh.setOnRefreshListener(this)

        // load data
        Handler().postDelayed({ getNotifications(true, true) }, 500)

        // Inflate the layout for this fragment
        return viewMain
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onRefresh() {
        getNotifications(true, false)
    }

    /**
     * get User data
     */
    private fun getNotifications(bRefresh: Boolean, bAnimation: Boolean) {
        if (bAnimation) {
            if (!this.swiperefresh.isRefreshing) {
                this.swiperefresh.isRefreshing = true
            }
        }

        // clear
        if (bAnimation) {
            this@MainNotificationFragment.adapter!!.notifyItemRangeRemoved(0, aryNotification.count())
        }
        aryNotification.clear()

        // add
        for (i in 0..2) {
            val data = Notification()
            data.type = i

            aryNotification.add(data)
        }

        updateList(bAnimation)

        if (aryNotification.isEmpty()) {
            this@MainNotificationFragment.text_empty_notice.visibility = View.VISIBLE
        }
    }

    fun updateList(bAnimation: Boolean) {
        stopRefresh()

        if (bAnimation) {
            this@MainNotificationFragment.adapter!!.notifyItemRangeInserted(0, aryNotification.count())
        }
        else {
            this@MainNotificationFragment.adapter!!.notifyDataSetChanged()
        }
    }

    fun stopRefresh() {
        this.swiperefresh.isRefreshing = false
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener : MainBaseFragment.OnFragmentInteractionListener {
    }

    companion object {
    }

    override fun onClick(view: View?) {
        when (view?.id) {

        }
    }

    override fun getInteractionListener() = mListener

}// Required empty public constructor
