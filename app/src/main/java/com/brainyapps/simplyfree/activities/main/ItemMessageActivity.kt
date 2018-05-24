package com.brainyapps.simplyfree.activities.main

import android.content.DialogInterface
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.TextureView
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.adapters.main.ChatAdapter
import com.brainyapps.simplyfree.helpers.UserDetailHelper
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.models.Message
import com.brainyapps.simplyfree.models.Notification
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Globals
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_item_message.*

class ItemMessageActivity : BaseActivity(), Item.FetchDatabaseListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    lateinit var item: Item

    private lateinit var mLinearLayoutManager: LinearLayoutManager

    var adapter: ChatAdapter? = null
    var aryChat = ArrayList<Message>()

    private var userToId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_message)

        setNavbar("", true)

        // get item from common objects
        item = Globals.selectedItem!!
        item.fetchUser(this)

        // get user-to id from intent
        val bundle = intent.extras
        val userId = bundle?.getString(UserDetailHelper.KEY_USER_ID)
        if (TextUtils.isEmpty(userId)) {
            // from item details, user-to is item owner
            userToId = item.userId
        }
        else {
            userToId = userId
        }

        // item info
        text_item.text = item.name
        Glide.with(this)
                .load(item.photoUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.user_default).fitCenter())
                .into(imgview_photo)

        // button
        but_take.setOnClickListener(this)

        // message send
        imgview_comment_send.setOnClickListener(this)

        updateTakeButton()

        // load message data
        getMessages(true, true)

        // init list
        mLinearLayoutManager = LinearLayoutManager(this)
        list.layoutManager = mLinearLayoutManager

        adapter = ChatAdapter(this, aryChat)
        list.adapter = adapter
        list.itemAnimator = DefaultItemAnimator()

        adapter!!.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart:Int, itemCount:Int) {
                super.onItemRangeInserted(positionStart, itemCount)

                val msgCount = adapter!!.getItemCount()
                val lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition()
                if (lastVisiblePosition == -1 || positionStart >= msgCount - 1 && lastVisiblePosition == positionStart - 1) {
                    list.scrollToPosition(positionStart)
                }
            }
        })

        swiperefresh.setOnRefreshListener(this)
    }

    private fun getMessages(bRefresh: Boolean, bAnimation: Boolean) {
        val database = FirebaseDatabase.getInstance().reference.child(Message.TABLE_NAME)
        val query = database.child(User.currentUser!!.id).child(userToId).child(Message.FIELD_CHAT)

        query.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot?, previousChildName: String?) {
                // A new message has been added, add it to the displayed list
                val msg = dataSnapshot?.getValue(Message::class.java)!!

                aryChat.add(msg)
                adapter?.notifyItemInserted(aryChat.size - 1)
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
            }
        })
    }

    private fun updateTakeButton() {
        val user = User.currentUser!!

        if (item.taken) {
            // I took this item
            if (item.userIdTaken.equals(user.id)) {
                but_take.text = "Owner Accepted Request"
                but_take.setTextColor(Color.WHITE)
                but_take.setBackgroundResource(R.drawable.bg_item_status_but_round_accepted)
            }
            // Other one took this
            else {
                but_take.text = "Taken"
                but_take.setTextColor(Color.WHITE)
                but_take.setBackgroundResource(R.drawable.bg_item_status_but_round_taken)
            }
        }
        else {
            // simple "Available" if item is his own
            if (item.userId.equals(user.id)) {
                // normal
                but_take.text = "Available"
                but_take.setTextColor(ContextCompat.getColor(this, R.color.colorTheme))
                but_take.setBackgroundResource(R.drawable.bg_item_status_but_round)
            }
            else {
                if (item.userIdTaken.isNotEmpty()) {
                    // request sent
                    but_take.text = "Request Sent"
                    but_take.setTextColor(Color.WHITE)
                    but_take.setBackgroundResource(R.drawable.bg_item_status_but_round_requested)

                    // check request
                    if (item.userId.equals(user.id)) {
                        item.fetchUserTaken(object : Item.FetchDatabaseListener {
                            override fun onFetchedItem(i: Item?) {
                            }

                            override fun onFetchedUser(success: Boolean) {
                                // show confirm dialog
                                AlertDialog.Builder(this@ItemMessageActivity)
                                        .setTitle("Do you accept the take request?")
                                        .setMessage("${item.userTaken?.userFullName()} would like to take the item.")
                                        .setPositiveButton("ACCEPT", DialogInterface.OnClickListener { dialog, which ->
                                            acceptTakeRequest()
                                        })
                                        .setNegativeButton("DECLINE", DialogInterface.OnClickListener { dialog, which ->
                                        })
                                        .create()
                                        .show()
                            }

                            override fun onFetchedComments(success: Boolean) {
                            }
                        })
                    }
                } else {
                    // normal
                    but_take.text = "Take Item"
                    but_take.setTextColor(ContextCompat.getColor(this, R.color.colorTheme))
                    but_take.setBackgroundResource(R.drawable.bg_item_status_but_round)
                }
            }
        }
    }

    private fun acceptTakeRequest() {
        // update item status
        item.taken = true
        item.saveToDatabase()

        // send notification to user
        val user = User.currentUser!!
        val newNotification = Notification(notificationType = Notification.NOTIFICATION_TOOK)
        newNotification.itemId = item.id
        newNotification.userId = item.userId

        user.addNotification(newNotification)

        updateTakeButton()
    }

    //
    // Item.FetchDatabaseListener
    //
    override fun onFetchedItem(i: Item?) {
    }
    override fun onFetchedUser(success: Boolean) {
        // update user info
        setTitle(item.userPosted?.userFullName())
    }
    override fun onFetchedComments(success: Boolean) {
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.but_take -> {
                // already submitted take
                if (item.userIdTaken.isNotEmpty()) {
                    return
                }

                // send request
                item.userIdTaken = User.currentUser!!.id
                item.saveToDatabase()

                updateTakeButton()
            }

            // send message
            R.id.imgview_comment_send -> {
                doSendMessage()
            }
        }
    }

    /**
     * Send message to owner
     */
    private fun doSendMessage() {
        val strMessage = edit_message.text.toString()
        if (TextUtils.isEmpty(strMessage)) {
            return
        }

        val newMsg = Message()
        newMsg.addMessageTo(item, strMessage)

        // clear edit
        edit_message.setText("")
    }

    //
    // SwipeRefreshLayout.OnRefreshListener
    //
    override fun onRefresh() {
        stopRefresh()
    }

    private fun stopRefresh() {
        swiperefresh.isRefreshing = false
    }
}
