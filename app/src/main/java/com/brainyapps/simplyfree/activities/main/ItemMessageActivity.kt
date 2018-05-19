package com.brainyapps.simplyfree.activities.main

import android.content.DialogInterface
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.models.Notification
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Globals
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_item_message.*

class ItemMessageActivity : BaseActivity(), Item.FetchDatabaseListener, View.OnClickListener {

    lateinit var item: Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_message)

        setNavbar("", true)

        // get item from common objects
        item = Globals.selectedItem!!
        item.fetchUser(this)

        // item info
        text_item.text = item.name
        Glide.with(this)
                .load(item.photoUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.user_default).fitCenter())
                .into(imgview_photo)

        // button
        but_take.setOnClickListener(this)

        updateTakeButton()
    }

    private fun updateTakeButton() {
        val user = User.currentUser!!

        if (item.taken) {
            // I took this item
            if (item.userIdTaken.equals(user.id)) {
                but_take.text = "Owner Accepted Request"
                but_take.setTextColor(Color.WHITE)
                but_take.setBackgroundResource(R.drawable.bg_item_status_but_round_accepted)
            }
            // Other one took this
            else {
                but_take.text = "Taken"
                but_take.setTextColor(Color.WHITE)
                but_take.setBackgroundResource(R.drawable.bg_item_status_but_round_taken)
            }
        }
        else if (item.userIdTaken.isNotEmpty()) {
            // request sent
            but_take.text = "Request Sent"
            but_take.setTextColor(Color.WHITE)
            but_take.setBackgroundResource(R.drawable.bg_item_status_but_round_requested)

            // check request
            if (item.userId.equals(user.id)) {
                item.fetchUserTaken(object: Item.FetchDatabaseListener {
                    override fun onFetchedItem(i: Item?) {
                    }

                    override fun onFetchedUser(success: Boolean) {
                        // show confirm dialog
                        AlertDialog.Builder(this@ItemMessageActivity)
                                .setTitle("Do you accept the take request?")
                                .setMessage("${item.userTaken?.userFullName()} would like to take the item.")
                                .setPositiveButton("ACCEPT", DialogInterface.OnClickListener { dialog, which ->
                                    acceptTakeRequest()
                                })
                                .setNegativeButton("DECLINE", DialogInterface.OnClickListener { dialog, which ->
                                })
                                .create()
                                .show()
                    }

                    override fun onFetchedComments(success: Boolean) {
                    }
                })
            }
        }
        else {
            // normal
            but_take.text = "Take Item"
            but_take.setTextColor(ContextCompat.getColor(this, R.color.colorTheme))
            but_take.setBackgroundResource(R.drawable.bg_item_status_but_round)
        }
    }

    private fun acceptTakeRequest() {
        // update item status
        item.taken = true
        item.saveToDatabase()

        // send notification to user
        val newNotification = Notification(notificationType = Notification.NOTIFICATION_TOOK)
        newNotification.itemId = item.id
        item.userPosted?.notifications!!.add(newNotification)
        item.userPosted?.saveToDatabase()

        updateTakeButton()
    }

    //
    // Item.FetchDatabaseListener
    //
    override fun onFetchedItem(i: Item?) {
    }
    override fun onFetchedUser(success: Boolean) {
        // update user info
        setTitle(item.userPosted?.userFullName())
    }
    override fun onFetchedComments(success: Boolean) {
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.but_take -> {
                // already submitted take
                if (item.userIdTaken.isNotEmpty()) {
                    return
                }

                // send request
                item.userIdTaken = User.currentUser!!.id
                item.saveToDatabase()

                updateTakeButton()
            }
        }
    }
}
