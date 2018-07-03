package com.brainyapps.simplyfree.fragments.main

import android.content.Context
import android.os.Bundle
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
import com.google.firebase.database.*
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
class MainMessageFragment : MainBaseFragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, MessageListAdapter.ListUpdateInterface {

    private val TAG = MainMessageFragment::class.java.getSimpleName()

    var aryMessage = ArrayList<Message>()
    var adapter: MessageListAdapter? = null

    var mListener: OnFragmentInteractionListener? = null

    var countFound = 0
    var countFetched = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewMain = inflater.inflate(R.layout.fragment_main_message, container, false)

        // init list
        val layoutManager = LinearLayoutManager(activity)
        viewMain.list.setLayoutManager(layoutManager)

        viewMain.list.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        adapter = MessageListAdapter(activity!!, this.aryMessage)
        adapter?.updateInterface = this

        viewMain.list.adapter = adapter
        viewMain.list.itemAnimator = DefaultItemAnimator()

        viewMain.swiperefresh.setOnRefreshListener(this)

        // Inflate the layout for this fragment
        return viewMain
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // load data
        getMessages(false, false)
    }

    override fun onRefresh() {
        stopRefresh()
    }

    private fun addMessage(msg: Message?, bAnimation: Boolean = false) {
        if (msg == null) {
            return
        }

        var bExist = false

        for (m in aryMessage) {
            if (m.itemId == msg?.itemId && m.targetUserId == msg.targetUserId) {
                // update message
                m.text = msg.text
                m.type = msg.type

                // update list
                updateList(false)
                bExist = true

                break
            }
        }

        if (!bExist) {
            countFound++

            msg.fetchTargetUser(object : Message.FetchDatabaseListener {
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
    }

    /**
     * get Message data
     */
    private fun getMessages(bRefresh: Boolean, bAnimation: Boolean) {
        // clear
        if (bAnimation) {
            this@MainMessageFragment.adapter!!.notifyItemRangeRemoved(0, aryMessage.count())
        }
        aryMessage.clear()

        val userCurrent = User.currentUser!!
        val database = FirebaseDatabase.getInstance().reference.child(Message.TABLE_NAME_CHAT + "/" + userCurrent.id)

        database.addChildEventListener(object : ChildEventListener {
            override fun onChildRemoved(p0: DataSnapshot?) {
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot?, previousChildName: String?) {
                //
                // latest message has been updated
                //
                val msg = parseDatasnapshot(dataSnapshot!!)
                addMessage(msg)
            }

            override fun onCancelled(p0: DatabaseError?) {
                stopRefresh()
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot?, previousChildName: String?) {
                val msg = parseDatasnapshot(dataSnapshot!!)
                addMessage(msg)
            }
        })
    }

    private fun parseDatasnapshot(data: DataSnapshot): Message? {

        for (chatItem in data.children) {
            val chat = chatItem.getValue(Message::class.java)
            chat?.itemId = data.key
            chat?.targetUserId = chatItem.key

            return chat
        }

        return null
    }

    private fun updateList(bAnimation: Boolean) {
        stopRefresh()

        if (bAnimation) {
            this@MainMessageFragment.adapter!!.notifyItemRangeInserted(0, aryMessage.count())
        }
        else {
            this@MainMessageFragment.adapter!!.notifyDataSetChanged()
        }

        updateEmptyNotice()
    }

    private fun updateEmptyNotice() {
        if (aryMessage.isEmpty()) {
            this@MainMessageFragment.text_empty_notice.visibility = View.VISIBLE
        }
        else {
            this@MainMessageFragment.text_empty_notice.visibility = View.GONE
        }
    }

    fun stopRefresh() {
        this.swiperefresh?.isRefreshing = false
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
    // MessageListAdapter.ListUpdateInterface
    //
    override fun onListUpdated() {
        updateEmptyNotice()
    }

}// Required empty public constructor
