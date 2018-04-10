package com.brainyapps.simplyfree.adapters.admin

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.UserDetailHelper
import com.brainyapps.simplyfree.activities.admin.AdminUserDetailActivity
import com.brainyapps.simplyfree.adapters.BaseItemAdapter
import com.brainyapps.simplyfree.views.admin.ViewHolderUserItem
import com.brainyapps.simplyfree.models.User

/**
 * Created by Administrator on 2/19/18.
 */

class AdminUserItemAdapter(val ctx: Context, private val aryUser: ArrayList<User>)
    : BaseItemAdapter(ctx) {

    companion object {
        val ITEM_VIEW_TYPE_USER = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var vhRes = super.onCreateViewHolder(parent, viewType)

        if (vhRes == null) {
            // create a new view
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_admin_user_list_item, parent, false)
            // set the view's size, margins, paddings and layout parameters

            val vh = ViewHolderUserItem(v, ctx)
            vh.setOnItemClickListener(this)
            vhRes = vh
        }

        return vhRes
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is ViewHolderUserItem) {
            holder.fillContent(this.aryUser[position])
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
            ITEM_VIEW_TYPE_USER
        }
        else {
            ITEM_VIEW_TYPE_FOOTER
        }
    }

    override fun onItemClick(view: View?, position: Int) {
//        val user = aryUser[position]

        val intent = Intent(this.context, AdminUserDetailActivity::class.java)
//        intent.putExtra(UserDetailHelper.KEY_USER, user)
        this.context!!.startActivity(intent)
    }
}
