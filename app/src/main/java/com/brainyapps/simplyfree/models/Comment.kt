package com.brainyapps.simplyfree.models

import com.google.firebase.database.Exclude

/**
 * Created by Administrator on 4/10/18.
 */

class Comment() : BaseModel() {

    companion object {
        //
        // table info
        //
        const val TABLE_NAME = "comments"
    }

    var userId = ""
    var content = ""

    // user posted
    @get:Exclude
    var userPosted: User? = null

    override fun tableName() = TABLE_NAME

    fun fetchUser(fetchListener: FetchDatabaseListener?) {
        User.readFromDatabase(userId, object: User.FetchDatabaseListener {
            override fun onFetchedReviews() {
            }

            override fun onFetchedItems() {
            }

            override fun onFetchedUser(u: User?, success: Boolean) {
                userPosted = u

                fetchListener?.onFetchedUser(u != null)
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