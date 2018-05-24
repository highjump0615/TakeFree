package com.brainyapps.simplyfree.activities.main

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
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

class ItemDetailActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, Item.FetchDatabaseListener {

    companion object {
        const val KEY_ITEM_ID = "item_id"
    }

    var adapter: ItemDetailAdapter? = null

    private lateinit var reportHelper: ReportHelper

    var item: Item? = null

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

    override fun onStart() {
        super.onStart()
    }

    private fun initView() {
        // title
        title = item?.name

        // init list
        list.layoutManager = LinearLayoutManager(this)

        adapter = ItemDetailAdapter(this, item!!)
        list.adapter = this.adapter
        list.itemAnimator = DefaultItemAnimator()

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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.report, menu)

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
                sendComment()
            }
        }
    }

    private fun sendComment() {
        val strComment = edit_comment.text.toString()
        // send comment
        if (TextUtils.isEmpty(strComment)) {
            return
        }

        val newComment = Comment()
        val user = User.currentUser!!

        newComment.content = strComment
        newComment.userId = user.id
        newComment.userPosted = user
        item!!.comments.add(0, newComment)

        // save data
        item?.saveToDatabaseChild(Item.FIELD_COMMENTS, item!!.comments)

        // update list
        adapter!!.notifyDataSetChanged()

        // clear edit & hide keyboard
        edit_comment.setText("")
        edit_comment.clearFocus()
        Utils.hideKeyboard(this)

        // add notification
        item!!.userPosted?.let {
            val newNotification = Notification(notificationType = Notification.NOTIFICATION_COMMENT)
            newNotification.itemId = item!!.id

            it.addNotification(newNotification)
        }
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
        for (comment in item!!.comments) {
            comment.fetchUser(object : Comment.FetchDatabaseListener {
                override fun onFetchedUser(success: Boolean) {
                    // update list
                    adapter!!.notifyDataSetChanged()
                }
            })
        }

        stopRefresh()
    }
}
