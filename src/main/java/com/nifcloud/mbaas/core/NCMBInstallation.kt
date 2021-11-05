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
import org.json.JSONException
import org.json.JSONObject
import java.util.Arrays

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
            if (mFields.isNull(APPLICATION_NAME)) {
                null
            } else mFields.getString(APPLICATION_NAME)
        } catch (e: JSONException) {
            throw NCMBException(IllegalArgumentException(e.message))
        }
        set(value) {
            try {
                mFields.put(APPLICATION_NAME, value)
                mUpdateKeys.add(APPLICATION_NAME)
            } catch (e: JSONException) {
                throw NCMBException(IllegalArgumentException(e.message))
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
                if (mFields.isNull(APP_VERSION)) {
                    null
                } else mFields.getString(APP_VERSION)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        set(value) {
            try {
                mFields.put(APP_VERSION, value)
                mUpdateKeys.add(APP_VERSION)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
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
    var badge: Int?
        get() {
            return try {
                if (mFields.isNull(BADGE)) {
                    0
                } else mFields.getInt(BADGE)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        set(value) {
            try {
                mFields.put(BADGE, value)
                mUpdateKeys.add(BADGE)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
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
    var channels: Array<String>?
        get() {
            return try {
                if (mFields.isNull(CHANNELS)) {
                    null
                } else arrayOf(mFields.getString(CHANNELS))
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        set(value) {
            try {
                mFields.put(CHANNELS, value)
                mUpdateKeys.add(CHANNELS)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
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
    var deviceType: String
        get() {
            return try {
                if (mFields.isNull(DEVICE_TYPE)) {
                    ""
                } else mFields.getString(DEVICE_TYPE)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        set(value) {
            try {
                mFields.put(DEVICE_TYPE, value)
                mUpdateKeys.add(DEVICE_TYPE)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
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
    internal var localDeviceToken: String
        get() {
            return try {
                if (mFields.isNull(DEVICE_TOKEN)) {
                    ""
                } else mFields.getString(DEVICE_TOKEN)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        set(value) {
            try {
                mFields.put(DEVICE_TOKEN, value)
                mUpdateKeys.add(DEVICE_TOKEN)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }

//    /**
//     * Get device token
//     *
//     * @return device token
//     */
//    val localDeviceToken: String?
//        get() {
//            return try {
//                if (mFields.isNull(DEVICE_TOKEN)) {
//                    null
//                } else mFields.getString(DEVICE_TOKEN)
//            } catch (error: JSONException) {
//                throw NCMBException(IllegalArgumentException(error.message))
//            }
//        }
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
                if (mFields.isNull(SDK_VERSION)) {
                    null
                } else mFields.getString(SDK_VERSION)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        set(value) {
            try {
                mFields.put(SDK_VERSION, value)
                mUpdateKeys.add(SDK_VERSION)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
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
                if (mFields.isNull(TIME_ZONE)) {
                    null
                } else mFields.getString(TIME_ZONE)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        set(value) {
            try {
                mFields.put(TIME_ZONE, value)
                mUpdateKeys.add(TIME_ZONE)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
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
            throw NCMBException(IllegalArgumentException(error.message))
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
    //todo
//    @Throws(NCMBException::class)
//    fun saveInstallation() {
//    }

    /**
     * Save installation object inBackground
     *
     * @param callback DoneCallback
     */
    override fun saveInBackground(saveCallback: NCMBCallback) {
        //connect
        val installationService = NCMBInstallationService()
        val objectId = getObjectId()
        if (objectId == null) {
            val deviceToken = localDeviceToken
            if(deviceToken != null) {
                //new create
                installationService.saveInstallationInBackground(
                    this,
                    deviceToken,
                    this.mFields,
                    saveCallback
                )
            }
            else {
                throw NCMBException(IllegalArgumentException("registrationId is must not be null."));
            }
        } else {
            try {
                //update
                val updateJson = try {
                    createUpdateJsonData()
                } catch (e: JSONException) {
                    throw NCMBException(IllegalArgumentException(e.message))
                }
                installationService.updateInstallationInBackground(
                    this,
                    objectId,
                    updateJson,
                    saveCallback
                )
            } catch (e: JSONException) {
                saveCallback.done(
                    NCMBException(e)
                )
            }
        }
    }

    companion object {

        const val APPLICATION_NAME = "applicationName"
        const val APP_VERSION = "appVersion"
        const val BADGE = "badge"
        const val CHANNELS = "channels"
        const val DEVICE_TYPE = "deviceType"
        const val DEVICE_TOKEN = "deviceToken"
        const val SDK_VERSION = "sdkVersion"
        const val TIME_ZONE= "timeZone"
        const val PUSH_TYPE= "pushType"
        const val ANDROID= "android"
        const val FCM= "fcm"

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
        fun getCurrentInstallation(): NCMBInstallation {
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
            return installation as NCMBInstallation
        }
    }
}
