package com.brainyapps.simplyfree.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.firebase.database.*

/**
 * Created by Administrator on 3/24/18.
 */
class User() : BaseModel(), Parcelable {

    companion object {
        val USER_TYPE_ADMIN = 0
        val USER_TYPE_CUSTOMER = 1

        val TAG = User::class.java.getSimpleName()

        var currentUser: User? = null

        fun readFromDatabase(withId: String, fetchListener: FetchDatabaseListener) {

            val database = FirebaseDatabase.getInstance().reference
            val query = database.child(User.TABLE_NAME + "/" + withId)

            // Read from the database
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    val u = dataSnapshot.getValue(User::class.java)
                    u?.id = withId

                    fetchListener.onFetchedUser(u, true)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "failed to read from database.", error.toException())
                    fetchListener.onFetchedUser(null, false)
                }
            })
        }

        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(parcel: Parcel): User {
                return User(parcel)
            }

            override fun newArray(size: Int): Array<User?> {
                return arrayOfNulls(size)
            }
        }

        //
        // table info
        //
        val TABLE_NAME = "users"
        val FIELD_EMAIL = "email"
        val FIELD_TYPE = "type"
        val FIELD_BANNED = "banned"
    }

    var type: Int = USER_TYPE_ADMIN
    var banned: Boolean = false

    var email = ""

    var facebookId = ""

    var firstName = ""
    var lastName = ""
    var photoUrl = ""

    override fun tableName() = TABLE_NAME

    constructor(parcel: Parcel) : this() {
        type = parcel.readInt()
    }

    constructor(id: String) : this() {
        this.id = id
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    /**
     * interface for reading from database
     */
    interface FetchDatabaseListener {
        fun onFetchedUser(user: User?, success: Boolean)
        fun onFetchedReviews()
    }
}