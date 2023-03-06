/*
 * Copyright 2017-2023 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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
import java.util.*

/**
 * Service class for push notification api
 */
internal class NCMBPushService : NCMBObjectService() {


    /** service path for API category  */
    override val SERVICE_PATH = "push"
    

    /**
     * Constructor
     *
     * @param context NCMBContext object for current context
     */
    init {
        this.mServicePath = this.SERVICE_PATH
    }

    /**
     * Create push object
     *
     * @param params push parameters
     * @return JSONObject
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun sendPush(params: JSONObject): JSONObject {
        // val request = createRequestParams(null, params, null, NCMBRequest.HTTP_METHOD_POST)
        val request = createRequestParamsPush(null, params, null, NCMBRequest.HTTP_METHOD_POST, null, null)
        val response = sendRequest(request)
        when (response) {
            is NCMBResponse.Success -> {
                return response.data as JSONObject
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
    }

    /**
     * Update push object
     * It can only be updated before delivery
     *
     * @param pushId object id
     * @param params update information
     * @return JSONObject
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun updatePush(pushId: String?, params: JSONObject): JSONObject {
        val request = createRequestParamsPush(pushId, params, null, NCMBRequest.HTTP_METHOD_PUT, null, null)
        val response = sendRequest(request)
        when (response) {
            is NCMBResponse.Success -> {
                return response.data as JSONObject
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
    }

    /**
     * Open push registration in background
     *
     * @param pushId   open push object id
     * @param callback NCMBCallback
     */
    @Throws(NCMBException::class)
    fun sendPushReceiptStatusInBackground(pushId: String?, callback: NCMBCallback) {
        try {
            //null check
            if (pushId == null) {
                throw NCMBException(NCMBException.REQUIRED, "pushId is must not be null.")
            }
            val params: JSONObject = try {
                JSONObject("{deviceType:android}")
            } catch (e: JSONException) {
                throw NCMBException(NCMBException.INVALID_JSON, "Invalid JSON format.")
            }

            //connect
            val paramsRequest = createRequestParamsPush(
                "$pushId/openNumber",
                params,
                null,
                NCMBRequest.HTTP_METHOD_POST,
                null,
                null
            )
            val pushReceiptStatusHandler = NCMBHandler { callback, response ->
                when (response) {
                    is NCMBResponse.Success -> {
                        try {
                            callback.done(null, response.data as JSONObject)
                        } catch (e: NCMBException) {
                            throw e
                        }
                    }
                    is NCMBResponse.Failure -> {
                        callback.done(response.resException)
                    }
                }
            }
            sendRequestAsync(paramsRequest, callback, pushReceiptStatusHandler)
        } catch (error: NCMBException) {
            callback.done(error)
        }
    }

    /**
     * Setup params to push
     *
     * @param pushId         push id
     * @param params         push parameters
     * @param queryParams    query parameters
     * @param method         method
     * @return parameters in object
     */
    @Throws(NCMBException::class)
    fun createRequestParamsPush(
        pushId: String?,
        params: JSONObject,
        queryParams: JSONObject?,
        method: String,
        callback: NCMBCallback?,
        handler: NCMBHandler?
    ): RequestParams {

        //url set
        val url: String = if (pushId != null) {
            //PUT,GET(fetch)
            NCMB.getApiBaseUrl() + mServicePath + "/" + pushId
        } else {
            //POST,GET(search)
            NCMB.getApiBaseUrl() + this.mServicePath
        }
        return RequestParams(
            url = url,
            method = method,
            params = params,
            contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON,
            callback = callback,
            handler = handler
        )
    }

    /**
     * Setup params to do find request for Query search functions
     *
     * @param className Class name
     * @param query JSONObject
     * @return parameters in object
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    override fun findObjectParams(className: String, query:JSONObject): RequestParams {
        var url = NCMB.getApiBaseUrl() + this.mServicePath
        if(query.length() > 0) {
            url = url.plus("?" + queryUrlStringGenerate(query))
        }
        val method = NCMBRequest.HTTP_METHOD_GET
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        return RequestParams(url = url, method = method, contentType = contentType, query=query)
    }

    @Throws(NCMBException::class)
    override fun createSearchResponseList(className: String, responseData: JSONObject): List<NCMBPush> {
        return try {
            val results = responseData.getJSONArray(NCMBQueryConstants.RESPONSE_PARAMETER_RESULTS)
            val array: MutableList<NCMBPush> = ArrayList()
            for (i in 0 until results.length()) {
                val tmpObj = NCMBPush(results.getJSONObject(i))
                array.add(tmpObj)
            }
            array
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_JSON, "Invalid JSON format.")
        }
    }
}


