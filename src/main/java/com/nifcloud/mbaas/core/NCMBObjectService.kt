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
import org.json.JSONObject

/**
 * A class of ncmb_kotlin.
 *
 * To do object service jobs, setup basis connection settings before doing connection.
 * SDK handler is also set here.
 *
 */
class NCMBObjectService() : NCMBService() {
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
        val query =  RequestParams(url = url, method = method, contentType = contentType).query
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
        val params = RequestParams(url = url, method = method, contentType = contentType).params
        val query =  RequestParams(url = url, method = method, contentType = contentType).query
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
        val query =  RequestParams(url = url, method = method, contentType = contentType).query
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
        val params = RequestParams(url = url, method = method, contentType = contentType).params
        val query =  RequestParams(url = url, method = method, contentType = contentType).query
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
        sendRequestAsync(url, method, params, contentType, query, deleteCallback, deleteHandler)
    }


//    /**
//     * Searching JSONObject data from NIFCLOUD mobile backend
//     * @param className Datastore class name which to search the object
//     * @param conditions JSONObject of search conditions
//     * @return List of NCMBObject of search results
//     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
//     */
//    @Throws(NCMBException::class)
//    fun findObject(className: String, conditions: JSONObject?): List<*>? {
//        if (!validateClassName(className)) {
//            throw NCMBException(
//                NCMBException.REQUIRED,
//                "className / objectId is must not be null or empty"
//            )
//        }
//        val url: String = mContext.baseUrl.toString() + mServicePath + className
//        val type = NCMBRequest.HTTP_METHOD_GET
//        val response: NCMBResponse = sendRequest(url, type, null, conditions)
//        if (response.statusCode !== NCMBResponse.HTTP_STATUS_OK) {
//            throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Invalid status code")
//        }
//        return createSearchResults(className, response.responseData)
//    }
//
    /**
     * Searching JSONObject data to NIFCLOUD mobile backend in background thread
     * @param className Datastore class name which to search the object
     * @param conditions JSONObject of search conditions
     * @param callback callback for after object search
     */
    fun findObjectInBackground(
        className: String,
        conditions: JSONObject?,
        findCallback: NCMBCallback
    ) {
        val url = NCMB.getApiBaseUrl() + this.mServicePath + className
        val method = NCMBRequest.HTTP_METHOD_GET
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        //conditions solving!!
        val query =  RequestParams(url = url, method = method, contentType = contentType).query
        val saveHandler = NCMBHandler { findCallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    //findCallback done to object
                    val listObj = listObject.reflectResponse(response.data)
                    findCallback.done(null, listObj)
                }
                is NCMBResponse.Failure -> {
                    findCallback.done(response.resException)
                }
            }
        }
        sendRequestAsync(url, method, null ,contentType, query, findCallback, saveHandler)

    }

    private fun validateClassName(className: String?): Boolean {
        return (className == null || className.isEmpty())
    }

    private fun validateObjectId(objectId: String?): Boolean {
        return (objectId == null || objectId.isEmpty())
    }
}
