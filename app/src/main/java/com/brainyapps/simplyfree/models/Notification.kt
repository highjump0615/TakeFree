package com.brainyapps.simplyfree.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

/**
 * Created by Administrator on 4/10/18.
 */

class Notification() : BaseModel() {

    companion object {
        const val NOTIFICATION_RATED = 0
        const val NOTIFICATION_TOOK = 1
        const val NOTIFICATION_COMMENT = 2

        //
        // table info
        //
        const val TABLE_NAME = "notifications"
        const val FIELD_READ_AT = "readAt"
    }

    var type: Int = NOTIFICATION_RATED

    // target user id
    var userId = ""
    var itemId = ""

    // time when notification has been read
    var readAt: Long? = null

    // user posted
    @get:Exclude
    var userPosted: User? = null

    // item related
    @get:Exclude
    var itemRelated: Item? = null

    override fun tableName() = TABLE_NAME

    constructor(notificationType: Int) : this() {
        type = notificationType
        userId = User.currentUser?.id!!
    }
}