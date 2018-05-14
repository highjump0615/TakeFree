package com.brainyapps.simplyfree.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.*
import java.util.*

/**
 * Created by Administrator on 4/10/18.
 */
class Item() : BaseModel(), Parcelable {

    companion object {
        //
        // table info
        //
        const val TABLE_NAME = "items"
        const val FIELD_USER = "userId"
        const val FIELD_TAKEN = "isTaken"
        const val FIELD_COMMENTS = "comments"

        @JvmField
        val CREATOR = object : Parcelable.Creator<Item> {
            override fun createFromParcel(parcel: Parcel): Item {
                return Item(parcel)
            }

            override fun newArray(size: Int): Array<Item?> {
                return arrayOfNulls(size)
            }
        }
    }

    var name = ""
    var description = ""
    var photoUrl = ""
    var category = 0
    var condition = 0
    var userId = ""
    var userIdTaken = ""
    var isTaken = false

    // user posted
    @get:Exclude
    var userPosted: User? = null

    @get:Exclude
    var userTaken: User? = null

    var comments = ArrayList<Comment>()

    override fun tableName() = TABLE_NAME

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()

        readFromParcelBase(parcel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)

        writeToParcelBase(parcel, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    /**
     * fetch posted user
     */
    fun fetchUser(fetchListener: FetchDatabaseListener?) {
        if (userPosted != null) {
            fetchListener?.onFetchedUser(true)
            return
        }

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
     * fetch user taken
     */
    fun fetchUserTaken(fetchListener: FetchDatabaseListener?) {
        if (userTaken != null) {
            fetchListener?.onFetchedUser(true)
            return
        }

        User.readFromDatabase(userIdTaken, object: User.FetchDatabaseListener {
            override fun onFetchedNotifications() {
            }

            override fun onFetchedItems() {
            }

            override fun onFetchedUser(user: User?, success: Boolean) {
                userTaken = user

                fetchListener?.onFetchedUser(user != null)
            }
        })
    }

    /**
     * fetch comments of the item
     */
    fun fetchComments(fetchListener: FetchDatabaseListener) {
        val database = FirebaseDatabase.getInstance().reference.child(Item.TABLE_NAME + "/" + id)
        val query = database.child(Item.FIELD_COMMENTS)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                comments.clear()

                for (itemComment in dataSnapshot.children) {
                    val comment = itemComment.getValue(Comment::class.java)
                    comment!!.id = itemComment.key

                    comments.add(comment)
                }

                // sort
                Collections.sort(comments, Collections.reverseOrder())

                fetchListener.onFetchedComments(true)
            }

            override fun onCancelled(error: DatabaseError) {
                fetchListener.onFetchedComments(false)
            }
        })
    }

    /**
     * interface for reading from database
     */
    interface FetchDatabaseListener {
        fun onFetchedUser(success: Boolean)
        fun onFetchedComments(success: Boolean)
    }

}