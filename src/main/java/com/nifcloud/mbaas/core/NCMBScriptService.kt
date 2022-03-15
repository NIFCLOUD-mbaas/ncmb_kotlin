package com.nifcloud.mbaas.core

import android.os.AsyncTask
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

/**
 * Service class for script api
 */
internal class NCMBScriptService : NCMBService() {

    /**
     * execute script to NIFCLOUD mobile backend
     *
     * @param scriptName script name
     * @param method     HTTP method
     * @param header     header data
     * @param body       content data
     * @param query      query params
     * @param baseUrl    script base url
     * @return Result to script
     * @throws NCMBException exception from NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun executeScript(
        scriptName: String,
        method: String,
        scriptHeader: HashMap<String, String>?,
        scriptBody: JSONObject?,
        scriptQuery: JSONObject?
    ) {
       //TODO
    }

    /**
     * execute script to NIFCLOUD mobile backend in background thread
     *
     * @param scriptName script name
     * @param method     HTTP method
     * @param header     header
     * @param body       content data
     * @param query      query params
     * @param baseUrl    script base url
     * @param callback   callback for after script execute
     */
    fun executeScriptInBackground(
        scriptName: String,
        method: String,
        scriptHeader: HashMap<String, String>,
        scriptBody: JSONObject,
        scriptQuery: JSONObject,
        executeCallback: NCMBCallback
    ) {
        val executeHandler = NCMBHandler { scriptcallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    executeCallback.done(null, responseScript = response.data as ByteArray)
                }
                is NCMBResponse.Failure -> {
                    executeCallback.done(response.resException)
                }
            }
        }
        val reqParams : RequestParams = executeScriptParams(
            scriptName,
            method,
            scriptHeader,
            scriptBody,
            scriptQuery,
            executeCallback,
            executeHandler)
        sendRequestAsync(reqParams, executeCallback, executeHandler)

    }

    /*
    * @param
    * @param executeCallback callback when process finished
    * @param executeHandler sdk after-connection tasks
    * @return parameters in object
    */
    protected fun executeScriptParams(scriptName: String,
                                      method: String,
                                      scriptHeader: HashMap<String, String>,
                                      scriptBody: JSONObject,
                                      scriptQuery: JSONObject,
                                      executeCallback: NCMBCallback?,
                                      executeHandler: NCMBHandler?): RequestParams {
        val url = NCMB.getApiBaseUrl(isScript = true) + mServicePath + "/" + scriptName
        val method = method
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        return RequestParams(url = url,
            method = method, contentType = contentType, callback = executeCallback, handler = executeHandler)
    }


    companion object {
        /**
         * execute api path
         */
        const val SERVICE_PATH = "script"

    }

    /**
     * Constructor
     *
     * @param context Service context
     */
    init {
        mServicePath = SERVICE_PATH
    }
}