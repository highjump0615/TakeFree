package com.brainyapps.simplyfree.views.admin

import android.content.Context
import android.view.View
import com.brainyapps.e2fix.views.admin.ViewHolderBase
import com.brainyapps.simplyfree.models.User
import kotlinx.android.synthetic.main.layout_admin_user_list_item.view.*

/**
 * Created by Administrator on 4/9/18.
 */
class ViewHolderUserItem(itemView: View, ctx: Context) : ViewHolderBase(itemView, ctx) {

    fun fillContent(data: User) {
        itemView.text_name.text = data.userFullName()
    }
}