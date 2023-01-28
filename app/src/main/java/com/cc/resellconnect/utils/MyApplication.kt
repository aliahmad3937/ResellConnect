package com.cc.resellconnect.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.cc.resellconnect.R
import com.cc.resellconnect.ui.activities.MainActivity
import com.cc.resellconnect.ui.fragments.CometChatGroupList
import com.cc.resellconnect.utils.Constants.APP_ID
import com.cc.resellconnect.utils.Constants.AUTH_KEY
import com.cc.resellconnect.utils.Constants.REGION
import com.cometchat.pro.core.AppSettings
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.uikit.ui_components.calls.call_manager.listener.CometChatCallListener.addCallListener
import com.cometchat.pro.uikit.ui_components.calls.call_manager.listener.CometChatCallListener.removeCallListener
import com.cometchat.pro.uikit.ui_settings.UIKitSettings

class MyApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        val appSettings = AppSettings.AppSettingsBuilder()
            .subscribePresenceForAllUsers()
            .setRegion(REGION)
         //   .autoEstablishSocketConnection(false)
            .build()

        CometChat.init(this, APP_ID, appSettings, object : CometChat.CallbackListener<String>() {
            override fun onSuccess(s: String) {
                Log.d(TAG, "onSuccess: $s")
                UIKitSettings.setAppID(APP_ID)
                UIKitSettings.setAuthKey(AUTH_KEY)
                CometChat.setSource("ui-kit", "android", "kotlin")

            }

            override fun onError(e: CometChatException) {
            }
        })
        val uiKitSettings = UIKitSettings(this)
        uiKitSettings.addConnectionListener(TAG)
        addCallListener(TAG, this)
        createNotificationChannel()
    }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.app_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        removeCallListener(TAG)
        CometChat.removeConnectionListener(TAG)
    }

    companion object {
        private const val TAG = "UIKitApplication"
        var isChannel = true
        var isGuest = false
        var isFree = false
        val CHANNEL_ID = "com.cc.resellconnect"

        var cometChatGroupList: CometChatGroupList? = null
        var activity: MainActivity? = null
    }


}