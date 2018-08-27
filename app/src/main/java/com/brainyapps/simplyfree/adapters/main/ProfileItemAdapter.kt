package com.brainyapps.simplyfree.adapters.main

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.admin.AdminUserDetailActivity
import com.brainyapps.simplyfree.activities.main.ItemDetailActivity
import com.brainyapps.simplyfree.adapters.BaseItemAdapter
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Globals
import com.brainyapps.simplyfree.views.main.ViewHolderProfileItem


/**
 * Created by Administrator on 2/19/18.
 */

class ProfileItemAdapter(val ctx: Context, private val aryItem: ArrayList<Item>)
    : BaseItemAdapter(ctx) {

    companion object {
        const val ITEM_VIEW_TYPE_ITEM = 1
    }

    var deleteListener: DeletePostListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var vhRes = makeViewHolder(parent, viewType)

        if (vhRes == null) {
            // create a new view
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_profile_item, parent, false)
            // set the view's size, margins, paddings and layout parameters

            val vh = ViewHolderProfileItem(v, ctx)
            vh.setOnItemClickListener(this)

            vhRes = vh
        }

        return vhRes
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderProfileItem) {
            holder.fillContent(aryItem[position])
        }
        else {
        }
    }

    override fun getItemCount(): Int {
        var nCount = aryItem.size

        if (mbNeedMore) {
            nCount++
        }

        return nCount
    }

    override fun getItemViewType(position: Int): Int {

        return if (position < aryItem.size) {
            ITEM_VIEW_TYPE_ITEM
        }
        else {
            ITEM_VIEW_TYPE_FOOTER
        }
    }

    override fun onItemClick(view: View?, position: Int) {
        when (view?.id) {
            R.id.but_delete -> {
                // show confirm dialog
                AlertDialog.Builder(context)
                        .setTitle("Are you sure to delete this item?")
                        .setMessage("The deleted item cannot be restored")
                        .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                            // delete item
                            User.currentUser?.deleteItem(aryItem[position].id)

                            aryItem.removeAt(position)
                            notifyItemRemoved(position)

                            // update list
                            deleteListener?.onDeletedPost()
                        })
                        .setNegativeButton(android.R.string.no, DialogInterface.OnClickListener { dialog, which ->
                        })
                        .create()
                        .show()
            }
            else -> {
                Globals.selectedItem = aryItem[position]

                val intent = Intent(this.context, ItemDetailActivity::class.java)
                this.context!!.startActivity(intent)
            }
        }
    }

    /**
     * interface for delete post
     */
    interface DeletePostListener {
        fun onDeletedPost()
    }
}
