package com.ibs.hrmioneemployee.notification
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ibs.hrmioneemployee.R
import com.ibs.hrmioneemployee.activities.MainActivity

class FirebaseMessaging: FirebaseMessagingService(){

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "HRmione"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Reference Token", token)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if(remoteMessage.notification != null){
            pushNotification(remoteMessage.notification!!.title!!,remoteMessage.notification!!.body!!)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag", "RemoteViewLayout")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun pushNotification(tittle:String, message:String) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // pendingIntent is an intent for future use i.e after
        // the notification is clicked, this intent will come into action
        val intent = Intent(this, MainActivity::class.java)

        // FLAG_UPDATE_CURRENT specifies that if a previous
        // PendingIntent already exists, then the current one
        // will update it with the latest intent
        // 0 is the request code, using it later with the
        // same method again will get back the same pending
        // intent for future reference
        // intent passed here is to our afterNotification class
        val pendingIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // RemoteViews are used to use the content of
        // some different layout apart from the current activity layout
        val contentView = RemoteViews(packageName, R.layout.show_notification_layout)
        contentView.setTextViewText(R.id.textView,tittle)
        contentView.setTextViewText(R.id.message,message)
        contentView.setImageViewResource(R.id.killaIcon, R.drawable.notification)
        contentView.setTextViewText(R.id.killa_detail, "HRmione")

        // checking if android version is greater than oreo(API 26) or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Custom Channel"
            notificationChannel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)


            builder = Notification.Builder(this, channelId)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(tittle)
                .setContentText(message)
                .setContent(contentView)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        } else {

            builder = Notification.Builder(this)
                .setContent(contentView)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(tittle)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        }

        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(1234, builder.build())
    }
}