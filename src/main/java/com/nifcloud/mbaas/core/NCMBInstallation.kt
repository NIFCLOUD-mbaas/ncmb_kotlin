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

import android.util.Log
//import com.google.android.gms.tasks.OnCanceledListener
//import com.google.android.gms.tasks.OnCompleteListener
//import com.google.android.gms.tasks.Task
//import com.google.firebase.FirebaseApp
//import com.google.firebase.iid.FirebaseInstanceIdReceiver
//import com.google.firebase.iid.FirebaseInstanceId
//import com.google.firebase.iid.InstanceIdResult
import com.nifcloud.mbaas.core.NCMBLocalFile.checkNCMBContext
import com.nifcloud.mbaas.core.NCMBLocalFile.create
import com.nifcloud.mbaas.core.NCMBLocalFile.readFile
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * NCMBInstallation is used to retrieve and upload the installation data
 */
class NCMBInstallation : NCMBObject {
    //region getter
    /**
     * Get application name
     *
     * @return application name
     */
    /**
     * Set application name
     *
     * @param value applicationName
     */
    var applicationName: String?
        get() = try {
            if (mFields.isNull("applicationName")) {
                null
            } else mFields.getString("applicationName")
        } catch (e: JSONException) {
            throw IllegalArgumentException(e.message)
        }
        set(value) {
            try {
                mFields.put("applicationName", value)
                mUpdateKeys.add("applicationName")
            } catch (e: JSONException) {
                throw IllegalArgumentException(e.message)
            }
        }
    /**
     * Get application version
     *
     * @return application Version
     */
    /**
     * Set application version
     * ReadOnly field
     *
     * @param value appVersion
     */
    var appVersion: String?
        get() {
            return try {
                if (mFields.isNull("appVersion")) {
                    null
                } else mFields.getString("appVersion")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                mFields.put("appVersion", value)
                mUpdateKeys.add("appVersion")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
    /**
     * Get badge count
     *
     * @return badge count
     */
    /**
     * Set badge count
     *
     * @param value applicationName
     */
    var badge: Int
        get() {
            return try {
                if (mFields.isNull("badge")) {
                    0
                } else mFields.getInt("badge")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                mFields.put("badge", value)
                mUpdateKeys.add("badge")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
    /**
     * Get channels
     *
     * @return channels
     */
    /**
     * Set channels
     *
     * @param value channels
     */
    var channels: JSONArray?
        get() {
            return try {
                if (mFields.isNull("channels")) {
                    null
                } else mFields.getJSONArray("channels")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                mFields.put("channels", value)
                mUpdateKeys.add("channels")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
    /**
     * Get device type
     *
     * @return device type
     */
    /**
     * Set device type
     * ReadOnly field
     *
     * @param value device type
     */
    var deviceType: String?
        get() {
            return try {
                if (mFields.isNull("deviceType")) {
                    null
                } else mFields.getString("deviceType")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                mFields.put("deviceType", value)
                mUpdateKeys.add("deviceType")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
    /**
     * Get device token
     *
     * @return device token
     */
    /**
     * Set device token
     *
     * @param value device token
     */
    @get:Deprecated(
        """replaced by {@link #getDeviceTokenInBackground}
      """
    )
    var deviceToken: String?
        get() {
            return try {
                if (mFields.isNull("deviceToken")) {
                    null
                } else mFields.getString("deviceToken")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                mFields.put("deviceToken", value)
                mUpdateKeys.add("deviceToken")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }

    //Todo
//    /**
//     * Get device token
//     *
//     * @param callback TokenCallback
//     */
//    fun getDeviceTokenInBackground(callback: TokenCallback) {
//        if (DeviceTokenCallbackQueue.getInstance().isDuringSaveInstallation()) {
//            DeviceTokenCallbackQueue.getInstance().addQueue(callback)
//            return
//        }
//        if (FirebaseApp.getApps(NCMB.getCurrentContext().context).isEmpty()) {
//            callback.done(null, NCMBException(IOException(CANNOT_GET_DEVICE_TOKEN_MESSAGE)))
//            return
//        }
//        val deviceToken = localDeviceToken
//        if (deviceToken != null) {
//            callback.done(localDeviceToken, null)
//            return
//        }
//        getDeviceTokenInternalProcess(callback)
//    }
//    /**
//     * Get device token (Internal Process)
//     *
//     * @param callback TokenCallback
//     */
//    fun getDeviceTokenInternalProcess(callback: NCMBCallback) {
//        if (!FirebaseApp.getApps(NCMB.getCurrentContext()!!.applicationContext).isEmpty()) {
//            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(object : OnCompleteListener<InstanceIdResult?>() {
//                    fun onComplete(task: Task<InstanceIdResult?>) {
//                        if (task.isSuccessful()) {
//                            callback.done(task.getResult().getToken(), null)
//                        } else {
//                            callback.done(
//                                null, NCMBException(
//                                    IOException(
//                                        CANNOT_GET_DEVICE_TOKEN_MESSAGE
//                                    )
//                                )
//                            )
//                        }
//                    }
//                })
//            FirebaseInstanceId.getInstance().getInstanceId().addOnCanceledListener(object : OnCanceledListener() {
//                    fun onCanceled() {
//                        callback.done(
//                            null, NCMBException(
//                                IOException(
//                                    CANNOT_GET_DEVICE_TOKEN_MESSAGE
//                                )
//                            )
//                        )
//                    }
//                })
//        } else {
//            callback.done(null, NCMBException(IOException(CANNOT_GET_DEVICE_TOKEN_MESSAGE)))
//        }
//    }

    /**
     * Get device token
     *
     * @return device token
     */
    val localDeviceToken: String?
        get() {
            return try {
                if (mFields.isNull("deviceToken")) {
                    null
                } else mFields.getString("deviceToken")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
    /**
     * Get SDK version
     *
     * @return SDK version
     */
    /**
     * Set SDK version
     * ReadOnly field
     *
     * @param value SDKversion
     */
    var sdkVersion: String?
        get() {
            return try {
                if (mFields.isNull("sdkVersion")) {
                    null
                } else mFields.getString("sdkVersion")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                mFields.put("sdkVersion", value)
                mUpdateKeys.add("sdkVersion")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
    /**
     * Get timezone
     *
     * @return timezone
     */
    /**
     * Set timezone
     * ReadOnly field
     *
     * @param value timezone
     */
    var timeZone: String?
        get() {
            return try {
                if (mFields.isNull("timeZone")) {
                    null
                } else mFields.getString("timeZone")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                mFields.put("timeZone", value)
                mUpdateKeys.add("timeZone")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }

    /**
     * Get custom field value
     *
     * @param name field name
     * @return field value
     */
    fun getValue(name: String?): Any? {
        return try {
            if (mFields.isNull(name)) {
                null
            } else mFields.get(name)
        } catch (error: JSONException) {
            throw IllegalArgumentException(error.message)
        }
    }
    //endregion
    //region setter
    /**
     * Constructor
     */
    constructor() : super("installation") {
        mIgnoreKeys = ignoreKeys
    }

    /**
     * Constructor from JSON
     *
     * @param params params source JSON
     * @throws NCMBException
     */
    internal constructor(params: JSONObject?) : super("installation", params!!) {
        mIgnoreKeys = ignoreKeys
    }
    // endregion
    //region save
    /**
     * Save installation object
     *
     * @throws NCMBException exception from NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun saveInstallation() {
        //connect
        val installationService = NCMBInstallationService()
        val responseData: JSONObject
        if (getObjectId() == null) {
            //new create
            //responseData = installationService.createInstallation(localDeviceToken, mFields)
        } else {
            //update
            var updateJson: JSONObject? = null
            updateJson = try {
                createUpdateJsonData()
            } catch (e: JSONException) {
                throw IllegalArgumentException(e.message)
            }
            //responseData = installationService.updateInstallation(getObjectId(), updateJson)
        }
        //localData = responseData
        mUpdateKeys.clear()
    }

    /**
     * Save installation object inBackground
     *
     * @param callback DoneCallback
     */
    override fun saveInBackground(saveCallback: NCMBCallback) {

        val exeCallback = NCMBCallback { e, ncmbObj ->
            val responseData = ncmbObj as JSONObject
            //instance set data
            try {
                localData = responseData
            } catch (e: NCMBException) {
                throw e
            }
            mUpdateKeys.clear()
        }

        //connect
        val installationService = NCMBInstallationService()
        if (getObjectId() == null) {
            //new create
            installationService.createInstallationInBackground(
                localDeviceToken,
                mFields,
                exeCallback
            )
        } else {
            //update
            var updateJson: JSONObject? = null
            updateJson = try {
                createUpdateJsonData()
            } catch (e: JSONException) {
                throw IllegalArgumentException(e.message)
            }
//            installationService.updateInstallationInBackground(
//                getObjectId(),
//                updateJson,
//                exeCallback
//            )
        }
    }

    companion object {
        /**
         * currentInstallation fileName
         */
        const val INSTALLATION_FILENAME = "currentInstallation"

        /**
         * channels folder Name
         */
        const val CHANNELS_FOLDER_NAME = "channels"

        /**
         * request code
         */
        private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000

        /**
         * cannot get device token message
         */
        private const val CANNOT_GET_DEVICE_TOKEN_MESSAGE =
            "Can not get device token, please check your google-service.json"

        /**
         * push device
         */
        var installation: NCMBInstallation? = null
        var current: NCMBInstallation? = null
        val ignoreKeys = Arrays.asList(
            "objectId", "applicationName", "appVersion", "badge", "channels", "deviceToken",
            "deviceType", "sdkVersion", "timeZone", "createDate", "updateDate", "acl", "pushType"
        )
        //endregion
        /**
         * Get current installation object
         *
         * @return NCMBInstallation object that is created from data that is saved to local file.<br></br>
         * If local file is not available, it returns empty NCMBInstallation object
         */

        fun getCurrentInstallation(): NCMBInstallation? {
            //null check
            checkNCMBContext()
            try {
                //create currentInstallation
                if (installation == null) {
                    installation = NCMBInstallation()
                    //ローカルファイルに配信端末情報があれば取得、なければ新規作成
                    val currentInstallationFile = create(INSTALLATION_FILENAME)
                    if (currentInstallationFile.exists()) {
                        //ローカルファイルから端末情報を取得
                        val localData = readFile(currentInstallationFile)
                        installation = NCMBInstallation(localData)
                    }
                }
            } catch (error: Exception) {
                Log.e("Error", error.toString())
            }
            return installation
        }
    }
}
