package com.brainyapps.simplyfree.helpers

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.views.ViewRateStar
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by Administrator on 3/2/18.
 */
class UserDetailHelper(private val contentView: View) {

    private var textName: TextView = contentView.findViewById<TextView>(R.id.text_username)
    private var imgViewPhoto: ImageView = contentView.findViewById<ImageView>(R.id.imgview_user)

    companion object {
        const val KEY_USER = "user"
        const val KEY_USER_ID = "user_id"
    }

    fun fillUserInfoSimple(user: User?) {
        //
        // show user info
        //

        // name
        textName.text = user?.userFullName()

        // photo
        Glide.with(contentView.context)
                .load(user?.photoUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.user_default).fitCenter())
                .into(imgViewPhoto)
    }

    fun fillUserWithRating(user: User?) {

        fillUserInfoSimple(user)

        // Read from the database for rate
        User.readFromDatabase(user!!.id, object: User.FetchDatabaseListener {
            override fun onFetchedReviews() {
            }

            override fun onFetchedItems() {
            }

            override fun onFetchedUser(u: User?, success: Boolean) {
                if (u?.id == User.currentUser?.id) {
                    User.currentUser?.rating = u?.rating!!
                }

                // rate
                val textRate = contentView.findViewById<TextView>(R.id.text_rate)
                val strRating = String.format("%.1f", u?.rating)
                textRate.text = "${strRating} / 5.0"

                val viewStar = contentView.findViewById<ViewRateStar>(R.id.view_star_user)
                viewStar.updateStar(u?.rating!!)
            }
        })
    }
}