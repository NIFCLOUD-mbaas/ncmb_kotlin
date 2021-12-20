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

import android.annotation.SuppressLint
import android.os.Build
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.SimpleTimeZone
import kotlin.collections.HashMap


/**
 * A class of ncmb_kotlin.
 *
 * NCMBRequest class is used to config api request
 *
 */

internal class NCMBRequest(
    var url: String,
    var method: String,
    var params: JSONObject = JSONObject(),
    var contentType: String = HEADER_CONTENT_TYPE_JSON,
    var query: JSONObject = JSONObject(),
    var sessionToken: String? = null,
    var applicationKey: String,
    var clientKey: String,
    var timestamp: String
) {

    /**
     * Get request signature
     *
     * @return request signature
     */
    var requestSignature: String? = ""

    /** リクエストヘッダーのリスト  */
    private val requestProperties =
        HashMap<String, String?>()

    fun getRequestProperty(key: String): String? {
        return this.requestProperties.get(key)
    }

    fun getAllRequestProperties(): HashMap<String, String?> {
        return this.requestProperties
    }

    companion object {
        // region Constant
        // HTTP method "GET"
        const val HTTP_METHOD_GET = "GET"

        // HTTP method "POST"
        const val HTTP_METHOD_POST = "POST"

        // HTTP method "PUT"
        const val HTTP_METHOD_PUT = "PUT"

        // HTTP method "DELETE"
        const val HTTP_METHOD_DELETE = "DELETE"

        //アプリケーションキー
        const val HEADER_APPLICATION_KEY = "X-NCMB-Application-Key"

        //シグネチャ
        const val HEADER_SIGNATURE = "X-NCMB-Signature"

        //タイムスタンプ
        const val HEADER_TIMESTAMP = "X-NCMB-Timestamp"

        //Access-Control-Allow-Origin
        const val HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin"

        //アプリセッショントークン
        const val HEADER_APPS_SESSION_TOKEN = "X-NCMB-Apps-Session-Token"

        //コンテントタイプ
        const val HEADER_CONTENT_TYPE = "Content-Type"

        //JSON形式のコンテントタイプの値
        const val HEADER_CONTENT_TYPE_JSON = "application/json"

        //SDKVersionのキー
        const val HEADER_SDK_VERSION = "X-NCMB-SDK-Version"

        //OSVersionのキー
        const val HEADER_OS_VERSION = "X-NCMB-OS-Version"

    }

    /**
     * コンストラクタ
     *
     * @param url            String
     * @param method         HTTPMethod
     * @param params         JSONObject
     * @param contentType    content-type
     * @param query          query
     * @param applicationKey applicationKey
     * @param clientKey      clientKey
     * @param timestamp      timestamp
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    init {
        //createHttpRequestで生成したRequestのヘッダ設定
        // コンテンツタイプ設定
        if (contentType != "" ) {
            requestProperties[HEADER_CONTENT_TYPE] = contentType
        } else {
            requestProperties[HEADER_CONTENT_TYPE] = HEADER_CONTENT_TYPE_JSON
        }
        // アプリケーションキー設定
        requestProperties[HEADER_APPLICATION_KEY] = applicationKey

        try {
            // タイムスタンプ生成/設定
            if (timestamp == "") {
                //timestamp引数なしコンストラクタの場合は現在時刻で生成する
                @SuppressLint("SimpleDateFormat") val df: DateFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
                df.setTimeZone(SimpleTimeZone(0, "GMT"))
                val ts = System.currentTimeMillis()
                timestamp = df.format(ts)
                requestProperties[HEADER_TIMESTAMP] = timestamp
            } else {
                requestProperties[HEADER_TIMESTAMP] = timestamp
            }

            // シグネチャ生成/設定
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val tmpNCMBSignatureCal = NCMBSignatureCal()
                val tmpUrl = URL(url)
                val queryMap: HashMap<String, String> = HashMap()
                for (key in query.keys())
                {
                    var value = query.get(key).toString();
                    queryMap.put(key, value)
                }
                var signature = tmpNCMBSignatureCal.calSignature(
                    this.method,
                    tmpUrl,
                    this.timestamp,
                    this.applicationKey,
                    this.clientKey,
                    queryMap
                )
                this.requestSignature = signature
                requestProperties[HEADER_SIGNATURE] = signature
            }

        } catch (e: UnsupportedEncodingException) {
            throw IllegalArgumentException(e.message)
        }
        // Access-Control-Allow-Origin設定
        requestProperties[HEADER_ACCESS_CONTROL_ALLOW_ORIGIN] = "*"
        // セッショントークン設定
        if (sessionToken != null && sessionToken?.length != null){
            this.requestProperties.put(HEADER_APPS_SESSION_TOKEN, sessionToken)
        }
        // 独自UserAgent設定
        requestProperties[HEADER_SDK_VERSION] = NCMB.SDK_NAME + "-" + NCMB.SDK_VERSION
        val osVersion = Build.VERSION.RELEASE
        requestProperties[HEADER_OS_VERSION] = "android-$osVersion"
    }
}
