package com.brainyapps.simplyfree.models

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
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

            override fun onFetchedNotifications() {
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
        if (userTaken != null) {
            fetchListener?.onFetchedUser(true)
            return
        }

        User.readFromDatabase(userIdTaken, object: User.FetchDatabaseListener {
            override fun onFetchedReviews() {
            }

            override fun onFetchedNotifications() {
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

    fun saveToDatabase(withId: String? = null, key: String? = null, value: Any? = null) {
        if (!TextUtils.isEmpty(withId)) {
            this.id = withId!!
        }

        val database = FirebaseDatabase.getInstance().reference
        val node = database.child(tableName()).child(id)

        if (key != null && value != null) {
            node.child(key).setValue(value)
        }
        else {
            node.child(FIELD_NAME).setValue(name)
            node.child(FIELD_DESC).setValue(description)
            node.child(FIELD_PHOTO_URL).setValue(photoUrl)
            node.child(FIELD_CATEGORY).setValue(category)
            node.child(FIELD_CONDITION).setValue(condition)
            node.child(FIELD_USER).setValue(userId)
            node.child(FIELD_USER_TAKEN).setValue(userIdTaken)
            node.child(FIELD_TAKEN).setValue(taken)
        }

        saveToDatabaseBase(node)
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