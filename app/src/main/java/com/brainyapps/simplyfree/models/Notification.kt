package com.brainyapps.simplyfree.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Administrator on 4/10/18.
 */

class Notification() : BaseModel() {

    companion object {
        val NOTIFICATION_RATED = 0
        val NOTIFICATION_TOOK = 1
        val NOTIFICATION_COMMENT = 2
    }

    var type: Int = NOTIFICATION_RATED
}