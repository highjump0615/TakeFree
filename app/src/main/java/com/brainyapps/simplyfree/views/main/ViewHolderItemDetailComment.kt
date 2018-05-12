package com.brainyapps.simplyfree.views.main

import android.content.Context
import android.view.View
import com.brainyapps.e2fix.views.admin.ViewHolderBase
import com.brainyapps.simplyfree.activities.UserDetailHelper
import com.brainyapps.simplyfree.models.Comment
import kotlinx.android.synthetic.main.layout_item_comment_item.view.*

/**
 * Created by Administrator on 4/9/18.
 */
class ViewHolderItemDetailComment(itemView: View, ctx: Context) : ViewHolderBase(itemView, ctx) {

    var helperUser: UserDetailHelper? = null

    init {
        helperUser = UserDetailHelper(itemView)
    }

    fun showCount(show: Boolean, count: Int) {
        if (show) {
            itemView.text_count.visibility = View.VISIBLE
            itemView.text_count.text = "${count} comments"
        }
        else {
            itemView.text_count.visibility = View.GONE
        }
    }

    fun fillContent(data: Comment) {
        // comment
        itemView.text_comment.text = data.content

        // user info
        helperUser!!.fillUserInfoSimple(data.userPosted)
    }
}