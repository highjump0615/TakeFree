package com.brainyapps.simplyfree.models

import android.app.Activity
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.UploadTask
import java.util.*

/**
 * Created by Administrator on 3/24/18.
 */
class User() : BaseModel(), Parcelable {

    companion object {
        val USER_TYPE_ADMIN = 0
        val USER_TYPE_CUSTOMER = 1

        val TAG = User::class.java.getSimpleName()

        var currentUser: User? = null

        fun readFromDatabase(withId: String, fetchListener: FetchDatabaseListener) {

            val database = FirebaseDatabase.getInstance().reference
            val query = database.child(User.TABLE_NAME + "/" + withId)

            // Read from the database
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    val u = dataSnapshot.getValue(User::class.java)
                    u?.id = withId

                    fetchListener.onFetchedUser(u, true)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "failed to read from database.", error.toException())
                    fetchListener.onFetchedUser(null, false)
                }
            })
        }

        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(parcel: Parcel): User {
                return User(parcel)
            }

            override fun newArray(size: Int): Array<User?> {
                return arrayOfNulls(size)
            }
        }

        //
        // table info
        //
        const val TABLE_NAME = "users"
        const val FIELD_EMAIL = "email"
        const val FIELD_TYPE = "type"
        const val FIELD_BANNED = "banned"
    }

    var type: Int = USER_TYPE_CUSTOMER
    var banned: Boolean = false

    var email = ""

    var firstName = ""
    var lastName = ""
    var photoUrl = ""

    @get:Exclude
    var items = ArrayList<Item>()

    override fun tableName() = TABLE_NAME

    constructor(parcel: Parcel) : this() {
        type = parcel.readInt()

        banned = parcel.readByte().toInt() != 0
        email = parcel.readString()
        firstName = parcel.readString()
        lastName = parcel.readString()
        photoUrl = parcel.readString()
    }

    constructor(id: String) : this() {
        this.id = id
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(type)
        parcel.writeByte((if (banned) 1 else 0).toByte())
        parcel.writeString(email)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(photoUrl)

        writeToParcelBase(parcel, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    /**
     * fetch items of the user
     */
    fun fetchItems(fetchListener: FetchDatabaseListener) {
        val database = FirebaseDatabase.getInstance().reference.child(Item.TABLE_NAME)
        val query = database.orderByChild(Item.FIELD_USER).equalTo(this.id)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                this@User.items.clear()

                for (itemItem in dataSnapshot.children) {
                    val item = itemItem.getValue(Item::class.java)
                    item!!.id = itemItem.key
                    item.userPosted = this@User

                    this@User.items.add(item)
                }

                // sort
                Collections.sort(this@User.items, Collections.reverseOrder())

                fetchListener.onFetchedItems()
            }

            override fun onCancelled(error: DatabaseError) {
                fetchListener.onFetchedItems()
            }
        })
    }

    /**
     * interface for reading from database
     */
    interface FetchDatabaseListener {
        fun onFetchedUser(user: User?, success: Boolean)
        fun onFetchedItems()
    }

    /**
     * returns full name
     */
    fun userFullName(): String {
        return "${this.firstName} ${this.lastName}"
    }

    /**
     * update profile photo
     */
    fun updateProfilePhoto(activity: Activity, data: ByteArray?, onCompleted:() -> Unit) {
        if (data != null) {
            // save photo image
            val metadata = StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build()
            val storageReference = FirebaseStorage.getInstance().getReference(User.TABLE_NAME).child(id + ".jpg")
            val uploadTask = storageReference.putBytes(data, metadata)

            uploadTask.addOnSuccessListener(activity, OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                photoUrl = taskSnapshot.downloadUrl.toString()
            }).addOnFailureListener(activity, OnFailureListener {
                Log.d(TAG, it.toString())
            }).addOnCompleteListener({
                onCompleted()
            })
        }
        else {
            onCompleted()
        }
    }

    /**
     * remove item from list
     */
    fun deleteItem(id: String) {

        val itemDelete = items.filter { element ->
            element.id.equals(id)
        }

        if (itemDelete.isNotEmpty()) {
            itemDelete[0].deleteFromDatabase()
            items.remove(itemDelete[0])
        }
    }
}