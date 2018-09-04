package com.brainyapps.simplyfree.views.main

import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.Notification
import com.brainyapps.simplyfree.utils.FontManager
import com.brainyapps.simplyfree.views.admin.ViewHolderBase
import kotlinx.android.synthetic.main.layout_notification_list_item.view.*

/**
 * Created by Administrator on 4/9/18.
 */
class ViewHolderNotification(itemView: View, ctx: Context) : ViewHolderBase(itemView, ctx) {

    init {
        // init icon
        val iconFont = FontManager.getTypeface(ctx, FontManager.FONTAWESOME)
        FontManager.markAsIconContainer(itemView.text_icon, iconFont)
        FontManager.markAsIconContainer(itemView.text_arrow, iconFont)
    }

    fun fillContent(data: Notification) {
        when (data.type) {
            Notification.NOTIFICATION_RATED -> {
                // icon
                itemView.text_icon.text = "\uf005"
            }

            Notification.NOTIFICATION_TOOK -> {
                // icon
                itemView.text_icon.text = "\uf00c"
            }

            Notification.NOTIFICATION_COMMENT -> {
                // icon
                itemView.text_icon.text = "\uf075"
            }

            else -> {
                // icon
                itemView.text_icon.text = "\uf086"
            }
        }

        // content
        itemView.text_content.text = getContentSpannable(data)
    }

    private fun getContentSpannable(data: Notification): SpannableString {
        val strUserName = data.userPosted?.userFullName()
        val strContent = data.displayDescription()

        val spannable = SpannableString(strContent)
        val colorTheme = ContextCompat.getColor(context!!, R.color.colorTheme)
        strUserName?.let {
            spannable.setSpan(ForegroundColorSpan(colorTheme), 0, it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(StyleSpan(android.graphics.Typeface.BOLD), 0, it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannable
    }
}