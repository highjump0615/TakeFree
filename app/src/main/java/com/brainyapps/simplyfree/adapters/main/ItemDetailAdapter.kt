package com.brainyapps.simplyfree.adapters.main

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.helpers.UserDetailHelper
import com.brainyapps.simplyfree.activities.main.ItemMessageActivity
import com.brainyapps.simplyfree.activities.main.UserDetailActivity
import com.brainyapps.simplyfree.adapters.BaseItemAdapter
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Globals
import com.brainyapps.simplyfree.views.main.ViewHolderItemDetailComment
import com.brainyapps.simplyfree.views.main.ViewHolderItemDetailCount
import com.brainyapps.simplyfree.views.main.ViewHolderItemDetailItem

/**
 * Created by Administrator on 2/19/18.
 */

class ItemDetailAdapter(val ctx: Context, private val item: Item)
    : BaseItemAdapter(ctx) {

    companion object {
        const val ITEM_VIEW_TYPE_ITEM = 1
        const val ITEM_VIEW_TYPE_COUNT = 2
        const val ITEM_VIEW_TYPE_COMMENT = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var vhRes = makeViewHolder(parent, viewType)

        if (viewType == ITEM_VIEW_TYPE_ITEM) {
            // create a new view
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_detail_content, parent, false)
            // set the view's size, margins, paddings and layout parameters

            val vh = ViewHolderItemDetailItem(v, ctx)
            vh.setOnItemClickListener(this)
            vhRes = vh
        }
        else if (viewType == ITEM_VIEW_TYPE_COUNT) {
            // create a new view
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_comment_count, parent, false)
            // set the view's size, margins, paddings and layout parameters

            val vh = ViewHolderItemDetailCount(v, ctx)
            vhRes = vh
        }
        else if (viewType == ITEM_VIEW_TYPE_COMMENT) {
            // create a new view
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_comment_item, parent, false)
            // set the view's size, margins, paddings and layout parameters

            val vh = ViewHolderItemDetailComment(v, ctx)
            vhRes = vh
        }

        return vhRes!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderItemDetailItem) {
            holder.fillContent(item)
        }
        else if (holder is ViewHolderItemDetailCount) {
            holder.showCount(item.comments.size)
        }
        else if (holder is ViewHolderItemDetailComment) {
            holder.fillContent(item.comments[position - 2])
        }
    }

    override fun getItemCount(): Int {
        // item
        var nCount = 1

        // comment
        if (item.comments.size > 0) {
            nCount++
        }
        nCount += item.comments.size

        if (mbNeedMore) {
            nCount++
        }

        return nCount
    }

    override fun getItemViewType(position: Int): Int {

        return if (position == 0) {
            ITEM_VIEW_TYPE_ITEM
        }
        else if (position == 1) {
            ITEM_VIEW_TYPE_COUNT
        }
        else if (position < item.comments.size + 2) {
            ITEM_VIEW_TYPE_COMMENT
        }
        else {
            ITEM_VIEW_TYPE_FOOTER
        }
    }

    override fun onItemClick(view: View?, position: Int) {
        when (view!!.id) {
            // user profile
            R.id.layout_user -> {
                item.userPosted.let {
                    // go to profile page for other users only
                    if (it?.id!!.equals(User.currentUser?.id)) {
                        return
                    }

                    val intent = Intent(context, UserDetailActivity::class.java)
                    intent.putExtra(UserDetailHelper.KEY_USER, it)
                    context!!.startActivity(intent)
                }
            }

            // message to user
            R.id.but_message -> {
                val intent = Intent(this.context, ItemMessageActivity::class.java)
                Globals.selectedItem = item
                this.context!!.startActivity(intent)
            }
        }
    }
}
