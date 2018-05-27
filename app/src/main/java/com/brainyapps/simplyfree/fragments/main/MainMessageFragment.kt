package com.brainyapps.simplyfree.fragments.main

import android.content.Context
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
import com.brainyapps.simplyfree.adapters.main.MessageListAdapter
import com.brainyapps.simplyfree.models.Message
import com.brainyapps.simplyfree.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_main_message.*
import kotlinx.android.synthetic.main.fragment_main_message.view.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MainMessageFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MainMessageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainMessageFragment : MainBaseFragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private val TAG = MainMessageFragment::class.java.getSimpleName()

    var aryMessage = ArrayList<Message>()
    var adapter: MessageListAdapter? = null

    var mListener: OnFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewMain = inflater.inflate(R.layout.fragment_main_message, container, false)

        // init list
        val layoutManager = LinearLayoutManager(activity)
        viewMain.list.setLayoutManager(layoutManager)

        viewMain.list.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        this.adapter = MessageListAdapter(activity!!, this.aryMessage)
        viewMain.list.setAdapter(this.adapter)
        viewMain.list.setItemAnimator(DefaultItemAnimator())

        viewMain.swiperefresh.setOnRefreshListener(this)

        // Inflate the layout for this fragment
        return viewMain
    }

    override fun onResume() {
        super.onResume()

        getMessages(aryMessage.isEmpty(), false)
    }

    override fun onRefresh() {
        getMessages(false, false)
    }

    /**
     * get Message data
     */
    private fun getMessages(bRefresh: Boolean, bAnimation: Boolean) {
        if (bRefresh) {
            if (!this.swiperefresh.isRefreshing) {
                this.swiperefresh.isRefreshing = true
            }
        }

        // clear
        if (bAnimation) {
            this@MainMessageFragment.adapter!!.notifyItemRangeRemoved(0, aryMessage.count())
        }
        aryMessage.clear()

        val userCurrent = User.currentUser!!
        val database = FirebaseDatabase.getInstance().reference.child(Message.TABLE_NAME + "/" + userCurrent.id)

        var countFound = 0
        var countFetched = 0

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                stopRefresh()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                for (itemItem in dataSnapshot!!.children) {
                    val itemId = itemItem.key
                    val itemUsers = itemItem.value as HashMap<String, Any>
                    val entryUser = itemUsers.entries.first()
                    val userId = entryUser.key
                    val entryMsg = (entryUser.value as HashMap<String, Any>).get(Message.FIELD_LATEST_MSG)
                    val msg = Message(entryMsg as HashMap<String, Any>)
                    msg.targetUserId = userId
                    msg.itemId = itemId

                    countFound++

                    msg.fetchTargetUser(object: Message.FetchDatabaseListener {
                        override fun onFetchedTargetUser(success: Boolean) {
                            aryMessage.add(msg)
                            countFetched++

                            // if all messages users are fetched
                            if (countFound == countFetched) {
                                // sort
                                Collections.sort(aryMessage, Collections.reverseOrder())

                                updateList(bAnimation)
                            }
                        }
                    })
                }

                // no found
                if (countFound == 0) {
                    updateList(bAnimation)
                }
            }
        })
    }

    private fun updateList(bAnimation: Boolean) {
        stopRefresh()

        if (bAnimation) {
            this@MainMessageFragment.adapter!!.notifyItemRangeInserted(0, aryMessage.count())
        }
        else {
            this@MainMessageFragment.adapter!!.notifyDataSetChanged()
        }

        if (aryMessage.isEmpty()) {
            this@MainMessageFragment.text_empty_notice.visibility = View.VISIBLE
        }
        else {
            this@MainMessageFragment.text_empty_notice.visibility = View.GONE
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
