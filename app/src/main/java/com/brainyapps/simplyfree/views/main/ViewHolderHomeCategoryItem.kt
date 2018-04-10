package com.brainyapps.simplyfree.views.main

import android.content.Context
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.brainyapps.e2fix.views.admin.ViewHolderBase
import com.brainyapps.simplyfree.adapters.main.HomeCategoryItemAdapter
import com.brainyapps.simplyfree.models.Category
import com.brainyapps.simplyfree.models.Item
import kotlinx.android.synthetic.main.layout_home_category_list_item.view.*

/**
 * Created by Administrator on 4/9/18.
 */
class ViewHolderHomeCategoryItem(itemView: View, ctx: Context) : ViewHolderBase(itemView, ctx) {

    var adapter: HomeCategoryItemAdapter? = null
    var aryItem = ArrayList<Item>()

    init {
        // init list
        val layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        itemView.list.setLayoutManager(layoutManager)

        for (i in 0..10) {
            aryItem.add(Item())
        }

        this.adapter = HomeCategoryItemAdapter(ctx, this.aryItem)
        itemView.list.setAdapter(this.adapter)
        itemView.list.setItemAnimator(DefaultItemAnimator())
    }

    fun fillContent(data: Category) {
        // set items data
    }
}