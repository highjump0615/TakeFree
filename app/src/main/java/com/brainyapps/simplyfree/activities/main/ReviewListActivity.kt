package com.brainyapps.simplyfree.activities.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.adapters.main.ReviewListAdapter
import com.brainyapps.simplyfree.helpers.UserDetailHelper
import com.brainyapps.simplyfree.models.Review
import com.brainyapps.simplyfree.models.User
import kotlinx.android.synthetic.main.activity_review_list.*

class ReviewListActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {

    lateinit var user: User
    var adapter: ReviewListAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_list)

        setNavbar("Reviews", true)

        // get user from intent
        val bundle = intent.extras
        user = bundle?.getParcelable<User>(UserDetailHelper.KEY_USER)!!

        // init list
        val layoutManager = LinearLayoutManager(this)
        list.setLayoutManager(layoutManager)

        this.adapter = ReviewListAdapter(this, user.reviews)
        list.setAdapter(this.adapter)
        list.setItemAnimator(DefaultItemAnimator())

        this.swiperefresh.setOnRefreshListener(this)

        // load data
        getReviews(false, true)
    }

    /**
     * get review data
     */
    private fun getReviews(bRefresh: Boolean, bAnimation: Boolean) {

        if (!bRefresh) {
            if (!this.swiperefresh.isRefreshing) {
                this.swiperefresh.isRefreshing = true
            }
        }

        user.fetchReviews(object: User.FetchDatabaseListener {
            override fun onFetchedReviews() {
                // clear
                if (bAnimation) {
                    this@ReviewListActivity.adapter!!.notifyItemRangeRemoved(0, user.reviews.count())
                }

                updateList(bAnimation)

                if (user.reviews.isEmpty()) {
                    this@ReviewListActivity.text_empty_notice.visibility = View.VISIBLE
                }
                else {
                    this@ReviewListActivity.text_empty_notice.visibility = View.GONE
                }
            }

            override fun onFetchedUser(u: User?, success: Boolean) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onFetchedItems() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    fun updateList(bAnimation: Boolean) {
        stopRefresh()

        if (bAnimation) {
            adapter!!.notifyItemRangeInserted(0, user.reviews.count())
        }
        else {
            adapter!!.notifyDataSetChanged()
        }
    }

    fun stopRefresh() {
        this.swiperefresh.isRefreshing = false
    }

    override fun onRefresh() {
        getReviews(true, false)
    }
}
