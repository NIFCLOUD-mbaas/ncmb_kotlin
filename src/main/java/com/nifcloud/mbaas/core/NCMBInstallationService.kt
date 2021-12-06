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
import java.util.TimeZone

/**
 * Service class for installation api
 */
class NCMBInstallationService: NCMBObjectService() {

    /**
     * service path for API category
     */
    override val SERVICE_PATH = "installations"

    /**
     * Constructor
     *
     * @param context NCMBContext
     */
    init {
        this.mServicePath = this.SERVICE_PATH
    }

    companion object {

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
            NCMBInstallation.installation = null
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
                if (objectId == NCMBInstallation.getCurrentInstallation()!!.getObjectId()) {
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
        installationObject: NCMBObject,
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
        installationObject: NCMBObject,
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
                    //checkDataNotFound(objectId, response.resException)
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
    fun deleteInstallation(objectId: String?) {
        try {
            //null check
            if (objectId == null) {
                throw NCMBException(IllegalArgumentException("objectId is must not be null."))
            }
            val url = NCMB.getApiBaseUrl() + this.mServicePath + "/" + objectId
            val method = NCMBRequest.HTTP_METHOD_DELETE
            val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
            val params = RequestParams(url = url, method = method, contentType = contentType).params
            val query =  RequestParams(url = url, method = method, contentType = contentType).query
            val response = sendRequest(url, method, params, contentType, query)
            when (response) {
                is NCMBResponse.Success -> {
                    if (response.resCode !== HTTP_STATUS_INSTALLATION_GOTTEN) {
                        throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Deleted failed.")
                    }
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
     * Get installation object
     *
     * @param objectId object id
     * @return result of get installation
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun fetchInstallation(fetchInstantiation: NCMBInstallation, objectId: String?): NCMBInstallation {
        //null check
        if (objectId == null) {
            throw NCMBException(IllegalArgumentException("objectId is must not be null."))
        }

        val url = NCMB.getApiBaseUrl() + this.mServicePath + "/" + objectId;
        val method = NCMBRequest.HTTP_METHOD_GET
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        val params = RequestParams(url = url, method = method, contentType = contentType).params
        val query =  RequestParams(url = url, method = method, contentType = contentType).query
        val response = sendRequest(url, method, params, contentType, query)
        when (response) {
            is NCMBResponse.Success -> {
                if (response.resCode !== HTTP_STATUS_INSTALLATION_GOTTEN) {
                    throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Getting failed.")
                }
                fetchInstantiation.reflectResponse(response.data)
                return NCMBInstallation(response.data)
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
    }

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
        val currentInstallation = NCMBInstallation.getCurrentInstallation()
        val currentData = currentInstallation.localData
        mergeJSONObject(currentData, params)

        //write file
        val file = create(NCMBInstallation.INSTALLATION_FILENAME)
        writeFile(file, currentData)

        //held in a static
        NCMBInstallation.installation = NCMBInstallation(currentData)
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
        val params = JSONObject()
        return RequestParams(url = url, method = method, params = params, contentType = contentType, query=query)
    }

    @Throws(NCMBException::class)
    override fun createSearchResponseList(className: String, responseData: JSONObject): List<NCMBInstallation> {
        return try {
            val results = responseData.getJSONArray(NCMBQueryConstants.RESPONSE_PARAMETER_RESULTS)
            val array: MutableList<NCMBInstallation> = ArrayList()
            for (i in 0 until results.length()) {
                val tmpObj = NCMBInstallation(results.getJSONObject(i))
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
