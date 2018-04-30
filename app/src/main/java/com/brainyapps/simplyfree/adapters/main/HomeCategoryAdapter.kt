package com.brainyapps.simplyfree.adapters.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.admin.AdminReportDetailActivity
import com.brainyapps.simplyfree.adapters.BaseItemAdapter
import com.brainyapps.simplyfree.models.Category
import java.util.ArrayList
import com.brainyapps.simplyfree.models.Report
import com.brainyapps.simplyfree.views.admin.ViewHolderUserItem
import com.brainyapps.simplyfree.views.main.ViewHolderHomeCategoryItem

/**
 * Created by Administrator on 2/19/18.
 */

class HomeCategoryAdapter(val ctx: Context, private val aryCategory: ArrayList<Category>)
    : BaseItemAdapter(ctx) {

    companion object {
        val ITEM_VIEW_TYPE_CATEGORY = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var vhRes = makeViewHolder(parent, viewType)

        if (vhRes == null) {
            // create a new view
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_home_category_list_item, parent, false)
            // set the view's size, margins, paddings and layout parameters

            val vh = ViewHolderHomeCategoryItem(v, ctx)
            vhRes = vh
        }

        return vhRes
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderHomeCategoryItem) {
//            holder.fillContent(this.aryReport[position].userReported!!)
        }
        else {
        }
    }

    override fun getItemCount(): Int {
        var nCount = aryCategory.size

        if (mbNeedMore) {
            nCount++
        }

        return nCount
    }

    override fun getItemViewType(position: Int): Int {

        return if (position < aryCategory.size) {
            ITEM_VIEW_TYPE_CATEGORY
        }
        else {
            ITEM_VIEW_TYPE_FOOTER
        }
    }
}
