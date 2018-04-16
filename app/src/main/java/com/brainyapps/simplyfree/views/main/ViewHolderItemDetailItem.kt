package com.brainyapps.simplyfree.views.main

import android.content.Context
import android.view.View
import com.brainyapps.e2fix.views.admin.ViewHolderBase
import kotlinx.android.synthetic.main.layout_item_detail_content.view.*

/**
 * Created by Administrator on 4/9/18.
 */
class ViewHolderItemDetailItem(itemView: View, ctx: Context) : ViewHolderBase(itemView, ctx) {

    init {
        itemView.but_message.setOnClickListener(this)
    }
}