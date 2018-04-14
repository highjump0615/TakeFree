package com.brainyapps.simplyfree.activities.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.adapters.main.ReviewListAdapter
import com.brainyapps.simplyfree.models.Review
import kotlinx.android.synthetic.main.activity_review_list.*

class ReviewListActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {

    var aryReview = ArrayList<Review>()

    var adapter: ReviewListAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_list)

        setNavbar("Reviews", true)

        // init list
        val layoutManager = LinearLayoutManager(this)
        list.setLayoutManager(layoutManager)

        this.adapter = ReviewListAdapter(this, aryReview)
        list.setAdapter(this.adapter)
        list.setItemAnimator(DefaultItemAnimator())

        this.swiperefresh.setOnRefreshListener(this)

        // load data
        Handler().postDelayed({ getReviews(true, true) }, 500)
    }

    /**
     * get review data
     */
    fun getReviews(bRefresh: Boolean, bAnimation: Boolean) {

        if (bAnimation) {
            if (!this.swiperefresh.isRefreshing) {
                this.swiperefresh.isRefreshing = true
            }
        }

//        val database = FirebaseDatabase.getInstance().reference
//        var query = database.child(User.TABLE_NAME).orderByChild(BaseModel.FIELD_DATE)
//
//        if (this.arguments!!.getInt(ARG_USER_LIST_TYPE) == AdminUserActivity.USER_BANNED) {
//            query = database.child(User.TABLE_NAME).orderByChild(User.FIELD_BANNED).equalTo(true)
//        }
//
//        query.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
        stopRefresh()
//
//                var bEmpty = false
//                // if empty, use animation for add
//                if (aryUser.isEmpty()) {
//                    bEmpty = true
//                }
//
        if (bAnimation) {
            this@ReviewListActivity.adapter!!.notifyItemRangeRemoved(0, aryReview.count())
        }
        aryReview.clear()

        for (i in 0..10) {
            aryReview.add(Review())
        }
//
//                if (!dataSnapshot.exists()) {
//                    this@AdminUserFragment.text_empty_notice.visibility = View.VISIBLE
//                }
//
//                for (userItem in dataSnapshot.children) {
//                    val user = userItem.getValue(User::class.java)
//                    user!!.id = userItem.key
//                    if (user.type == User.USER_TYPE_ADMIN) {
//                        continue
//                    }
//
//                    aryUser.add(user)
//                }
//
        if (bAnimation) {
            this@ReviewListActivity.adapter!!.notifyItemRangeInserted(0, aryReview.count())
        }
        else {
            this@ReviewListActivity.adapter!!.notifyDataSetChanged()
        }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                stopRefresh()
//            }
//        })
    }

    fun stopRefresh() {
        this.swiperefresh.isRefreshing = false
    }

    override fun onRefresh() {
        getReviews(true, false)
    }
}
