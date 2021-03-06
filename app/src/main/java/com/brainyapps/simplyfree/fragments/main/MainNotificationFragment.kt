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
import com.brainyapps.simplyfree.activities.main.HomeActivity
import com.brainyapps.simplyfree.adapters.main.NotificationAdapter
import com.brainyapps.simplyfree.models.Notification
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Globals
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_main_notification.*
import kotlinx.android.synthetic.main.fragment_main_notification.view.*
import java.util.*

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

    var adapter: NotificationAdapter? = null

    private var mListener: OnFragmentInteractionListener? = null

    private var mDbQuery: DatabaseReference? = null
    private var mChildListener: ChildEventListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewMain = inflater.inflate(R.layout.fragment_main_notification, container, false)

        // init list
        viewMain.list.layoutManager = LinearLayoutManager(activity)

        viewMain.list.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        this.adapter = NotificationAdapter(activity!!, User.currentUser?.notifications!!)
        viewMain.list.adapter = this.adapter
        viewMain.list.itemAnimator = DefaultItemAnimator()

        viewMain.swiperefresh.setOnRefreshListener(this)

        // Inflate the layout for this fragment
        return viewMain
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // load data
        getNotifications(false, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        mDbQuery?.removeEventListener(mChildListener)
    }

    override fun onResume() {
        updateList(false)

        super.onResume()

        // hide badge
        Globals.hasNewNotification = false
        val activityHome = activity as HomeActivity
        activityHome.showBadge()
    }

    override fun onRefresh() {
        stopRefresh()
    }

    /**
     * get User data
     */
    private fun getNotifications(bRefresh: Boolean, bAnimation: Boolean) {

        val user = User.currentUser!!
        user.notifications.clear()

        val database = FirebaseDatabase.getInstance().reference.child(Notification.TABLE_NAME)
        mDbQuery = database.child(user.id)

        var countFound = 0
        var countFetched = 0

        mChildListener = mDbQuery?.addChildEventListener(object : ChildEventListener {
            override fun onChildRemoved(p0: DataSnapshot?) {
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot?, p1: String?) {
                val notifi = dataSnapshot?.getValue(Notification::class.java)
                if (notifi?.readAt != null) {
                    // remove read notifications
                    user.notifications.remove(user.notifications.first { it.id == dataSnapshot.key})

                    updateList(false)
                }
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot?, previousChildName: String?) {

                val notifi = dataSnapshot?.getValue(Notification::class.java)
                notifi!!.id = dataSnapshot.key

                if (notifi.readAt != null) {
                    // read notifications, skip it
                    return
                }

                // fetch its user
                User.readFromDatabase(notifi.userId, object: User.FetchDatabaseListener {
                    override fun onFetchedUser(u: User?, success: Boolean) {

                        notifi.userPosted = u

                        user.notifications.add(notifi)
                        countFetched++

                        // if all notification users are fetched
                        if (countFound == countFetched) {
                            // sort
                            Collections.sort(user.notifications, Collections.reverseOrder())

                            updateList(bAnimation)
                        }
                    }

                    override fun onFetchedItems() {
                    }

                    override fun onFetchedReviews() {
                    }
                })

                countFound++
            }

            override fun onCancelled(error: DatabaseError) {
                updateList(bAnimation)
            }
        })
    }

    fun updateList(bAnimation: Boolean) {
        // clear
        if (bAnimation) {
            this@MainNotificationFragment.adapter!!.notifyItemRangeRemoved(0, User.currentUser?.notifications!!.count())
        }

        stopRefresh()

        if (bAnimation) {
            adapter!!.notifyItemRangeInserted(0, User.currentUser?.notifications!!.count())
        }
        else {
            adapter!!.notifyDataSetChanged()
        }

        if (User.currentUser?.notifications!!.isEmpty()) {
            this@MainNotificationFragment.text_empty_notice.visibility = View.VISIBLE
        }
        else {
            this@MainNotificationFragment.text_empty_notice.visibility = View.GONE
        }
    }

    private fun stopRefresh() {
        swiperefresh.isRefreshing = false
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
