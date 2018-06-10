package com.brainyapps.simplyfree.models

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Exclude
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Administrator on 4/10/18.
 */

class Message() : BaseModel() {

    companion object {
        const val MESSAGE_TYPE_CHAT = 0
        const val MESSAGE_TYPE_REQUEST = 1
        const val MESSAGE_TYPE_ACCEPT = 2

        //
        // table info
        //
        const val TABLE_NAME = "messages"
        const val FIELD_CHAT = "chats"
        const val FIELD_LATEST_MSG = "latest"

        const val FIELD_SENDER_ID = "senderId"
        const val FIELD_TEXT = "text"
        const val FIELD_TYPE = "type"
    }

    var text = ""
    var senderId = ""

    @get:Exclude
    var targetUserId = ""

    @get:Exclude
    var targetUser: User? = null

    @get:Exclude
    var itemId = ""

    var type: Int = MESSAGE_TYPE_CHAT

    constructor(value: HashMap<String, Any>) : this() {
        createdAt = value.get(FIELD_DATE) as Long
        senderId = value.get(FIELD_SENDER_ID) as String
        text = value.get(FIELD_TEXT) as String
        type = (value.get(FIELD_TYPE) as Long).toInt()
    }

    fun addMessageTo(item: Item, userIdTo: String, message: String, msgType: Int = MESSAGE_TYPE_CHAT) {
        text = message
        senderId = User.currentUser!!.id
        type = msgType

        val database = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)

        // add to history of oneself
        addToChatDatabase(database.child(senderId).child(item.id).child(userIdTo))
        // add to history of target
        addToChatDatabase(database.child(userIdTo).child(item.id).child(senderId))
    }

    private fun addToChatDatabase(db: DatabaseReference) {
        // update latest msg

        // generate new id
        id = db.child(FIELD_CHAT).push().key

        // save chat message
        db.child(FIELD_CHAT).child(id).setValue(this)

        // save latest message
        db.child(FIELD_LATEST_MSG).setValue(this)
    }

    /**
     * fetch target user
     */
    fun fetchTargetUser(fetchListener: FetchDatabaseListener?) {
        if (targetUser != null) {
            fetchListener?.onFetchedTargetUser(true)
            return
        }

        User.readFromDatabase(targetUserId, object: User.FetchDatabaseListener {
            override fun onFetchedReviews() {
            }

            override fun onFetchedItems() {
            }

            override fun onFetchedUser(u: User?, success: Boolean) {
                targetUser = u

                fetchListener?.onFetchedTargetUser(u != null)
            }
        })
    }

    /**
     * get notice message
     */
    fun messageNoticeText(): String {
        val dtCreatedAt = Date(createdAt)
        // format date time
        val strDate = SimpleDateFormat("hh.mm a MM.dd.yy").format(dtCreatedAt)

        var strMsg = "Request sent at $strDate"
        if (type == Message.MESSAGE_TYPE_ACCEPT) {
            strMsg = "Owner accepted at $strDate"

            if (senderId == User.currentUser?.id) {
                strMsg = "You accepted at $strDate"
            }
        }

        return strMsg
    }

    /**
     * interface for reading from database
     */
    interface FetchDatabaseListener {
        fun onFetchedTargetUser(success: Boolean)
    }
}