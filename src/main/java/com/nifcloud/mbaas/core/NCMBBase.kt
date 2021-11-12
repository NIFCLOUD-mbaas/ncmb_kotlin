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

import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.collections.HashSet

/**
 * A class of ncmb_kotlin.
 *
 * To do neccessary tasks for NCMBBase, which is base for NCMBObject
 *
 */

open class NCMBBase(){

    /**
     * ACL
     */
    val ACCESS_CONTROL_LIST = "acl"

    var mFields = JSONObject()
    var localData = JSONObject()
        @Throws(NCMBException::class) get() {
            return field
        }
        protected set
    protected var mUpdateKeys = HashSet<String>()
    protected var mIgnoreKeys: List<String>? = null
    protected var keys = HashSet<String>()

    @Throws(NCMBException::class)
    fun setObjectId(objectId: String) {
        try {
            mFields.put("objectId", objectId)
            keys.add("objectId")
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_FORMAT, e.message!!)
        }
    }

    fun getObjectId(): String? {
        return try {
            mFields.getString("objectId")
        } catch (e: JSONException) {
            null
        }
    }

    @Throws(NCMBException::class)
    open fun setCreateDate(createDate: Date?) {
        try {
            val df: SimpleDateFormat = NCMBDateFormat.getIso8601()
            mFields.put("createDate", df.format(createDate))
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_FORMAT, e.message!!)
        }
    }

    @Throws(NCMBException::class)
    fun getCreateDate(): Date? {
        return try {
            if (mFields.isNull("createDate")) {
                return null
            }
            val df: SimpleDateFormat = NCMBDateFormat.getIso8601()
            df.parse(mFields.getString("createDate"))
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_FORMAT, e.message!!)
        } catch (e: ParseException) {
            throw NCMBException(NCMBException.INVALID_FORMAT, e.message!!)
        }
    }

    @Throws(NCMBException::class)
    fun getUpdateDate(): Date? {
        return try {
            if (mFields.isNull("updateDate")) {
                return null
            }
            val df: SimpleDateFormat = NCMBDateFormat.getIso8601()
            df.parse(mFields.getString("updateDate"))
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_FORMAT, e.message!!)
        } catch (e: ParseException) {
            throw NCMBException(NCMBException.INVALID_FORMAT, e.message!!)
        }
    }

    /**
     * get string value from given key
     *
     * @param key field name to get the value
     * @return value of specified key or null
     */
    open fun getString(key: String): String? {
        return try {
            mFields.getString(key)
        } catch (e: JSONException) {
            null
        }
    }

    /**
     * Update object from Response Data
     *
     * @param response Response Data
     */
    @Throws(JSONException::class)
    fun reflectResponse(responseData: JSONObject) {
        for (key in responseData.keys()) {
            mFields.put(key, responseData[key])
            keys.add(key)
        }
    }

    @Throws(JSONException::class)
    protected fun createUpdateJsonData(): JSONObject {
        val json = JSONObject()
        for (key in mUpdateKeys) {
            if (mFields.isNull(key)) {
                json.put(key, JSONObject.NULL)
            } else {
                json.put(key, mFields[key])
            }
        }
        return json
    }

    /**
     * put any type value to given key
     *
     * @param key   field name for put the value
     * @param value value to put
     */
    @Throws(NCMBException::class)
    fun put(key: String, value: Any) {
        if (isIgnoreKey(key)) {
            throw NCMBException(
                NCMBException.INVALID_SETTING_NAME,
                "Can't put data to same name with special key."
            )
        }
        try {
            if(value is NCMBGeoPoint){
                val locationJson = JSONObject("{'__type':'GeoPoint'}")
                locationJson.put("longitude", value.mlongitude)
                locationJson.put("latitude", value.mlatitude)
                mFields.put(key, locationJson)
            }
            else{
                mFields.put(key, value)
            }
            mUpdateKeys.add(key)
            keys.add(key)
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_FORMAT, e.message!!)
        }
    }

    /**
     * get any type value to given key
     *
     * @param key   field name for put the value
     * @param value value to put
     */
    @Throws(NCMBException::class)
    fun get(key: String):Any? {
        if (!isContainsKey(key)) {
            throw NCMBException(
                NCMBException.INVALID_SETTING_NAME,
                "Can not get value from not contained key."
            )
        }
        return try {
            mFields.get(key)
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_FORMAT, e.message!!)
        }
    }


    /**
     * Check key is in keys
     *
     * @param key key name
     * @return ignore list contains given key or not
     */
    fun isContainsKey(key: String?): Boolean {
        if (this.keys == null) {
            return false
        } else {
            return keys.contains(key)
        }
    }

    /**
     * Check key is in ignore list
     *
     * @param key key name
     * @return ignore list contains given key or not
     */
    fun isIgnoreKey(key: String?): Boolean {
        if (this.mIgnoreKeys == null) {
            return false
        } else {
            return mIgnoreKeys!!.contains(key)
        }
    }

    /**
     * Copy from another JSON
     *
     * @param from JSON that copy from
     */
    @Throws(JSONException::class)
    open fun copyFrom(from: JSONObject) {
        for(key in from.keys()){
            if (isIgnoreKey(key)) {
                continue
            }
            mFields.put(key, from[key])
        }
    }

    @Throws(NCMBException::class)
    fun setAclFromInternal(acl: NCMBAcl?) {
        try {
            if (acl == null) {
                mFields.put(ACCESS_CONTROL_LIST, null)
            } else {
                mFields.put(ACCESS_CONTROL_LIST, acl.toJson())
            }
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_FORMAT, e.message!!)
        }
    }

    @Throws(NCMBException::class)
    fun setAcl(acl: NCMBAcl) {
        setAclFromInternal(acl)
        mUpdateKeys.add(ACCESS_CONTROL_LIST)
    }

    @Throws(NCMBException::class)
    fun getAcl(): NCMBAcl {
        return try {
            NCMBAcl(mFields.getJSONObject(ACCESS_CONTROL_LIST))
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_FORMAT, e.message!!)
        }
    }
}
