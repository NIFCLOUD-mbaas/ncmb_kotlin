package com.nifcloud.mbaas.core

/**
 * A class of ncmb_kotlin.
 *
 * NCMBException is a class that defines the error to be returned from NIFCLOUD mobile backend or from inner process of SDK
 *
 */

class NCMBException : Exception {
    /**
     * 例外のコード番号を取得
     *
     * @return エラーコード
     */
    var code: String

    override var message: String = ""
    private set

    /**
     * コンストラクタ
     *
     * @param code    エラーコード
     * @param message エラーメッセージ
     */
    constructor(code: String, message: String) : super(message) {
        this.code = code
        this.message = message
    }

    /**
     * コンストラクタ
     *
     * @param cause 例外
     */

    constructor(cause: Exception) : super(cause) {
        code = GENERIC_ERROR
        if (cause.message != null) {
            this.message = cause.message!!
        }
    }

    companion object {
        /**
         * E000001 SDK 汎用エラー (not API)
         */
        const val GENERIC_ERROR = "E000001"

        /**
         * E100001 レスポンスシグネチャ不正
         */
        const val INVALID_RESPONSE_SIGNATURE = "E100001"

        /**
         * E400001 JSON形式不正
         */
        const val INVALID_JSON = "E400001"

        /**
         * E400002 型が不正
         */
        const val INVALID_TYPE = "E400002"

        /**
         * E400003 必須項目で未入力
         */
        const val REQUIRED = "E400003"

        /**
         * E400004 フォーマットが不正
         */
        const val INVALID_FORMAT = "E400004"

        /**
         * E400005 有効な値でない
         */
        const val NOT_EFFICIENT_VALUE = "E400005"

        /**
         * E400006 存在しない値
         */
        const val MISSING_VALUE = "E400006"

        /**
         * E401001 Header不正による認証エラー
         */
        const val INVALID_AUTH_HEADER = "E401001"

        /**
         * E401002 ID/Pass認証エラー
         */
        const val AUTH_FAILURE = "E401002"

        /**
         * E401003 OAuth認証エラー
         */
        const val OAUTH_FAILURE = "E401003"

        /**
         * E403001 ＡＣＬによるアクセス権なし
         */
        const val OPERATION_FORBIDDEN_BY_ACL = "E403001"

        /**
         * E403002 コラボレータ/管理者（サポート）権限なし
         */
        const val OPERATION_FORBIDDEN_BY_USER_TYPE = "E403002"

        /**
         * E403003 禁止されているオペレーション
         */
        const val OPERATION_FORBIDDEN = "E403003"

        /**
         * E403004 ワンタイムキー有効期限切れ
         */
        const val EXPIRED_ONETIME_KEY = "E403004"

        /**
         * E403005 設定不可の項目
         */
        const val INVALID_SETTING_NAME = "E403005"

        /**
         * E404001 該当データなし
         */
        const val DATA_NOT_FOUND = "E404001"

        /**
         * E404002 該当サービスなし
         */
        const val SERVICE_NOT_FOUND = "E404002"

        /**
         * E404003 該当フィールドなし
         */
        const val FIELD_NOT_FOUND = "E404003"

        /**
         * E409001 重複エラー<br></br>
         * 項目によって一意の内容が異なる<br></br>
         */
        const val DUPLICATE_VALUE = "E409001"

        /**
         * E413001 1ファイルあたりのサイズ上限エラー
         */
        const val FILE_TOO_LARGE = "E413001"

        /**
         * E429001 使用制限（APIコール数、PUSH通知数、ストレージ容量）超過
         */
        const val RESTRICTED = "E429001"

        /**
         * E500001 内部エラー
         */
        const val INTERNAL_SERVER_ERROR = "E500001"

        /**
         * E502001 ストレージエラー<br></br>
         * NIFCLOUD ストレージでエラーが発生した場合のエラー
         */
        const val STORAGE_ERROR = "E502001"
    }
}
