package com.cc.resellconnect.utils


import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cc.resellconnect.R
import com.cc.resellconnect.ui.activities.MainActivity
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants
import org.json.JSONObject


// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0
val GROUP_KEY = "com.cc.resellconnect.GROUP_NOTIF"
val SUMMARY_ID = 0
// TODO: Step 1.1 extension function to send messages (GIVEN)
/**
 * Builds and delivers the notification.
 *
 * @param messageBody, notification text.
 * @param context, activity context.
 */
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {

//    createChannel(
//        applicationContext,
//        applicationContext.getString(R.string.breakfast_notification_channel_id),
//        applicationContext.getString(R.string.breakfast_notification_channel_name)
//    )

    // TODO: Step 1.11 create intent
    var contentIntent = Intent(applicationContext, MainActivity::class.java)
        .putExtra("from", "notification")

    // TODO: Step 1.12 create PendingIntent
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

// TODO: Step 2.0 add style
    val eggImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.logo
    )
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(eggImage)
        .bigLargeIcon(null)


    // TODO: Step 2.2 add snooze action
//    val snoozeIntent = Intent(applicationContext, SnoozeReceiver::class.java)
//    val snoozePendingIntent: PendingIntent =
//        PendingIntent.getBroadcast(applicationContext, REQUEST_CODE, snoozeIntent, FLAGS)

    // TODO: Step 1.2 get an instance of NotificationCompat.Builder
    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        // TODO: Step 1.8 use the new 'breakfast' notification channel
        applicationContext.getString(R.string.resell_notification_channel_id)
    )
        // TODO: Step 1.3 set title, text and icon to builder
        .setSmallIcon(R.drawable.logo)
        .setContentTitle(applicationContext.getString(R.string.app_name))
        .setContentText(messageBody)
        // TODO: Step 1.13 set content intent
        .setContentIntent(contentPendingIntent)
        // TODO: Step 2.1 add style to builder

        // TODO: Step 2.3 add snooze action
        .setContentIntent(contentPendingIntent)
        // TODO: Step 2.5 set priority
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    // TODO Step 1.4 call notify
    // Deliver the notification
    notify(NOTIFICATION_ID, builder.build())
}


fun NotificationManager.sendNotification(
    messageBody: MutableMap<String, String>,
    applicationContext: Context
) {
    Log.d("TAG7", "Message data")
     val message1 = messageBody["message"]
    val message = JSONObject(message1)
     val data = message["data"] as JSONObject
     val entities = data["entities"] as JSONObject
     val receiver = entities["receiver"]as JSONObject
     val entity = receiver["entity"] as JSONObject


    val contentIntent = Intent(applicationContext, MainActivity::class.java)
        .apply {
            putExtra(UIKitConstants.IntentStrings.GUID,entity["guid"].toString())
            putExtra(UIKitConstants.IntentStrings.AVATAR,entity["icon"].toString())
            putExtra(UIKitConstants.IntentStrings.GROUP_OWNER,"app_system")
            putExtra(UIKitConstants.IntentStrings.NAME,entity["name"].toString())
            putExtra(UIKitConstants.IntentStrings.GROUP_TYPE,"public")
            putExtra(UIKitConstants.IntentStrings.MEMBER_COUNT,entity["membersCount"] as Int)
            putExtra(UIKitConstants.IntentStrings.GROUP_DESC,"")
            putExtra(UIKitConstants.IntentStrings.GROUP_PASSWORD,"")
            putExtra("from","Notification")
        }

     val notification_id = System.currentTimeMillis().toInt()
    // TODO: Step 1.12 create PendingIntent
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        notification_id,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE
    )
    val newMessageNotification1 = NotificationCompat.Builder(applicationContext,
        applicationContext.getString(R.string.resell_notification_channel_id)
    )
        .setSmallIcon(R.drawable.logo_black)
        .setContentTitle(messageBody["title"])
        .setContentText(messageBody["alert"])
        .setGroup(GROUP_KEY)
        .setContentIntent(contentPendingIntent)
        .build()

//        val newMessageNotification2 = NotificationCompat.Builder(this@MainActivity, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_baseline_email_24)
//            .setContentTitle("emailObject2.getSummary()")
//            .setContentText("Please join us to celebrate the...")
//            .setGroup(GROUP_KEY_WORK_EMAIL)
//            .build()

    val summaryNotification = NotificationCompat.Builder(applicationContext,
        applicationContext.getString(R.string.resell_notification_channel_id)
    ).setContentTitle(messageBody["title"])
        //set content text to support devices running API level < 24
        .setContentText(messageBody["alert"])
        .setSmallIcon(R.drawable.logo_black)
        //build summary info into InboxStyle template
        .setStyle(NotificationCompat.InboxStyle()
            .addLine("Alex Faarborg Check this out")
            .addLine("Jeff Chang Launch Party")
            .setBigContentTitle("2 new messages")
            .setSummaryText(""))
        //specify which group this notification belongs to
        .setGroup(GROUP_KEY)
        //set this notification as the summary for the group
        .setGroupSummary(true)
        .build()

//    NotificationManagerCompat.from(this).apply {
        notify(notification_id, newMessageNotification1)
        //  notify(emailNotificationId2, newMessageNotification2)
        notify(SUMMARY_ID, summaryNotification)
  //  }



}

//fun NotificationManager.sendNotification(
//    messageBody: MutableMap<String, String>,
//    applicationContext: Context
//) {
//    Log.d("TAG7", "Message data")
////    createChannel(
////        applicationContext,
////        applicationContext.getString(R.string.breakfast_notification_channel_id),
////        applicationContext.getString(R.string.breakfast_notification_channel_name)
////    )
//
////    // TODO: Step 1.11 create intent
////    var contentIntent = Intent(applicationContext, MainActivity::class.java)
////        .putExtra("from", "notification")
////
////    // TODO: Step 1.12 create PendingIntent
////    val contentPendingIntent = PendingIntent.getActivity(
////        applicationContext,
////        NOTIFICATION_ID,
////        contentIntent,
////        PendingIntent.FLAG_IMMUTABLE
////    )
//
//
//    val bundle = Bundle()
////    bundle.putSerializable("flag",)
//
////
////    val pendingIntent: PendingIntent = NavDeepLinkBuilder(applicationContext)
////        .setGraph(R.navigation.nav_graph)
////        .setDestination(R.id.friends)
////        .setArguments(bundle)
////        .createPendingIntent()
//
//
//// TODO: Step 2.0 add style
//    val eggImage = BitmapFactory.decodeResource(
//        applicationContext.resources,
//        R.drawable.logo
//    )
//    val bigPicStyle = NotificationCompat.BigPictureStyle()
//        .bigPicture(eggImage)
//        .bigLargeIcon(null)
//
//
//    // TODO: Step 2.2 add snooze action
////    val snoozeIntent = Intent(applicationContext, SnoozeReceiver::class.java)
////    val snoozePendingIntent: PendingIntent =
////        PendingIntent.getBroadcast(applicationContext, REQUEST_CODE, snoozeIntent, FLAGS)
//
//    // TODO: Step 1.2 get an instance of NotificationCompat.Builder
//    // Build the notification
//    val builder = NotificationCompat.Builder(
//        applicationContext,
//        // TODO: Step 1.8 use the new 'breakfast' notification channel
//        applicationContext.getString(R.string.resell_notification_channel_id)
//    )
//        // TODO: Step 1.3 set title, text and icon to builder
//        .setSmallIcon(R.drawable.logo_black)
//        .setContentTitle(applicationContext.getString(R.string.app_name))
//        .setContentText(messageBody["msg"])
//        // TODO: Step 1.13 set content intent
//
//        // TODO: Step 2.1 add style to builder
//
//        // TODO: Step 2.5 set priority
//        .setPriority(NotificationCompat.PRIORITY_HIGH)
//        .setAutoCancel(true)
//
//    // TODO Step 1.4 call notify
//    // Deliver the notification
//    notify(NOTIFICATION_ID, builder.build())
//}

// TODO: Step 1.14 Cancel all notifications
/**
 * Cancels all notifications.
 *
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}

//private fun createChannel(context: Context, channelId: String, channelName: String) {
//    // TODO: Step 1.6 START create a channel
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        // Create channel to show notifications.
//        val notificationChannel = NotificationChannel(
//            channelId,
//            channelName,
//            // TODO: Step 2.4 change importance
//            NotificationManager.IMPORTANCE_HIGH
//        )
//            // TODO: Step 2.6 disable badges for this channel
//            .apply {
//                setShowBadge(false)
//            }
//
//        notificationChannel.enableLights(true)
//        notificationChannel.lightColor = Color.RED
//        notificationChannel.enableVibration(true)
//        notificationChannel.description =
//            context.getString(R.string.breakfast_notification_channel_description)
//
//        val notificationManager = context.getSystemService(
//            NotificationManager::class.java
//        )
//
//        notificationManager.createNotificationChannel(notificationChannel)
//
//    }
//    // TODO: Step 1.6 END create channel
//}


//fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
//
//    createChannel(
//        applicationContext,
//        applicationContext.getString(R.string.breakfast_notification_channel_id),
//        applicationContext.getString(R.string.breakfast_notification_channel_name)
//    )
//
//    // TODO: Step 1.11 create intent
//    val contentIntent = Intent(applicationContext, MainActivity::class.java)
//
//    // TODO: Step 1.12 create PendingIntent
//    val contentPendingIntent = PendingIntent.getActivity(
//        applicationContext,
//        NOTIFICATION_ID,
//        contentIntent,
//        PendingIntent.FLAG_IMMUTABLE
//    )
//
//// TODO: Step 2.0 add style
//    val eggImage = BitmapFactory.decodeResource(
//        applicationContext.resources,
//        R.drawable.account
//    )
//    val bigPicStyle = NotificationCompat.BigPictureStyle()
//        .bigPicture(eggImage)
//        .bigLargeIcon(null)
//
//
//    // TODO: Step 2.2 add snooze action
////    val snoozeIntent = Intent(applicationContext, SnoozeReceiver::class.java)
////    val snoozePendingIntent: PendingIntent =
////        PendingIntent.getBroadcast(applicationContext, REQUEST_CODE, snoozeIntent, FLAGS)
//
//    // TODO: Step 1.2 get an instance of NotificationCompat.Builder
//    // Build the notification
//    val builder = NotificationCompat.Builder(
//        applicationContext,
//        // TODO: Step 1.8 use the new 'breakfast' notification channel
//        applicationContext.getString(R.string.breakfast_notification_channel_id)
//    )
//        // TODO: Step 1.3 set title, text and icon to builder
//        .setSmallIcon(R.drawable.account)
//        .setContentTitle(applicationContext.getString(R.string.app_name))
//        .setContentText(messageBody)
//        // TODO: Step 1.13 set content intent
//        .setContentIntent(contentPendingIntent)
//        // TODO: Step 2.1 add style to builder
//        .setStyle(bigPicStyle)
//        .setLargeIcon(eggImage)
//        // TODO: Step 2.3 add snooze action
//        .addAction(
//            R.drawable.like,
//            applicationContext.getString(R.string.add_your_review),
//            contentPendingIntent
//        )
//        // TODO: Step 2.5 set priority
//        .setPriority(NotificationCompat.PRIORITY_HIGH)
//        .setAutoCancel(true)
//
//    // TODO Step 1.4 call notify
//    // Deliver the notification
//    notify(NOTIFICATION_ID, builder.build())
//}