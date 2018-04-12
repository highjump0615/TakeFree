package com.brainyapps.simplyfree.views.main

import android.content.Context
import android.view.View
import com.brainyapps.e2fix.views.admin.ViewHolderBase
import com.brainyapps.simplyfree.models.User
import kotlinx.android.synthetic.main.layout_profile_item.view.*

/**
 * Created by Administrator on 4/9/18.
 */
class ViewHolderProfileItem(itemView: View, ctx: Context) : ViewHolderBase(itemView, ctx) {

    init {
        itemView.but_open.setOnClickListener(this)
        itemView.but_delete.setOnClickListener(this)
    }

    fun fillContent(data: User) {

    }
}