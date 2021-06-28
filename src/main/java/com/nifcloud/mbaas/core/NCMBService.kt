package com.nifcloud.mbaas.core

import org.json.JSONObject

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
        var query: JSONObject = JSONObject()
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
        query: JSONObject,
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
}
