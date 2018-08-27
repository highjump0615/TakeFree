package com.brainyapps.simplyfree.activities.admin

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.helpers.UserDetailHelper
import com.brainyapps.simplyfree.models.User
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_admin_user_detail.*

class AdminUserDetailActivity : BaseActivity() {

    lateinit var user: User
    lateinit var banHelper: AdminBanUserHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_user_detail)

        // get user from intent
        val bundle = intent.extras
        user = bundle?.getParcelable<User>(UserDetailHelper.KEY_USER)!!

        setNavbar(user.userFullName(), true)

        //
        // fill user info
        //

        // photo
        Glide.with(this)
                .load(user.photoUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.user_default).fitCenter())
                .into(imgview_user)

        // name
        edit_name.text = user.userFullName()

        // email
        edit_email.text = user.email

        banHelper = AdminBanUserHelper(this, user)
    }
}
