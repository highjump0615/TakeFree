package com.brainyapps.simplyfree.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Administrator on 4/10/18.
 */

data class Category(val id: Int, val name:String) {

    companion object {
    }

    var items = ArrayList<Item>()
}