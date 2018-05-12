package com.brainyapps.simplyfree.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

/**
 * Created by Administrator on 4/10/18.
 */
class Item() : BaseModel(), Parcelable {

    companion object {
        //
        // table info
        //
        const val TABLE_NAME = "items"
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

    // user posted
    @get:Exclude
    var userPosted: User? = null

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

    fun fetchUser(fetchListener: FetchDatabaseListener?) {
        if (userPosted != null) {
            fetchListener?.onFetchedUser(true)
        }

        User.readFromDatabase(userId, object: User.FetchDatabaseListener {
            override fun onFetchedReviews() {
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