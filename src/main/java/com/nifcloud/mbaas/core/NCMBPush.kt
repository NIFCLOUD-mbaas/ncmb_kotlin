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

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.IllegalArgumentException
import java.util.*

/**
 * NCMBPush is used to retrieve and send the push notification.<br></br>
 * NCMBPush can not add any field.<br></br>
 * Information about the field names that can be set , refer to the following reference .<br></br>
 * @see [NIFCLOUD mobile backend API Reference
](https://mbaas.nifcloud.com/doc/current/rest/push/pushRegistration.html) */
class NCMBPush : NCMBObject {
    var target = arrayListOf<String>()
    var isSendToIOS :Boolean = false
        get(){
            return field
        }
        set(value) {
            field = value
            if(value) {
                target.add("ios")
            }
        }
    var isSendToAndroid :Boolean = false
        get(){
            return field
        }
        set(value) {
            field = value
            if(value) {
                target.add("android")
            }
        }

//    /**
//     * Get target device
//     *
//     * @return JSONArray target device
//     */
//    var target: ArrayList<String>?
//        get() {
//            return try {
//                if (mFields.isNull(TARGET)) {
//                    null
//                } else mFields.get(TARGET) as ArrayList<String>
//            } catch (error: JSONException) {
//                throw NCMBException(IllegalArgumentException(error.message))
//            }
//        }
//        internal set(value) {
//            try {
//                mFields.put(TARGET, value)
//                mUpdateKeys.add(TARGET)
//            } catch (error: JSONException) {
//                throw NCMBException(IllegalArgumentException(error.message))
//            }
//        }

//    /**
//     * Get badge increment flag
//     *
//     * @return Boolean badge increment flag
//     */
//    /**
//     * Set badge increment flag
//     *
//     * @param value badge increment flag
//     */
//    var isSendToAndroid: Boolean? = null
//        get(){
//            return try {
//                if (mFields.isNull(IMMEDIATE_DELIVERY_FLAG)) {
//                    null
//                } else mFields.getBoolean(IMMEDIATE_DELIVERY_FLAG)
//            } catch (error: JSONException) {
//                throw NCMBException(IllegalArgumentException(error.message))
//            }
//        }
//        set(value) {
//            try {
//                mFields.put(IMMEDIATE_DELIVERY_FLAG, value)
//                mUpdateKeys.add(IMMEDIATE_DELIVERY_FLAG)
//            } catch (error: JSONException) {
//                throw NCMBException(IllegalArgumentException(error.message))
//            }
//        }

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
