package com.brainyapps.simplyfree.models

import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import com.brainyapps.simplyfree.activities.main.ItemDetailActivity
import com.brainyapps.simplyfree.activities.main.ItemMessageActivity
import com.brainyapps.simplyfree.activities.main.RateActivity
import com.brainyapps.simplyfree.activities.main.ReviewListActivity
import com.brainyapps.simplyfree.helpers.UserDetailHelper
import com.brainyapps.simplyfree.utils.Globals
import com.brainyapps.simplyfree.utils.Utils
import com.google.firebase.database.Exclude

/**
 * Created by Administrator on 4/10/18.
 */

class Notification() : BaseModel(), Parcelable {

    companion object {
        const val NOTIFICATION_RATED = 0
        const val NOTIFICATION_TOOK = 1
        const val NOTIFICATION_COMMENT = 2

        const val NOTIFICATION_MESSAGE = 3
        const val NOTIFICATION_TAKE_REQUEST = 4

        @JvmField
        val CREATOR: Parcelable.Creator<Notification> = object : Parcelable.Creator<Notification> {
            override fun createFromParcel(parcel: Parcel): Notification {
                return Notification(parcel)
            }

            override fun newArray(size: Int): Array<Notification?> {
                return arrayOfNulls(size)
            }
        }

        //
        // table info
        //
        const val TABLE_NAME = "notifications"
        const val FIELD_READ_AT = "readAt"
        const val FIELD_TYPE = "type"
        const val FIELD_USER = "userId"
        const val FIELD_ITEM = "itemId"

        // push notification
        const val FIELD_MSG = "message"
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

    override fun describeContents(): Int {
        return 0
    }

    constructor(parcel: Parcel) : this() {
        readFromParcelBase(parcel)

        type = parcel.readInt()
        itemId = parcel.readString()
        userId = parcel.readString()
        userPosted = parcel.readParcelable(User::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        writeToParcelBase(parcel, flags)

        parcel.writeInt(type)
        parcel.writeString(itemId)
        parcel.writeString(userId)
        parcel.writeParcelable(userPosted, flags)
    }

    fun getIntentForDetail(ctx: Context?) : Intent {

        Globals.selectedNotification = this

        val intent: Intent?

        if (type == Notification.NOTIFICATION_RATED) {
            intent = Intent(ctx, ReviewListActivity::class.java)
            intent.putExtra(UserDetailHelper.KEY_USER, User.currentUser)
        }
        else if (type == Notification.NOTIFICATION_TOOK) {
            intent = Intent(ctx, RateActivity::class.java)
            intent.putExtra(UserDetailHelper.KEY_USER_ID, userId)
            intent.putExtra(ItemDetailActivity.KEY_ITEM_ID, itemId)
        }
        else if (type == Notification.NOTIFICATION_MESSAGE ||
                type == Notification.NOTIFICATION_TAKE_REQUEST) {
            intent = Intent(ctx, ItemMessageActivity::class.java)
            intent.putExtra(UserDetailHelper.KEY_USER_ID, userId)
            intent.putExtra(ItemMessageActivity.KEY_ITEM_ID, itemId)
        }
        else {
            Globals.selectedItem = null
            intent = Intent(ctx, ItemDetailActivity::class.java)
            intent.putExtra(ItemDetailActivity.KEY_ITEM_ID, itemId)
        }

        return intent
    }

    fun displayDescription() : String {
        var strDesc = "You took an item. Rate owner!"
        val strUserName = userPosted?.userFullName()

        when (type) {
            Notification.NOTIFICATION_RATED -> {
                strDesc = "$strUserName left you a rating"
            }

            Notification.NOTIFICATION_MESSAGE -> {
                strDesc = "$strUserName sent you a message"
            }

            Notification.NOTIFICATION_TAKE_REQUEST -> {
                strDesc = "$strUserName sent request for your item"
            }

            Notification.NOTIFICATION_COMMENT -> {
                // content
                strDesc = "$strUserName commented on your posted item. Check it out!"
            }
        }

        return strDesc
    }

    fun markAsRead(userId: String) {
        readAt = Utils.getServerLongTime()
        saveToDatabaseChild(Notification.FIELD_READ_AT, Utils.getServerLongTime(), userId)
    }
}