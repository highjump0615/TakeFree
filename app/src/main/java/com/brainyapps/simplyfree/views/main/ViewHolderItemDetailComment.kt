package com.brainyapps.simplyfree.views.main

import android.content.Context
import android.opengl.Visibility
import android.view.View
import com.brainyapps.e2fix.views.admin.ViewHolderBase
import kotlinx.android.synthetic.main.layout_item_comment_item.view.*

/**
 * Created by Administrator on 4/9/18.
 */
class ViewHolderItemDetailComment(itemView: View, ctx: Context) : ViewHolderBase(itemView, ctx) {

    init {
    }

    fun showCount(show: Boolean) {
        if (show) {
            itemView.layout_count.visibility = View.VISIBLE
        }
        else {
            itemView.layout_count.visibility = View.GONE
        }
    }
}