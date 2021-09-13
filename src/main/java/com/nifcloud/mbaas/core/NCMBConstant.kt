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

/**
 * A class of ncmb_kotlin.
 *
 * To keep NCMBConstants
 *
 */

class NCMBConstant {
    companion object {
        enum class HTTP_METHOD {
            GET, POST, PUT, DELETE
        }

        //アプリケーションキー
        val HEADER_APPLICATION_KEY = "X-NCMB-Application-Key"
        //シグネチャ
        val HEADER_SIGNATURE = "X-NCMB-Signature"
        //タイムスタンプ
        val HEADER_TIMESTAMP = "X-NCMB-Timestamp"
        //Access-Control-Allow-Origin
        val HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin"
        //アプリセッショントークン
        val HEADER_APPS_SESSION_TOKEN = "X-NCMB-Apps-Session-Token"
        //コンテントタイプ
        val HEADER_CONTENT_TYPE = "Content-Type"
        //JSON形式のコンテントタイプの値
        val HEADER_CONTENT_TYPE_JSON = "application/json"
        //ファイル形式のコンテントタイプの値
        val HEADER_CONTENT_TYPE_FILE = "multipart/form-data"
        //SDKVersionのキー
        val HEADER_SDK_VERSION = "X-NCMB-SDK-Version"
        //OSVersionのキー
        val HEADER_OS_VERSION = "X-NCMB-OS-Version"

        //シグネチャメソッドのキー
        val SIGNATURE_METHOD_KEY = "SignatureMethod"
        //シグネチャメソッドの値
        val SIGNATURE_METHOD_VALUE = "HmacSHA256"
        //シグネチャバージョンのキー
        val SIGNATURE_VERSION_KEY = "SignatureVersion"
        // シグネチャバージョンの値
        val SIGNATURE_VERSION_VALUE = "2"
    }
}

/// NCMBQueryで使用するコンスタント値を管理するクラスです。
class NCMBQueryConstants {

    companion object {
        const val REQUEST_PARAMETER_WHERE : String = "where"
        const val REQUEST_PARAMETER_ORDER : String = "order"
        const val REQUEST_PARAMETER_SKIP : String = "skip"
        const val REQUEST_PARAMETER_LIMIT : String = "limit"
        const val REQUEST_PARAMETER_COUNT : String = "count"
        const val RESPONSE_PARAMETER_RESULTS : String = "results"
        const val RESPONSE_PARAMETER_COUNT : String = "count"
        const val QUERY_OPERATOR_NE : String = "ne"
        const val QUERY_OPERATOR_LT : String = "lt"
        const val QUERY_OPERATOR_GT : String = "gt"
        const val QUERY_OPERATOR_LTE : String = "lte"
        const val QUERY_OPERATOR_GTE : String = "gte"
        const val QUERY_OPERATOR_IN : String = "in"
        const val QUERY_OPERATOR_NIN : String = "nin"
        const val QUERY_OPERATOR_EXISTS : String = "exists"
        const val QUERY_OPERATOR_REGEX : String = "regex"
        const val QUERY_OPERATOR_INARRAY : String = "inArray"
        const val QUERY_OPERATOR_NINARRAY : String = "ninArray"
        const val QUERY_OPERATOR_ALL : String = "all"
        const val QUERY_OPERATOR_OR : String = "or"
    }
}