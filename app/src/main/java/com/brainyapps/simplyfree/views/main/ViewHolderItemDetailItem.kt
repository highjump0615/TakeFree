package com.brainyapps.simplyfree.views.main

import android.content.Context
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.helpers.UserDetailHelper
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Globals
import com.brainyapps.simplyfree.utils.Utils
import com.brainyapps.simplyfree.views.admin.ViewHolderBase
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.layout_item_detail_content.view.*
import java.util.*

/**
 * Created by Administrator on 4/9/18.
 */
class ViewHolderItemDetailItem(itemView: View, ctx: Context) : ViewHolderBase(itemView, ctx) {

    var helperUser: UserDetailHelper? = null

    init {
        itemView.but_message.setOnClickListener(this)
        itemView.layout_user.setOnClickListener(this)

        helperUser = UserDetailHelper(itemView)
    }

    fun fillContent(data: Item) {

        // name
        itemView.text_name.text = data.name

        // date
        val dtCreatedAt = Date(data.createdAt)
        itemView.text_date.text = Utils.getFormattedDate(dtCreatedAt)

        // condition
        val aryStrCondition = context!!.resources.getStringArray(R.array.item_condition_array);
        itemView.text_condition.text = aryStrCondition[data.condition]

        // category
        itemView.text_category.text = Globals.Categories[data.category].name

        // description
        itemView.text_description.text = "Item Description: ${data.description}"

        // photo
        Glide.with(context!!)
                .load(data.photoUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.img_item_default).fitCenter())
                .into(itemView.imgview_photo)

        // user info
        helperUser!!.fillUserInfoSimple(data.userPosted)

        // hide the message button if item is his own
        if (data.userId.equals(User.currentUser!!.id)) {
            itemView.but_message.visibility = View.GONE
        }
    }
}