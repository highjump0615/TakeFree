package com.brainyapps.simplyfree.views.main

import android.content.Context
import android.view.View
import com.brainyapps.e2fix.views.admin.ViewHolderBase
import com.brainyapps.simplyfree.models.Message
import com.brainyapps.simplyfree.utils.FontManager
import kotlinx.android.synthetic.main.layout_message_list_item.view.*

/**
 * Created by Administrator on 4/9/18.
 */
class ViewHolderMessageListItem(itemView: View, ctx: Context) : ViewHolderBase(itemView, ctx) {

    init {
        // init icon
        val iconFont = FontManager.getTypeface(ctx, FontManager.FONTAWESOME)
        FontManager.markAsIconContainer(itemView.but_delete, iconFont)

        itemView.but_delete.setOnClickListener(this)
    }

    fun fillContent(data: Message) {
    }
}