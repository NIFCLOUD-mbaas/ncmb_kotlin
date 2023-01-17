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

import android.content.Context
import android.content.Intent
import com.nifcloud.mbaas.core.NCMBDateFormat.getIso8601
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * NCMBPush is used to retrieve and send the push notification.<br></br>
 * NCMBPush can not add any field.<br></br>
 * Information about the field names that can be set , refer to the following reference .<br></br>
 * @see [NIFCLOUD mobile backend API Reference
](https://mbaas.nifcloud.com/doc/current/rest/push/pushRegistration.html) */
class NCMBPush : NCMBObject {
    var target = arrayListOf<String>()
    var isSendToIOS: Boolean = false
        get() {
            return field
        }
        set(value) {
            field = value
            if (value) {
                target.add("ios")
            }
        }
    var isSendToAndroid: Boolean = false
        get() {
            return field
        }
        set(value) {
            field = value
            if (value) {
                target.add("android")
            }
        }

    /**
     * Get delivery date
     *
     * @return Date delivery time(UTC)
     */
    /**
     * Set delivery date
     * The argument is the time based on default time zone of the device.
     * @param value delivery Time(UTC)
     */
    var deliveryTime: Date?
        get() {
            return try {
                if (mFields.isNull("deliveryTime")) {
                    return null
                }
                val format: DateFormat = getIso8601()
                format.parse(mFields.getJSONObject("deliveryTime").getString("iso"))
            } catch (error: Exception) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        set(value) {
            try {
                //mBaaSの日付型文字列に変換して設定
                mFields.put("deliveryTime", createIsoDate(value))
                mUpdateKeys.add("deliveryTime")
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }

    /**
     * Set delivery time as a character string.
     * Type of string to set is "yyyy-MM-dd HH:mm:ss".
     * An example is "2022-02-03 12:34:56".
     * Also, The registered value is {"iso":"yyyy-MM-ddTHH:mm:ss.000Z","__type":"Date"}.
     *
     * @param value deliveryTime
     * @param timezone Timezone of time value. Default timezone of system will be use if not be set
     */
    open fun setDeliveryTimeString(value: String, timeZone: TimeZone? = null){
        val date: Date?
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        if (timeZone != null){
            df.timeZone = timeZone
        }
        try {
            date = df.parse(value)
            deliveryTime = date
        } catch (e: ParseException) {
            throw NCMBException(e)
        }
    }

    /**
     * Get delivery expiration date
     *
     * @return Date delivery expiration date
     */
    /**
     * Set delivery expiration date
     * The argument is the time based on default time zone of the device.
     * @param value delivery expiration date
     */
    var deliveryExpirationDate: Date?
        get(){
            return try {
                if (mFields.isNull("deliveryExpirationDate")) {
                    return null
                }
                val format: DateFormat = getIso8601()
                format.parse(mFields.getJSONObject("deliveryExpirationDate").getString("iso"))
            } catch (error: Exception) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        set(value){
            try {
                //mBaaSの日付型に変換して設定
                mFields.put("deliveryExpirationDate", createIsoDate(value))
                mUpdateKeys.add("deliveryExpirationDate")
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }

    /**
     * Get delivery expiration time
     *
     * @return Date delivery expiration time
     */
    /**
     * Set delivery expiration time
     * The argument is numbers + half-width spaces + hour or day.
     * For Example, 「3 hour」,「5 day」
     * @param value delivery expiration date
     */
    var deliveryExpirationTime: String?
        get(){
            return try {
                if (mFields.isNull("deliveryExpirationTime")) {
                    null
                } else mFields.getString("deliveryExpirationTime")
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        set(value) {
            try {
                //数値+day or hourでない場合はException
                val re1 = Regex("[0-9]{1,} day$")
                val re2 = Regex("[0-9]{1,} hour$")
                if (value != null) {
                    if (value.matches(re1) || value.matches(re2)) {
                        mFields.put("deliveryExpirationTime", value)
                        mUpdateKeys.add("deliveryExpirationTime")
                    }
                    else{
                        throw NCMBException(NCMBException.INVALID_FORMAT, "deliveryExpirationTime is invalid format. Please set string such as 'XX day' or 'YY hour'.")
                    }
                }
            } catch (error: JSONException) {
            throw NCMBException(IllegalArgumentException(error.message))
            }
        }

    /**
     * Get push message
     *
     * @return String push message
     */
    /**
     * Set push message
     *
     * @param value message
     */
    var message: String?
        get() {
            return try {
                if (mFields.isNull(MESSAGE)) {
                    null
                } else mFields.getString(MESSAGE)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        set(value) {
            try {
                mFields.put(MESSAGE, value)
                mUpdateKeys.add(MESSAGE)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }

    /**
     * Get push title
     *
     * @return String get push title
     */
    /**
     * Set push title
     *
     * @param value title
     */
    var title: String?
        get() {
            return try {
                if (mFields.isNull(TITLE)) {
                    null
                } else mFields.getString(TITLE)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        set(value) {
            try {
                mFields.put(TITLE, value)
                mUpdateKeys.add(TITLE)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }

    /**
     * Get richUrl
     *
     * @return String richUrl
     */
    /**
     * Set richUrl
     *
     * @param value richUrl
     */
    var richUrl: String?
        get() {
            return try {
                if (mFields.isNull(RICH_URL)) {
                    null
                } else mFields.getString(RICH_URL)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        set(value) {
            try {
                mFields.put(RICH_URL, value)
                mUpdateKeys.add(RICH_URL)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }

    /**
     * Get search condition
     *
     * @return JSONObject search condition
     */
    fun getSearchCondition(): JSONObject? {
        return try {
            if (mFields.isNull(SEARCH_CONDITION)) {
                null
            } else mFields.getJSONObject(SEARCH_CONDITION)
        } catch (error: JSONException) {
            throw NCMBException(IllegalArgumentException(error.message))
        }
    }

    /**
     * Set search condition
     *
     * @param query NCMBQuery for installation search
     */
    fun setSearchCondition(query: NCMBQuery<NCMBInstallation>) {
        try {
            val whereConditions = query.query
            var value: JSONObject? = JSONObject()
            if (whereConditions.has("where")) {
                value = whereConditions.getJSONObject("where")
            }
            mFields.put(SEARCH_CONDITION, value)
            mUpdateKeys.add(SEARCH_CONDITION)
        } catch (error: JSONException) {
            throw NCMBException(IllegalArgumentException(error.message))
        }
    }

    /**
     * Get badge increment flag
     *
     * @return Boolean badge increment flag
     */
    /**
     * Set badge increment flag
     *
     * @param value badge increment flag
     */
    var immediateDeliveryFlag: Boolean?
        get(){
            return try {
                if (mFields.isNull(IMMEDIATE_DELIVERY_FLAG)) {
                    null
                } else mFields.getBoolean(IMMEDIATE_DELIVERY_FLAG)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }
        set(value) {
            try {
                mFields.put(IMMEDIATE_DELIVERY_FLAG, value)
                mUpdateKeys.add(IMMEDIATE_DELIVERY_FLAG)
            } catch (error: JSONException) {
                throw NCMBException(IllegalArgumentException(error.message))
            }
        }

    /**
     * Constructor
     */
    constructor() : super("push") {
        mIgnoreKeys = ignoreKeys
    }

    /**
     * Constructor
     *
     * @param params input parameters
     * @throws NCMBException
     */
    internal constructor(params: JSONObject) : super("push", params) {
        mIgnoreKeys = ignoreKeys
    }

    // region send
    /**
     * Send push object
     *
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    override fun save() {
        //connect
        val pushService = NCMBPushService()
        val responseData: JSONObject
        if(target.size > 0){
            mFields.put(TARGET, JSONArray(target.distinct()))
            mUpdateKeys.add(TARGET)
        }
        else{
            throw NCMBException(NCMBException.INVALID_FORMAT,
                "'target' do not set."
            )
        }
        if (getObjectId() == null) {
            //new create
            responseData = pushService.sendPush(mFields)
        }
        else {
            //update
            val updateJson = try {
                createUpdateJsonData()
            } catch (e: JSONException) {
                throw NCMBException(IllegalArgumentException(e.message))
            }
            responseData = pushService.updatePush(getObjectId(), updateJson)
        }
        setPushLocalData(responseData)
        mUpdateKeys.clear()
    }

    /**
     * create a date of mBaaS correspondence
     *
     * @param value iso value
     * @return JSONObject
     * @throws JSONException
     */
    @Throws(JSONException::class)
    fun createIsoDate(value: Date?): JSONObject? {
        val dateJson = JSONObject()
        dateJson.put("iso", getIso8601().format(value))
        dateJson.put("__type", "Date")
        return dateJson
    }

    // region internal method
    /**
     * Set data to instance
     *
     * @param data json params
     */
    @Throws(NCMBException::class)
    fun setPushLocalData(data: JSONObject) {
        try {
            //新規作成時
            if (data.has("createDate") && !data.has("updateDate")) {
                data.put("updateDate", data.getString("createDate"))
            }
            run {
                val keys = data.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    mFields.put(key, data[key])
                }
            }
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_JSON, e.message!!)
        }
    }

    /**
     * Do  display the webview when an URL is containted in the payload data
     *
     * @param context context
     * @param intent  URL
     */
    fun richPushHandler(context: Context?, intent: Intent?) {
        if (intent == null) {
            return
        }
        // URLチェック
        val url = intent.getStringExtra("com.nifcloud.mbaas.RichUrl") ?: return
        // URLのバリデーションチェック
        if (!url.matches(MATCH_URL_REGEX)) {
            return
        }

        // ダイアログ表示
        val dialog = NCMBRichPush(context, url)
        dialog.show()
    }

    // endregion

    /**
     * Open push registration in background
     *
     * @param intent ActivityIntent
     */
    fun trackAppOpened(intent: Intent?) {
        if (intent == null) {
            return
        }
        val pushId = intent.getStringExtra("com.nifcloud.mbaas.PushId") ?: return
        val pushService = NCMBPushService()
        val callback = NCMBCallback{e, response ->}
        pushService.sendPushReceiptStatusInBackground(pushId, callback)
    }

    companion object {
        private val MATCH_URL_REGEX =
            "^(https?)(:\\/\\/[-_.!~*\\'()a-zA-Z0-9;\\/?:\\@&=+\\$,%#]+)$".toRegex()
        val ignoreKeys = Arrays.asList(
            "objectId","createDate", "updateDate"
        )

        const val TARGET = "target"
        const val MESSAGE = "message"
        const val TITLE = "title"
        const val IMMEDIATE_DELIVERY_FLAG = "immediateDeliveryFlag"
        const val RICH_URL = "richUrl"
        const val SEARCH_CONDITION = "searchCondition"
    }
}