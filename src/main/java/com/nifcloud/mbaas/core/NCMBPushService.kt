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

import com.nifcloud.mbaas.core.NCMBService.RequestParams
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Service class for push notification api
 */
class NCMBPushService : NCMBService() {
    /**
     * Constructor
     *
     * @param context NCMBContext object for current context
     */
//    fun NCMBPushService(context: NCMBContext?) {
//        super(context)
//        mServicePath = SERVICE_PATH
//    }
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

//    /**
//     * Create push object in background
//     *
//     * @param params   push parameters
//     * @param callback ExecuteServiceCallback
//     */
//    fun sendPushInBackground(params: JSONObject?, callback: ExecuteServiceCallback?) {
//        try {
//            if (params == null) {
//                throw NCMBException(NCMBException.INVALID_JSON, "params must not be null")
//            } else if (params.has("deliveryTime") && params.has("immediateDeliveryFlag")) {
//                throw NCMBException(
//                    NCMBException.INVALID_JSON,
//                    "'deliveryTime' and 'immediateDeliveryFlag' can not be set at the same time"
//                )
//            }
//            if (!params.has("deliveryTime")) {
//                try {
//                    params.put("immediateDeliveryFlag", true)
//                } catch (e: JSONException) {
//                    throw NCMBException(NCMBException.INVALID_JSON, "prams invalid JSON")
//                }
//            }
//
//            //connect
//            val request = createRequestParams(null, params, null, NCMBRequest.HTTP_METHOD_POST)
//            sendRequestAsync(request, object : PushServiceCallback(this, callback) {
//                fun handleResponse(response: NCMBResponse) {
//                    val callback: ExecuteServiceCallback? = mCallback as ExecuteServiceCallback?
//                    if (callback != null) {
//                        callback.done(response.responseData, null)
//                    }
//                }
//
//                fun handleError(e: NCMBException?) {
//                    val callback: ExecuteServiceCallback? = mCallback as ExecuteServiceCallback?
//                    if (callback != null) {
//                        callback.done(null, e)
//                    }
//                }
//            })
//        } catch (error: NCMBException) {
//            if (callback != null) {
//                callback.done(null, error)
//            }
//        }
//    }
//
//    /**
//     * Update push object
//     * It can only be updated before delivery
//     *
//     * @param pushId object id
//     * @param params update information
//     * @return JSONObject
//     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
//     */
//    @Throws(NCMBException::class)
//    fun updatePush(pushId: String?, params: JSONObject?): JSONObject {
//        if (pushId == null) {
//            throw NCMBException(NCMBException.INVALID_JSON, "pushId must no be null")
//        } else if (params == null) {
//            throw NCMBException(NCMBException.INVALID_JSON, "params must no be null")
//        } else if (params.has("deliveryTime") && params.has("immediateDeliveryFlag")) {
//            throw NCMBException(
//                NCMBException.INVALID_JSON,
//                "'deliveryTime' and 'immediateDeliveryFlag' can not be set at the same time."
//            )
//        }
//        val request = createRequestParams(pushId, params, null, NCMBRequest.HTTP_METHOD_PUT)
//        val response = sendRequest(request)
//        if (response.statusCode !== NCMBPushService.Companion.HTTP_STATUS_PUSH_UPDATED) {
//            throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Updated failed.")
//        }
//        return response.responseData
//    }
//
//    /**
//     * Update push object in background
//     * It can only be updated before delivery
//     *
//     * @param pushId   object id
//     * @param params   update information
//     * @param callback ExecuteServiceCallback
//     */
//    fun updatePushInBackground(
//        pushId: String?,
//        params: JSONObject?,
//        callback: ExecuteServiceCallback?
//    ) {
//        try {
//            if (pushId == null) {
//                throw NCMBException(NCMBException.INVALID_JSON, "pushId must no be null")
//            } else if (params == null) {
//                throw NCMBException(NCMBException.INVALID_JSON, "params must no be null")
//            } else if (params.has("deliveryTime") && params.has("immediateDeliveryFlag")) {
//                throw NCMBException(
//                    NCMBException.INVALID_JSON,
//                    "'deliveryTime' and 'immediateDeliveryFlag' can not be set at the same time."
//                )
//            }
//
//            //connect
//            val request = createRequestParams(pushId, params, null, NCMBRequest.HTTP_METHOD_PUT)
//            sendRequestAsync(request, object : PushServiceCallback(this, callback) {
//                fun handleResponse(response: NCMBResponse) {
//                    val callback: ExecuteServiceCallback? = mCallback as ExecuteServiceCallback?
//                    if (callback != null) {
//                        callback.done(response.responseData, null)
//                    }
//                }
//
//                fun handleError(e: NCMBException?) {
//                    val callback: ExecuteServiceCallback? = mCallback as ExecuteServiceCallback?
//                    if (callback != null) {
//                        callback.done(null, e)
//                    }
//                }
//            })
//        } catch (error: NCMBException) {
//            if (callback != null) {
//                callback.done(null, error)
//            }
//        }
//    }
//
//    /**
//     * Delete push object
//     *
//     * @param pushId object id
//     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
//     */
//    @Throws(NCMBException::class)
//    fun deletePush(pushId: String?) {
//        //null check
//        if (pushId == null) {
//            throw NCMBException(NCMBException.INVALID_JSON, "pushId is must not be null.")
//        }
//
//        //connect
//        val request = createRequestParams(pushId, null, null, NCMBRequest.HTTP_METHOD_DELETE)
//        val response = sendRequest(request)
//        if (response.statusCode !== NCMBPushService.Companion.HTTP_STATUS_PUSH_DELETED) {
//            throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Deleted failed.")
//        }
//    }
//
//    /**
//     * Delete push object in background
//     *
//     * @param pushId   objectId
//     * @param callback ActionCallback
//     */
//    fun deletePushInBackground(pushId: String?, callback: DoneCallback?) {
//        try {
//            //null check
//            if (pushId == null) {
//                throw NCMBException(NCMBException.INVALID_JSON, "pushId is must not be null.")
//            }
//
//            //connect
//            val request = createRequestParams(pushId, null, null, NCMBRequest.HTTP_METHOD_DELETE)
//            sendRequestAsync(request, object : PushServiceCallback(this, callback) {
//                fun handleResponse(response: NCMBResponse?) {
//                    val callback: DoneCallback? = mCallback as DoneCallback?
//                    if (callback != null) {
//                        callback.done(null)
//                    }
//                }
//
//                fun handleError(e: NCMBException?) {
//                    val callback: DoneCallback? = mCallback as DoneCallback?
//                    if (callback != null) {
//                        callback.done(e)
//                    }
//                }
//            })
//        } catch (error: NCMBException) {
//            if (callback != null) {
//                callback.done(error)
//            }
//        }
//    }
//
//    /**
//     * Get push object
//     *
//     * @param pushId object id
//     * @return JSONObject
//     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
//     */
//    @Throws(NCMBException::class)
//    fun fetchPush(pushId: String?): NCMBPush {
//        //null check
//        if (pushId == null) {
//            throw NCMBException(NCMBException.INVALID_JSON, "pushId is must not be null.")
//        }
//
//        //connect
//        val request = createRequestParams(pushId, null, null, NCMBRequest.HTTP_METHOD_GET)
//        val response = sendRequest(request)
//        if (response.statusCode !== NCMBPushService.Companion.HTTP_STATUS_PUSH_GOTTEN) {
//            throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Gotten failed.")
//        }
//        return NCMBPush(response.responseData)
//    }
//
//    /**
//     * Get push object in background
//     *
//     * @param pushId   object id
//     * @param callback ExecuteServiceCallback
//     */
//    fun fetchPushInBackground(pushId: String?, callback: FetchCallback?) {
//        try {
//            if (pushId == null) {
//                throw NCMBException(NCMBException.INVALID_JSON, "pushId must no be null")
//            }
//
//            //connect
//            val request = createRequestParams(pushId, null, null, NCMBRequest.HTTP_METHOD_GET)
//            sendRequestAsync(request, object : PushServiceCallback(this, callback) {
//                fun handleResponse(response: NCMBResponse) {
//                    val callback: FetchCallback<NCMBPush>? = mCallback as FetchCallback?
//                    if (callback != null) {
//                        callback.done(NCMBPush(response.responseData), null)
//                    }
//                }
//
//                fun handleError(e: NCMBException?) {
//                    if (callback != null) {
//                        callback.done(null, e)
//                    }
//                }
//            })
//        } catch (error: NCMBException) {
//            if (callback != null) {
//                callback.done(null, error)
//            }
//        }
//    }
//
//    /**
//     * Search push
//     *
//     * @param conditions search conditions
//     * @return List
//     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
//     */
//    @Throws(NCMBException::class)
//    fun searchPush(conditions: JSONObject?): List<*> {
//        //connect
//        val request = createRequestParams(null, null, conditions, NCMBRequest.HTTP_METHOD_GET)
//        val response = sendRequest(request)
//        if (response.statusCode !== NCMBPushService.Companion.HTTP_STATUS_PUSH_GOTTEN) {
//            throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Gotten failed.")
//        }
//        //return the value of the key 'results'
//        return createSearchResults(response.responseData)
//    }
//
//    /**
//     * Search installations in background
//     *
//     * @param conditions search conditions
//     * @param callback   ExecuteServiceCallback
//     */
//    fun searchPushInBackground(conditions: JSONObject?, callback: SearchPushCallback?) {
//        try {
//            val request = createRequestParams(null, null, conditions, NCMBRequest.HTTP_METHOD_GET)
//            sendRequestAsync(request, object : PushServiceCallback(this, callback) {
//                fun handleResponse(response: NCMBResponse) {
//                    //return the value of the key 'results'
//                    var results: ArrayList<NCMBPush?>? = null
//                    try {
//                        results = createSearchResults(response.responseData)
//                    } catch (e: NCMBException) {
//                        callback.done(null, e)
//                    }
//                    val callback: SearchPushCallback? = mCallback as SearchPushCallback?
//                    if (callback != null) {
//                        callback.done(results, null)
//                    }
//                }
//
//                fun handleError(e: NCMBException?) {
//                    val callback: SearchPushCallback? = mCallback as SearchPushCallback?
//                    if (callback != null) {
//                        callback.done(null, e)
//                    }
//                }
//            })
//        } catch (error: NCMBException) {
//            if (callback != null) {
//                callback.done(null, error)
//            }
//        }
//    }
//
//    /**
//     * Open push registration in background
//     *
//     * @param pushId   open push object id
//     * @param callback ExecuteServiceCallback
//     */
//    fun sendPushReceiptStatusInBackground(pushId: String?, callback: ExecuteServiceCallback?) {
//        try {
//            //null check
//            if (pushId == null) {
//                throw NCMBException(NCMBException.INVALID_JSON, "pushId is must not be null.")
//            }
//            val params: JSONObject
//            params = try {
//                JSONObject("{deviceType:android}")
//            } catch (e: JSONException) {
//                throw NCMBException(NCMBException.INVALID_JSON, "prams invalid JSON")
//            }
//
//            //connect
//            val request = createRequestParams(
//                "$pushId/openNumber",
//                params,
//                null,
//                NCMBRequest.HTTP_METHOD_POST
//            )
//            sendRequestAsync(request, object : PushServiceCallback(this, callback) {
//                fun handleResponse(response: NCMBResponse) {
//                    val callback: ExecuteServiceCallback? = mCallback as ExecuteServiceCallback?
//                    if (callback != null) {
//                        callback.done(response.responseData, null)
//                    }
//                }
//
//                fun handleError(e: NCMBException?) {
//                    val callback: ExecuteServiceCallback? = mCallback as ExecuteServiceCallback?
//                    if (callback != null) {
//                        callback.done(null, e)
//                    }
//                }
//            })
//        } catch (error: NCMBException) {
//            if (callback != null) {
//                callback.done(null, error)
//            }
//        }
//    }

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

//    /**
//     * Create search results
//     *
//     * @param responseData API response data
//     * @return List
//     * @throws NCMBException
//     */
//    @Throws(NCMBException::class)
//    fun createSearchResults(responseData: JSONObject): ArrayList<NCMBPush?> {
//        return try {
//            val results = responseData.getJSONArray("results")
//            val array = ArrayList<NCMBPush?>()
//            for (i in 0 until results.length()) {
//                val push = NCMBPush(results.getJSONObject(i))
//                array.add(push)
//            }
//            array
//        } catch (e: JSONException) {
//            throw NCMBException(NCMBException.INVALID_JSON, "Invalid JSON format.")
//        }
//    }

    /**
     * Remove the keys that can not be set to params
     *
     * @param params Parameter
     */
    fun removeNotSetKeys(params: JSONObject) {
        val removeKeys = Arrays.asList(
            "objectId",
            "deliveryPlanNumber",
            "deliveryNumber",
            "status",
            "error",
            "createDate",
            "updateDate"
        )
        for (i in removeKeys.indices) {
            if (params.has(removeKeys[i])) {
                params.remove(removeKeys[i])
            }
        }
    }

    companion object {
        /** service path for API category  */
        const val SERVICE_PATH = "push"

        /** Status code of push created  */
        const val HTTP_STATUS_PUSH_CREATED = 201

        /** Status code of push updated  */
        const val HTTP_STATUS_PUSH_UPDATED = 200

        /** Status code of push deleted  */
        const val HTTP_STATUS_PUSH_DELETED = 200

        /** Status code of push gotten  */
        const val HTTP_STATUS_PUSH_GOTTEN = 200

        /** Status code of push receiptStatus  */
        const val HTTP_STATUS_PUSH_RECEIPTSTATUS = 200
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