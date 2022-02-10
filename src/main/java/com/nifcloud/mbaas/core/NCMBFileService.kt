package com.nifcloud.mbaas.core

import android.content.pm.PackageManager
import org.json.JSONException
import org.json.JSONObject

internal class NCMBFileService : NCMBObjectService(){
    /**
     * service path for API category
     */
    override val SERVICE_PATH = "file"

    /**
     * Constructor
     *
     * @param context NCMBContext
     */
    init {
        this.mServicePath = this.SERVICE_PATH
    }

    /**TODO
     * save file object in background
     *
     * @param fileObject File object
     * @param callback   JSONCallback
     */
    fun saveFileInBackground(
        fileObject: NCMBFile,
        callback: NCMBCallback
    ) {
        print("in saveFileInBG")
        val fileHandler = NCMBHandler { fileCallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    try {
                        //dosthing(params, response.data)
                    } catch (e: NCMBException) {
                        throw e
                    }
                    fileObject.reflectResponse(response.data)
                    callback.done(null, fileObject)
                }
                is NCMBResponse.Failure -> {
                    callback.done(response.resException)
                }
            }
        }
        val request = createRequestParamsFile(fileObject.fileName, fileObject.mFields,null, fileObject.fileData, NCMBRequest.HTTP_METHOD_POST,callback, fileHandler)
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
    fun saveFile(fileObject: NCMBFile): JSONObject {
        val request = createRequestParamsFile(fileObject.fileName, fileObject.mFields, null, fileObject.fileData, NCMBRequest.HTTP_METHOD_POST, null, null)
        val response = sendRequest(request)
        when (response) {
            is NCMBResponse.Success -> {
                return response.data
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
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
        queryParams: JSONObject?,
        fileData: ByteArray?,
        method: String,
        callback: NCMBCallback?,
        handler: NCMBHandler?
    ): RequestParams {
        //url set
        val url: String = NCMB.getApiBaseUrl() + mServicePath + "/" + fileName
        return RequestParams(
            url = url,
            method = method,
            params = params,
            contentType = NCMBRequest.HEADER_CONTENT_TYPE_FILE,
            callback = callback,
            handler = handler
        )
    }


}