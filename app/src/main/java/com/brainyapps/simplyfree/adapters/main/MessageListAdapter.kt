package com.brainyapps.simplyfree.adapters.main

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.main.ItemMessageActivity
import com.brainyapps.simplyfree.activities.main.UserDetailActivity
import com.brainyapps.simplyfree.helpers.UserDetailHelper
import com.brainyapps.simplyfree.models.Message
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Globals
import java.util.ArrayList
import com.brainyapps.simplyfree.utils.SFItemClickListener
import com.brainyapps.simplyfree.views.main.ViewHolderMessageListItem
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.layout_message_list_item.view.*

/**
 * Created by Administrator on 2/19/18.
 */

class MessageListAdapter(val ctx: Context, private val aryData: ArrayList<Message>)
    : RecyclerSwipeAdapter<RecyclerView.ViewHolder>(), SFItemClickListener {

    companion object {
        const val ITEM_VIEW_TYPE_MESSAGE_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        // create a new view
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_message_list_item, parent, false)
        // set the view's size, margins, paddings and layout parameters

        val vh = ViewHolderMessageListItem(v, ctx)
        vh.setOnItemClickListener(this)

        return vh
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderMessageListItem) {
            holder.fillContent(aryData[position])
        }
    }

    override fun getItemCount(): Int {
        val nCount = aryData.size

        return nCount
    }

    override fun getItemViewType(position: Int): Int {
        return ITEM_VIEW_TYPE_MESSAGE_ITEM
    }

    override fun onItemClick(view: View?, position: Int) {
        when (view?.id) {
            // go to message detail
            R.id.layout_surface -> {
                val intent = Intent(ctx, ItemMessageActivity::class.java)
                Globals.selectedItem = null
                intent.putExtra(ItemMessageActivity.KEY_ITEM_ID, aryData[position].itemId)
                intent.putExtra(UserDetailHelper.KEY_USER, aryData[position].targetUser)
                ctx.startActivity(intent)
            }

            // delete
            R.id.but_delete -> {
                // show confirm dialog
                AlertDialog.Builder(ctx)
                        .setTitle("Sure to delete this message?")
                        .setMessage("The deleted message cannot be restored")
                        .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                            // remove from db
                            val database = FirebaseDatabase.getInstance().reference.child(Message.TABLE_NAME)
                            val query = database.child(User.currentUser!!.id)
                                    .child(aryData[position].itemId)
                                    .child(aryData[position].targetUserId)
                            query.child(Message.FIELD_LATEST_MSG).removeValue()

                            // delete message
                            aryData.removeAt(position)
                            notifyItemRemoved(position)
                        })
                        .setNegativeButton(android.R.string.no, DialogInterface.OnClickListener { dialog, which ->
                            // close the drawer
                            val layoutSwipe = view.parent.parent as SwipeLayout
                            layoutSwipe.close()
                        })
                        .create()
                        .show()
            }
        }
    }
}
