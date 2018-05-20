package com.brainyapps.simplyfree.views.main

import android.content.Context
import android.view.View
import com.brainyapps.e2fix.views.admin.ViewHolderBase
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.Review
import com.brainyapps.simplyfree.models.User
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.layout_item_review_list_item.view.*

/**
 * Created by Administrator on 4/9/18.
 */
class ViewHolderReview(itemView: View, ctx: Context) : ViewHolderBase(itemView, ctx) {

    init {
    }

    fun fillContent(data: Review) {
        val item = data.itemRelated!!

        // name
        itemView.text_name.text = item.name

        // photo
        Glide.with(context!!)
                .load(item.photoUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.img_item_default).fitCenter())
                .into(itemView.imgview_photo)

        // rate
        itemView.view_star_rate.updateStar(data.rate)

        // review
        itemView.text_review.text = data.review
    }
}