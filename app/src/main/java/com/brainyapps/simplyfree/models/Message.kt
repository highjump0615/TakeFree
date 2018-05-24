package com.brainyapps.simplyfree.models

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by Administrator on 4/10/18.
 */

class Message() : BaseModel() {

    companion object {
        //
        // table info
        //
        const val TABLE_NAME = "messages"
        const val FIELD_CHAT = "chats"
        const val FIELD_LATEST_MSG = "latest"
    }

    var text = ""
    var senderId = ""

    fun addMessageTo(userTo: String, message: String) {
        text = message
        senderId = User.currentUser!!.id

        val database = FirebaseDatabase.getInstance().reference.child(TABLE_NAME)

        // add to history of oneself
        addToChatDatabase(database.child(senderId).child(userTo))
        // add to history of target
        addToChatDatabase(database.child(userTo).child(senderId))
    }

    private fun addToChatDatabase(db: DatabaseReference) {
        // update latest msg

        // generate new id
        id = db.child(FIELD_CHAT).push().key

        // save
        db.child(FIELD_CHAT).child(id).setValue(this)
    }
}