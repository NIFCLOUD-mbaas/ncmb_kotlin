/*
 * Copyright 2017-2022 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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
import com.google.android.gms.tasks.OnCanceledListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*


/**
 * Installation information handle class
 *
 * NCMBInstallation is used to retrieve and save, update the installation data.
 * Basic features are inherit from NCMBObject and NCMBBase
 */
class NCMBInstallation : NCMBObject {

    /**
     * Application name
     */
    var applicationName: String?
        /**
         * Get application name
         *
         * @return application name
         */
        get() = try {
            if (mFields.isNull(APPLICATION_NAME)) {
                null
            } else mFields.getString(APPLICATION_NAME)
        } catch (e: JSONException) {
            throw NCMBException(IllegalArgumentException(e.message))
        }
        /**
         * Set application name
         *
         * @param value applicationName
         */
        set(value) {
            try {
                mFields.put(APPLICATION_NAME, value)
                mUpdateKeys.add(APPLICATION_NAME)
            } catch (e: JSONException) {
                throw NCMBException(IllegalArgumentException(e.message))
            }
        }


    /**
     * Application Version
     */
    var appVersion: String?
        /**
         * Get application version
         *
         * @return application Version
         */
        get() {
            return try {
                if (mFields.isNull(APP_VERSION)) {
                    null
                } else mFields.getString(APP_VERSION)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        /**
         * Set application version
         * ReadOnly field
         *
         * @param value appVersion
         */
        set(value) {
            try {
                mFields.put(APP_VERSION, value)
                mUpdateKeys.add(APP_VERSION)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }

    /**
     * Badge number (iOS)
     */
    var badge: Int?
        /**
         * Get badge count
         *
         * @return badge count
         */
        get() {
            return try {
                if (mFields.isNull(BADGE)) {
                    0
                } else mFields.getInt(BADGE)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        /**
         * Set badge count
         *
         * @param value applicationName
         */
        set(value) {
            try {
                mFields.put(BADGE, value)
                mUpdateKeys.add(BADGE)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }

    /**
     * Channels information
     */
    var channels: JSONArray?
        /**
         * Get channels
         *
         * @return channels
         */
        get() {
            return try {
                if (mFields.isNull(CHANNELS)) {
                    null
                } else {
                    mFields.getJSONArray(CHANNELS)
                }
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        /**
         * Set channels
         *
         * @param value channels
         */
        set(value) {
            try {
                mFields.put(CHANNELS, value)
                mUpdateKeys.add(CHANNELS)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }

    /**
     * Device type information
     */
    var deviceType: String
        /**
         * Get device type
         *
         * @return device type
         */
        get() {
            return try {
                if (mFields.isNull(DEVICE_TYPE)) {
                    ""
                } else mFields.getString(DEVICE_TYPE)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        /**
         * Set device type
         * ReadOnly field
         *
         * @param value device type
         */
        set(value) {
            try {
                mFields.put(DEVICE_TYPE, value)
                mUpdateKeys.add(DEVICE_TYPE)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }

    /**
     * Device token
     */
    var deviceToken: String
        /**
         * Get device token
         *
         * @return device token
         */
        get() {
            return try {
                if (mFields.isNull(DEVICE_TOKEN)) {
                    ""
                } else mFields.getString(DEVICE_TOKEN)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        /**
         * Set device token
         *
         * @param value device token
         */
        set(value) {
            try {
                mFields.put(DEVICE_TOKEN, value)
                mUpdateKeys.add(DEVICE_TOKEN)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }


    /**
     * SDK Version information
     */
    var sdkVersion: String?
        /**
         * Get SDK version
         *
         * @return SDK version
         */
        get() {
            return try {
                if (mFields.isNull(SDK_VERSION)) {
                    null
                } else mFields.getString(SDK_VERSION)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        /**
         * Set SDK version
         * ReadOnly field
         *
         * @param value SDKversion
         */
        set(value) {
            try {
                mFields.put(SDK_VERSION, value)
                mUpdateKeys.add(SDK_VERSION)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }

    /**
     * Timezone info
     */
    var timeZone: String?
        /**
         * Get timezone
         *
         * @return timezone
         */
        get() {
            return try {
                if (mFields.isNull(TIME_ZONE)) {
                    null
                } else mFields.getString(TIME_ZONE)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        /**
         * Set timezone
         * ReadOnly field
         *
         * @param value timezone
         */
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

    /**
     * Constructor method
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
    internal constructor(params: JSONObject) : super("installation", params) {
        mIgnoreKeys = ignoreKeys
    }

    /**
     * This method is not available because of In synchronous processing Acquisition of deviceToken is deprecated.
     *
     * @throws NCMBException exception from NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    override fun save(){
        throw NCMBException(UnsupportedOperationException("For NCMBInstallation class this method cannot be used. Please use saveInBackground() instead."))
    }

    /**
     * Save installation object in Background
     *
     * @param callback Save Callback
     */
    override fun saveInBackground(saveCallback: NCMBCallback) {
        //connect
        val installationService = NCMBInstallationService()
        val objectId = getObjectId()
        if (objectId == null) {
            val deviceToken = deviceToken
            if(deviceToken != "") {
                //new create
                installationService.saveInstallationInBackground(
                    this,
                    deviceToken,
                    this.createRegisterJsonData(),
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
    /**
     * Get device token in background
     *
     * @param callback TokenCallback
     */
    fun getDeviceTokenInBackground(callback: NCMBCallback) {
        val instance = DeviceTokenCallbackQueue.instance
        if(instance != null) {
            if (instance.isDuringSaveInstallation) {
                instance.addQueue(callback)
                return
            }
        }
        val context = NCMB.getCurrentContext()
        if(context != null) {
            if (FirebaseApp.getApps(context.applicationContext).isEmpty()) {
                callback.done(NCMBException(IOException(CANNOT_GET_DEVICE_TOKEN_MESSAGE)))
                return
            }
        }
        val deviceToken = deviceToken
        if (deviceToken != "") {
            callback.done(null, deviceToken)
            return
        }
        getDeviceTokenInternalProcess(callback)
    }

    /**
     * Get device token (Internal Process)
     *
     * @param callback TokenCallback
     */
    internal fun getDeviceTokenInternalProcess(callback: NCMBCallback) {
        val context = NCMB.getCurrentContext()
        if(context!= null) {
            if (FirebaseApp.getApps(context.applicationContext).isNotEmpty()) {
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            callback.done(NCMBException(IOException(CANNOT_GET_DEVICE_TOKEN_MESSAGE)))
                            //return@OnCompleteListener
                        } else {
                            callback.done(null, task.result)
                        }
                        // Get new FCM registration token
                    }).addOnCanceledListener(OnCanceledListener {
                        fun onCanceled() {
                            callback.done(NCMBException(IOException(CANNOT_GET_DEVICE_TOKEN_MESSAGE)))
                        }
                    })
            } else {
                callback.done(NCMBException(IOException(CANNOT_GET_DEVICE_TOKEN_MESSAGE)))
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
        const val TIME_ZONE = "timeZone"
        const val PUSH_TYPE = "pushType"
        const val ANDROID = "android"
        const val FCM = "fcm"

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
        val ignoreKeys = Arrays.asList(
            "objectId", "updateDate", "acl"
        )

        /**
         * Get current installation object
         *
         * @return NCMBInstallation object that is created from data that is saved to local file.<br></br>
         * If local file is not available, it returns empty NCMBInstallation object
         */
        var currentInstallation: NCMBInstallation = NCMBInstallation()
            get() {
                if(field.getObjectId() == null){
                    try {
                        val installationService = NCMBInstallationService()
                        field = installationService.getCurrentInstallationFromFile()
                    } catch (error: NCMBException) {
                        throw NCMBException(error)
                    }
                }
                return field
            }
            internal set(value) {
                field = value
            }
    }

        @Throws(NCMBException::class)
        override fun fetch()  {
            val objectId = getObjectId()
            val installationService = NCMBInstallationService()
            if (objectId != null) {
                installationService.fetchInstallation(this, objectId)
            } else {
                throw NCMBException(IllegalArgumentException("objectId is must not be null."))
            }
        }

        @Throws(NCMBException::class)
        override fun fetchInBackground(fetchCallback: NCMBCallback) {
            val objectId = getObjectId()
            val installationService = NCMBInstallationService()
            if (objectId != null) {
                installationService.fetchInstallationInBackground(this, objectId, fetchCallback)
            } else {
                throw NCMBException(IllegalArgumentException("objectId is must not be null."))
            }
        }

        /**
         * save current NCMBObject to data store
         * @throws NCMBException exception from NIFCLOUD mobile backend
         */
        @Throws(NCMBException::class)
        override fun delete() {
            val objectId = getObjectId()
            val installationService = NCMBInstallationService()
            if (objectId != null) {
                installationService.deleteInstallation(objectId)
            } else {
                throw NCMBException(IllegalArgumentException("objectId is must not be null."))
            }
        }

        @Throws(NCMBException::class)
        override fun deleteInBackground(deleteCallback: NCMBCallback) {
            val objectId = getObjectId()
            val installationService = NCMBInstallationService()
            if (objectId != null) {
                installationService.deleteInstallationInBackground(objectId, deleteCallback)
            } else {
                deleteCallback.done(NCMBException(IllegalArgumentException("objectId is must not be null.")))
            }
        }
}
