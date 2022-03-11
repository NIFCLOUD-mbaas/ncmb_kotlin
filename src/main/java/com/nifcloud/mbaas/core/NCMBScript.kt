package com.nifcloud.mbaas.core

import org.json.JSONObject
import java.util.Map

/**
 * NCMBScript is used to script.
 */
class NCMBScript {

    var mMethod: MethodType
    var mScriptName: String
//    var method: MethodType
//        get() {
//            return  mMethod
//        }
//        set(value) {
//            mMethod = value
//        }

    var mBaseUrl: String =  "script.mbaas.api.nifcloud.com" //SCRIPT URL //TODO

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
    fun execute(header: Map<String?, String?>?, body: JSONObject?, query: JSONObject?){
//        val scriptService = NCMBScriptService()
//        return scriptService.executeScript(
//            mScriptName,
//            mMethod,
//            header,
//            body,
//            query,
//            mBaseUrl
//        )
    }

    /**
     * Execute the script asynchronously with request parameters
     *
     * @param header   header data
     * @param body     content data
     * @param query    query params
     * @param callback callback after execute script
     */
    fun executeInBackground(
        header: Map<String?, String?>?,
        body: JSONObject?,
        query: JSONObject?,
        callback: NCMBCallback?
    ) {
//        val scriptService = NCMB.factory(NCMB.ServiceType.SCRIPT) as NCMBScriptService
//        scriptService.executeScriptInBackground(
//            mScriptName,
//            mMethod, header, body, query,
//            mBaseUrl, object : ExecuteScriptCallback() {
//                fun done(data: ByteArray?, e: NCMBException?) {
//                    if (callback != null) {
//                        callback.done(data, e)
//                    }
//                }
//            })
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
        mMethod = method
    }

}