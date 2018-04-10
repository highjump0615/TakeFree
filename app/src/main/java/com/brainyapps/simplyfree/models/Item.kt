package com.brainyapps.simplyfree.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Administrator on 4/10/18.
 */

class Item() : BaseModel(), Parcelable {

    companion object {

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