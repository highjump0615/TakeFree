package com.brainyapps.simplyfree.adapters.main

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.adapters.BaseItemAdapter
import com.brainyapps.simplyfree.models.Review
import com.brainyapps.simplyfree.views.main.ViewHolderReview


/**
 * Created by Administrator on 2/19/18.
 */

class ReviewListAdapter(val ctx: Context, private val aryReview: ArrayList<Review>)
    : BaseItemAdapter(ctx) {

    companion object {
        val ITEM_VIEW_TYPE_REVIEW = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var vhRes = makeViewHolder(parent, viewType)

        if (vhRes == null) {
            // create a new view
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_review_list_item, parent, false)
            // set the view's size, margins, paddings and layout parameters

            val vh = ViewHolderReview(v, ctx)
            vh.setOnItemClickListener(this)

            vhRes = vh
        }

        return vhRes
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderReview) {
            holder.fillContent(aryReview[position])
        }
        else {
        }
    }

    override fun getItemCount(): Int {
        var nCount = aryReview.size

        if (mbNeedMore) {
            nCount++
        }

        return nCount
    }

    override fun getItemViewType(position: Int): Int {

        return if (position < aryReview.size) {
            ITEM_VIEW_TYPE_REVIEW
        }
        else {
            ITEM_VIEW_TYPE_FOOTER
        }
    }

    override fun onItemClick(view: View?, position: Int) {
    }
}
