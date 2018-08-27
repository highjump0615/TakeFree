package com.brainyapps.simplyfree.views.main

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.main.ItemMessageActivity
import com.brainyapps.simplyfree.models.Message
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.FontManager
import com.brainyapps.simplyfree.views.admin.ViewHolderBase
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.layout_message_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Administrator on 4/9/18.
 */
class ViewHolderChatItem(itemView: View, ctx: Context) : ViewHolderBase(itemView, ctx) {

    private var textMessage: TextView? = itemView.findViewById<TextView>(R.id.text_message)

    private var textNotice: TextView? = itemView.findViewById<TextView>(R.id.text_notice)
    private var textDate: TextView? = itemView.findViewById<TextView>(R.id.text_time)
    private var imgViewUser: CircleImageView? = itemView.findViewById<CircleImageView>(R.id.imgview_user)

    fun fillContent(data: Message) {
        // time
        val dtCreatedAt = Date(data.createdAt)
        // format date time
        val strDate = SimpleDateFormat("hh.mm a MM.dd.yy").format(dtCreatedAt)

        if (data.type == Message.MESSAGE_TYPE_CHAT) {
            val activity = context as ItemMessageActivity
            val userTarget = activity.userTo

            // user photo
            imgViewUser?.let {
                Glide.with(context!!)
                        .load(userTarget?.photoUrl)
                        .apply(RequestOptions.placeholderOf(R.drawable.user_default).fitCenter())
                        .into(it)
            }
            imgViewUser?.setOnClickListener(this)

            // text
            textMessage?.text = data.text

            // time
            textDate?.text = strDate
        }
        else {
            if (data.type == Message.MESSAGE_TYPE_ACCEPT) {
                textNotice?.setTextColor(ContextCompat.getColor(context!!, R.color.colorTheme))
            }
            else {
                textNotice?.setTextColor(ContextCompat.getColor(context!!, R.color.colorGreen))
            }

            textNotice?.text = data.messageNoticeText()
        }
    }
}