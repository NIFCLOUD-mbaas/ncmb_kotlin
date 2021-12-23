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

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager

import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
import androidx.core.app.NotificationCompat
import java.lang.IllegalArgumentException
import java.util.*
import android.media.RingtoneManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import org.json.JSONException
import org.json.JSONObject
import java.io.File

/**
 * Firebase messaging service class
 *
 * This class do initialize firebase and retrieve deviceToken,
 * also handling push receive message show activity.
 *
 */


internal open class NCMBFirebaseMessagingService: FirebaseMessagingService() {
    private val OPEN_PUSH_START_ACTIVITY_KEY = "openPushStartActivity"
    private val SMALL_ICON_KEY = "smallIcon"
    private val SMALL_ICON_COLOR_KEY = "smallIconColor"
    private val NOTIFICATION_OVERLAP_KEY = "notificationOverlap"
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     *
     * @param token  New devicetoken
     *
     */
    override fun onNewToken(token: String) {
        //todo
        print("On new token")
//        if (NCMBApplicationController.applicationState != null) {
//            NCMBInstallationUtils.updateToken(token)
//        }
        super.onNewToken(token)
    }

    /**
     * Called when received push message
     *
     * @param remoteMessage  RemoteMessage
     *
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage != null && remoteMessage.data != null) {
            val recentPushIdPref = getSharedPreferences("ncmbPushId", Context.MODE_PRIVATE)
            val recentPushId = recentPushIdPref.getString("recentPushId", "")
            val currentPushId = remoteMessage.data["com.nifcloud.mbaas.PushId"]
            // Skip duplicated message
            if (recentPushId != currentPushId) {
                val editor = recentPushIdPref.edit()
                editor.putString("recentPushId", currentPushId)
                editor.apply()
                super.onMessageReceived(remoteMessage)
                val bundle: Bundle = getBundleFromRemoteMessage(remoteMessage)
                println("NCMB send start : " + bundle)
                sendNotification(bundle)
            }
        }
        // TODO(developer): Handle FCM messages here.
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    /**
     * Called when received push message, get bundle data from remote message
     *
     * @param remoteMessage  RemoteMessage
     *
     */
    private fun getBundleFromRemoteMessage(remoteMessage: RemoteMessage): Bundle {
        val bundle = Bundle()
        val data = remoteMessage.data
        for ((key, value) in data) {
            bundle.putString(key, value)
        }
        return bundle
    }

    /**
     * Show notification based on pushData
     *
     * @param pushData  RemoteMessage
     *
     */
    private fun sendNotification(pushData: Bundle) {
        //サイレントプッシュ
        if (!pushData.containsKey("message") && !pushData.containsKey("title")) {
            return
        }
        val notificationBuilder: NotificationCompat.Builder = notificationSettings(pushData)

        /*
         * 通知重複設定
         * 0:常に最新の通知のみ表示
         * 1:最新以外の通知も複数表示
         */
        var appInfo: ApplicationInfo
        appInfo = try {
            packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            throw IllegalArgumentException(e)
        }
        val containsKey = appInfo.metaData.containsKey(NOTIFICATION_OVERLAP_KEY)
        val overlap = appInfo.metaData.getInt(NOTIFICATION_OVERLAP_KEY)

        //デフォルト複数表示
        var notificationId: Int = Random().nextInt()
        if (overlap == 0 && containsKey) {
            //最新のみ表示
            notificationId = 0
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        try {
            notificationManager.notify(notificationId, notificationBuilder.build())
        }
        catch(error: Exception){
            throw NCMBException(error)
        }
    }


    /**
     * Notification setup method
     * Set up notifications properties for Notification.
     *
     * @param pushData  RemoteMessage
     *
     */
    open fun notificationSettings(pushData: Bundle): NotificationCompat.Builder {
        //AndroidManifestから情報を取得
        var appInfo: ApplicationInfo? = null
        var startClass: Class<*>? = null
        var applicationName: String? = null
        var activityName: String? = null
        var packageName: String? = null
        var channelIcon = 0
        try {
            appInfo =
                packageManager.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA)
            applicationName = packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(
                    getPackageName(),
                    0
                )
            ).toString()
            activityName =
                appInfo.packageName + appInfo.metaData.getString(OPEN_PUSH_START_ACTIVITY_KEY)
            packageName = appInfo.packageName
            /*
            プッシュデータにチャネルが指定されているかつ、チャネルファイルが登録されている場合は、
            通知タップ起動時のactivityNameをファイル内指定のactivityNameに更新する
            */
            val channel = pushData.getString("com.nifcloud.mbaas.Channel")
            println("channel = $channel")
            if (channel != null) {
                val channelDirectory = File(
                    getDir(
                        NCMBLocalFile.FOLDER_NAME,
                        MODE_PRIVATE
                    ), NCMBInstallation.CHANNELS_FOLDER_NAME
                )
                val channelFile = File(channelDirectory, channel)
                if (channelFile.exists()) {
                    var json = JSONObject()
                    try {
                        json = NCMBLocalFile.readFile(channelFile)
                    } catch (e: NCMBException) {
                        Log.e("Error", e.toString())
                    }
                    if (json.has("activityClass")) {
                        activityName = json.getString("activityClass")
                    }
                    if (json.has("icon")) {
                        channelIcon = json.getInt("icon")
                    }
                    //v1→v2時のみTrue. v2でチャネル登録した場合には設定されない
                    if (json.has("activityPackage")) {
                        packageName = json.getString("activityPackage")
                    }
                }
            }
            //通知起動時のActivityクラスを作成
            startClass = Class.forName(activityName!!)
        } catch (e: PackageManager.NameNotFoundException) {
            throw NCMBException(e)
        } catch (e: ClassNotFoundException) {
            throw NCMBException(e)
        } catch (e: JSONException) {
            throw NCMBException(e)
        }

        //通知エリアに表示されるプッシュ通知をタップした際に起動するアクティビティ画面を設定する
        val intent = Intent(this, startClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val componentName = ComponentName(packageName, activityName)
        intent.component = componentName
        intent.putExtras(pushData)
        val pendingIntent = PendingIntent.getActivity(
            this, Random().nextInt(), intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        //pushDataから情報を取得
        var message: String? = ""
        var title: String? = ""
        title = if (pushData.getString("title") != null) {
            pushData.getString("title")
        } else {
            //titleの設定が無い場合はアプリ名をセットする
            applicationName
        }
        if (pushData.getString("message") != null) {
            message = pushData.getString("message")
        }

        //SmallIconを設定。manifestsにユーザー指定の設定が無い場合はアプリアイコンを設定する
        val userSmallIcon = appInfo.metaData.getInt(SMALL_ICON_KEY)
        val icon: Int
        icon = if (channelIcon != 0) {
            //チャネル毎にアイコン設定がされている場合
            channelIcon
        } else if (userSmallIcon != 0) {
            //manifestsにアイコン設定がされている場合
            userSmallIcon
        } else {
            //それ以外はアプリのアイコンを設定する
            appInfo.icon
        }
        //SmallIconカラーを設定
        val smallIconColor = appInfo.metaData.getInt(SMALL_ICON_COLOR_KEY)

        //Notification作成
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        return NotificationCompat.Builder(this, NCMBNotificationUtils.defaultChannel)
            .setSmallIcon(icon) //通知エリアのアイコン設定
            .setColor(smallIconColor) //通知エリアのアイコンカラー設定
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(title))
            .setAutoCancel(true) //通知をタップしたら自動で削除する
            .setSound(defaultSoundUri) //端末のデフォルトサウンド
            .setContentIntent(pendingIntent) //通知をタップした際に起動するActivity
    }

}