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

import android.content.pm.PackageManager
import org.json.JSONException
import org.json.JSONObject
import java.io.File

internal class NCMBFileService : NCMBObjectService(){
    /**
     * service path for API category
     */
    override val SERVICE_PATH = "files"

    /**
     * Constructor
     *
     * @param context NCMBContext
     */
    init {
        this.mServicePath = this.SERVICE_PATH
    }

    /**
     * save file object in background
     *
     * @param fileObject File object
     * @param callback   JSONCallback
     */
    fun saveFileInBackground(
        fileObject: NCMBFile,
        callback: NCMBCallback
    ) {
        val fileHandler = NCMBHandler { fileCallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    fileObject.reflectResponse(response.data as JSONObject)
                    callback.done(null, fileObject)
                }
                is NCMBResponse.Failure -> {
                    callback.done(response.resException)
                }
            }
        }
        val request = createRequestParamsFile(fileObject.fileName, fileObject.mFields,JSONObject(), NCMBRequest.HTTP_METHOD_POST,
            NCMBRequest.HEADER_CONTENT_TYPE_FILE, callback, fileHandler)
        sendRequestAsync(request)
    }

    /**
     * Update file acl
     *
     * @param params file parameters
     * @return JSONObject
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun saveFile(fileObject: NCMBFile){
        val request = createRequestParamsFile(fileObject.fileName, fileObject.mFields, JSONObject(), NCMBRequest.HTTP_METHOD_POST,
            NCMBRequest.HEADER_CONTENT_TYPE_FILE, null, null)
        val response = sendRequest(request)
        when (response) {
            is NCMBResponse.Success -> {
                fileObject.reflectResponse(response.data as JSONObject)
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
    }

    /**
     * update file acl in background
     *
     * @param fileObject File object
     * @param callback   JSONCallback
     */
    fun updateFileInBackground(
        fileObject: NCMBFile,
        callback: NCMBCallback
    ) {
        val fileHandler = NCMBHandler { fileCallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    fileObject.reflectResponse(response.data as JSONObject)
                    callback.done(null, fileObject)
                }
                is NCMBResponse.Failure -> {
                    callback.done(response.resException)
                }
            }
        }
        val request = createRequestParamsFile(fileObject.fileName, fileObject.mFields,JSONObject(), NCMBRequest.HTTP_METHOD_PUT,
            NCMBRequest.HEADER_CONTENT_TYPE_JSON, callback, fileHandler)
        sendRequestAsync(request)
    }

    /**
     * Save file object
     *
     * @param params file parameters
     * @return JSONObject
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun updateFile(fileObject: NCMBFile){
        val request = createRequestParamsFile(fileObject.fileName, fileObject.mFields, JSONObject(), NCMBRequest.HTTP_METHOD_PUT,
            NCMBRequest.HEADER_CONTENT_TYPE_JSON, null, null)
        val response = sendRequest(request)
        when (response) {
            is NCMBResponse.Success -> {
                fileObject.reflectResponse(response.data as JSONObject)
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
    }

    /**
     * Fetch file object
     *
     * @param params file parameters
     * @return JSONObject
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun fetchFile(fileObject: NCMBFile){
        val request = createRequestParamsFile(fileObject.fileName, fileObject.mFields, JSONObject(), NCMBRequest.HTTP_METHOD_GET,
            NCMBRequest.HEADER_CONTENT_TYPE_JSON, null, null)
        val response = sendRequest(request)
        when (response) {
            is NCMBResponse.Success -> {
                //println("SUCCESS" + response.data)
                fileObject.reflectResponseFileData(response.data as ByteArray)
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
    }

    /**
     * Fetch file data in background
     *
     * @param fileObject File object
     * @param callback   JSONCallback
     */
    fun fetchFileInBackground(
        fileObject: NCMBFile,
        callback: NCMBCallback
    ) {
        val fileHandler = NCMBHandler { fileCallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    fileObject.reflectResponseFileData(response.data as ByteArray)
                    callback.done(null, fileObject)
                }
                is NCMBResponse.Failure -> {
                    callback.done(response.resException)
                }
            }
        }
        val request = createRequestParamsFile(fileObject.fileName, JSONObject(),JSONObject(), NCMBRequest.HTTP_METHOD_GET,
            NCMBRequest.HEADER_CONTENT_TYPE_JSON, callback, fileHandler)
        sendRequestAsync(request)
    }

    /**
     * Setup params to file save
     *
     * @param params         file parameters
     * @param queryParams    query parameters
     * @param method         method
     * @return parameters in object
     */
    @Throws(NCMBException::class)
    fun createRequestParamsFile(
        fileName: String,
        params: JSONObject,
        queryParams: JSONObject,
        method: String,
        contentType: String,
        callback: NCMBCallback?,
        handler: NCMBHandler?
    ): RequestParams {
        //url set
        val url: String = NCMB.getApiBaseUrl() + mServicePath + "/" + fileName
        return RequestParams(
            url = url,
            method = method,
            params = params,
            contentType = contentType,
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
    override fun createSearchResponseList(className: String, responseData: JSONObject): List<NCMBFile> {
        return try {
            val results = responseData.getJSONArray(NCMBQueryConstants.RESPONSE_PARAMETER_RESULTS)
            val array: MutableList<NCMBFile> = ArrayList()
            for (i in 0 until results.length()) {
                val tmpObj = NCMBFile(results.getJSONObject(i)) //TODO mimeType, fileSize ADD, JSON create obj
                array.add(tmpObj)
            }
            array
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_JSON, "Invalid JSON format.")
        }
    }

    /**
     * Setup params to do count request for Query search functions
     *
     * @param className Class name
     * @param query JSONObject
     * @return parameters in object
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    override fun countObjectParams(className: String, query:JSONObject): RequestParams {
        var url = NCMB.getApiBaseUrl() + this.mServicePath
        if(query.length() > 0) {
            url = url.plus("?" + queryUrlStringGenerate(query))
        }
        val method = NCMBRequest.HTTP_METHOD_GET
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        return RequestParams(url = url, method = method, contentType = contentType, query = query)
    }


}