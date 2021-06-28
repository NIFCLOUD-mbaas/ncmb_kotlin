package com.nifcloud.mbaas.core

import android.os.Build
import androidx.annotation.RequiresApi
import java.net.URL
import java.net.URLEncoder
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


/**
 * A class of ncmb_kotlin.
 *
 * To do calculate signature task
 *
 */


class NCMBSignatureCal {

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
        clientKey: String?,
        queryParamMap: HashMap<String, String>?
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

        val apiPath = getPath(url)

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
            .append("mbaas.api.nifcloud.com").append('\n')
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

    private fun encodeParamMap(queryParamMap: HashMap<String, String>): HashMap<String, String>?{
        return if (queryParamMap != null) {
            for((k, v) in queryParamMap) {
                queryParamMap.put(k, URLEncoder.encode(v, "utf-8"))
            }
            queryParamMap
        } else {
            null
        }
    }
}
