package com.brainyapps.simplyfree.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

/**
 * Created by Administrator on 3/24/18.
 */
class User() : BaseModel(), Parcelable {

    companion object {
        val USER_TYPE_ADMIN = 0
        val USER_TYPE_CUSTOMER = 1

        var currentUser: User? = null

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(type)
    }

    override fun describeContents(): Int {
        return 0
    }
}