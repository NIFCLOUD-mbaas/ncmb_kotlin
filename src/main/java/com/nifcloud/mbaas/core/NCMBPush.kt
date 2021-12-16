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

import com.nifcloud.mbaas.core.NCMBDateFormat.getIso8601
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.IllegalArgumentException
import java.text.DateFormat
import java.text.ParseException
import java.util.*

/**
 * NCMBPush is used to retrieve and send the push notification.<br></br>
 * NCMBPush can not add any field.<br></br>
 * Information about the field names that can be set , refer to the following reference .<br></br>
 * @see [NIFCLOUD mobile backend API Reference
](https://mbaas.nifcloud.com/doc/current/rest/push/pushRegistration.html) */
class NCMBPush : NCMBObject {
    // region getter
    /**
     * Get delivery date
     *
     * @return Date delivery date
     *///mBaaSの日付型文字列に変換して設定
    /**
     * Set delivery date
     *
     * @param value deliveryTime
     */
    var deliveryTime: Date?
        get() {
            return try {
                if (mFields.isNull("deliveryTime")) {
                    null
                }else{
                    val format: DateFormat = getIso8601()
                    format.parse(mFields.getJSONObject("deliveryTime").getString("iso"))
                }
            } catch (error: JSONException) {
                throw NCMBException(error)
            } catch (error: ParseException) {
                throw NCMBException(error)
            }
        }
        set(value) {
            try {
                mFields.put("deliveryTime", value)
                mUpdateKeys.add("deliveryTime")
            } catch (error: JSONException) {
                throw NCMBException(error)
            }
        }

    /**
     * Get target device
     *
     * @return JSONArray target device
     */
    var target: JSONArray?
        get() {
            return try {
                if (mFields.isNull("target")) {
                    null
                } else mFields.getJSONArray("target")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                mFields.put("target", value)
                mUpdateKeys.add("target")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
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
                if (mFields.isNull("message")) {
                    null
                } else mFields.getString("message")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                mFields.put("message", value)
                mUpdateKeys.add("message")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }

    /**
     * Get user setting value
     *
     * @return JSONObject user setting value
     */
    /**
     * Set user setting value
     *
     * @param value user setting value
     */
    var userSettingValue: JSONObject?
        get() {
            return try {
                if (mFields.isNull("userSettingValue")) {
                    null
                } else mFields.getJSONObject("userSettingValue")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                mFields.put("userSettingValue", value)
                mUpdateKeys.add("userSettingValue")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }

    /**
     * Get delivery expiration date
     *
     * @return Date delivery expiration date
     */
    /**
     * Set delivery expiration date
     *
     * @param value delivery expiration date
     */
    var deliveryExpirationDate: Date?
        get() {
            return try {
                if (mFields.isNull("deliveryExpirationDate")) {
                    null
                }
                else {
                    val format: DateFormat = getIso8601()
                    format.parse(mFields.getJSONObject("deliveryExpirationDate").getString("iso"))
                }
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            } catch (error: ParseException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                //mBaaSの日付型に変換して設定
                mFields.put("deliveryExpirationDate", createIsoDate(value))
                mUpdateKeys.add("deliveryExpirationDate")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }

    /**
     * Get delivery expiration time
     *
     * @return Date delivery expiration time
     */
    /**
     * Set delivery expiration time
     *
     * @param value delivery expiration date
     */
    var deliveryExpirationTime: String?
        get() {
            return try {
                if (mFields.isNull("deliveryExpirationTime")) {
                    null
                } else mFields.getString("deliveryExpirationTime")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                mFields.put("deliveryExpirationTime", value)
                mUpdateKeys.add("deliveryExpirationTime")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }

    /**
     * Get delivery plan number
     *
     * @return int delivery plan number
     */
    var deliveryPlanNumber: Int = 0
        get() {
            return try {
                if (mFields.isNull("deliveryPlanNumber")) {
                    0
                } else mFields.getInt("deliveryPlanNumber")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }

    /**
     * Get delivery number
     *
     * @return int delivery number
     */
    var deliveryNumber: Int = 0
        get(){
            return try {
                if (mFields.isNull("deliveryNumber")) {
                    0
                } else mFields.getInt("deliveryNumber")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }

    /**
     * Get delivery status
     *
     * @return int delivery status
     */
    var status: Int = 0
        get() {
            return try {
                if (mFields.isNull("status")) {
                    0
                } else mFields.getInt("status")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }

    /**
     * Get delivery error
     *
     * @return JSONObject delivery error
     */
    var error: JSONObject? = null
        get(){
            return try {
                if (mFields.isNull("error")) {
                    null
                } else mFields.getJSONObject("error")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }

    /**
     * Get action
     *
     * @return String action
     */
    /**
     * Set action
     *
     * @param value action
     */
    var action: String?
        get() {
            return try {
                if (mFields.isNull("action")) {
                    null
                } else mFields.getString("action")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                mFields.put("action", value)
                mUpdateKeys.add("action")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
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
    var badgeIncrementFlag: Boolean?
        get() {
            return try {
                if (mFields.isNull("badgeIncrementFlag")) {
                    null
                } else mFields.getBoolean("badgeIncrementFlag")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                mFields.put("badgeIncrementFlag", value)
                mUpdateKeys.add("badgeIncrementFlag")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }

    /**
     * Get sound
     *
     * @return String sound
     */
    /**
     * Set sound
     *
     * @param value sound
     */
    var sound: String?
        get(){
            return try {
                if (mFields.isNull("sound")) {
                    null
                } else mFields.getString("sound")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                mFields.put("sound", value)
                mUpdateKeys.add("sound")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }

    /**
     * Get content available flag
     *
     * @return Boolean content available flag
     */
    var contentAvailable: Boolean?
        get(){
            return try {
                if (mFields.isNull("contentAvailable")) {
                    null
                } else mFields.getBoolean("contentAvailable")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                mFields.put("contentAvailable", value)
                mUpdateKeys.add("contentAvailable")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
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
                if (mFields.isNull("title")) {
                    null
                } else mFields.getString("title")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
       set(value) {
            try {
                mFields.put("title", value)
                mUpdateKeys.add("title")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }

    /**
     * Get dialog flag
     *
     * @return Boolean dialog flag
     */
    /**
     * Set dialog flag
     *
     * @param value dialog flag
     */
    var dialog: Boolean?
        get(){
            return try {
                if (mFields.isNull("dialog")) {
                    null
                } else mFields.getBoolean("dialog")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                mFields.put("dialog", value)
                mUpdateKeys.add("dialog")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
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
        get(){
            return try {
                if (mFields.isNull("richUrl")) {
                    null
                } else mFields.getString("richUrl")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                mFields.put("richUrl", value)
                mUpdateKeys.add("richUrl")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }

    /**
     * Get badge setting count
     *
     * @return Integer badge setting count
     */
    /**
     * Set badge setting count
     *
     * @param value badge setting count
     */
    var badgeSetting: Int
        get(){
            return try {
                if (mFields.isNull("badgeSetting")) {
                    0
                } else mFields.getInt("badgeSetting")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                mFields.put("badgeSetting", value)
                mUpdateKeys.add("badgeSetting")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }

    /**
     * Get category
     *
     * @return String category
     */
    /**
     * Set category
     *
     * @param value category
     */
    var category: String?
        get(){
            return try {
                if (mFields.isNull("category")) {
                    null
                } else mFields.getString("category")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
            }
        }
        set(value) {
            try {
                mFields.put("category", value)
                mUpdateKeys.add("category")
            } catch (error: JSONException) {
                throw IllegalArgumentException(error.message)
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
    fun send() {
        //connect
        val pushService = NCMBPushService()
        val responseData: JSONObject
        if (getObjectId() == null) {
            //new create
            responseData = pushService.sendPush(mFields)
        }
        else {
            //update
            val updateJson = try {
                createUpdateJsonData()
            } catch (e: JSONException) {
                throw IllegalArgumentException(e.message)
            }
            responseData = pushService.updatePush(getObjectId(), updateJson)
        }
        setPushLocalData(responseData)
        mUpdateKeys.clear()
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
     * create a date of mBaaS correspondence
     *
     * @param value iso value
     * @return JSONObject
     * @throws JSONException
     */
    @Throws(JSONException::class)
    fun createIsoDate(value: Date?): JSONObject {
        val dateJson = JSONObject()
        dateJson.put("iso", getIso8601().format(value))
        dateJson.put("__type", "Date")
        return dateJson
    }

    companion object {
        private const val MATCH_URL_REGEX =
            "^(https?)(:\\/\\/[-_.!~*\\'()a-zA-Z0-9;\\/?:\\@&=+\\$,%#]+)$"
        val ignoreKeys = Arrays.asList(
            "objectId", "deliveryTime", "target",
            "searchCondition", "message", "userSettingValue",
            "deliveryExpirationDate", "deliveryExpirationTime", "deliveryPlanNumber",
            "deliveryNumber", "status", "error",
            "action", "badgeIncrementFlag", "sound",
            "contentAvailable", "title", "dialog",
            "richUrl", "badgeSetting", "category",
            "acl", "createDate", "updateDate"
        )
    }
}