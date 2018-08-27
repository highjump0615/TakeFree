package com.brainyapps.simplyfree.adapters

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.utils.SFItemClickListener
import com.brainyapps.simplyfree.views.ViewHolderLoading
import java.util.ArrayList

/**
 * Created by Administrator on 2/19/18.
 */

abstract class BaseItemAdapter(ctx: Context)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(), SFItemClickListener {

    var context: Context? = null
    var mbNeedMore = false

    companion object {
        const val ITEM_VIEW_TYPE_FOOTER = 10
    }

    init {
        this.context = ctx
    }

    fun makeViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {

        var vhRes: RecyclerView.ViewHolder? = null

        if (viewType == ITEM_VIEW_TYPE_FOOTER) {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_item_loading, parent, false)

            val vh = ViewHolderLoading(v)
            vhRes = vh
        }

        return vhRes
    }

    override fun onItemClick(view: View?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
