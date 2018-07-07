package com.brainyapps.simplyfree.activities.main

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.adapters.main.ItemDetailAdapter
import com.brainyapps.simplyfree.helpers.ReportHelper
import com.brainyapps.simplyfree.models.Comment
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.models.Notification
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Globals
import com.brainyapps.simplyfree.utils.Utils
import kotlinx.android.synthetic.main.activity_item_detail.*

class ItemDetailActivity : BaseItemActivity(), SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, Item.FetchDatabaseListener {

    companion object {
        const val KEY_ITEM_ID = "item_id"
    }

    var adapter: ItemDetailAdapter? = null

    private lateinit var reportHelper: ReportHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        // get item from common objects
        item = Globals.selectedItem

        setNavbar("", true, false)

        if (item != null) {
            initView()
        }
        else {
            // get item id from intent
            val bundle = intent.extras
            val itemId = bundle?.getString(KEY_ITEM_ID)!!

            Item.readFromDatabase(withId = itemId, fetchListener = this)
        }

        reportHelper = ReportHelper(this)
    }

    private fun initView() {
        // title
        title = item?.name

        // init list
        list.layoutManager = LinearLayoutManager(this)

        adapter = ItemDetailAdapter(this, item!!)
        list.adapter = this.adapter

        val animator = DefaultItemAnimator()
        animator.supportsChangeAnimations = false
        list.itemAnimator = animator

        this.swiperefresh.setOnRefreshListener(this)

        getComments(false, true)

        // comment
        edit_comment.setOnEditorActionListener { v, actionId, event ->
            sendComment()

            true
        }

        // send button
        imgview_comment_send.setOnClickListener(this)

        // init data
        item?.fetchUser(this)

        // refresh menu
        invalidateOptionsMenu()

        // check if item has been deleted
        checkItemRemoval()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        // report button if item is not his own
        item?.let {
            if (!it.userId.equals(User.currentUser!!.id)) {
                menuInflater.inflate(R.menu.report, menu)
            }
        }

        return true
    }

    override fun onOptionsItemSelected(i: MenuItem?): Boolean {
        when (i?.itemId) {
            R.id.menu_report -> {
                item?.userPosted?.let {
                    reportHelper.addReport(it)
                }
            }
        }

        return super.onOptionsItemSelected(i)
    }

    override fun onRefresh() {
        getComments(true, false)
    }

    // fetch comment user
    private fun getComments(bRefresh: Boolean, bAnimation: Boolean) {
        item?.fetchComments(this)
    }

    private fun stopRefresh() {
        this.swiperefresh.isRefreshing = false
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.imgview_comment_send -> {
                if (!checkItemRemoval()) {
                    sendComment()
                }
            }
        }
    }

    private fun sendComment() {
        val strComment = edit_comment.text.toString()
        // send comment
        if (!Utils.isStringEmpty(strComment)) {
            val newComment = Comment()
            val user = User.currentUser!!

            newComment.content = strComment
            newComment.userId = user.id
            newComment.userPosted = user
            item!!.comments.add(0, newComment)

            // save data
            newComment.saveToDatabase(parent = item!!.id)

            // update list
            if (item!!.comments.count() == 1) {
                // includes comments count cell
                adapter?.notifyItemRangeInserted(1, 2)
            }
            else {
                adapter?.notifyItemChanged(1)
                adapter?.notifyItemInserted(2)
            }

            //
            // if items is not mine, add notification
            //
            if (item!!.userId != user.id) {
                // add notification
                item!!.userPosted?.let {
                    val newNotification = Notification(notificationType = Notification.NOTIFICATION_COMMENT)
                    newNotification.itemId = item!!.id

                    it.addNotification(newNotification)

                    // push notification
                    sendPushNotification(it.token, Notification.NOTIFICATION_COMMENT, item!!.id)
                }
            }
        }

        // clear edit & hide keyboard
        edit_comment.setText("")
        edit_comment.clearFocus()
        Utils.hideKeyboard(this)
    }

    //
    // Item.FetchDatabaseListener
    //
    override fun onFetchedItem(i: Item?) {
        item = i
        initView()
    }
    override fun onFetchedUser(success: Boolean) {
        adapter!!.notifyItemChanged(0)
    }
    override fun onFetchedComments(success: Boolean) {
        if (!success) {
            return
        }

        adapter!!.notifyItemRangeChanged(1, item!!.comments.count() + 1)

        stopRefresh()
    }
}
