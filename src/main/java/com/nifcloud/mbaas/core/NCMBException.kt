/*
 * Copyright 2017-2022 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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
 * Exceptions and error handling class.
 *
 * NCMBException is a class that defines and handling the errors and exceptions which were
 * returned from NIFCLOUD mobile backend server or from internal process of SDK.
 *
 */

class NCMBException : Exception {
    /**
     * Get exception error code
     *
     * @return Error code
     */
    var code: String
    /**
     * Get exception error message
     *
     * @return Error message
     */
    override var message: String = ""
    private set

    /**
     * Constructor
     *
     * @param code    Error code
     * @param message Error message
     */
    constructor(code: String, message: String) : super(message) {
        this.code = code
        this.message = message
    }

    /**
     * Constructor
     *
     * @param cause Exception
     */

    constructor(cause: Exception) : super(cause) {
        code = GENERIC_ERROR
        if (cause.message != null) {
            this.message = cause.localizedMessage
        }
    }

    companion object {
        /**
         * E000001 SDK Generic error (汎用エラー) (not API)
         */
        const val GENERIC_ERROR = "E000001"

        /**
         * E100001 Invalid response signature (レスポンスシグネチャ不正)  ※未対応
         */
        const val INVALID_RESPONSE_SIGNATURE = "E100001"

        /**
         * E400001 Invalid Json format (JSON形式不正)
         */
        const val INVALID_JSON = "E400001"

        /**
         * E400002 Invalid data type  (型が不正)
         */
        const val INVALID_TYPE = "E400002"

        /**
         * E400003 Required data is missing (必須項目で未入力)
         */
        const val REQUIRED = "E400003"

        /**
         * E400004 Invalid format (フォーマットが不正)
         */
        const val INVALID_FORMAT = "E400004"

        /**
         * E400005 Not efficient value (有効な値でない)
         */
        const val NOT_EFFICIENT_VALUE = "E400005"

        /**
         * E400006 Missing value (存在しない値)
         */
        const val MISSING_VALUE = "E400006"

        /**
         * E400008 Invalid Correlation (相関関係でエラー)
         */
        const val INVALID_CORRELATION = "E400008"

        /**
         * E401001 Invalid Authentication header (Header不正による認証エラー)
         */
        const val INVALID_AUTH_HEADER = "E401001"

        /**
         * E401002 Authenticate failed (ID/Pass認証エラー)
         */
        const val AUTH_FAILURE = "E401002"

        /**
         * E401003 OAuth authenticate error (OAuth認証エラー)
         */
        const val OAUTH_FAILURE = "E401003"

        /**
         * E403001 ACL access control failed (ＡＣＬによるアクセス権なし)
         */
        const val OPERATION_FORBIDDEN_BY_ACL = "E403001"

        /**
         * E403002 Operation forbidden by collaborated user (コラボレータ/管理者（サポート）権限なし)
         */
        const val OPERATION_FORBIDDEN_BY_USER_TYPE = "E403002"

        /**
         * E403003 Operation forbidden ( 禁止されているオペレーション)
         */
        const val OPERATION_FORBIDDEN = "E403003"

        /**
         * E403004 Onetime key expired (ワンタイムキー有効期限切れ)
         */
        const val EXPIRED_ONETIME_KEY = "E403004"

        /**
         * E403005 Invalid setting item (設定不可の項目)
         */
        const val INVALID_SETTING_NAME = "E403005"

        /**
         * E404001 Data not found (該当データなし)
         */
        const val DATA_NOT_FOUND = "E404001"

        /**
         * E404002 Service not found (該当サービスなし)
         */
        const val SERVICE_NOT_FOUND = "E404002"

        /**
         * E404003 Field not found (該当フィールドなし)
         */
        const val FIELD_NOT_FOUND = "E404003"

        /**
         * E409001 Dupplicate error (重複エラー, 項目によって一意の内容が異なる)
         */
        const val DUPLICATE_VALUE = "E409001"

        /**
         * E413001 Too large file size (1ファイルあたりのサイズ上限エラー)
         */
        const val FILE_TOO_LARGE = "E413001"

        /**
         * E415001 Unsupported media type (非サポートContent Type)
         */
        const val UNSUPPORT_MEDIA_TYPE = "E415001"

        /**
         * E429001 Usage restrict over (使用制限（APIコール数、PUSH通知数、ストレージ容量）超過)
         */
        const val RESTRICTED = "E429001"

        /**
         * E500001 Internal server error (内部エラー)
         */
        const val INTERNAL_SERVER_ERROR = "E500001"

        /**
         * E502001 Storage error (NIFCLOUD ストレージでエラーが発生した場合のエラー)
         */
        const val STORAGE_ERROR = "E502001"
    }
}
