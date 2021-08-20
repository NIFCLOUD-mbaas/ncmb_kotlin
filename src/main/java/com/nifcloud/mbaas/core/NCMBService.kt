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

import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder


/**
 * A class of ncmb_kotlin.
 *
 * To do main service tasks, base class for NCMBObjectService and others.
 *
 */

open class NCMBService {

    /**
     * Service path of API URL
     */
    protected var mServicePath: String? = null

    /**
     * Data class for params of request
     */
    data class RequestParams(
        var url: String,
        var method: String,
        var params: JSONObject = JSONObject(),
        var contentType: String,
        var query : JSONObject = JSONObject()
    )

    /**
     * Send request in asynchronously
     *
     * @param url         URL
     * @param method      http method
     * @param params     contnt body
     * @param queryString query string
     * @param callback    callback on finished
     */

    fun sendRequest(
        url: String,
        method: String,
        params: JSONObject,
        contentType: String,
        query : JSONObject?
    ): NCMBResponse {
        val sessionToken: String? = NCMB.getSessionToken()
        val applicationKey: String = NCMB.getApplicationKey()
        val clientKey: String = NCMB.getClientKey()
        val timestamp = ""
        val request = NCMBRequest(
            url,
            method,
            params,
            contentType,
            query,
            sessionToken,
            applicationKey,
            clientKey,
            timestamp
        )
        val connection = NCMBConnection(request)
        val response = connection.sendRequest()
        return response
    }

    /**
     * Send request in asynchronously
     *
     * @param url         URL
     * @param method      http method
     * @param content     contnt body
     * @param queryString query string
     */
    fun sendRequest(params: RequestParams): NCMBResponse {
        return this.sendRequest(
            params.url,
            params.method,
            params.params,
            params.contentType,
            params.query
        )
    }

    /**
     * Send request in asynchronously
     *
     * @param url         URL
     * @param method      http method
     * @param content     contnt body
     * @param query       query
     * @param callback    callback on finished
     */
    fun sendRequestAsync(
        url: String,
        method: String,
        params: JSONObject,
        contentType: String,
        query : JSONObject,
        callback: NCMBCallback?,
        handler: NCMBHandler
    ){
        if (NCMB.SESSION_TOKEN == null) {
            NCMB.SESSION_TOKEN = NCMBUser().getSessionToken()
        }
        val sessionToken: String? = NCMB.getSessionToken()
        val applicationKey: String = NCMB.getApplicationKey()
        val clientKey: String = NCMB.getClientKey()
        val timestamp = ""
        val request = NCMBRequest(
            url,
            method,
            params,
            contentType,
            query,
            sessionToken,
            applicationKey,
            clientKey,
            timestamp
        )
        val connection = NCMBConnection(request)
        connection.sendRequestAsynchronously(callback, handler)
    }


    //クエリ用のURLに付ける文字列を作成
    @Throws(NCMBException::class)
    fun queryUrlStringGenerate(conditions:JSONObject): String {
        var queryUrlString = ""
        if (conditions != null) {
            val iter: Iterator<String> = conditions.keys()
            while (iter.hasNext()) {
                val key = iter.next()
                try {
                    val value: Any = conditions.get(key)
                    queryUrlString = queryUrlString.plus(
                        key + "=" + URLEncoder.encode(
                            value.toString(),
                            "utf-8"
                        ) + "&"
                    )
                } catch (e: JSONException) {
                    throw NCMBException(NCMBException.INVALID_JSON, "Invalid JSON format.")
                }
            }
            queryUrlString.dropLast(1)
            return queryUrlString
        } else {
            return ""
        }
    }


}
