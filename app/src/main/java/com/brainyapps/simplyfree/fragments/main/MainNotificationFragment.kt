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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.adapters.main.NotificationAdapter
import com.brainyapps.simplyfree.models.Notification
import com.brainyapps.simplyfree.models.User
import kotlinx.android.synthetic.main.fragment_main_notification.*
import kotlinx.android.synthetic.main.fragment_main_notification.view.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MainNotificationFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MainNotificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainNotificationFragment : MainBaseFragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, User.FetchDatabaseListener {

    private val TAG = MainNotificationFragment::class.java.getSimpleName()

    var adapter: NotificationAdapter? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewMain = inflater.inflate(R.layout.fragment_main_notification, container, false)

        // init list
        viewMain.list.layoutManager = LinearLayoutManager(activity)

        viewMain.list.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        this.adapter = NotificationAdapter(activity!!, User.currentUser?.notifications!!)
        viewMain.list.adapter = this.adapter
        viewMain.list.itemAnimator = DefaultItemAnimator()

        viewMain.swiperefresh.setOnRefreshListener(this)

        // load data
        Handler().postDelayed({ getNotifications(false, false) }, 500)

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
        if (!bRefresh) {
            if (!this.swiperefresh.isRefreshing) {
                this.swiperefresh.isRefreshing = true
            }
        }

        User.currentUser?.fetchNotifications(object: User.FetchDatabaseListener {
            override fun onFetchedUser(user: User?, success: Boolean) {
            }

            override fun onFetchedItems() {
            }

            override fun onFetchedNotifications() {
                // clear
                if (bAnimation) {
                    this@MainNotificationFragment.adapter!!.notifyItemRangeRemoved(0, User.currentUser?.notifications!!.count())
                }

                updateList(bAnimation)

                if (User.currentUser?.notifications!!.isEmpty()) {
                    this@MainNotificationFragment.text_empty_notice.visibility = View.VISIBLE
                }
                else {
                    this@MainNotificationFragment.text_empty_notice.visibility = View.GONE
                }
            }

        })
    }

    fun updateList(bAnimation: Boolean) {
        stopRefresh()

        if (bAnimation) {
            this@MainNotificationFragment.adapter!!.notifyItemRangeInserted(0, User.currentUser?.notifications!!.count())
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

    //
    // User.FetchDatabaseListener
    //
    override fun onFetchedUser(user: User?, success: Boolean) {
    }

    override fun onFetchedItems() {
    }

    override fun onFetchedNotifications() {
    }

}// Required empty public constructor
