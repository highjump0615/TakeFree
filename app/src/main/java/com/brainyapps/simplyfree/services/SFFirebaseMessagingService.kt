package com.brainyapps.simplyfree.services

import android.app.Notification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationManager
import com.brainyapps.simplyfree.R.mipmap.ic_launcher
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.User


/**
 * Created by Administrator on 7/5/18.
 */
class SFFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = SFFirebaseInstanceIdService::class.java.getSimpleName()

    private var count = 0


    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        Log.d(TAG, "onMessageReceived")

        //Calling method to generate notification
        sendNotification(remoteMessage?.notification?.title,
                remoteMessage?.notification?.body,
                remoteMessage?.data as Map<String, String>);
    }

    private fun sendNotification(messageTitle: String?, messageBody: String?, row: Map<String, String>) {

        if (User.currentUser == null) {
            // not logged in, return
            return
        }

        // check data
        val type = row[com.brainyapps.simplyfree.models.Notification.FIELD_TYPE]?.toInt()
        val userId = row[com.brainyapps.simplyfree.models.Notification.FIELD_USER]
        val itemId = row[com.brainyapps.simplyfree.models.Notification.FIELD_ITEM]

        Log.e(TAG, "Notification information")
        Log.d(TAG, "type: $type")
        Log.d(TAG, "userId: $userId")
        Log.d(TAG, "itemId: $itemId")

        val notifyNew = com.brainyapps.simplyfree.models.Notification()
        notifyNew.type = type!!
        notifyNew.userId = userId!!
        notifyNew.itemId = itemId!!

        // fetch its user
        User.readFromDatabase(notifyNew.userId, object: User.FetchDatabaseListener {
            override fun onFetchedUser(u: User?, success: Boolean) {

                notifyNew.userPosted = u

                // send notification
                val intent = notifyNew.getIntentForDetail(this@SFFirebaseMessagingService)

                val contentIntent = PendingIntent.getActivity(this@SFFirebaseMessagingService,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                val notificationBuilder = NotificationCompat.Builder(this@SFFirebaseMessagingService, "100")
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notifyNew.getDescription())
                        .setContentText("Tap to check details")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(contentIntent)

                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(count, notificationBuilder.build())
                count++
            }

            override fun onFetchedItems() {
            }

            override fun onFetchedReviews() {
            }
        })
    }

}