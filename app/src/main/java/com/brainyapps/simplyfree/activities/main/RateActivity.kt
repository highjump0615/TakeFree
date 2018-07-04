package com.brainyapps.simplyfree.activities.main

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.helpers.UserDetailHelper
import com.brainyapps.simplyfree.models.Notification
import com.brainyapps.simplyfree.models.Review
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Globals
import com.brainyapps.simplyfree.utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_rate.*

class RateActivity : BaseActivity(), User.FetchDatabaseListener, View.OnClickListener {

    var user: User? = null
    lateinit var helperUser: UserDetailHelper

    var itemId = ""

    var mnReviewCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate)

        setNavbar("", true)

        helperUser = UserDetailHelper(findViewById<View>(android.R.id.content))

        // get user from intent
        val bundle = intent.extras
        val userId = bundle?.getString(UserDetailHelper.KEY_USER_ID)!!

        // item id
        itemId = bundle.getString(ItemDetailActivity.KEY_ITEM_ID)!!

        User.readFromDatabase(withId = userId, fetchListener = this)

        but_submit.setOnClickListener(this)

        getReviewCount(userId)
    }

    private fun initView() {
        helperUser.fillUserWithRating(user)
        
        // title
        setNavbar(user?.userFullName(), true)
    }

    private fun getReviewCount(userId: String) {
        val database = FirebaseDatabase.getInstance().reference.child(Review.TABLE_NAME)
        val query = database.child(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                dataSnapshot?.let {
                    mnReviewCount = it.childrenCount.toInt()
                }
            }
        })
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.but_submit -> {
                submitReview()
            }
        }
    }

    private fun submitReview() {
        val userCurrent = User.currentUser!!

        //
        // check input validate
        //

        // star is not selected
        val dRate = view_star_edit.starValue
        if (dRate <= 0) {
            Toast.makeText(this, "Please select rate", Toast.LENGTH_SHORT).show()
            return
        }

        val strReview = edit_review.text.toString()
        if (Utils.isStringEmpty(strReview)) {
            Toast.makeText(this, "Please write review", Toast.LENGTH_SHORT).show()
            return
        }

        // add review
        val newReview = Review()

        newReview.userId = userCurrent.id
        newReview.user = userCurrent

        newReview.rate = dRate
        newReview.review = strReview

        newReview.itemId = itemId

        user?.let {
            // calculate average user rate
            val dSum = it.rating * mnReviewCount + dRate

            it.rating = dSum / (mnReviewCount + 1)
            it.saveToDatabaseChild(User.FIELD_RATING, it.rating)

            // save to database
            newReview.saveToDatabase(parent = it.id)

            // add notification
            val newNotification = Notification(notificationType = Notification.NOTIFICATION_RATED)

            // save
            it.addNotification(newNotification)

            // mark notification as read
            val notify = Globals.selectedNotification!!
            notify.readAt = Utils.getServerLongTime()
            notify.saveToDatabaseChild(Notification.FIELD_READ_AT, Utils.getServerLongTime(), userCurrent.id)
            userCurrent.notifications.remove(notify)

            // done, back to prev page
            finish()
        }
    }

    //
    // User.FetchDatabaseListener
    //
    override fun onFetchedUser(u: User?, success: Boolean) {
        user = u
        initView()
    }

    override fun onFetchedItems() {
    }

    override fun onFetchedReviews() {
    }
}
