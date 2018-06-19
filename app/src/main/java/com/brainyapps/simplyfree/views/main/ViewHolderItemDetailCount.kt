package com.brainyapps.simplyfree.views.main

import android.content.Context
import android.view.View
import com.brainyapps.simplyfree.helpers.UserDetailHelper
import com.brainyapps.simplyfree.models.Comment
import com.brainyapps.simplyfree.views.admin.ViewHolderBase
import kotlinx.android.synthetic.main.layout_item_comment_count.view.*

/**
 * Created by Administrator on 4/9/18.
 */
class ViewHolderItemDetailCount(itemView: View, ctx: Context) : ViewHolderBase(itemView, ctx) {

    init {
    }

    fun showCount(count: Int) {
        if (count == 1) {
            itemView.text_count.text = "${count} comment"
        }
        else {
            itemView.text_count.text = "${count} comments"
        }
    }
}