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
internal class NCMBPushService : NCMBService() {
    /**
     * Create push object
     *
     * @param params push parameters
     * @return JSONObject
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun sendPush(params: JSONObject): JSONObject {
        val request = createRequestParams(null, params, null, NCMBRequest.HTTP_METHOD_POST)
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
        val request = createRequestParams(pushId, params, null, NCMBRequest.HTTP_METHOD_PUT)
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
            val paramsRequest = createRequestParams(
                "$pushId/openNumber",
                params,
                null,
                NCMBRequest.HTTP_METHOD_POST
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
     * Setup params to installation
     *
     * @param installationId installation id
     * @param params         installation parameters
     * @param queryParams    query parameters
     * @param method         method
     * @return parameters in object
     */
    @Throws(NCMBException::class)
    fun createRequestParams(
        installationId: String?,
        params: JSONObject,
        queryParams: JSONObject?,
        method: String
    ): RequestParams {

        //url set
        val url: String = if (installationId != null) {
            //PUT,GET(fetch)
            NCMB.getApiBaseUrl() + mServicePath + "/" + installationId
        } else {
            //POST,GET(search)
            NCMB.getApiBaseUrl() + this.mServicePath
        }
        return RequestParams(
            url = url,
            method = method,
            params = params,
            contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        )
    }

    companion object {
        /** service path for API category  */
        const val SERVICE_PATH = "push"
    }

    /**
     * Constructor
     *
     * @param context NCMBContext object for current context
     */
    init {
        mServicePath = SERVICE_PATH
    }
}
