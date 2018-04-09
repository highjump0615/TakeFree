package com.brainyapps.e2fix.views.admin

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.brainyapps.simplyfree.utils.SFItemClickListener

/**
 * Created by Administrator on 2/19/18.
 */

open class ViewHolderBase(itemView: View, ctx: Context) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    var context: Context? = null
    internal var mClickListener: SFItemClickListener? = null

    init {
        this.context = ctx
        itemView.setOnClickListener(this)
    }

    fun setOnItemClickListener(listener: SFItemClickListener) {
        mClickListener = listener
    }

    override fun onClick(view: View?) {
        if (mClickListener != null) {
            mClickListener!!.onItemClick(view, layoutPosition)
        }
    }


}
