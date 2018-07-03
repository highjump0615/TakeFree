package com.brainyapps.simplyfree.models

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import com.brainyapps.simplyfree.models.User.FetchDatabaseListener
import com.google.firebase.database.*
import java.util.*

/**
 * Created by Administrator on 4/10/18.
 */
class Item() : BaseModel(), Parcelable {

    companion object {
        const val ITEM_CONDITION_EXCELLENT = 0
        const val ITEM_CONDITION_GOOD = 1
        const val ITEM_CONDITION_FAIR = 2
        const val ITEM_CONDITION_BAD = 3

        //
        // table info
        //
        const val TABLE_NAME = "items"
        const val TABLE_NAME_GEOLOCATION = "geolocationsItem"

        const val FIELD_NAME = "name"
        const val FIELD_DESC = "description"
        const val FIELD_PHOTO_URL = "photoUrl"
        const val FIELD_CATEGORY = "category"
        const val FIELD_CONDITION = "condition"
        const val FIELD_USER = "userId"
        const val FIELD_USER_TAKEN = "userIdTaken"
        const val FIELD_TAKEN = "taken"
        const val FIELD_COMMENTS = "comments"

        val TAG = Item::class.java.simpleName

        fun readFromDatabase(withId: String, fetchListener: Item.FetchDatabaseListener) {

            val database = FirebaseDatabase.getInstance().reference
            val query = database.child(Item.TABLE_NAME + "/" + withId)

            // Read from the database
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    val i = dataSnapshot.getValue(Item::class.java)
                    i?.id = withId

                    fetchListener.onFetchedItem(i)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "failed to read from database.", error.toException())
                    fetchListener.onFetchedItem(null)
                }
            })
        }

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
    var taken = false

    // user posted
    @get:Exclude
    var userPosted: User? = null

    @get:Exclude
    var userTaken: User? = null

    @get:Exclude
    var comments = ArrayList<Comment>()

    override fun tableName() = TABLE_NAME

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        description = parcel.readString()
        photoUrl = parcel.readString()
        category = parcel.readInt()
        condition = parcel.readInt()
        userId = parcel.readString()
        userIdTaken = parcel.readString()
        taken = parcel.readByte().toInt() != 0

        readFromParcelBase(parcel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(photoUrl)
        parcel.writeInt(category)
        parcel.writeInt(condition)
        parcel.writeString(userId)
        parcel.writeString(userIdTaken)
        parcel.writeByte((if (taken) 1 else 0).toByte())

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
     * fetch user taken
     */
    fun fetchUserTaken(fetchListener: FetchDatabaseListener?) {
        if (TextUtils.isEmpty(userIdTaken)) {
            fetchListener?.onFetchedUser(false)
            return
        }

        if (userTaken != null) {
            fetchListener?.onFetchedUser(true)
            return
        }

        User.readFromDatabase(userIdTaken, object: User.FetchDatabaseListener {
            override fun onFetchedReviews() {
            }

            override fun onFetchedItems() {
            }

            override fun onFetchedUser(u: User?, success: Boolean) {
                userTaken = u

                fetchListener?.onFetchedUser(u != null)
            }
        })
    }

    /**
     * fetch comments of the item
     */
    fun fetchComments(fetchListener: FetchDatabaseListener) {

        val database = FirebaseDatabase.getInstance().reference.child(Comment.TABLE_NAME)
        val query = database.child(id)

        var nFetchedCount = 0
        var nFetchedUserCount = 0

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                comments.clear()

                for (itemComment in dataSnapshot.children) {
                    val comment = itemComment.getValue(Comment::class.java)
                    comment!!.id = itemComment.key

                    comments.add(comment)
                    nFetchedCount++

                    // Fetch user
                    User.readFromDatabase(comment.userId, object:User.FetchDatabaseListener {
                        override fun onFetchedUser(u: User?, success: Boolean) {
                            comment.userPosted = u
                            nFetchedUserCount++

                            if (nFetchedCount == nFetchedUserCount) {
                                fetchListener.onFetchedComments(true)
                            }
                        }

                        override fun onFetchedItems() {
                        }

                        override fun onFetchedReviews() {
                        }
                    })
                }

                // sort
                Collections.sort(comments, Collections.reverseOrder())

                if (nFetchedCount == 0) {
                    fetchListener.onFetchedComments(true)
                }
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
        fun onFetchedItem(i: Item?)
        fun onFetchedUser(success: Boolean)
        fun onFetchedComments(success: Boolean)
    }

}