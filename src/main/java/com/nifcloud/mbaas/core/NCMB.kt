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

import android.content.Context
import android.os.Build
import android.util.Log

/**
 * A class of ncmb_kotlin.
 *
 * This class is do initialize the SDK and necessary setting up to start to connect to NCMB.
 *
 */


class NCMB {

    companion object Settings{
        /**
         * Name of this SDK
         */
        const val SDK_NAME = "Kotlin"

        /**
         * Version of this SDK
         */
        const val SDK_VERSION = "1.0.0"

        /**
         * Prefix of keys in metadata for NCMB settings
         */
        const val METADATA_PREFIX = "com.nifcloud.mbaas."

        /**
         * Default base URL of API
         */
        const val DEFAULT_DOMAIN_URL = "https://mbaas.api.nifcloud.com/"

        /**
         * Default API version
         */
        const val DEFAULT_API_VERSION = "2013-09-01"

        /**
         * Default API timeout
         */
        const val DEFAULT_API_TIMEOUT = 10000

        /**
         * OAuth type of Twitter
         */
        const val OAUTH_TWITTER = "twitter"

        /**
         * OAuth type of Facebook
         */
        const val OAUTH_FACEBOOK = "facebook"

        /**
         * OAuth type of Google
         */
        const val OAUTH_GOOGLE = "google"

        /**
         * Anonymous authentication
         */
        const val OAUTH_ANONYMOUS = "anonymous"


        // SharedPreferences file name
        private const val PREFERENCE_FILE_NAME = "NCMB"

        var CURRENT_CONTEXT: Context? = null
        var APPLICATION_KEY = ""
        var CLIENT_KEY = ""
        var API_BASE_URL = ""
        private var DOMAINURL = ""
        private var APIVERSION = ""
        var SESSION_TOKEN: String? = null
        var TIMEOUT = 0
        var USER_ID: String? = ""

        /**
         * apikeyの初期化
         */
        fun initialize(
            context: Context,
            applicationKey: String,
            clientKey: String,
            domainUrl: String = DEFAULT_DOMAIN_URL,
            apiVersion: String = DEFAULT_API_VERSION
        ){
            APPLICATION_KEY = applicationKey
            CLIENT_KEY = clientKey
            DOMAINURL = domainUrl
            APIVERSION = apiVersion
            API_BASE_URL = "$DOMAINURL$APIVERSION/"
            TIMEOUT = DEFAULT_API_TIMEOUT
            CURRENT_CONTEXT = context
        }

        /**
         * push機能を利用するための初期設定メソッド
         * channelの設定、devicetokenの登録
         */
        fun initializePush(context: Context){
            //チャネル登録
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val utils = NCMBNotificationUtils(context)
                utils.settingDefaultChannels()
            }
            NCMBInstallation().getDeviceTokenInBackground(NCMBCallback { e, deviceToken ->
            if (e != null) {
                Log.d("error", "devicetoken GET ERROR : " + e.message)
            } else {
                Log.d("success", "deviceToken :$deviceToken")
                val installationObj = NCMBInstallation.currentInstallation
                if (installationObj != null) {
                    installationObj.deviceToken = deviceToken as String
                    installationObj.saveInBackground(NCMBCallback { e, ncmbInstallation ->
                        if (e != null) {
                            Log.d("error","installation SAVE ERROR : " + e.message)
                        } else {
                            val result = ncmbInstallation as NCMBInstallation
                            Log.d("success","installation DONE ObjectID :" + result.getObjectId())
                        }
                    })
                }
            }
        })
        }

        /**
         * APPLICATION_KEYをgetする.
         * @return APPLICATION_KEY.
         */
        fun getApplicationKey(): String {
            return APPLICATION_KEY
        }

        /**
         * CLIENT_KEYをgetする.
         * @return CLIENT_KEY.
         */
        fun getClientKey(): String {
            return CLIENT_KEY
        }

        /**
        * SESSION_TOKENをgetする.
        * @return CLIENT_KEY.
        */
        fun getSessionToken(): String? {
            return SESSION_TOKEN
        }

        /**
         * API BASE URLをgetする.
         * @return BASE_URL.
         */
        fun getApiBaseUrl(): String {
            return API_BASE_URL
        }

        /**
         * API TIMEOUTをgetする.
         * @return TIMEOUT.
         */
        fun getTimeOut(): Int {
            return TIMEOUT
        }

        /**
         * API TIMEOUTをsetする.
         * timeout Int
         * @return null.
         */
        fun setTimeOut(timeout: Int){
            TIMEOUT = timeout

        }

        /**
         * Get NCMBContext
         */
        fun getCurrentContext(): Context? {
            if (CURRENT_CONTEXT == null) {
                val context: Context? = NCMBApplicationController.applicationState
                CURRENT_CONTEXT = context
            }
            return CURRENT_CONTEXT
        }
    }
}
