/*
 * Copyright 2017-2023 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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

import android.os.Build
import androidx.annotation.RequiresApi
import java.net.URL
import java.net.URLEncoder
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


/**
 * Signature calculate class.
 *
 * To do calculate signature headers every request.
 *
 */


internal class NCMBSignatureCal {

    companion object {
        //シグネチャメソッドのキー
        private const val SIGNATURE_METHOD_KEY = "SignatureMethod"

        //シグネチャメソッドの値
        private const val SIGNATURE_METHOD_VALUE = "HmacSHA256"

        //シグネチャバージョンのキー
        private const val SIGNATURE_VERSION_KEY = "SignatureVersion"

        // シグネチャバージョンの値
        private const val SIGNATURE_VERSION_VALUE = "2"

    }

    //Query parameters is passed as HashedMap NOT QuerySTRING
    @RequiresApi(Build.VERSION_CODES.O)
    fun calSignature(
        method: String,
        url: URL,
        timestamp: String,
        applicationKey: String,
        clientKey: String,
        queryParamMap: HashMap<String, String>
    ): String {
        return this.createSignature(
            method,
            url,
            timestamp,
            applicationKey,
            clientKey,
            queryParamMap
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createSignature(
        method: String,
        url: URL,
        timestamp: String,
        applicationKey: String,
        clientKey: String?,
        queryParamMap: HashMap<String, String>?
    ): String {

        val apiPath = url.path
        val apiFqdn = url.host
        val parameters = mutableListOf(
            SIGNATURE_METHOD_KEY to SIGNATURE_METHOD_VALUE,
            SIGNATURE_VERSION_KEY to SIGNATURE_VERSION_VALUE,
            NCMBRequest.HEADER_APPLICATION_KEY to applicationKey,
            NCMBRequest.HEADER_TIMESTAMP to timestamp
        )

        //Encode query parameter
        if (queryParamMap != null) {
            val encodedParamMap = encodeParamMap(queryParamMap)
            if (encodedParamMap != null) {
                for((k, v) in encodedParamMap) {
                    parameters.add(k to v)
                }
            }
        }

        parameters.sortWith(Comparator { o1, o2 -> o1.first.compareTo(o2.first) })

        val sortedQuery = StringBuilder(256)
        for (param in parameters) {
            sortedQuery.append(param.first).append('=').append(param.second).append('&')
        }
        sortedQuery.deleteCharAt(sortedQuery.lastIndexOf("&"))

        //署名用文字列を生成
        val sign = StringBuilder(256)
        sign.append(method.toUpperCase()).append('\n')
            .append(apiFqdn).append('\n')
            .append(apiPath).append('\n')
            .append(sortedQuery)

        //シグネチャを生成
        val algorithm = "HmacSHA256"

        // 暗号化
        val keySpec = SecretKeySpec(clientKey?.toByteArray(), algorithm)
        val alg = Mac.getInstance(algorithm)
        alg.init(keySpec)
        val tmpsign : ByteArray? = alg.doFinal(sign.toString().toByteArray(charset("UTF-8")))
        val encodedString: String = Base64.getEncoder().encodeToString(tmpsign)
        return encodedString
    }

    private fun getPath(url: URL): String {
        return url.path
    }

    private fun encodeParamMap(queryParamMap: HashMap<String, String>): HashMap<String, String>{
        for((k, v) in queryParamMap) {
            queryParamMap.put(k, URLEncoder.encode(v, "utf-8"))
        }
        return queryParamMap
    }
}
