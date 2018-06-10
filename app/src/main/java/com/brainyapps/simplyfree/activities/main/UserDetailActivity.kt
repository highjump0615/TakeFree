package com.brainyapps.simplyfree.activities.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.helpers.UserDetailHelper
import com.brainyapps.simplyfree.adapters.main.ProfileItemAdapter
import com.brainyapps.simplyfree.helpers.ReportHelper
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Utils
import kotlinx.android.synthetic.main.activity_user_detail.*

class UserDetailActivity : BaseActivity(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    var adapter: ProfileItemAdapter? = null

    lateinit var user: User
    lateinit var helperUser: UserDetailHelper

    private lateinit var reportHelper: ReportHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        // get user from intent
        val bundle = intent.extras
        user = bundle?.getParcelable<User>(UserDetailHelper.KEY_USER)!!

        setNavbar(user.userFullName(), true)

        layout_review.setOnClickListener(this)

        helperUser = UserDetailHelper(findViewById<View>(android.R.id.content))

        // fill user info
        helperUser.fillUserWithRating(user)

        // init list
        list.layoutManager = LinearLayoutManager(this)

        this.adapter = ProfileItemAdapter(this, user.items)
        list.adapter = this.adapter
        list.itemAnimator = DefaultItemAnimator()

        this.swiperefresh.setOnRefreshListener(this)

        // load data
        Handler().postDelayed({ getItems(true, true) }, 500)

        reportHelper = ReportHelper(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.report, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_report -> {
                reportHelper.addReport(user)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * get Item data
     */
    private fun getItems(bRefresh: Boolean, bAnimation: Boolean) {

        if (bAnimation) {
            if (!this.swiperefresh.isRefreshing) {
                this.swiperefresh.isRefreshing = true
            }
        }

        user.fetchItems(object: User.FetchDatabaseListener {
            override fun onFetchedReviews() {
            }

            override fun onFetchedUser(u: User?, success: Boolean) {
            }

            override fun onFetchedItems() {
                stopRefresh()

                if (bAnimation) {
                    adapter!!.notifyItemRangeRemoved(0, user.items.count())
                }

                // show empty notice
                if (user.items.isEmpty()) {
                    text_empty_notice.visibility = View.VISIBLE
                }
                else {
                    text_empty_notice.visibility = View.GONE
                }

                if (bAnimation) {
                    adapter!!.notifyItemRangeInserted(0, user.items.count())
                }
                else {
                    adapter!!.notifyDataSetChanged()
                }
            }
        })

    }

    fun stopRefresh() {
        this.swiperefresh.isRefreshing = false
    }

    override fun onRefresh() {
        getItems(true, false)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.layout_review -> {
                val intent = Intent(this, ReviewListActivity::class.java)
                intent.putExtra(UserDetailHelper.KEY_USER, user)
                startActivity(intent)
            }
        }
    }
}
