package com.brainyapps.simplyfree.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

/**
 * Created by Administrator on 4/10/18.
 */

class Comment() : BaseModel() {

    companion object {
    }

    var userId = ""
    var content = ""

    // user posted
    @get:Exclude
    var userPosted: User? = null

    fun fetchUser(fetchListener: FetchDatabaseListener?) {
        User.readFromDatabase(userId, object: User.FetchDatabaseListener {
            override fun onFetchedNotifications() {
            }

            override fun onFetchedItems() {
            }

            override fun onFetchedUser(user: User?, success: Boolean) {
                userPosted = user

                fetchListener?.onFetchedUser(user != null)
            }
        })
    }

    /**
     * interface for reading from database
     */
    interface FetchDatabaseListener {
        fun onFetchedUser(success: Boolean)
    }
}