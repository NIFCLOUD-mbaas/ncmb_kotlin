package com.nifcloud.mbaas.core

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Build


/**
 * The NCMBNotificationUtils Class contains register channel and get channel method
 */
class NCMBNotificationUtils(base: Context?) : ContextWrapper(base) {
    private var mManager: NotificationManager? = null
    @TargetApi(Build.VERSION_CODES.O)
    fun settingDefaultChannels() {

        // チャンネルを作成
        val androidChannel = NotificationChannel(
            defaultChannel,
            DEFAULT_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
        )
        androidChannel.description = DEFAULT_CHANNEL_DES
        androidChannel.enableLights(true)
        androidChannel.enableVibration(true)
        androidChannel.lightColor = Color.GREEN
        androidChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        manager!!.createNotificationChannel(androidChannel)
    }

    val manager: NotificationManager?
        get() {
            if (mManager == null) {
                mManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            }
            return mManager
        }

    companion object {
        // デフォルトチャンネルID
        const val defaultChannel = "com.nifcloud.mbaas.push.channel"

        // デフォルトチャンネル名
        private const val DEFAULT_CHANNEL_NAME = "NCMB Push Channel"

        // デフォルトチャンネル説明
        private const val DEFAULT_CHANNEL_DES = "NIFCLOUD mobile backend push notification channel"
    }
}