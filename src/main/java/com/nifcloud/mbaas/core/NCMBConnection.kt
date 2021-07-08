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

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.net.URL


/**
 * A class of ncmb_kotlin.
 *
 * To do connection to NCMB and do request, receive response/error and process callback after finishing receiving response.
 * Based on okhttp3 to do requests and receive response.
 *
 * <okhttp3 licences will be refer here>
 *
 */

class NCMBConnection(request: NCMBRequest) {

    //time out millisecond from NIF Cloud mobile backend
    var sConnectionTimeout = NCMB.TIMEOUT
    //synchronized lock
    val lock = Any()
    //API request object
    var ncmbRequest: NCMBRequest
    //API response object
    var ncmbResponse: NCMBResponse? = null

    /**
     * Constructor with NCMBRequest
     *
     * @param request API request object
     */
    init{
        this.ncmbRequest = request
    }

    /**
     * Request NIF Cloud mobile backed api synchronously
     *
     * @return result object from NIF Cloud mobile backend
     * @throws NCMBException exception from NIF Cloud mobile backend
     */
    @Throws(NCMBException::class)
    fun sendRequest(): NCMBResponse {
        runBlocking {
            withContext(Dispatchers.Default) {
                val headers: Headers = createHeader()
                val client = OkHttpClient()
                val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

                println("Request Info:")
                println(ncmbRequest.params.toString())
                println(ncmbRequest.url)
                println(headers)

                val body: RequestBody = RequestBody.create(JSON, ncmbRequest.params.toString())
                val url = Uri.parse(ncmbRequest.url)
                    .buildUpon()
                for (key in ncmbRequest.query.keys()){
                    val value = ncmbRequest.query.get(key) as String
                    url.appendQueryParameter(key, value)
                }
                    url.build()
                var request: Request
                synchronized(lock) {
                    request = request(ncmbRequest.method, URL(url.toString()), headers, body)
                }
                val response = client.newCall(request).execute()
                ncmbResponse = NCMBResponseBuilder.build(response)
            }
        }
        return ncmbResponse!!
    }

    /**
     * Request NIF Cloud mobile backed api synchronously
     *
     * @return result object from NIF Cloud mobile backend
     * @throws NCMBException exception from NIF Cloud mobile backend
     */
    @Throws(NCMBException::class)
    fun sendRequestAsynchronously(callback: NCMBCallback?, responseHandler: NCMBHandler?) {
        val headers: Headers = createHeader()
        val client = OkHttpClient()
        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

        println("Request Info:")
        println(ncmbRequest.params.toString())
        println(ncmbRequest.url)
        println(headers)

        val body: RequestBody = RequestBody.create(JSON, ncmbRequest.params.toString())
        val request = request(ncmbRequest.method, URL(ncmbRequest.url), headers, body)

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                ncmbResponse = NCMBResponse.Failure(NCMBException(e))
                responseHandler?.doneSolveResponse(callback, ncmbResponse)
            }

            override fun onResponse(call: Call, response: Response) {
                //NCMBResponse 処理
                ncmbResponse = NCMBResponseBuilder.build(response)
                responseHandler?.doneSolveResponse(callback, ncmbResponse)
            }
        })
    }

    /**
     * The key identified as an outlier depends on it on this map
     *
     * @return header map
     */
    fun createHeader():Headers{
        var headerMap = HashMap<String, String>()
        headerMap = headerMapSet(headerMap, NCMBRequest.HEADER_APPS_SESSION_TOKEN)
        headerMap = headerMapSet(headerMap, NCMBRequest.HEADER_APPLICATION_KEY)
        headerMap = headerMapSet(headerMap, NCMBRequest.HEADER_TIMESTAMP)
        headerMap = headerMapSet(headerMap, NCMBRequest.HEADER_SIGNATURE)
        headerMap = headerMapSet(headerMap, NCMBRequest.HEADER_CONTENT_TYPE)
        headerMap = headerMapSet(headerMap, NCMBRequest.HEADER_SDK_VERSION)
        headerMap = headerMapSet(headerMap, NCMBRequest.HEADER_ACCESS_CONTROL_ALLOW_ORIGIN)
        headerMap = headerMapSet(headerMap, NCMBRequest.HEADER_OS_VERSION)
        return headerMap.toHeaders()
    }

    /**
     * Generate Request object while setting URL etc. in Builder class
     *
     * @return request object
     */
    fun request(
        requestMethod: String,
        requestUrl: URL,
        requestHeaders: Headers,
        requestBody: RequestBody
    ):Request{
        var request: Request? = null
        if (requestMethod == "GET") {
            request = Request.Builder()
                .url(requestUrl)
                .headers(requestHeaders)
                .build()
        }
        else if (requestMethod == "POST") {
            request = Request.Builder()
                .url(requestUrl)
                .headers(requestHeaders)
                .post(requestBody)
                .build()
        }
        else if (requestMethod == "PUT") {
            request = Request.Builder()
                .url(requestUrl)
                .headers(requestHeaders)
                .put(requestBody)
                .build()
        }
        else if (requestMethod == "DELETE") {
            request = Request.Builder()
                .url(requestUrl)
                .headers(requestHeaders)
                .delete(requestBody)
                .build()
        }
        return request!!
    }

    fun headerMapSet(headerMap: HashMap<String, String>, headerInfo: String): HashMap<String, String>{
        val h_info = ncmbRequest.getRequestProperty(headerInfo)
        if (h_info != null) {
            headerMap.put(headerInfo, h_info)
        }
        return headerMap
    }
}
