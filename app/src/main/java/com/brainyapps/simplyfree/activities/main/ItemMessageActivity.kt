package com.brainyapps.simplyfree.activities.main

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.adapters.main.ChatAdapter
import com.brainyapps.simplyfree.helpers.UserDetailHelper
import com.brainyapps.simplyfree.models.*
import com.brainyapps.simplyfree.utils.Globals
import com.brainyapps.simplyfree.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_item_message.*

class ItemMessageActivity : BaseItemActivity(), Item.FetchDatabaseListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    companion object {
        const val KEY_ITEM_ID = "item_id"
    }

    private val TAG = ItemMessageActivity::class.java.simpleName

    private var itemId: String? = null

    private var mLinearLayoutManager: LinearLayoutManager? = null

    var adapter: ChatAdapter? = null
    var aryChat = ArrayList<Message>()

    var userTo: User? = null
    private var userToId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_message)

        setNavbar("", true)

        val bundle = intent.extras

        //
        // init item
        //

        // get item from common objects
        item = Globals.selectedItem
        if (item != null) {
            initItemView()
            itemId = item?.id
            monitorItemUpdate()
        }
        else {
            // get item id from intent
            itemId = bundle?.getString(KEY_ITEM_ID)

            Item.readFromDatabase(withId = itemId!!, fetchListener = this)
        }

        //
        // init user
        //

        // from message tab item
        if (intent.hasExtra(UserDetailHelper.KEY_USER)) {
            userTo = bundle?.getParcelable<User>(UserDetailHelper.KEY_USER)!!
            userToId = userTo!!.id
        }

        // from item detail page
        initUserId()

        // button
        but_take.setOnClickListener(this)

        // message send
        imgview_comment_send.setOnClickListener(this)

        updateTakeButton()

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
                val lastVisiblePosition = mLinearLayoutManager?.findLastCompletelyVisibleItemPosition()
                if (lastVisiblePosition == -1 || positionStart >= msgCount - 1 && lastVisiblePosition == positionStart - 1) {
                    list.scrollToPosition(positionStart)
                }
            }
        })

        swiperefresh.setOnRefreshListener(this)

        // load message data
        getMessages(true, true)
    }

    private fun initUserId() {
        if (TextUtils.isEmpty(userToId)) {
            // from item details, user-to is item owner
            userToId = item?.userId
        }

        initUserView()

        if (userTo == null) {
            User.readFromDatabase(userToId!!, object:User.FetchDatabaseListener {
                override fun onFetchedUser(u: User?, success: Boolean) {
                    userTo = u

                    initUserView()
                }

                override fun onFetchedItems() {
                }

                override fun onFetchedReviews() {
                }

            })
        }
    }

    private fun initUserView() {
        // update user info
        setTitle(userTo?.userFullName())
    }

    private fun initItemView() {
        // item info
        text_item.text = item?.name
        Glide.with(this)
                .load(item?.photoUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.img_item_default).fitCenter())
                .into(imgview_photo)

        updateTakeButton()

        // check if item has been deleted
        checkItemRemoval()
    }

    private fun getMessages(bRefresh: Boolean, bAnimation: Boolean) {
        val database = FirebaseDatabase.getInstance().reference.child(Message.TABLE_NAME)
        val query = database.child(User.currentUser!!.id)
                .child(itemId)
                .child(userToId)

        Log.e(TAG, "${itemId}, ${userToId}")

        query.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot?, p1: String?) {
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

    /**
     * update take item button
     */
    private fun updateTakeButton() {
        if (item == null) {
            return
        }

        val user = User.currentUser!!

        if (item?.taken!!) {
            // I took this item
            if (item?.userIdTaken.equals(user.id)) {
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
            if (item?.userId.equals(user.id)) {
                // normal
                but_take.text = if (item?.deletedAt == null) "Available" else "Deleted"
                but_take.setTextColor(ContextCompat.getColor(this, R.color.colorTheme))
                but_take.setBackgroundResource(R.drawable.bg_item_status_but_round)

                // return if deleted
                if (checkItemRemoval()) {
                    return
                }

                // check request
                item?.fetchUserTaken(object : Item.FetchDatabaseListener {
                    override fun onFetchedItem(i: Item?) {
                    }

                    override fun onFetchedUser(success: Boolean) {
                        // no user submitted
                        if (!success) {
                            return
                        }

                        // show confirm dialog
                        AlertDialog.Builder(this@ItemMessageActivity)
                                .setTitle("Do you accept the take request?")
                                .setMessage("${item?.userTaken?.userFullName()} would like to take the item.")
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
            else {
                if (!TextUtils.isEmpty(item?.userIdTaken)) {
                    // request sent
                    but_take.text = "Request Sent"
                    but_take.setTextColor(Color.WHITE)
                    but_take.setBackgroundResource(R.drawable.bg_item_status_but_round_requested)
                }
                else {
                    // normal
                    but_take.text = "Take Item"
                    but_take.setTextColor(ContextCompat.getColor(this, R.color.colorTheme))
                    but_take.setBackgroundResource(R.drawable.bg_item_status_but_round)
                }
            }
        }
    }

    private fun acceptTakeRequest() {
        if (item == null) {
            return
        }

        // update item status
        item!!.taken = true
        item!!.saveToDatabaseChild(Item.FIELD_TAKEN, true)

        // send notification to user
        val user = User.currentUser!!
        val newNotification = Notification(notificationType = Notification.NOTIFICATION_TOOK)
        newNotification.itemId = item!!.id
        newNotification.userId = user.id

        userTo?.addNotification(newNotification)

        // send accept message
        val newMsg = Message()
        newMsg.addMessageTo(item!!, userToId!!, "", Message.MESSAGE_TYPE_ACCEPT)

        updateTakeButton()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.but_take -> {
                // already submitted take
                if (!TextUtils.isEmpty(item?.userIdTaken)) {
                    return
                }

                // return if deleted
                if (checkItemRemoval()) {
                    return
                }

                doSendRequest()

                updateTakeButton()
            }

            // send message
            R.id.imgview_comment_send -> {
                doSendMessage()
            }
        }
    }

    private fun doSendRequest() {
        // if item is his own, skip
        if (item?.userId == User.currentUser!!.id) {
            return
        }

        // send request
        item?.userIdTaken = User.currentUser!!.id
        item?.saveToDatabaseChild(Item.FIELD_USER_TAKEN, User.currentUser!!.id)

        val newMsg = Message()
        newMsg.addMessageTo(item!!, userToId!!, "", Message.MESSAGE_TYPE_REQUEST)
    }

    /**
     * Send message to owner
     */
    private fun doSendMessage() {
        val strMessage = edit_message.text.toString()
        if (Utils.isStringEmpty(strMessage)) {
            return
        }
        if (item == null) {
            return
        }

        val newMsg = Message()
        newMsg.addMessageTo(item!!, userToId!!, strMessage)

        // clear edit & hide keyboard
        edit_message.setText("")
        edit_message.clearFocus()
        Utils.hideKeyboard(this)
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

    private fun monitorItemUpdate() {
        //
        // monitor item update
        //
        val database = FirebaseDatabase.getInstance().reference
        val query = database.child(Item.TABLE_NAME + "/" + itemId)

        // Read from the database
        query.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot?, previousChildName: String?) {
                if (dataSnapshot?.key == Item.FIELD_USER_TAKEN) {
                    item?.userIdTaken = dataSnapshot.value as String
                    updateTakeButton()
                }
                else if (dataSnapshot?.key == Item.FIELD_TAKEN) {
                    item?.taken = dataSnapshot.value as Boolean
                    updateTakeButton()
                }
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot?, previousChildName: String?) {
                if (dataSnapshot?.key == BaseModel.FIELD_DELETED_AT) {
                    item?.deletedAt = dataSnapshot.value as Long
                }
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
            }
        })

    }

    //
    // Item.FetchDatabaseListener
    //
    override fun onFetchedItem(i: Item?) {
        item = i
        initItemView()
        monitorItemUpdate()
    }
    override fun onFetchedUser(success: Boolean) {
        // update user info
        setTitle(item?.userPosted?.userFullName())
    }
    override fun onFetchedComments(success: Boolean) {
    }
}
