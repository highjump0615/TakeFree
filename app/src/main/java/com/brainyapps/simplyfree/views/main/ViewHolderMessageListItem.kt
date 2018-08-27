package com.brainyapps.simplyfree.views.main

import android.content.Context
import android.view.View
import com.brainyapps.simplyfree.helpers.UserDetailHelper
import com.brainyapps.simplyfree.models.Message
import com.brainyapps.simplyfree.utils.FontManager
import com.brainyapps.simplyfree.utils.Utils
import com.brainyapps.simplyfree.views.admin.ViewHolderBase
import kotlinx.android.synthetic.main.layout_message_list_item.view.*
import java.util.*

/**
 * Created by Administrator on 4/9/18.
 */
class ViewHolderMessageListItem(itemView: View, ctx: Context) : ViewHolderBase(itemView, ctx) {

    private var helperUser: UserDetailHelper = UserDetailHelper(itemView)

    init {
        // init icon
        val iconFont = FontManager.getTypeface(ctx, FontManager.FONTAWESOME)
        FontManager.markAsIconContainer(itemView.but_delete, iconFont)

        itemView.swipe.isClickToClose = true

        itemView.but_delete.setOnClickListener(this)
        itemView.imgview_user.setOnClickListener(this)
        itemView.text_username.setOnClickListener(this)

        // surface view
        itemView.layout_surface.setOnClickListener(this)
    }

    fun fillContent(data: Message) {
        // user info
        helperUser.fillUserInfoSimple(data.targetUser)

        // message
        if (data.type == Message.MESSAGE_TYPE_CHAT) {
            itemView.text_msg.text = data.text
        }
        else {
            itemView.text_msg.text = data.messageNoticeText()
        }

        // time
        itemView.text_time.text = Utils.getFormattedDateTime(Date(data.createdAt))
    }
}