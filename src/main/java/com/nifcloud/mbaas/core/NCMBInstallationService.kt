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
import android.util.Log
import com.nifcloud.mbaas.core.NCMBLocalFile.checkNCMBContext
import com.nifcloud.mbaas.core.NCMBLocalFile.create
import com.nifcloud.mbaas.core.NCMBLocalFile.deleteFile
import com.nifcloud.mbaas.core.NCMBLocalFile.writeFile
import org.json.JSONException
import org.json.JSONObject
import java.util.TimeZone

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
         * Run at the time of "Delete" and "POST" or "PUT" and "E404001 Error"
         */
        fun clearCurrentInstallation() {
            //delete file
            val file = create(NCMBInstallation.INSTALLATION_FILENAME)
            deleteFile(file)
            //discarded from the static
            NCMBInstallation.currentInstallation = NCMBInstallation()
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

        /**
         * automatic deletion of the registration currentInstallation during E404001 return
         * Use at the time of the 'POST' and 'DELETE'
         *
         * @param code error code
         */
        fun checkDataNotFound(objectId: String?, code: String) {
            if (NCMBException.DATA_NOT_FOUND == code) {
                if (objectId == NCMBInstallation.currentInstallation.getObjectId()) {
                    clearCurrentInstallation()
                }
            }
        }
    }

    //Todo
//    @Throws(NCMBException::class)
//    fun createInstallation(registrationId: String?, params: JSONObject): JSONObject {
//    }

  /**
     * save installation object in background
     *
     * @param registrationId registration id
     * @param params         installation parameters
     * @param callback       JSONCallback
     */
    fun saveInstallationInBackground(
        installationObject: NCMBInstallation,
        registrationId: String,
        params: JSONObject,
        callback: NCMBCallback
    ) {
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
                        writeCurrentInstallation(params, response.data)
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
        val request = createRequestParams(null, params, NCMBRequest.HTTP_METHOD_POST,callback, installationHandler)
        sendRequestAsync(request)
    }


    //Todo
//    @Throws(NCMBException::class)
//    fun updateInstallation(objectId: String?, params: JSONObject): JSONObject {
//    }

    /**
     * Update installation object in background
     *
     * @param objectId objectId
     * @param params   installation parameters
     * @param callback JSONCallback
     */
    fun updateInstallationInBackground(
        installationObject: NCMBInstallation,
        objectId: String,
        params: JSONObject,
        callback: NCMBCallback
    ) {
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
                        writeCurrentInstallation(params, response.data)
                    } catch (e: NCMBException) {
                        throw e
                    }
                    installationObject.reflectResponse(response.data)
                    callback.done(null, installationObject)
                }
                is NCMBResponse.Failure -> {
                    //ToDo installation自動削除
                    //checkDataNotFound(objectId, response.resException.code)
                    callback.done(response.resException)
                }
            }
        }
        val request = createRequestParams(objectId, params, NCMBRequest.HTTP_METHOD_PUT, callback, installationHandler)
        sendRequestAsync(request)
    }

    /**
     * Delete installation object
     *
     * @param objectId object id
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun deleteInstallation(objectId: String) {
        try {
            val url = NCMB.getApiBaseUrl() + this.mServicePath + "/" + objectId
            val method = NCMBRequest.HTTP_METHOD_DELETE
            val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
            val params = RequestParams(url = url, method = method, contentType = contentType).params
            val query =  RequestParams(url = url, method = method, contentType = contentType).query
            val response = sendRequest(url, method, params, contentType, query)
            when (response) {
                is NCMBResponse.Success -> {
                    clearCurrentInstallation()
                }
                is NCMBResponse.Failure -> {
                    throw response.resException
                }
            }
        } catch (error: NCMBException) {
            //currentInstallation auto delete
            checkDataNotFound(objectId, error.code)
            throw error
        }
    }

    /**
     * Delete installation object in background
     *
     * @param objectId objectId
     * @param callback DoneCallback
     */
    fun deleteInstallationInBackground(objectId: String, deleteCallback: NCMBCallback) {
        try {
            val url = NCMB.getApiBaseUrl() + this.mServicePath + "/" + objectId
            val method = NCMBRequest.HTTP_METHOD_DELETE
            val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
            val params = RequestParams(url = url, method = method, contentType = contentType).params
            val query =  RequestParams(url = url, method = method, contentType = contentType).query
            val deleteHandler = NCMBHandler{ deletecallback, response ->
                when (response) {
                    is NCMBResponse.Success -> {
                        clearCurrentInstallation()
                        deleteCallback.done(null, null)
                    }
                    is NCMBResponse.Failure -> {
                        deleteCallback.done(response.resException)
                    }
                }
            }
            sendRequestAsync(url, method, params, contentType, query, deleteCallback, deleteHandler)
        } catch (error: NCMBException) {
            //currentInstallation auto delete
            checkDataNotFound(objectId, error.code)
            throw error
        }
    }

    /**
     * Get installation object
     *
     * @param objectId object id
     * @return result of get installation
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun fetchInstallation(fetchInstantiation: NCMBInstallation, objectId: String?): NCMBInstallation {
        val url = NCMB.getApiBaseUrl() + this.mServicePath + "/" + objectId;
        val method = NCMBRequest.HTTP_METHOD_GET
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        val params = RequestParams(url = url, method = method, contentType = contentType).params
        val query =  RequestParams(url = url, method = method, contentType = contentType).query
        val response = sendRequest(url, method, params, contentType, query)
        when (response) {
            is NCMBResponse.Success -> {
                fetchInstantiation.reflectResponse(response.data)
                return NCMBInstallation(response.data)
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
    }

    /**
     * Get installation object
     *
     * @param objectId object id
     * @return result of get installation
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun fetchInstallationInBackground(fetchInstantiation: NCMBInstallation, objectId: String?, fetchCallback: NCMBCallback){
        val url = NCMB.getApiBaseUrl() + this.mServicePath + "/" + objectId;
        val method = NCMBRequest.HTTP_METHOD_GET
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        val params = RequestParams(url = url, method = method, contentType = contentType).params
        val query =  RequestParams(url = url, method = method, contentType = contentType).query
        val fetchHandler = NCMBHandler { deletecallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    fetchInstantiation.reflectResponse(response.data)
                    fetchCallback.done(null, NCMBInstallation(response.data))
                }
                is NCMBResponse.Failure -> {
                    fetchCallback.done(response.resException)
                }
            }
        }
        sendRequestAsync(url, method, params, contentType, query, fetchCallback, fetchHandler)
    }


//        sendRequestAsync(url, method, params, contentType, query, deleteCallback, deleteHandler)
//    } catch (error: NCMBException) {
//        //currentInstallation auto delete
//        checkDataNotFound(objectId, error.code)
//        throw error
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
            //Todo fix
            params.put(NCMBInstallation.DEVICE_TYPE, NCMBInstallation.ANDROID)
            params.put(NCMBInstallation.APPLICATION_NAME, applicationName)
            params.put(NCMBInstallation.APP_VERSION, appVersion)
            params.put(NCMBInstallation.SDK_VERSION, NCMB.SDK_VERSION)
            params.put(NCMBInstallation.TIME_ZONE, timeZone)
            params.put(NCMBInstallation.PUSH_TYPE, NCMBInstallation.FCM)
        }
    }

    @Throws(NCMBException::class)
    fun createRequestParams(
        objectId: String?,
        params: JSONObject,
        method: String,
        installationCallback: NCMBCallback,
        installationHandler: NCMBHandler
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

        return RequestParamsAsync(
            url = url,
            method = method,
            params = params,
            contentType = contentType,
            callback = installationCallback,
            handler = installationHandler
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

    //Todo
//    @Throws(NCMBException::class)
//    fun createSearchResults(responseData: JSONObject): ArrayList<NCMBInstallation?> {
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
        val currentInstallation = NCMBInstallation.currentInstallation
        val currentData = currentInstallation.localData
        mergeJSONObject(currentData, params)
        //write file
        val file = create(NCMBInstallation.INSTALLATION_FILENAME)
        writeFile(file, currentData)

        //held in a static
        NCMBInstallation.currentInstallation = NCMBInstallation(currentData)
    }
}
