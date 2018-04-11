package com.brainyapps.simplyfree.activities.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.adapters.main.ItemDetailAdapter
import com.brainyapps.simplyfree.models.Comment
import kotlinx.android.synthetic.main.activity_item_detail.*

class ItemDetailActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    var aryComment = ArrayList<Comment>()
    var adapter: ItemDetailAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        setNavbar("", true, false)

        // init list
        val layoutManager = LinearLayoutManager(this)
        list.setLayoutManager(layoutManager)

        this.adapter = ItemDetailAdapter(this, aryComment)
        list.setAdapter(this.adapter)
        list.setItemAnimator(DefaultItemAnimator())

        this.swiperefresh.setOnRefreshListener(this)

        getComments(false, true)

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

    private fun getComments(bRefresh: Boolean, bAnimation: Boolean) {

        if (bAnimation) {
            if (!this.swiperefresh.isRefreshing) {
                this.swiperefresh.isRefreshing = true
            }
        }

        // clear
        if (bAnimation) {
            this@ItemDetailActivity.adapter!!.notifyItemRangeRemoved(1, aryComment.count())
        }
        aryComment.clear()

        // add
        for (i in 0..2) {
            aryComment.add(Comment())
        }

        stopRefresh()

        if (bAnimation) {
            this@ItemDetailActivity.adapter!!.notifyItemRangeInserted(1, aryComment.count())
        }
        else {
            this@ItemDetailActivity.adapter!!.notifyDataSetChanged()
        }
    }

    fun stopRefresh() {
        this.swiperefresh.isRefreshing = false
    }

    override fun onClick(view: View?) {
        when (view?.id) {

        }
    }
}
