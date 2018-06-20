package com.brainyapps.simplyfree.models

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import com.brainyapps.simplyfree.utils.Utils
import com.google.firebase.database.*
import java.util.*

/**
 * Created by Administrator on 2/27/18.
 */
open class BaseModel() : Comparable<BaseModel> {

    companion object {
        //
        // table info
        //
        const val FIELD_DATE = "createdAt"
        const val FIELD_DELETED_AT = "deletedAt"
    }

    @get:Exclude
    var id = ""

    var createdAt: Long
    var deletedAt: Long? = null

    init {
        this.createdAt = Utils.getServerLongTime()
    }

    open fun tableName() = "base"

    override operator fun compareTo(other: BaseModel): Int {
        return if (this.createdAt > other.createdAt) {
            1
        } else if (this.createdAt < other.createdAt) {
            -1
        } else {
            0
        }
    }

    fun readFromParcelBase(parcel: Parcel) {
        id = parcel.readString()
        createdAt = parcel.readLong()
        val dDeleteAt = parcel.readLong()
        if (dDeleteAt > 0) {
            deletedAt = parcel.readLong()
        }
    }

    fun writeToParcelBase(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeLong(createdAt)
        if (deletedAt != null) {
            parcel.writeLong(deletedAt!!)
        }
        else {
            parcel.writeLong(0)
        }
    }

    fun saveToDatabaseBase(node: DatabaseReference) {
        node.child(FIELD_DATE).setValue(this.createdAt)
        node.child(FIELD_DELETED_AT).setValue(deletedAt)
    }

    /**
     * read specific field to
     */
    fun readFromDatabaseChild(fieldName: String, onGet:(data: Any?) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference.child(tableName())
        database.child(this.id).child(fieldName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                onGet(dataSnapshot?.value)
            }
        })
    }

    /**
     * Save specific field to db
     */
    fun saveToDatabaseChild(fieldName: String, data: Any) {
        val database = FirebaseDatabase.getInstance().reference.child(tableName())
        database.child(this.id).child(fieldName).setValue(data)
    }

    fun deleteFromDatabase(softDelete: Boolean = false) {
        if (softDelete) {
            saveToDatabaseChild(FIELD_DELETED_AT, Utils.getServerLongTime())
        }
        else {
            val database = FirebaseDatabase.getInstance().reference.child(tableName())
            database.child(this.id).removeValue()
        }
    }

}