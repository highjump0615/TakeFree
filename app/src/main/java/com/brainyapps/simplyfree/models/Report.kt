package com.brainyapps.simplyfree.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

/**
 * Created by Administrator on 4/10/18.
 */

class Report() : BaseModel(), Parcelable {

    companion object {
        //
        // table info
        //
        const val TABLE_NAME = "reports"

        @JvmField
        val CREATOR = object : Parcelable.Creator<Report> {
            override fun createFromParcel(parcel: Parcel): Report {
                return Report(parcel)
            }

            override fun newArray(size: Int): Array<Report?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun tableName() = TABLE_NAME

    var userId = ""
    @get:Exclude
    var user: User? = null

    @get:Exclude
    var userReported: User? = null

    var content = ""

    constructor(parcel: Parcel) : this() {
        userId = parcel.readString()
        user = parcel.readParcelable(User::class.java.classLoader)
        content = parcel.readString()
        userReported = parcel.readParcelable(User::class.java.classLoader)

        readFromParcelBase(parcel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeParcelable(user, flags)
        parcel.writeString(content)
        parcel.writeParcelable(userReported, flags)

        writeToParcelBase(parcel, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

}