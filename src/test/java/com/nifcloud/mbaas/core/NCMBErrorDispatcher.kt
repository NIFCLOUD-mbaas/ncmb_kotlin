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

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Headers
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONException
import org.json.JSONObject
import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareMode
import org.yaml.snakeyaml.Yaml
import java.io.*
import java.net.URLDecoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

class NCMBErrorDispatcher: Dispatcher() {

    private val NUMBER_PATTERN = """"[0-9]+""".toRegex()
    private val BOOL_PATTERN = """[true|false]""".toRegex()

    @Throws(InterruptedException::class)
    override fun dispatch(request: RecordedRequest): MockResponse {
        var input: InputStream? = null
        try {
            input = FileInputStream(File("src/test/assets/yaml/mbaas_error.yml"))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val yaml = Yaml()
        var map: Map<String?, Any?>? = null
        var requestMap: Map<String?, Any>? = null
        var responseMap: Map<String?, Any>? = null
        var requestBody: String? = null
        for (data in yaml.loadAll(input)) {
            map = data as Map<String?, Any?>?
            requestMap = map?.get("request") as Map<String?, Any>?
            responseMap = map?.get("response") as Map<String?, Any>?
            val pathAndQuery =
                request.path?.split("\\?".toRegex())?.dropLastWhile { it.isEmpty() }
                    ?.toTypedArray()
            val path = pathAndQuery?.get(0)
            var query: String? = null
            if (pathAndQuery?.size!! > 1) {
                query = pathAndQuery?.get(1)
            }
            if (requestMap!!["url"] != path) {
                continue
                //return defaultErrorResponse();
            }
            if (requestMap["url"] == "/2013-09-01/classes/ResponseSignatureTest") {
                return MockResponse().setResponseCode(responseMap!!["status"] as Int)
                    .setHeader("Content-Type", "application/json")
                    .setHeader(
                        "X-NCMB-Response-Signature",
                        "9XuXzrgyI/B7E8KpXzeGUaAXcdO5h/hlL+4o/GVm/T4="
                    )
                    .setBody(readJsonResponse(responseMap["file"].toString()))
            }
            //Form data file upload request and response
            if(requestMap["url"] == "/2013-09-01/files/tempFileErr413.txt" || requestMap["url"] == "/2013-09-01/files/tempFileErr415.txt") {
                if (requestMap["method"] != request.method) {
                    continue
                }
                if (requestMap.containsKey("body")) {
                    return MockResponse().setResponseCode(responseMap!!["status"] as Int)
                        .setHeader("Content-Type", "application/json")
                        .setBody(readJsonResponse(responseMap["file"].toString()))
                }
            }
            if (requestMap["method"] != request.method) {
                continue
                //return defaultErrorResponse();
            }
            if (query != null) {
                if (requestMap.containsKey("query")) {
                    val mockQuery = requestMap["query"]
                    val mockQueryStr: String = Gson().toJson(mockQuery)
                    if (checkRequestQuery(mockQueryStr, query)!!) {
                        continue
                    }
                } else {
                    continue
                }
            }
            if (requestMap.containsKey("body")) {
                return try {
                    if (requestBody == null) {
                        requestBody = request.body.readString(
                            request.bodySize,
                            Charset.forName("UTF-8")
                        )
                    }
                    val mockBody = requestMap["body"]
                    val gson: Gson = GsonBuilder().serializeNulls().create()
                    val mockBodyStr: String = gson.toJson(mockBody)
                    println("mock:$mockBodyStr")
                    println("req:$requestBody")
                    if (requestBody?.let { checkRequestBody(mockBodyStr, it) }!!) {
                        //Responseをreturn
                        MockResponse().setResponseCode(responseMap!!["status"] as Int)
                            .setHeader("Content-Type", "application/json")
                            .setBody(readJsonResponse(responseMap["file"].toString()))
                    } else {
                        continue
                    }
                } catch (e: IOException) {
                    defaultErrorResponse()
                }
            }
            if (requestMap.containsKey("header")) {
                val requestHeaders: Headers = request.headers
                val gson: Gson = GsonBuilder().serializeNulls().create()
                val mock: String = gson.toJson(requestMap["header"])
                try {
                    val mockHeaders =
                        JSONObject(requestMap["header"].toString())
                    //println("mock:" + mockHeaders);
                    //println("req:" + requestHeaders);
                    val keys: Iterator<*> = mockHeaders.keys()
                    while (keys.hasNext()) {
                        val key = keys.next() as String
                        if (requestHeaders.get(key) != null && requestHeaders.get(key)
                                .equals(mockHeaders.getString(key))
                        ) {
                            return MockResponse().setResponseCode(responseMap!!["status"] as Int)
                                .setHeader("Content-Type", "application/json")
                                .setBody(readJsonResponse(responseMap["file"].toString()))
                        }
                    }
                } catch (e: JSONException) {
                    return defaultErrorResponse()
                }
                continue
            }
            return MockResponse().setResponseCode(responseMap!!["status"] as Int)
                .setHeader("Content-Type", "application/json")
                .setBody(readJsonResponse(responseMap["file"].toString()))
        }
        return defaultErrorResponse()
    }

    private fun defaultErrorResponse(): MockResponse {
        return MockResponse().setResponseCode(500)
            .setHeader("Content-Type", "application/json")
            .setBody(readJsonResponse("E500001_error_response.json"))
    }

    /**
     * Utilities **
     */
    private fun checkRequestQuery(
        mockRequestQueryStr: String,
        realRequestQueryStr: String
    ): Boolean? {
        println("checkRequestQuery")
        try {
            val mockQuery = JSONObject(mockRequestQueryStr)
            val decodedQueryStr =
                URLDecoder.decode(realRequestQueryStr, "UTF-8")
            val realQueryMap =
                HashMap<String?, Any?>()
            val queryArray =
                decodedQueryStr.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (query in queryArray) {
                val queryData =
                    query.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val key = queryData[0]
                var value = ""
                if (queryData.size == 2) {
                    value = queryData[1]
                }
                if (value.matches(NUMBER_PATTERN)) {
                    realQueryMap[key] = value.toInt()
                } else if (value.matches(BOOL_PATTERN)) {
                    realQueryMap[key] = java.lang.Boolean.parseBoolean(value)
                } else {
                    realQueryMap[key] = value
                }
            }
            val realQuery = JSONObject(realQueryMap)
            println("mockQuery:$mockQuery")
            println("realQuery:$realQuery")
            if (!JSONCompare.compareJSON(mockQuery, realQuery, JSONCompareMode.NON_EXTENSIBLE).passed()) {
                return false
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            return false
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            return false
        }
        return true
    }
    private fun checkRequestBody(
        mockRequestBodyStr: String,
        realRequestBodyStr: String
    ): Boolean {
        try {
            val mockBody = JSONObject(mockRequestBodyStr)
            val realBody = JSONObject(URLDecoder.decode(realRequestBodyStr, "UTF-8"))
            if (mockBody.length() != realBody.length()) {
                return false
            }
            if (!JSONCompare.compareJSON(mockBody, realBody, JSONCompareMode.NON_EXTENSIBLE).passed()) {
                return false
            }
            /*
            Iterator<String> iter = mockBody.keys();
            while (iter.hasNext()){
                String key = iter.next();
                System.out.println("key: " + key);
                if (!mockBody.get(key).equals(realBody.get(key))){
                    System.out.println(mockBody.get(key) + " is not equals " + realBody.get(key));
                    result = false;
                }
            }
            */
        } catch (e: JSONException) {
            e.printStackTrace()
            return false
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun readJsonResponse(file_name: String): String {
        //val file = File("src/test/assets/json/$file_name")
        val file = Paths.get("src/test/assets/json_error/$file_name")

        var json: String = ""
        try {
            json = Files.readAllLines(file, StandardCharsets.UTF_8).toString();
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return json.drop(1).dropLast(1) //NEED extra process since [] is included in return string
    }
}
