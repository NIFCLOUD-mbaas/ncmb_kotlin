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

import com.nifcloud.mbaas.core.NCMBDateFormat.getIso8601
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.IllegalArgumentException
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
                //mBaaS??????????????????????????????????????????
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
                //mBaaS?????????????????????????????????
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
     * For Example, ???3 hour???,???5 day???
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
                //??????+day or hour??????????????????Exception
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
            //???????????????
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

        const val TARGET = "target"
        const val MESSAGE = "message"
        const val TITLE = "title"
        const val IMMEDIATE_DELIVERY_FLAG = "immediateDeliveryFlag"
    }
}