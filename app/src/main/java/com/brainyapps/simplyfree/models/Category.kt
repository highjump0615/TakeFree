package com.brainyapps.simplyfree.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Administrator on 4/10/18.
 */

class Category() : BaseModel(), Parcelable {

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<Category> {
            override fun createFromParcel(parcel: Parcel): Category {
                return Category(parcel)
            }

            override fun newArray(size: Int): Array<Category?> {
                return arrayOfNulls(size)
            }
        }
    }

    var name = ""

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

}