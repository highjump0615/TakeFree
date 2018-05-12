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
    private var aryItem = ArrayList<Item>()

    init {
        // init list
        val layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false)
        itemView.list.layoutManager = layoutManager

        adapter = HomeCategoryItemAdapter(ctx, aryItem)
        itemView.list.adapter = adapter
        itemView.list.itemAnimator = DefaultItemAnimator()
    }

    fun fillContent(data: Category) {
        // set title
        itemView.text_name.text = data.name

        // set items
        aryItem.clear()
        aryItem.addAll(data.items)

        adapter!!.notifyDataSetChanged()
    }
}