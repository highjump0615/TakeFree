package com.brainyapps.simplyfree.adapters.main

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.admin.AdminUserDetailActivity
import com.brainyapps.simplyfree.activities.main.ItemDetailActivity
import com.brainyapps.simplyfree.adapters.BaseItemAdapter
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.views.main.ViewHolderProfileItem


/**
 * Created by Administrator on 2/19/18.
 */

class ProfileItemAdapter(val ctx: Context, private val aryUser: ArrayList<Item>)
    : BaseItemAdapter(ctx) {

    companion object {
        val ITEM_VIEW_TYPE_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var vhRes = super.onCreateViewHolder(parent, viewType)

        if (vhRes == null) {
            // create a new view
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_profile_item, parent, false)
            // set the view's size, margins, paddings and layout parameters

            val vh = ViewHolderProfileItem(v, ctx)
            vh.setOnItemClickListener(this)

            vhRes = vh
        }

        return vhRes
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is ViewHolderProfileItem) {
        }
        else {
        }
    }

    override fun getItemCount(): Int {
        var nCount = aryUser.size

        if (mbNeedMore) {
            nCount++
        }

        return nCount
    }

    override fun getItemViewType(position: Int): Int {

        return if (position < aryUser.size) {
            ITEM_VIEW_TYPE_ITEM
        }
        else {
            ITEM_VIEW_TYPE_FOOTER
        }
    }

    override fun onItemClick(view: View?, position: Int) {
//        val user = aryUser[position]

        val intent = Intent(this.context, ItemDetailActivity::class.java)
//        intent.putExtra(UserDetailHelper.KEY_USER, user)
        this.context!!.startActivity(intent)
    }
}
