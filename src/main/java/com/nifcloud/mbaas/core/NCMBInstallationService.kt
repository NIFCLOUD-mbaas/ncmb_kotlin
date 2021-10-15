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

import android.content.pm.PackageManager
import com.nifcloud.mbaas.core.NCMBLocalFile.checkNCMBContext
import com.nifcloud.mbaas.core.NCMBLocalFile.create
import com.nifcloud.mbaas.core.NCMBLocalFile.deleteFile
import com.nifcloud.mbaas.core.NCMBLocalFile.writeFile
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Service class for installation api
 */
class NCMBInstallationService: NCMBService() {

    /**
     * Constructor
     *
     * @param context NCMBContext
     */
    init {
        mServicePath = SERVICE_PATH
    }

    companion object {
        /**
         * service path for API category
         */
        const val SERVICE_PATH = "installations"

        /**
         * Status code of installation created
         */
        const val HTTP_STATUS_INSTALLATION_CREATED = 201

        /**
         * Status code of installation updated
         */
        const val HTTP_STATUS_INSTALLATION_UPDATED = 200

        /**
         * Status code of installation deleted
         */
        const val HTTP_STATUS_INSTALLATION_DELETED = 200

        /**
         * Status code of installation gotten
         */
        const val HTTP_STATUS_INSTALLATION_GOTTEN = 200

        /**
         * Run at the time of "Delete" and "POST" or "PUT" and "E404001 Error"
         */
        fun clearCurrentInstallation() {
            //delete file
            val file = create(NCMBInstallation.INSTALLATION_FILENAME)
            deleteFile(file)
            //discarded from the static
            NCMBInstallation.current = null
        }

        /**
         * merge the JSONObject
         *
         * @param base    base JSONObject
         * @param compare merge JSONObject
         * @throws NCMBException
         */
        @Throws(NCMBException::class)
        fun mergeJSONObject(base: JSONObject, compare: JSONObject) {
            try {
                val keys: Iterator<*> = compare.keys()
                while (keys.hasNext()) {
                    val key = keys.next() as String
                    base.put(key, compare[key])
                }
            } catch (error: JSONException) {
                throw NCMBException(NCMBException.INVALID_JSON, error.message!!)
            }
        }

        //Todo
//        /**
//         * automatic deletion of the registration currentInstallation during E404001 return
//         * Use at the time of the 'POST' and 'DELETE'
//         *
//         * @param code error code
//         */
//        fun checkDataNotFound(objectId: String?, code: String) {
//            if (NCMBException.DATA_NOT_FOUND == code) {
//                if (objectId == NCMBInstallation.getCurrentInstallation()!!.getObjectId()) {
//                    clearCurrentInstallation()
//                }
//            }
//        } //endregion
    }

    //Todo
    // region API method
//    /**
//     * Create installation object
//     *
//     * @param registrationId registration id
//     * @param params         installation parameters
//     * @return JSONObject response of installation create
//     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
//     */
//    @Throws(NCMBException::class)
//    fun createInstallation(registrationId: String?, params: JSONObject): JSONObject {
//        //null check
//        var params = params
//        params = argumentNullCheckForPOST(registrationId, params)
//
//        //set installation data
//        try {
//            //set registrationId
//            params.put("deviceToken", registrationId)
//            //set basic data
//            setInstallationBasicData(params)
//        } catch (e: JSONException) {
//            throw NCMBException(NCMBException.INVALID_JSON, "Invalid json format.")
//        } catch (e: PackageManager.NameNotFoundException) {
//            throw NCMBException(NCMBException.DATA_NOT_FOUND, "PackageManager not found.")
//        }
//
//        //connect
//        val request = createRequestParams(null, params, "push", null, NCMBRequest.HTTP_METHOD_POST)
//        val response = sendRequest(request)
//        when (response) {
//            is NCMBResponse.Success -> {
//                if (response.resCode !== HTTP_STATUS_INSTALLATION_CREATED) {
//                    throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Created failed.")
//                }
//                //create currentInstallation
//                writeCurrentInstallation(params, response.data)
//                return response.data
//            }
//            is NCMBResponse.Failure -> {
//                throw response.resException
//            }
//        }
//    }

    /**
     * Create installation object in background
     *
     * @param registrationId registration id
     * @param params         installation parameters
     * @param callback       JSONCallback
     */
    fun createInstallationInBackground(
        installationObject: NCMBObject,
        registrationId: String?,
        params: JSONObject,
        callback: NCMBCallback
    ) {
        //null check
        val argumentParams = argumentNullCheckForPOST(registrationId, params)

        //set installation data
        try {
            //set registrationId
            params.put("deviceToken", registrationId)
            //set basic data
            setInstallationBasicData(params)
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_JSON, "Invalid json format.")
        } catch (e: PackageManager.NameNotFoundException) {
            throw NCMBException(NCMBException.DATA_NOT_FOUND, "PackageManager not found.")
        }

        val installationHandler = NCMBHandler { installationcallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    try {
                        writeCurrentInstallation(argumentParams, response.data)
                    } catch (e: NCMBException) {
                        throw e
                    }
                    installationObject.reflectResponse(response.data)
                    callback.done(null, installationObject)
                }
                is NCMBResponse.Failure -> {
                    callback.done(response.resException)
                }
            }
        }
        val request = createRequestParams(null, params, null, NCMBRequest.HTTP_METHOD_POST,callback, installationHandler)
        sendRequestAsync(request)
    }

    //Todo
//    /**
//     * Update installation object
//     *
//     * @param objectId objectId
//     * @param params   installation parameters
//     * @return result of update installation
//     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
//     */
//    @Throws(NCMBException::class)
//    fun updateInstallation(objectId: String?, params: JSONObject): JSONObject {
//        var params = params
//        return try {
//            //null check
//            params = argumentNullCheckForPUT(objectId, params)
//
//            //set installation data
//            try {
//                //set basic data
//                setInstallationBasicData(params)
//            } catch (e: JSONException) {
//                throw NCMBException(NCMBException.INVALID_JSON, "Invalid json format.")
//            } catch (e: PackageManager.NameNotFoundException) {
//                throw NCMBException(NCMBException.DATA_NOT_FOUND, "PackageManager not found.")
//            }
//
//            //connect
//            val request = createRequestParams(objectId, params, null, null, NCMBRequest.HTTP_METHOD_PUT)
//            val response = sendRequest(request)
//            when (response) {
//                is NCMBResponse.Success -> {
//                    if (response.resCode !== HTTP_STATUS_INSTALLATION_UPDATED) {
//                        throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Updated failed.")
//                    }
//                    //create currentInstallation
//                    writeCurrentInstallation(params, response.data)
//                    response.data
//                }
//                is NCMBResponse.Failure -> {
//                    throw response.resException
//                }
//            }
//        } catch (error: NCMBException) {
//            //currentInstallation auto delete
//            checkDataNotFound(objectId, error.code)
//            throw error
//        }
//    }

    /**
     * Update installation object in background
     *
     * @param objectId objectId
     * @param params   installation parameters
     * @param callback JSONCallback
     */
    fun updateInstallationInBackground(
        installationObject: NCMBObject,
        objectId: String?,
        params: JSONObject,
        callback: NCMBCallback
    ) {
            //null check
            val argumentParams = argumentNullCheckForPOST(objectId, params)

            //set installation data
            try {
                //set basic data
                setInstallationBasicData(params)
            } catch (e: JSONException) {
                throw NCMBException(NCMBException.INVALID_JSON, "Invalid json format.")
            } catch (e: PackageManager.NameNotFoundException) {
                throw NCMBException(NCMBException.DATA_NOT_FOUND, "PackageManager not found.")
            }

        val installationHandler = NCMBHandler { installationcallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    try {
                        writeCurrentInstallation(argumentParams, response.data)
                    } catch (e: NCMBException) {
                        throw e
                    }
                    installationObject.reflectResponse(response.data)
                    callback.done(null, installationObject)
                }
                is NCMBResponse.Failure -> {
                    //ToDo installation自動削除
                    //checkDataNotFound(objectId, response.resException)
                    callback.done(response.resException)
                }
            }
        }
        val request = createRequestParams(objectId, params, null, NCMBRequest.HTTP_METHOD_PUT,callback, installationHandler)
        sendRequestAsync(request)
    }

    //Todo
//    /**
//     * Delete installation object
//     *
//     * @param objectId object id
//     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
//     */
//    @Throws(NCMBException::class)
//    fun deleteInstallation(objectId: String?) {
//        try {
//            //null check
//            if (objectId == null) {
//                throw NCMBException(IllegalArgumentException("objectId is must not be null."))
//            }
//
//            //connect
//            val request = createRequestParams(objectId, null, "", null, NCMBRequest.HTTP_METHOD_DELETE)
//            val response = sendRequest(request)
//            when (response) {
//                is NCMBResponse.Success -> {
//                    if (response.resCode !== HTTP_STATUS_INSTALLATION_DELETED) {
//                        throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Deleted failed.")
//                    }
//                    //clear currentInstallation
//                    clearCurrentInstallation()
//                }
//                is NCMBResponse.Failure -> {
//                    throw response.resException
//                }
//            }
//        } catch (error: NCMBException) {
//            //currentInstallation auto delete
//            checkDataNotFound(objectId, error.code)
//            throw error
//        }
//    }
//    /**
//     * Delete installation object in background
//     *
//     * @param objectId objectId
//     * @param callback DoneCallback
//     */
//    fun deleteInstallationInBackground(objectId: String?, callback: DoneCallback?) {
//        try {
//            //null check
//            if (objectId == null) {
//                throw NCMBException(IllegalArgumentException("objectId is must not be null."))
//            }
//
//            //connect
//            val request = createRequestParams(objectId, null, null, NCMBRequest.HTTP_METHOD_DELETE)
//            sendRequestAsync(request, object : InstallationServiceCallback(this, callback) {
//                fun handleResponse(response: NCMBResponse?) {
//
//                    //clear currentInstallation
//                    clearCurrentInstallation()
//                    val callback: DoneCallback? = mCallback as DoneCallback?
//                    if (callback != null) {
//                        callback.done(null)
//                    }
//                }
//
//                fun handleError(e: NCMBException) {
//                    //currentInstallation auto delete
//                    checkDataNotFound(objectId, e.code)
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
//    /**
//     * Get installation object
//     *
//     * @param objectId object id
//     * @return result of get installation
//     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
//     */
//    @Throws(NCMBException::class)
//    fun fetchInstallation(objectId: String?): NCMBInstallation {
//        //null check
//        if (objectId == null) {
//            throw NCMBException(IllegalArgumentException("objectId is must not be null."))
//        }
//
//        //connect
//        val request = createRequestParams(objectId, null, null, NCMBRequest.HTTP_METHOD_GET)
//        val response = sendRequest(request)
//        when (response) {
//            is NCMBResponse.Success -> {
//                if (response.resCode !== HTTP_STATUS_INSTALLATION_GOTTEN) {
//                    throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Getting failed.")
//                }
//                return NCMBInstallation(response.data)
//            }
//            is NCMBResponse.Failure -> {
//                throw response.resException
//            }
//        }
//    }
//    /**
//     * Get installation object in background
//     *
//     * @param objectId objectId
//     * @param callback callback is executed after get installation
//     */
//    fun fetchInstallationInBackground(objectId: String?, callback: FetchCallback?) {
//        try {
//            //null check
//            if (objectId == null) {
//                throw NCMBException(IllegalArgumentException("objectId is must not be null."))
//            }
//
//            //connect
//            val request = createRequestParams(objectId, null, null, NCMBRequest.HTTP_METHOD_GET)
//            sendRequestAsync(request, object : InstallationServiceCallback(this, callback) {
//                fun handleResponse(response: NCMBResponse) {
//                    val callback: FetchCallback<NCMBInstallation>? = mCallback as FetchCallback?
//                    if (callback != null) {
//                        callback.done(NCMBInstallation(response.responseData), null)
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

    //Todo
//    /**
//     * Search installations
//     *
//     * @param conditions search conditions
//     * @return JSONObject
//     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
//     */
//    @Throws(NCMBException::class)
//    fun searchInstallation(conditions: JSONObject?): List<*> {
//        //connect
//        val request = createRequestParams(null, null, conditions, NCMBRequest.HTTP_METHOD_GET)
//        val response = sendRequest(request)
//        when (response) {
//            is NCMBResponse.Success -> {
//                if (response.resCode !== HTTP_STATUS_INSTALLATION_GOTTEN) {
//                    throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Gotten failed.")
//                }
//                //return the value of the key 'results'
//                return createSearchResults(response.data)
//            }
//            is NCMBResponse.Failure -> {
//                throw response.resException
//            }
//        }
//
//
//    }
//    /**
//     * Search installations in background
//     *
//     * @param conditions search conditions
//     * @param callback   JSONCallback
//     */
//    fun searchInstallationInBackground(
//        conditions: JSONObject?,
//        callback: SearchInstallationCallback?
//    ) {
//        try {
//            val request = createRequestParams(null, null, conditions, NCMBRequest.HTTP_METHOD_GET)
//            sendRequestAsync(request, object : InstallationServiceCallback(this, callback) {
//                fun handleResponse(response: NCMBResponse) {
//                    //return the value of the key 'results'
//                    var array: ArrayList<NCMBInstallation?>? = null
//                    try {
//                        array = createSearchResults(response.responseData)
//                    } catch (e: NCMBException) {
//                        callback.done(null, e)
//                    }
//                    val callback: SearchInstallationCallback? =
//                        mCallback as SearchInstallationCallback?
//                    if (callback != null) {
//                        callback.done(array, null)
//                    }
//                }
//
//                fun handleError(e: NCMBException?) {
//                    val callback: SearchInstallationCallback? =
//                        mCallback as SearchInstallationCallback?
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

    // endregion
    // region internal method
    /**
     * @param params installation parameters
     * @throws JSONException
     * @throws PackageManager.NameNotFoundException
     */
    @Throws(JSONException::class, PackageManager.NameNotFoundException::class)
    fun setInstallationBasicData(params: JSONObject) {
        checkNCMBContext()
        //value get
        val timeZone = TimeZone.getDefault().id
        val context = NCMB.getCurrentContext()
        if (context != null) {
            val packageName: String = context.getPackageName()
            val pm: PackageManager = context.getPackageManager()
            val applicationName =
                pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
            val appVersion = pm.getPackageInfo(packageName, 0).versionName
            //value set
            params.put("deviceType", "android")
            params.put("applicationName", applicationName)
            params.put("appVersion", appVersion)
            params.put("sdkVersion", NCMB.SDK_VERSION)
            params.put("timeZone", timeZone)
            params.put("pushType", "fcm")
        }
    }

    @Throws(NCMBException::class)
    fun createRequestParams(
        objectId: String?,
        params: JSONObject,
        queryParams: JSONObject?,
        method: String,
        signUpCallback: NCMBCallback,
        signUpHandler: NCMBHandler
    ): RequestParamsAsync {

        //url set
        val url: String = if (objectId != null) {
            //PUT,GET(fetch)
            NCMB.getApiBaseUrl() + mServicePath + "/" + objectId
        } else {
            //POST,GET(search)
            NCMB.getApiBaseUrl() + this.mServicePath
        }
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON

//        //content set
//        if (params != null) {
//            reqParams.content = params.toString()
//        }
        var query = JSONObject()
        if (queryParams == null && method == NCMBRequest.HTTP_METHOD_GET) {
            if (queryParams != null && method == NCMBRequest.HTTP_METHOD_GET) {
                query = queryParams
            }
        }
//        //type set
//        reqParams.type = method

        return RequestParamsAsync(
            url = url,
            method = method,
            params = params,
            query = query,
            contentType = contentType,
            callback = signUpCallback,
            handler = signUpHandler
        )
    }
        /**
         * Argument checking of POST
         *
         * @param registrationId registration id
         * @param params         installation parameters
         * @throws NCMBException
         */
    @Throws(NCMBException::class)
    fun argumentNullCheckForPOST(registrationId: String?, params: JSONObject?): JSONObject {
        var params = params
        if (registrationId == null) {
            throw NCMBException(IllegalArgumentException("registrationId is must not be null."))
        }
        if (params == null) {
            params = JSONObject()
        }
        return params
    }

        /**
         * Argument checking of PUT
         *
         * @param objectId objectId
         * @param params   installation parameters
         * @throws NCMBException
         */
    @Throws(NCMBException::class)
    fun argumentNullCheckForPUT(objectId: String?, params: JSONObject?): JSONObject {
        var params = params
        if (objectId == null) {
            throw NCMBException(IllegalArgumentException("objectId is must not be null."))
        }
        if (params == null) {
            params = JSONObject()
        }
        return params
    }

    //Todo
//    /**
//     * Create search results
//     *
//     * @param responseData API response data
//     * @return JSONArray
//     * @throws NCMBException
//     */
//    @Throws(NCMBException::class)
//    fun createSearchResults(responseData: JSONObject): ArrayList<NCMBInstallation?> {
//        return try {
//            val results = responseData.getJSONArray("results")
//            val array = ArrayList<NCMBInstallation?>()
//            for (i in 0 until results.length()) {
//                val installation = NCMBInstallation(results.getJSONObject(i))
//                array.add(installation)
//            }
//            array
//        } catch (e: JSONException) {
//            throw NCMBException(NCMBException.INVALID_JSON, "Invalid JSON format.")
//        }
//    }

        /**
         * Run at the time of "POST" and "PUT"
         * write the currentInstallation data in the file
         *
         * @param responseData installation parameters
         */
    @Throws(NCMBException::class)
    fun writeCurrentInstallation(params: JSONObject, responseData: JSONObject) {
        //merge responseData to the params
        mergeJSONObject(params, responseData)

        //merge params to the currentData
        val currentInstallation = NCMBInstallation.getCurrentInstallation()
        val currentData = currentInstallation!!.localData
        mergeJSONObject(currentData, params)

        //write file
        val file = create(NCMBInstallation.INSTALLATION_FILENAME)
        writeFile(file, currentData)

        //held in a static
        NCMBInstallation.current = NCMBInstallation(currentData)
    }
}