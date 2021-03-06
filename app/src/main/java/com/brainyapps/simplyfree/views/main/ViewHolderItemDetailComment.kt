package com.brainyapps.simplyfree.views.main

import android.content.Context
import android.view.View
import com.brainyapps.simplyfree.helpers.UserDetailHelper
import com.brainyapps.simplyfree.models.Comment
import com.brainyapps.simplyfree.views.admin.ViewHolderBase
import kotlinx.android.synthetic.main.layout_item_comment_item.view.*

/**
 * Created by Administrator on 4/9/18.
 */
class ViewHolderItemDetailComment(itemView: View, ctx: Context) : ViewHolderBase(itemView, ctx) {

    var helperUser: UserDetailHelper? = null

    init {
        helperUser = UserDetailHelper(itemView)
    }

    fun fillContent(data: Comment) {
        // comment
        itemView.text_comment.text = data.content

        // user info
        helperUser!!.fillUserInfoSimple(data.userPosted)
    }
}