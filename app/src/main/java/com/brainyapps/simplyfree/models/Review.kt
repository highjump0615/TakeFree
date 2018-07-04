package com.brainyapps.simplyfree.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

/**
 * Created by Administrator on 4/10/18.
 */

class Review() : BaseModel() {

    companion object {
        //
        // table info
        //
        const val TABLE_NAME = "reviews"
    }

    override fun tableName() = TABLE_NAME

    var userId = ""
    @get:Exclude
    var user: User? = null

    var itemId = ""
    // item related
    @get:Exclude
    var itemRelated: Item? = null

    var rate = 0.0
    var review = ""
}