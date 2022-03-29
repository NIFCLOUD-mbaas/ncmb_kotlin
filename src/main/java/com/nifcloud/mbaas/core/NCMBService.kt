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

import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.net.URLEncoder


/**
 * Service parent class.
 *
 * To do main service tasks, base class for NCMBObjectService and others.
 *
 */

internal open class NCMBService {

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
        var query: JSONObject = JSONObject(),
        var callback: NCMBCallback? = null,
        var handler: NCMBHandler? = null
    )

    /**
     * Send request in asynchronously
     *
     * @param url         URL
     * @param method      http method
     * @param params      content body
     * @param contentType content type
     * @param query       query
     */

    fun sendRequest(
        url: String,
        method: String,
        params: JSONObject,
        contentType: String,
        query: JSONObject
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
        return if (contentType == NCMBRequest.HEADER_CONTENT_TYPE_FILE) {
            connection.sendRequestForUploadFile()
        }else {
            connection.sendRequest()
        }
    }

    /**
     * Send request in sync
     *
     * @param params      request params
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
     * @param params      content body
     * @param contentType content type
     * @param query       query
     * @param callback    callback on finished
     * @param handler     after-connection tasks
     */
    fun sendRequestAsync(
        url: String,
        method: String,
        params: JSONObject,
        contentType: String,
        query: JSONObject,
        callback: NCMBCallback?,
        handler: NCMBHandler?
    ){
        if (NCMB.SESSION_TOKEN == null) {
            NCMB.SESSION_TOKEN = NCMBUser().sessionToken
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
        //print("In sendRequestAsync check ConntentType:" + contentType + "|" +NCMBRequest.HEADER_CONTENT_TYPE_FILE )
        if(callback != null && handler != null) {
            if(contentType == NCMBRequest.HEADER_CONTENT_TYPE_FILE) {
                connection.sendRequestAsynchronouslyForUploadFile(callback, handler)
            }else {
                connection.sendRequestAsynchronously(callback, handler)
            }
        }
        else{
            throw NCMBException(NCMBException.INVALID_FORMAT, "Need to set callback and handler for an inbackground method.")
        }
    }

    /**
     * Send request in asynchronously
     *
     * @param params       Parameters
     * @param callback     Callback
     * @param handler     SDK Handler
     */
    fun sendRequestAsync(params: RequestParams,callback: NCMBCallback,handler: NCMBHandler ) {
        return this.sendRequestAsync(
            params.url,
            params.method,
            params.params,
            params.contentType,
            params.query,
            callback,
            handler
        )
    }

    /**
     * Send request in asynchronously
     *
     * @param params      Request Parameters For Async method
     */
    fun sendRequestAsync(params: RequestParams) {
        return this.sendRequestAsync(
            params.url,
            params.method,
            params.params,
            params.contentType,
            params.query,
            params.callback,
            params.handler
        )
    }
    
    //クエリ用のURLに付ける文字列を作成
    @Throws(NCMBException::class)
    fun queryUrlStringGenerate(query:JSONObject): String {
        val queryItemlist: MutableList<String> = mutableListOf()
        for (key in query.keys()) {
            try {
                val value = query.get(key).toString()
                queryItemlist.add("%s=%s".format(key, URLEncoder.encode(value, "utf-8")))
            } catch (e: JSONException) {
                throw NCMBException(NCMBException.INVALID_JSON, "Invalid JSON format.")
            }
        }
        return queryItemlist.joinToString(separator = "&")
    }
}
