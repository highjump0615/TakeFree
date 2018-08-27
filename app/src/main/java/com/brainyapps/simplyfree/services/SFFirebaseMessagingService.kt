package com.brainyapps.simplyfree.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import android.util.Log
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.activities.SplashActivity
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Globals




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

        // check data
        val type = row[com.brainyapps.simplyfree.models.Notification.FIELD_TYPE]?.toInt()
        val userId = row[com.brainyapps.simplyfree.models.Notification.FIELD_USER]
        val itemId = row[com.brainyapps.simplyfree.models.Notification.FIELD_ITEM]
        val strMessage = row[com.brainyapps.simplyfree.models.Notification.FIELD_MSG]

        Log.e(TAG, "Notification information")
        Log.d(TAG, "type: $type")
        Log.d(TAG, "userId: $userId")
        Log.d(TAG, "itemId: $itemId")

        val notifyNew = com.brainyapps.simplyfree.models.Notification()
        notifyNew.type = type!!
        notifyNew.userId = userId!!
        notifyNew.itemId = itemId!!

        // show badge
        if (type == com.brainyapps.simplyfree.models.Notification.NOTIFICATION_MESSAGE) {
            Globals.hasNewMessage = true
        }
        else {
            Globals.hasNewNotification = true
        }
        Globals.activityMain?.let {
            it.runOnUiThread {
                // Stuff that updates the UI
                it.showBadge()
            }
        }

        // fetch its user
        User.readFromDatabase(notifyNew.userId, object: User.FetchDatabaseListener {
            override fun onFetchedUser(u: User?, success: Boolean) {

                notifyNew.userPosted = u

                // intent to splash
                val intent = Intent(this@SFFirebaseMessagingService, SplashActivity::class.java)
                intent.putExtra(BaseActivity.KEY_NOTIFICATION, notifyNew)

                val contentIntent = PendingIntent.getActivity(this@SFFirebaseMessagingService,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                val notificationBuilder = NotificationCompat.Builder(this@SFFirebaseMessagingService, "100")
                        .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notifyNew.displayDescription())
                        .setContentText(if (TextUtils.isEmpty(strMessage)) "Tap to check details" else strMessage)
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