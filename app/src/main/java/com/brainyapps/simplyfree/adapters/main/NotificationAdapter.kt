package com.brainyapps.simplyfree.adapters.main

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.helpers.UserDetailHelper
import com.brainyapps.simplyfree.activities.main.ItemDetailActivity
import com.brainyapps.simplyfree.activities.main.RateActivity
import com.brainyapps.simplyfree.activities.main.ReviewListActivity
import com.brainyapps.simplyfree.activities.main.UserDetailActivity
import com.brainyapps.simplyfree.adapters.BaseItemAdapter
import com.brainyapps.simplyfree.models.Notification
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Globals
import java.util.ArrayList
import com.brainyapps.simplyfree.views.main.ViewHolderNotification

/**
 * Created by Administrator on 2/19/18.
 */

class NotificationAdapter(val ctx: Context, private val aryData: ArrayList<Notification>)
    : BaseItemAdapter(ctx) {

    companion object {
        const val ITEM_VIEW_TYPE_NOTIFICATION = 1
    }

    private fun getAvailableList(): List<Notification> = aryData.filter { element ->
        element.userPosted != null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var vhRes = makeViewHolder(parent, viewType)

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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderNotification) {
            holder.fillContent(getAvailableList()[position])
        }
        else {
        }
    }

    override fun getItemCount(): Int {
        val listNotification = aryData.filter { element ->
            element.userPosted != null
        }
        var nCount = listNotification.size

        if (mbNeedMore) {
            nCount++
        }

        return nCount
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < getAvailableList().size) {
            ITEM_VIEW_TYPE_NOTIFICATION
        }
        else {
            ITEM_VIEW_TYPE_FOOTER
        }
    }

    override fun onItemClick(view: View?, position: Int) {
        val listNotification = getAvailableList()
        val notification = listNotification[position]

        val intent = notification.getIntentForDetail(context)
        context!!.startActivity(intent)
    }
}
