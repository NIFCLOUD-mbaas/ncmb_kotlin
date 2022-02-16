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
 *
 *
 *  OkHttp3 licence Information
 *
 *     Copyright 2019 Square, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.nifcloud.mbaas.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.URL
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


/**
 * NCMB connection solving main class.
 *
 * This class is to do connection to NCMB and execute request, do receive response/error and
 * process callback after finishing receiving response.
 * Requests done based on okhttp3.
 *
 */

internal class NCMBConnection(request: NCMBRequest) {

    //time out millisecond from NIF Cloud mobile backend
    // var sConnectionTimeout = NCMB.TIMEOUT  //TODO
    //synchronized lock
    val lock = Any()
    //API request object
    var ncmbRequest: NCMBRequest
    //API response object
    lateinit var ncmbResponse: NCMBResponse

    /**
     * Constructor with NCMBRequest
     *
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

                println("Request Info (Sync):")
                println("params: " + ncmbRequest.params.toString())
                println("querys: " + ncmbRequest.query.toString())
                println(ncmbRequest.url)
                println(headers)
                println(ncmbRequest.query)

                val body = ncmbRequest.params.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                var request: Request
                synchronized(lock) {
                    request = request(ncmbRequest.method, URL(ncmbRequest.url), headers, body)
                }
                val response = client.newCall(request).execute()
                ncmbResponse = NCMBResponseBuilder.build(response)
            }
        }
        return ncmbResponse
    }

    /**
     * Request NIF Cloud mobile backed api synchronously (For file features)
     *
     * @return result object from NIF Cloud mobile backend
     * @throws NCMBException exception from NIF Cloud mobile backend
     */
    @Throws(NCMBException::class)
    fun sendRequestForFile(): NCMBResponse {
        runBlocking {
            withContext(Dispatchers.Default) {
                val headers: Headers = createHeader()
                val client = OkHttpClient()

                println("Request Info for File (Sync):")
                println("params: " + ncmbRequest.params.toString())
                println("querys: " + ncmbRequest.query.toString())
                println(ncmbRequest.url)
                println(headers)
                println(ncmbRequest.query)

                val body = ncmbRequest.params.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                var request: Request
                synchronized(lock) {
                    request = request(ncmbRequest.method, URL(ncmbRequest.url), headers, body)
                }
                val response = client.newCall(request).execute()
                ncmbResponse = NCMBResponseBuilder.build(response)
            }
        }
        return ncmbResponse
    }

    /**
     * Request NIF Cloud mobile backed api synchronously (Excepts file features)
     *
     * @param callback NCMBCallback
     * @param responseHandler NCMBHandler
     * @throws NCMBException exception from NIF Cloud mobile backend
     */
    @Throws(NCMBException::class)
    fun sendRequestAsynchronously(callback: NCMBCallback, responseHandler: NCMBHandler) {

        val headers: Headers = createHeader()
        val client = OkHttpClient()

        println("Request Info (Async):")
        println("params: " + ncmbRequest.params.toString())
        println("querys: " + ncmbRequest.query.toString())
        println(ncmbRequest.url)
        println(headers)
        val body = ncmbRequest.params.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = request(ncmbRequest.method, URL(ncmbRequest.url), headers, body)
        try {
            println("START TO REQUEST")
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    ncmbResponse = NCMBResponse.Failure(NCMBException(e))
                    responseHandler.doneSolveResponse(callback, ncmbResponse)
                }

                override fun onResponse(call: Call, response: Response) {
                    //NCMBResponse 処理
                    ncmbResponse = NCMBResponseBuilder.build(response)
                    responseHandler.doneSolveResponse(callback, ncmbResponse)
                }
            })
        } catch (e: Exception) {
            println("EXCEPTION")
            e.printStackTrace()
            ncmbResponse = NCMBResponse.Failure(NCMBException(e))
            responseHandler.doneSolveResponse(callback, ncmbResponse)
        }
    }

    /**
     * Request NIF Cloud mobile backed api synchronously (For file)
     *
     * @param callback NCMBCallback
     * @param responseHandler NCMBHandler
     * @throws NCMBException exception from NIF Cloud mobile backend
     */
    @Throws(NCMBException::class)
    fun sendRequestAsynchronouslyForFile(callback: NCMBCallback, responseHandler: NCMBHandler) {
        try {
            val headers: Headers = createHeader()
            val client = OkHttpClient()

            println("Request Info for File(Async):")
            println("params: " + ncmbRequest.params.toString())
            println("querys: " + ncmbRequest.query.toString())
            println(ncmbRequest.url)
            println(headers)

            //Get file from params
            var fileObj = ncmbRequest.params.get("file") as File

            checkNotNull(fileObj)
            val media = "application/json; charset=utf-8".toMediaType()
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", null,
                    fileObj.asRequestBody(media))
                .build()

            val request = request(ncmbRequest.method, URL(ncmbRequest.url), headers, requestBody)

            println("SYNC REQUEST")
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                println(response.body!!.string())
            }


//            println("ASYNC FILE POST REQUEST")
//            client.newCall(request).enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    println("ON FAILED")
//                    e.printStackTrace()
//                    //ncmbResponse = NCMBResponse.Failure(NCMBException(e))
//                    //responseHandler.doneSolveResponse(callback, ncmbResponse)
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    println("ON SUCCEED" + response.toString())
//                    //NCMBResponse 処理
//                    //ncmbResponse = NCMBResponseBuilder.build(response)
//                    //responseHandler.doneSolveResponse(callback, ncmbResponse)
//                }
//            })

        } catch (e: Exception) {
            println(e.printStackTrace())
            ncmbResponse = NCMBResponse.Failure(NCMBException(e))
            responseHandler.doneSolveResponse(callback, ncmbResponse)
        }

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
     * @param requestMethod  Method of request
     * @param requestUrl  Url of request
     * @param requestHeaders Headers info of request
     * @param requestBody  Header body info of request
     * @return request object
     */
    fun request(
        requestMethod: String,
        requestUrl: URL,
        requestHeaders: Headers,
        requestBody: RequestBody
    ):Request{
        lateinit var request: Request
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
        return request
    }

    /**
     * Generate headers info map set
     *
     * @param headerMap  Headers map
     * @param headerInfo  Headers info
     * @return HashMap of headers info
     */
    fun headerMapSet(headerMap: HashMap<String, String>, headerInfo: String): HashMap<String, String>{
        val h_info = ncmbRequest.getRequestProperty(headerInfo)
        if (h_info != null) {
            headerMap.put(headerInfo, h_info)
        }
        return headerMap
    }
}
