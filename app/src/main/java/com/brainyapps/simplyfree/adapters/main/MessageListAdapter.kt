package com.brainyapps.simplyfree.adapters.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.Message
import java.util.ArrayList
import com.brainyapps.simplyfree.utils.SFItemClickListener
import com.brainyapps.simplyfree.views.main.ViewHolderMessageListItem
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter

/**
 * Created by Administrator on 2/19/18.
 */

class MessageListAdapter(val ctx: Context, private val aryData: ArrayList<Message>)
    : RecyclerSwipeAdapter<RecyclerView.ViewHolder>(), SFItemClickListener {

    companion object {
        val ITEM_VIEW_TYPE_MESSAGE_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        // create a new view
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_message_list_item, parent, false)
        // set the view's size, margins, paddings and layout parameters

        val vh = ViewHolderMessageListItem(v, ctx)
        vh.setOnItemClickListener(this)

        return vh
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderMessageListItem) {
        }
        else {
        }
    }

    override fun getItemCount(): Int {
        val nCount = aryData.size

        return nCount
    }

    override fun getItemViewType(position: Int): Int {
        return ITEM_VIEW_TYPE_MESSAGE_ITEM
    }

    override fun onItemClick(view: View?, position: Int) {
    }
}
