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
import com.brainyapps.simplyfree.models.Notification
import java.util.ArrayList
import com.brainyapps.simplyfree.models.Report
import com.brainyapps.simplyfree.views.admin.ViewHolderUserItem
import com.brainyapps.simplyfree.views.main.ViewHolderHomeCategoryItem
import com.brainyapps.simplyfree.views.main.ViewHolderNotification

/**
 * Created by Administrator on 2/19/18.
 */

class NotificationAdapter(val ctx: Context, private val aryData: ArrayList<Notification>)
    : BaseItemAdapter(ctx) {

    companion object {
        val ITEM_VIEW_TYPE_NOTIFICATION = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var vhRes = super.onCreateViewHolder(parent, viewType)

        if (vhRes == null) {
            // create a new view
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_notification_list_item, parent, false)
            // set the view's size, margins, paddings and layout parameters

            val vh = ViewHolderNotification(v, ctx)
            vh.setOnItemClickListener(this)
            vhRes = vh
        }

        return vhRes
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is ViewHolderNotification) {
            holder.fillContent(this.aryData[position])
        }
        else {
        }
    }

    override fun getItemCount(): Int {
        var nCount = aryData.size

        if (mbNeedMore) {
            nCount++
        }

        return nCount
    }

    override fun getItemViewType(position: Int): Int {

        return if (position < aryData.size) {
            ITEM_VIEW_TYPE_NOTIFICATION
        }
        else {
            ITEM_VIEW_TYPE_FOOTER
        }
    }

    override fun onItemClick(view: View?, position: Int) {
    }
}
