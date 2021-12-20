/*
 * Copyright 2017-2021 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nifcloud.mbaas.core

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build

/**
 * The NCMBNotificationUtils Class contains register channel and get channel method
 */
internal class NCMBNotificationUtils(base: Context?) : ContextWrapper(base) {
    private var mManager: NotificationManager? = null
    private val CHANNEL_ID = "ChannelId"
    private val CHANNEL_NAME = "ChannelName"
    private val CHANNEL_DESCRIPTION = "ChannelDescription"
    private var channel_name: String?  = null
    private var channel_id: String? = null
    private var channel_des: String?  = null

    @TargetApi(Build.VERSION_CODES.O)
    fun settingDefaultChannels() {
        try {
            val appInfo =
                packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            channel_id = if (appInfo.metaData.containsKey(CHANNEL_ID)) {
                appInfo.metaData.getString(CHANNEL_ID)
            } else{
                defaultChannel
            }
            channel_name = if (appInfo.metaData.containsKey(CHANNEL_NAME)) {
                appInfo.metaData.getString(CHANNEL_NAME)
            } else{
                DEFAULT_CHANNEL_NAME
            }
            channel_des = if (appInfo.metaData.containsKey(CHANNEL_DESCRIPTION)) {
                appInfo.metaData.getString(CHANNEL_DESCRIPTION)
            } else{
                DEFAULT_CHANNEL_DES
            }
            println("channel_id = $channel_id")
            println("channel_name = $channel_name")
            println("channel_des = $channel_des")

        } catch (e: PackageManager.NameNotFoundException) {
            throw NCMBException(e)
        }
        // チャンネルを作成
        val androidChannel = NotificationChannel(
            channel_id,
            channel_name, NotificationManager.IMPORTANCE_DEFAULT
        )
        println("androidChannel = $androidChannel")
        androidChannel.description = channel_des
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