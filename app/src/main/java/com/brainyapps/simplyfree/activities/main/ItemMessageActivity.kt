package com.brainyapps.simplyfree.activities.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.utils.Globals
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_item_message.*

class ItemMessageActivity : BaseActivity(), Item.FetchDatabaseListener {
    lateinit var item: Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_message)

        setNavbar("", true)

        // get item from common objects
        item = Globals.selectedItem!!
        item.fetchUser(this)

        // item info
        text_item.text = item.name
        Glide.with(this)
                .load(item.photoUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.user_default).fitCenter())
                .into(imgview_photo)
    }

    //
    // Item.FetchDatabaseListener
    //
    override fun onFetchedUser(success: Boolean) {
        // update user info
        setTitle(item.userPosted?.userFullName())
    }
}
