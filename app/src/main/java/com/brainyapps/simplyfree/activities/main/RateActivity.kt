package com.brainyapps.simplyfree.activities.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.activities.UserDetailHelper
import com.brainyapps.simplyfree.models.Review
import com.brainyapps.simplyfree.models.User
import kotlinx.android.synthetic.main.activity_rate.*

class RateActivity : BaseActivity(), User.FetchDatabaseListener, View.OnClickListener {

    var user: User? = null
    lateinit var helperUser: UserDetailHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate)

        setNavbar("", true)

        helperUser = UserDetailHelper(findViewById<View>(android.R.id.content))

        // get user from intent
        val bundle = intent.extras
        val userId = bundle?.getString(UserDetailHelper.KEY_USER_ID)!!

        User.readFromDatabase(withId = userId, fetchListener = this)

        but_submit.setOnClickListener(this)
    }

    private fun initView() {
        helperUser.fillUserInfoSimple(user)

        // rate
        text_rate.text = "${user?.rating} / 5.0"
        view_star_user.updateStar(user?.rating!!)

        // title
        setNavbar(user?.userFullName(), true)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.but_submit -> {
                submitReview()
            }
        }
    }

    private fun submitReview() {
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
        if (TextUtils.isEmpty(strReview)) {
            Toast.makeText(this, "Please write review", Toast.LENGTH_SHORT).show()
            return
        }

        // add review
        val newReview = Review()

        newReview.userId = User.currentUser!!.id
        newReview.user = User.currentUser

        newReview.rate = dRate
        newReview.review = strReview

        user?.let {
            // calculate average user rate
            val dSum = it.rating * it.reviews.count() + dRate
            it.rating = dSum / (it.reviews.count() + 1)

            it.reviews.add(newReview)
            it.saveToDatabase()

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFetchedNotifications() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
