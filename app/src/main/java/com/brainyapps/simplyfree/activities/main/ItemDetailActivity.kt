package com.brainyapps.simplyfree.activities.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import android.widget.TextView
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.adapters.main.ItemDetailAdapter
import com.brainyapps.simplyfree.models.Comment
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Globals
import com.brainyapps.simplyfree.utils.Utils
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_item_detail.*

class ItemDetailActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, Item.FetchDatabaseListener {

    var adapter: ItemDetailAdapter? = null

    lateinit var item: Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        // get item from common objects
        item = Globals.selectedItem!!
        item.fetchUser(this)

        setNavbar("", true, false)

        // title
        toolbar.title = item.name

        // init list
        list.layoutManager = LinearLayoutManager(this)

        this.adapter = ItemDetailAdapter(this, item)
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.report, menu)

        return true
    }

    override fun onRefresh() {
        getComments(true, false)
    }

    // fetch comment user
    private fun getComments(bRefresh: Boolean, bAnimation: Boolean) {

        for (comment in item.comments) {
            comment.fetchUser(object : Comment.FetchDatabaseListener {
                override fun onFetchedUser(success: Boolean) {
                    // update list
                    adapter!!.notifyDataSetChanged()
                }
            })
        }

        stopRefresh()
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

        newComment.content = strComment
        newComment.userId = User.currentUser!!.id
        newComment.userPosted = User.currentUser
        item.comments.add(0, newComment)

        // save data
        item.saveToDatabase()

        // update list
        adapter!!.notifyDataSetChanged()

        // clear edit & hide keyboard
        edit_comment.setText("")
        edit_comment.clearFocus()
        Utils.hideKeyboard(this)
    }

    //
    // Item.FetchDatabaseListener
    //
    override fun onFetchedUser(success: Boolean) {
        adapter!!.notifyItemChanged(0)
    }
}
