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
