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
import com.brainyapps.simplyfree.activities.main.ItemDetailActivity
import com.brainyapps.simplyfree.adapters.BaseItemAdapter
import com.brainyapps.simplyfree.models.Item
import java.util.ArrayList
import com.brainyapps.simplyfree.views.main.ViewHolderHomeCategoryItem
import com.brainyapps.simplyfree.views.main.ViewHolderHomeItem

/**
 * Created by Administrator on 2/19/18.
 */

class HomeCategoryItemAdapter(val ctx: Context, private val aryCategory: ArrayList<Item>)
    : BaseItemAdapter(ctx) {

    companion object {
        val ITEM_VIEW_TYPE_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var vhRes = makeViewHolder(parent, viewType)

        if (vhRes == null) {
            // create a new view
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_home_item_list_item, parent, false)
            // set the view's size, margins, paddings and layout parameters

            val vh = ViewHolderHomeItem(v, ctx)
            vh.setOnItemClickListener(this)
            vhRes = vh
        }

        return vhRes
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//            holder.fillContent(this.aryReport[position].userReported!!)
    }

    override fun getItemCount(): Int {
        var nCount = aryCategory.size

        if (mbNeedMore) {
            nCount++
        }

        return nCount
    }

    override fun getItemViewType(position: Int): Int {
        return ITEM_VIEW_TYPE_ITEM
    }

    override fun onItemClick(view: View?, position: Int) {
        val intent = Intent(this.context, ItemDetailActivity::class.java)
//        intent.putExtra(UserDetailHelper.KEY_USER, user)
        this.context!!.startActivity(intent)
    }
}
