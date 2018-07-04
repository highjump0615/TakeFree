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
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.models.Review
import com.brainyapps.simplyfree.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_review_list.*
import java.util.*

class ReviewListActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {

    lateinit var user: User
    var adapter: ReviewListAdapter? = null
    var reviews = ArrayList<Review>()


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

        this.adapter = ReviewListAdapter(this, reviews)
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

        val database = FirebaseDatabase.getInstance().reference.child(Review.TABLE_NAME + "/" + user.id)

        var countFound = 0
        var countFetched = 0

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                reviews.clear()

                for (itemReview in dataSnapshot.children) {

                    val review = itemReview.getValue(Review::class.java)
                    review!!.id = itemReview.key

                    countFound++

                    // fetch its user
                    Item.readFromDatabase(review.itemId, object: Item.FetchDatabaseListener {
                        override fun onFetchedItem(i: Item?) {
                            review.itemRelated = i

                            reviews.add(review)
                            countFetched++

                            // if all notification users are fetched
                            if (countFound == countFetched) {
                                // sort
                                Collections.sort(reviews, Collections.reverseOrder())

                                updateList()
                            }
                        }

                        override fun onFetchedUser(success: Boolean) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onFetchedComments(success: Boolean) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }
                    })
                }

                // not found new, fetched callback
                if (countFound == 0) {
                    updateList()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                updateList()
            }
        })
    }

    fun updateList() {
        stopRefresh()

        // clear
        adapter!!.notifyDataSetChanged()

        if (reviews.isEmpty()) {
            text_empty_notice.visibility = View.VISIBLE
        }
        else {
            text_empty_notice.visibility = View.GONE
        }
    }

    fun stopRefresh() {
        this.swiperefresh.isRefreshing = false
    }

    override fun onRefresh() {
        getReviews(true, false)
    }
}
