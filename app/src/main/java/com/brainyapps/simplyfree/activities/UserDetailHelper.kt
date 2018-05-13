package com.brainyapps.simplyfree.activities

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.User
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/**
 * Created by Administrator on 3/2/18.
 */
class UserDetailHelper(private val contentView: View) {

    companion object {
        val KEY_USER = "user"
    }

    init {
    }

    fun fillUserInfoSimple(user: User?) {
        //
        // show user info
        //

        // name
        val text = contentView.findViewById<TextView>(R.id.text_username)
        text.text = user?.userFullName()

        // photo
        val imgview = contentView.findViewById<ImageView>(R.id.imgview_user)
        Glide.with(contentView.context)
                .load(user?.photoUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.user_default).fitCenter())
                .into(imgview)
    }
}