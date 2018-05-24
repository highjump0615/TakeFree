package com.brainyapps.simplyfree.views.main

import android.content.Context
import android.view.View
import android.widget.TextView
import com.brainyapps.e2fix.views.admin.ViewHolderBase
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.Message
import com.brainyapps.simplyfree.utils.FontManager
import kotlinx.android.synthetic.main.layout_message_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Administrator on 4/9/18.
 */
class ViewHolderChatItem(itemView: View, ctx: Context) : ViewHolderBase(itemView, ctx) {

    private var textMessage: TextView = itemView.findViewById<TextView>(R.id.text_message)
    private var textDate: TextView = itemView.findViewById<TextView>(R.id.text_time)

    fun fillContent(data: Message) {
        // user photo

        // text
        textMessage.text = data.text

        // time
        val dtCreatedAt = Date(data.createdAt)
        // format date time
        val strDate = SimpleDateFormat("hh.mm a MM.dd.yy").format(dtCreatedAt)
        textDate.text = strDate
    }
}