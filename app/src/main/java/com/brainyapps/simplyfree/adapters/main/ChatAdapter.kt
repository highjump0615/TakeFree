package com.brainyapps.simplyfree.adapters.main

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brainyapps.e2fix.views.admin.ViewHolderBase
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.helpers.UserDetailHelper
import com.brainyapps.simplyfree.activities.main.ItemMessageActivity
import com.brainyapps.simplyfree.activities.main.UserDetailActivity
import com.brainyapps.simplyfree.adapters.BaseItemAdapter
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.models.Message
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.views.main.ViewHolderChatItem
import com.brainyapps.simplyfree.views.main.ViewHolderItemDetailComment
import com.brainyapps.simplyfree.views.main.ViewHolderItemDetailItem
import java.util.ArrayList

/**
 * Created by Administrator on 2/19/18.
 */

class ChatAdapter(val ctx: Context, private val aryData: ArrayList<Message>)
    : BaseItemAdapter(ctx) {

    companion object {
        const val CHAT_VIEW_TYPE_FROM = 1
        const val CHAT_VIEW_TYPE_TO = 2
        const val CHAT_VIEW_TYPE_NOTICE = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var vhRes = makeViewHolder(parent, viewType)
        var v: View? = null

        if (viewType == CHAT_VIEW_TYPE_FROM) {
            // create a new view
            v = LayoutInflater.from(parent.context).inflate(R.layout.layout_chat_item_from, parent, false)
            // set the view's size, margins, paddings and layout parameters
        }
        else if (viewType == CHAT_VIEW_TYPE_TO) {
            // create a new view
            v = LayoutInflater.from(parent.context).inflate(R.layout.layout_chat_item_to, parent, false)
        }
        else if (viewType == CHAT_VIEW_TYPE_NOTICE) {
            // create a new view
            v = LayoutInflater.from(parent.context).inflate(R.layout.layout_chat_item_notice, parent, false)
        }

        val vh = ViewHolderChatItem(v!!, ctx)
        vhRes = vh

        return vhRes
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderChatItem) {
            holder.fillContent(aryData[position])
        }
    }

    override fun getItemCount(): Int {
        return aryData.size
    }

    override fun getItemViewType(position: Int): Int {
        val msg = aryData[position]

        return if (msg.type == Message.MESSAGE_TYPE_REQUEST || msg.type == Message.MESSAGE_TYPE_ACCEPT) {
                CHAT_VIEW_TYPE_NOTICE
            }
            else if (User.currentUser!!.id.equals(msg.senderId)) {
                CHAT_VIEW_TYPE_TO
            }
            else {
                CHAT_VIEW_TYPE_FROM
            }
    }
}
