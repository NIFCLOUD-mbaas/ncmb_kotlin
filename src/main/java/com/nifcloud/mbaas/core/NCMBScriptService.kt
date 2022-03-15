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
        method: NCMBScript.MethodType,
        scriptHeader: HashMap<String, String>?,
        scriptBody: JSONObject?,
        scriptQuery: JSONObject?,
        baseUrl: String?
    ) {


//        val scriptUrl: String
//        scriptUrl = if (baseUrl != null && baseUrl.length > 0) {
//            "$baseUrl/$scriptName"
//        } else {
//            DEFAULT_SCRIPT_DOMAIN_URL + "/" + DEFAULT_SCRIPT_API_VERSION + "/" + mServicePath + "/" + scriptName
//        }
//        var responseByte: ByteArray? = null
//        var urlConnection: HttpURLConnection? = null
//        val type: String
//        try {
//            type = when (method) {
//                NCMBScript.MethodType.POST -> NCMBRequest.HTTP_METHOD_POST
//                NCMBScript.MethodType.PUT -> NCMBRequest.HTTP_METHOD_PUT
//                NCMBScript.MethodType.GET -> NCMBRequest.HTTP_METHOD_GET
//                NCMBScript.MethodType.DELETE -> NCMBRequest.HTTP_METHOD_DELETE
//                else -> throw IllegalArgumentException("Invalid methodType")
//            }
//            var content: String? = null
//            if (body != null) {
//                content = body.toString()
//            }
//            if (mContext.sessionToken == null) {
//                mContext.sessionToken = NCMBUser.sessionToken
//            }
//            val sessionToken: String = mContext.sessionToken
//            val applicationKey: String = mContext.applicationKey
//            val clientKey: String = mContext.clientKey
//            val request = NCMBRequest(
//                scriptUrl,
//                type,
//                content,
//                query,
//                sessionToken,
//                applicationKey,
//                clientKey
//            )
//
//            // query連結済みURLでコネクション作成
//            val url: URL = request.url
//            urlConnection = url.openConnection() as HttpURLConnection
//
//            // メソッド設定
//            urlConnection.requestMethod = request.method
//
//            // NCMB定義のヘッダー設定
//            for (requestKey in request.getAllRequestProperties().keys) {
//                urlConnection!!.setRequestProperty(
//                    requestKey,
//                    request.getRequestProperty(requestKey)
//                )
//            }
//
//            // User定義のヘッダー設定
//            if (header != null && !header.isEmpty()) {
//                for (requestKey in header.keys) {
//                    urlConnection!!.setRequestProperty(requestKey, header[requestKey])
//                }
//            }
//
//            // body設定
//            if (request.getContent() != null) {
//                urlConnection!!.doOutput = true
//                val out = DataOutputStream(urlConnection.outputStream)
//                val writer = BufferedWriter(OutputStreamWriter(out, "UTF-8"))
//                writer.write(request.getContent())
//                writer.flush()
//                writer.close()
//            }
//
//            // 通信
//            urlConnection!!.connect()
//
//            // 判定
//            if (urlConnection.responseCode == HttpURLConnection.HTTP_CREATED
//                || urlConnection.responseCode == HttpURLConnection.HTTP_OK
//            ) {
//                // 成功
//                val buffer = ByteArrayOutputStream()
//                val data = ByteArray(32768)
//                var nRead: Int
//                while (urlConnection.inputStream.read(data, 0, data.size).also {
//                        nRead = it
//                    } != -1) {
//                    buffer.write(data, 0, nRead)
//                }
//                responseByte = buffer.toByteArray()
//            } else {
//                // 失敗
//                val br = BufferedReader(
//                    InputStreamReader(
//                        urlConnection.errorStream
//                    )
//                )
//                val sb = StringBuilder()
//                var line: String?
//                while (br.readLine().also { line = it } != null) {
//                    sb.append(line)
//                }
//                br.close()
//                var statusCode = urlConnection.responseCode.toString()
//                var message = sb.toString()
//                if (message.length > 0 && isJSONString(message)) {
//                    val responseData = JSONObject(message)
//                    if (responseData.has("status")) {
//                        statusCode = responseData.getString("status")
//                    } else if (responseData.has("code")) {
//                        statusCode = responseData.getString("code")
//                    }
//                    if (responseData.has("error")) {
//                        message = responseData.getString("error")
//                    }
//                }
//                throw NCMBException(statusCode, message)
//            }
//        } catch (e: IOException) {
//            throw NCMBException(e)
//        } catch (e: JSONException) {
//            throw NCMBException(e)
//        } finally {
//            urlConnection?.disconnect()
//        }
//        return responseByte
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
        scriptHeader: HashMap<String, String>?,
        scriptBody: JSONObject?,
        scriptQuery: JSONObject?,
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
                                      scriptHeader: HashMap<String, String>?,
                                      scriptBody: JSONObject?,
                                      scriptQuery: JSONObject?,
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