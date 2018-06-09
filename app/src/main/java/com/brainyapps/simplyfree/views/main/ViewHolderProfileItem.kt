package com.brainyapps.simplyfree.views.main

import android.content.Context
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Utils
import com.brainyapps.simplyfree.views.admin.ViewHolderBase
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.layout_profile_item.view.*
import java.util.*

/**
 * Created by Administrator on 4/9/18.
 */
class ViewHolderProfileItem(itemView: View, ctx: Context) : ViewHolderBase(itemView, ctx) {

    init {
        itemView.but_open.setOnClickListener(this)
        itemView.but_delete.setOnClickListener(this)
    }

    fun fillContent(data: Item) {
        // name
        itemView.text_name.text = data.name

        // date
        val dtCreatedAt = Date(data.createdAt)
        itemView.text_date.text = Utils.getFormattedDate(dtCreatedAt)

        // photo
        Glide.with(context!!)
                .load(data.photoUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.img_item_default).fitCenter())
                .into(itemView.imgview_photo)

        // hide delete button on other users
        if (!data.userPosted?.id.equals(User.currentUser?.id)) {
            itemView.but_delete.visibility = View.GONE
        }
    }
}