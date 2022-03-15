package com.nifcloud.mbaas.core

import org.json.JSONObject
import java.util.Map

/**
 * NCMBScript is used to script.
 */
class NCMBScript {

    var mMethodType : MethodType
    var mMethod: String = ""
        get() {
            return (when (mMethodType) {
                MethodType.DELETE -> NCMBRequest.HTTP_METHOD_DELETE
                MethodType.GET -> NCMBRequest.HTTP_METHOD_GET
                MethodType.POST -> NCMBRequest.HTTP_METHOD_POST
                MethodType.PUT -> NCMBRequest.HTTP_METHOD_PUT
            })
        }
    var mScriptName: String

    /**
     * HTTP method types
     */
    enum class MethodType {
        POST, PUT, GET, DELETE
    }

    /**
     * Execute the script with request parameters
     *
     * @param header header data
     * @param body   content data
     * @param query  query params
     * @return Result to script
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun execute(header: HashMap<String, String>?, body: JSONObject?, query: JSONObject?){
        val scriptService = NCMBScriptService()
        return scriptService.executeScript(
            mScriptName,
            mMethod,
            header,
            body,
            query
        )
    }

    /**
     * Execute the script asynchronously with request parameters
     *
     * @param header   header data  //Not available yet
     * @param body     content data //Not available yet
     * @param query    query params //Not available yet
     * @param callback callback after execute script
     */
    fun executeInBackground(
        scriptHeader: HashMap<String, String> = HashMap<String, String>(),
        scriptBody: JSONObject = JSONObject(),
        scriptQuery: JSONObject = JSONObject(),
        callback: NCMBCallback
    ) {
        val scriptService = NCMBScriptService()
        scriptService.executeScriptInBackground(
            mScriptName,
            mMethod,
            scriptHeader,
            scriptBody,
            scriptQuery,
            callback
            )
    }

    /**
     * Create NCMBScript instance with specified script name and request method
     * This constructor can set the custom endpoint for debug
     *
     * @param scriptName script name
     * @param method     HTTP method
     * @param baseUrl    script base url
     */
    /**
     * Create NCMBScript instance with specified script name and request method
     *
     * @param scriptName script name
     * @param method     HTTP method
     */
    constructor(scriptName: String,method: MethodType){
        mScriptName = scriptName
        mMethodType = method
    }

}