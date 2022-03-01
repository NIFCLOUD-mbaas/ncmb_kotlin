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
        val request = createRequestParamsFile(fileObject.fileName, fileObject.mFields,JSONObject(), NCMBRequest.HTTP_METHOD_POST, NCMBRequest.HEADER_CONTENT_TYPE_FILE, fileObject.fileData, callback, fileHandler)
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
    fun saveFile(fileObject: NCMBFile){
        val request = createRequestParamsFile(fileObject.fileName, fileObject.mFields, JSONObject(), NCMBRequest.HTTP_METHOD_POST, NCMBRequest.HEADER_CONTENT_TYPE_FILE, fileObject.fileData, null, null)
        val response = sendRequest(request)
        when (response) {
            is NCMBResponse.Success -> {
                fileObject.reflectResponse(response.data as JSONObject)
                response.data
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
        val request = createRequestParamsFile(fileObject.fileName, JSONObject(),JSONObject(), NCMBRequest.HTTP_METHOD_GET, NCMBRequest.HEADER_CONTENT_TYPE_JSON, fileObject.fileData, callback, fileHandler)
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
        fileData: File?,
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


}