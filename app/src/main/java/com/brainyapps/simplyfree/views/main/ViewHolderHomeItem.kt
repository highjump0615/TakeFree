package com.brainyapps.simplyfree.views.main

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.brainyapps.e2fix.views.admin.ViewHolderBase
import com.brainyapps.simplyfree.models.Category
import kotlinx.android.synthetic.main.layout_home_item_list_item.view.*

/**
 * Created by Administrator on 4/9/18.
 */
class ViewHolderHomeItem(itemView: View, ctx: Context) : ViewHolderBase(itemView, ctx) {

    init {
        // init list
        itemView.view_main.setOnClickListener(this)
    }

    fun fillContent(data: Category) {

    }
}