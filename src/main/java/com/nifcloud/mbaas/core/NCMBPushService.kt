/*
 * Copyright 2017-2018 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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
class NCMBPushService : NCMBService() {
    /**
     * Create push object
     *
     * @param params push parameters
     * @return JSONObject
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun sendPush(params: JSONObject?): JSONObject {
        if (params == null) {
            throw NCMBException(NCMBException.INVALID_JSON, "params must not be null")
        } else if (params.has("deliveryTime") && params.has("immediateDeliveryFlag")) {
            throw NCMBException(
                NCMBException.INVALID_JSON,
                "'deliveryTime' and 'immediateDeliveryFlag' can not be set at the same time."
            )
        }
        if (!params.has("deliveryTime")) {
            try {
                params.put("immediateDeliveryFlag", true)
            } catch (e: JSONException) {
                throw NCMBException(NCMBException.INVALID_JSON, "prams invalid JSON.")
            }
        }
        val request = createRequestParams(null, params, null, NCMBRequest.HTTP_METHOD_POST)
        val response = sendRequest(request)
        when (response) {
            is NCMBResponse.Success -> {
                return response.data
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
    fun updatePush(pushId: String?, params: JSONObject?): JSONObject {
        if (pushId == null) {
            throw NCMBException(NCMBException.INVALID_JSON, "pushId must no be null")
        } else if (params == null) {
            throw NCMBException(NCMBException.INVALID_JSON, "params must no be null")
        } else if (params.has("deliveryTime") && params.has("immediateDeliveryFlag")) {
            throw NCMBException(
                NCMBException.INVALID_JSON,
                "'deliveryTime' and 'immediateDeliveryFlag' can not be set at the same time."
            )
        }
        val request = createRequestParams(pushId, params, null, NCMBRequest.HTTP_METHOD_PUT)
        val response = sendRequest(request)
        when (response) {
            is NCMBResponse.Success -> {
                return response.data
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
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