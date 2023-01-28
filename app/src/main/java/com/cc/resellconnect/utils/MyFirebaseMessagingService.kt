package com.cc.resellconnect.utils


import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.cc.resellconnect.R
import com.google.firebase.messaging.FirebaseMessaging

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage



//2022-12-01 17:36:32.988 15104-17143/com.cc.resellconnect D/TAG7: Message data payload: {alert=Anyone getting errors when logging into very?, sound=default, title=Judgie @ 8. General Chat, message={"receiver":"generalchat","data":{"resource":"WEB-2_4_0-daba795a-80ef-492f-b235-bf49cd46cdf3-1669806021855","entities":{"receiver":{"entityType":"group","entity":{"owner":"app_system","createdAt":1617352318,"membersCount":239,"conversationId":"group_generalchat","name":"8. General Chat","icon":"https:\/\/i.ibb.co\/wQynpf9\/Unknown.png","guid":"generalchat","type":"public","updatedAt":1669849124}},"sender":{"entityType":"user","entity":{"lastActiveAt":1669884355,"uid":"user1655886634702","role":"default","name":"Judgie","avatar":"https:\/\/firebasestorage.googleapis.com\/v0\/b\/resellconnect-b8aca.appspot.com\/o\/Users%2Fz1F2jf5arjX072LCTcpAPgXNJPq1%2Fz1F2jf5arjX072LCTcpAPgXNJPq1.jpeg?alt=media&token=1b70c1e5-6b62-48e5-9e23-bb8e1ffdef62","status":"available"}}},"text":"Anyone getting errors when logging into very?"},"sender":"user1655886634702","conversationId":"group_generalchat","receiverType":"group","id":"165700","sentAt":1669898192,"category":"message","type":"text","updatedAt":1669898192}}
//2022-12-01 17:36:32.990 15104-17143/com.cc.resellconnect D/TAG7: Message Notification Body: Anyone getting errors when logging into very?
//2022-12-01 17:36:45.667 15104-17169/com.cc.resellconnect D/TAG7: From: null
//2022-12-01 17:36:45.667 15104-17169/com.cc.resellconnect D/TAG7: Message data payload: {alert=Nah, sound=default, title=GENERAL_Z33 @ 8. General Chat, message={"receiver":"generalchat","data":{"muid":"1669898204935","resource":"ios-2_4_1-v7elDKNDRlu2CA8O56CAchmsC7HH39DO-1669894664757","entities":{"receiver":{"entityType":"group","entity":{"owner":"app_system","createdAt":1617352318,"membersCount":239,"conversationId":"group_generalchat","name":"8. General Chat","icon":"https:\/\/i.ibb.co\/wQynpf9\/Unknown.png","guid":"generalchat","type":"public","updatedAt":1669849124}},"sender":{"entityType":"user","entity":{"lastActiveAt":1669566100,"uid":"user166929210561","role":"default","name":"GENERAL_Z33","status":"offline"}}},"text":"Nah"},"sender":"user166929210561","conversationId":"group_generalchat","receiverType":"group","id":"165701","sentAt":1669898205,"category":"message","type":"text","updatedAt":1669898205}}
//2022-12-01 17:36:45.668 15104-17169/com.cc.resellconnect D/TAG7: Message Notification Body: Nah
// https://github.com/Karn/notify    notification library




class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("TAG7", "From: ${remoteMessage.messageType}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("TAG7", "Message data payload: ${remoteMessage.data}")
               sendNotification(remoteMessage.data)

        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d("TAG7", "Message Notification Body: ${it.body}")

             //  sendNotification(it.body as String)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private fun sendNotification(messageBody: MutableMap<String, String>) {
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.sendNotification(messageBody, applicationContext)
    }


    override fun onNewToken(token: String) {
        Log.v("TAG7", "token :$token")
        SavedPreference.setToken(applicationContext,token)
        super.onNewToken(token)
    }


    private fun sendNotification(messageBody: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.sendNotification(messageBody, applicationContext)
    }

    // TODO: Step 3.3 subscribe to breakfast topic
    private fun subscribeTopic(topic: String) {
        // [START subscribe_topics]
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                var msg = "Subscribes"
                if (!task.isSuccessful) {
                    msg = "Not Subscribed! :${task.exception.toString()}"
                }
                Log.v("TAG9","firebase Messaging :$msg")
                //  ToastUtils.showToast(mContext, msg)
            }
        // [END subscribe_topics]
    }


}