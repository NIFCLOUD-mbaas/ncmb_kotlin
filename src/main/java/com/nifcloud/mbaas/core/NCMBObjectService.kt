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


/**
 * A class of ncmb_kotlin.
 *
 * To do object service jobs, setup basis connection settings before doing connection.
 * SDK handler is also set here.
 *
 */
class NCMBObjectService() : NCMBService(), NCMBServiceInterface<NCMBObject> {
    val SERVICE_PATH = "classes/"

    /**
     * Initialization
     *
     */
    init  {
        this.mServicePath = this.SERVICE_PATH
    }

    /**
     * Saving JSONObject data to NIFCLOUD mobile backend
     * @param className Datastore class name which to save the object
     * @param params Saving Object data
     * @return result of save object
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    /**
     * save current NCMBObject to data store
     * @throws NCMBException exception from NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun saveObject(
        saveObject: NCMBObject,
        className: String,
        params: JSONObject
    ){
        val url = NCMB.getApiBaseUrl() + this.mServicePath + className
        val method = NCMBRequest.HTTP_METHOD_POST
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        val query =  RequestParams(url = url, method = method, contentType = contentType).query
        var response = sendRequest(url, method, params, contentType, query)
        when (response) {
            is NCMBResponse.Success -> {
                saveObject.reflectResponse(response.data)
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
    }

    /**
     * Saving JSONObject data to NIFCLOUD mobile backend in background thread
     * @param className Datastore class name which to save the object
     * @param params saving Object data
     * @param callback callback for after object save
     */
    fun saveObjectInBackground(
        saveObject: NCMBObject,
        className: String,
        params: JSONObject,
        saveCallback: NCMBCallback
    ) {
        val url = NCMB.getApiBaseUrl() + this.mServicePath + className
        val method = NCMBRequest.HTTP_METHOD_POST
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        val saveHandler = NCMBHandler { savecallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    saveObject.reflectResponse(response.data)
                    //saveCallback done to object
                    saveCallback.done(null, saveObject)
                }
                is NCMBResponse.Failure -> {
                    saveCallback.done(response.resException)
                }
            }
        }
        val query =  RequestParamsAsync(url = url, method = method, contentType = contentType, callback = saveCallback, handler = saveHandler).query
        sendRequestAsync(url, method, params, contentType, query, saveCallback, saveHandler)
    }

    /**
     * Saving JSONObject data to NIFCLOUD mobile backend
     * @param className Datastore class name which to save the object
     * @param params Saving Object data
     * @return result of save object
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun fetchObject(
        fetchObject: NCMBObject,
        className: String,
        objectId: String
    ){
        val url = NCMB.getApiBaseUrl() + this.mServicePath + className + "/" + objectId;
        val method = NCMBRequest.HTTP_METHOD_GET
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        val params = RequestParams(url = url, method = method, contentType = contentType).params
        val query =  RequestParams(url = url, method = method, contentType = contentType).query
        val response = sendRequest(url, method, params, contentType, query)
        when (response) {
            is NCMBResponse.Success -> {
                fetchObject.reflectResponse(response.data)
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
    }

    /**
     * Fetching JSONObject data to NIFCLOUD mobile backend in background thread
     * @param className Datastore class name which to fetch the object
     * @param params fetching Object data
     * @param callback callback for after object save
     */
    fun fetchObjectInBackground(
        fetchObject: NCMBObject,
        className: String,
        objectId: String,
        fetchCallback: NCMBCallback
    ) {
        val url = NCMB.getApiBaseUrl() + this.mServicePath + className + "/" + objectId;
        val method = NCMBRequest.HTTP_METHOD_GET
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        val fetchHandler = NCMBHandler{ fetchcallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    fetchObject.reflectResponse(response.data)
                    //saveCallback done to object
                    fetchCallback.done(null, fetchObject)
                }
                is NCMBResponse.Failure -> {
                    fetchCallback.done(response.resException)
                }
            }
        }
        val params = RequestParamsAsync(url = url, method = method, contentType = contentType, callback = fetchCallback, handler = fetchHandler).params
        val query =  RequestParamsAsync(url = url, method = method, contentType = contentType, callback = fetchCallback, handler = fetchHandler).query
        sendRequestAsync(url, method, params, contentType, query, fetchCallback, fetchHandler)
    }

    /**
     * Updating JSONObject data to NIFCLOUD mobile backend
     * @param className Datastore class name which to update the object
     * @param params Updating Object data
     * @return result of update object
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun updateObject(
        updateObject: NCMBObject,
        className: String,
        objectId: String,
        params: JSONObject
    ){
        val url = NCMB.getApiBaseUrl() + this.mServicePath + className + "/" + objectId;
        val method = NCMBRequest.HTTP_METHOD_PUT
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        val query =  RequestParams(url = url, method = method, contentType = contentType).query
        val response = sendRequest(url, method, params, contentType, query)
        when (response) {
            is NCMBResponse.Success -> {
                updateObject.reflectResponse(response.data)
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
    }

    /**
     * Updating JSONObject data to NIFCLOUD mobile backend in background thread
     * @param className Datastore class name which to update the object
     * @param objectId Datastore object id of update data
     * @param params JSONObject of update data
     * @param callback callback for after object update
     */
    fun updateObjectInBackground(
        updateObject: NCMBObject,
        className: String,
        objectId: String,
        params: JSONObject,
        updateCallback: NCMBCallback
    ) {
        val url = NCMB.getApiBaseUrl() + this.mServicePath + className + "/" + objectId;
        val method = NCMBRequest.HTTP_METHOD_PUT
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        val updateHandler = NCMBHandler { updatecallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    updateObject.reflectResponse(response.data)
                    //saveCallback done to object
                    updateCallback.done(null, updateObject)
                }
                is NCMBResponse.Failure -> {
                    updateCallback.done(response.resException)
                }
            }
        }
        val query =  RequestParamsAsync(url = url, method = method, contentType = contentType, callback = updateCallback, handler = updateHandler).query
        sendRequestAsync(url, method, params, contentType, query, updateCallback, updateHandler)
    }

    /**
     * Deleting JSONObject data to NIFCLOUD mobile backend
     * @param className Datastore class name which to delete the object
     * @param params Deleting Object data
     * @return null
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun deleteObject(
        deleteObject: NCMBObject,
        className: String,
        objectId: String
    ){
        val url = NCMB.getApiBaseUrl() + this.mServicePath + className + "/" + objectId
        val method = NCMBRequest.HTTP_METHOD_DELETE
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        val params = RequestParams(url = url, method = method, contentType = contentType).params
        val query =  RequestParams(url = url, method = method, contentType = contentType).query
        var response = sendRequest(url, method, params, contentType, query)
        when (response) {
            is NCMBResponse.Success -> {
                deleteObject.reflectResponse(response.data)
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
    }

    /**
     * Deleting JSONObject data to NIFCLOUD mobile backend
     * @param className Datastore class name which to save the object
     * @param objectId Delete Object Id
     * @param callback callback for after object save
     */
    fun deleteObjectInBackground(
        deleteObject: NCMBObject,
        className: String,
        objectId: String,
        deleteCallback: NCMBCallback
    ) {
        val url = NCMB.getApiBaseUrl() + this.mServicePath + className + "/" + objectId;
        val method = NCMBRequest.HTTP_METHOD_DELETE
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        val deleteHandler = NCMBHandler{ deletecallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    deleteObject.reflectResponse(response.data)
                    //saveCallback done to object
                    deleteCallback.done(null, deleteObject)
                }
                is NCMBResponse.Failure -> {
                    deleteCallback.done(response.resException)
                }
            }
        }
        val params = RequestParamsAsync(url = url, method = method, contentType = contentType, callback = deleteCallback, handler = deleteHandler).params
        val query =  RequestParamsAsync(url = url, method = method, contentType = contentType, callback = deleteCallback, handler = deleteHandler).query
        sendRequestAsync(url, method, params, contentType, query, deleteCallback, deleteHandler)
    }


    /**　
     * Searching JSONObject data from NIFCLOUD mobile backend
     * @param className Datastore class name which to search the object
     * @param query JSONObject of search conditions
     * @return List of NCMBObject of search results
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    override fun find(className: String, query: JSONObject): List<NCMBObject> {
        //return emptyList()
        var listObj = listOf<NCMBObject>()
        val reqParam = findObjectParams(className, query)
        val response = sendRequest(reqParam)
        when (response) {
            is NCMBResponse.Success -> {
                listObj = createSearchResponseList(className, response.data)
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
        return listObj
    }

    /**
     * Searching JSONObject data to NIFCLOUD mobile backend in background thread
     * @param className Datastore class name which to search the object
     * @param query JSONObject of search conditions
     * @param callback callback for after object search
     */
    override fun findInBackground(
        className: String,
        query: JSONObject,
        findCallback: NCMBCallback
    ) {

        val reqParam = findObjectParams(className, query)
        val findHandler = NCMBHandler { findCallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    var listObj = createSearchResponseList(className, response.data)
                    findCallback.done(null, listObj)
                }
                is NCMBResponse.Failure -> {
                    var listObj = listOf<NCMBObject>()
                    findCallback.done(response.resException, listObj)
                }
            }
        }
        sendRequestAsync(reqParam, findCallback, findHandler)
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
    protected fun findObjectParams(className: String, query:JSONObject): RequestParams {
        var url = NCMB.getApiBaseUrl() + this.mServicePath + className
        if(query.length() > 0) {
            url = url.plus("?" + queryUrlStringGenerate(query))
        }
        val method = NCMBRequest.HTTP_METHOD_GET
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        val params = JSONObject()
        return RequestParams(url = url, method = method, params = params, contentType = contentType, query=query)
    }

    private fun validateClassName(className: String?): Boolean {
        return (className == null || className.isEmpty())
    }

    private fun validateObjectId(objectId: String?): Boolean {
        return (objectId == null || objectId.isEmpty())
    }

    @Throws(NCMBException::class)
    fun createSearchResponseList(className: String, responseData: JSONObject): List<NCMBObject> {
        return try {
            val results = responseData.getJSONArray(NCMBQueryConstants.RESPONSE_PARAMETER_RESULTS)
            val array: MutableList<NCMBObject> = ArrayList()
            for (i in 0 until results.length()) {
                val tmpObj = NCMBObject(className, results.getJSONObject(i))
                array.add(tmpObj)
            }
            array
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_JSON, "Invalid JSON format.")
        }
    }

    /**　
     * Searching JSONObject data from NIFCLOUD mobile backend
     * @param className Datastore class name which to search the object
     * @param query JSONObject of search conditions
     * @return List of NCMBObject of search results
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    override fun count(className: String, query: JSONObject): Int {
        var countNumber = 0
        val reqParam = countObjectParams(className, query)
        val response = sendRequest(reqParam)
        when (response) {
            is NCMBResponse.Success -> {
                countNumber = response.data.getInt(NCMBQueryConstants.REQUEST_PARAMETER_COUNT)
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
        return countNumber
    }

    /**
     * Searching JSONObject data to NIFCLOUD mobile backend in background thread
     * @param className Datastore class name which to search the object
     * @param query JSONObject of search conditions
     * @param callback callback for after object search
     */
    override fun countInBackground(
        className: String,
        query: JSONObject,
        countCallback: NCMBCallback
    ) {
        val reqParam = countObjectParams(className, query)
        val countHandler = NCMBHandler { countCallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    var countNumber = response.data.getInt(NCMBQueryConstants.REQUEST_PARAMETER_COUNT)
                    countCallback.done(null, countNumber)
                }
                is NCMBResponse.Failure -> {
                    countCallback.done(response.resException, 0)
                }
            }
        }
        sendRequestAsync(reqParam, countCallback, countHandler)
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
    protected fun countObjectParams(className: String, query:JSONObject): RequestParams {
        var url = NCMB.getApiBaseUrl() + this.mServicePath + className
        if(query.length() > 0) {
            url = url.plus("?" + queryUrlStringGenerate(query))
        }
        val method = NCMBRequest.HTTP_METHOD_GET
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        return RequestParams(url = url, method = method, contentType = contentType, query = query)
    }

}
