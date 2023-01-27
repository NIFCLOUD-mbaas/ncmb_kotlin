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

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.collections.HashSet

/**
 *
 * Base class for NCMBObject.
 *
 * This class do necessary tasks such as put/set interface methods even internal tasks,
 * which will be base for NCMBObject.
 *
 */

open class NCMBBase(){

    /**
     * ACL
     */
    val ACCESS_CONTROL_LIST = "acl"
    internal var mFields = JSONObject()
    internal var localData = JSONObject()
    internal var mUpdateKeys = HashSet<String>()
        @Throws(NCMBException::class) get() {
            return field
        }
        internal set
    protected open var mIgnoreKeys= listOf<String>()
    var keys = HashSet<String>()

    @Throws(NCMBException::class)
    fun setObjectId(objectId: String) {
        try {
            mFields.put("objectId", objectId)
            keys.add("objectId")
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_FORMAT, e.localizedMessage)
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
    open fun setCreateDate(createDate: Date) {
        try {
            val df: SimpleDateFormat = NCMBDateFormat.getIso8601()
            mFields.put("createDate", df.format(createDate))
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_FORMAT, e.localizedMessage)
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
            throw NCMBException(NCMBException.INVALID_FORMAT, e.localizedMessage)
        } catch (e: ParseException) {
            throw NCMBException(NCMBException.INVALID_FORMAT, e.localizedMessage)
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
            throw NCMBException(NCMBException.INVALID_FORMAT, e.localizedMessage)
        } catch (e: ParseException) {
            throw NCMBException(NCMBException.INVALID_FORMAT, e.localizedMessage)
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

    fun getInt(key: String, value: Int? = null): Int?{
        return try{
            mFields.get(key) as Int
        } catch (e: JSONException){
            return value
        }
    }

    fun getDouble(key: String, value: Double? = null): Double?{
        return try{
            mFields.get(key) as Double
        } catch (e: JSONException){
            return value
        }
    }

    fun getBoolean(key: String, value: Boolean? = null): Boolean?{
        return try{
            mFields.get(key) as Boolean
        } catch (e: JSONException){
            return value
        }
    }

    fun getJson(key: String, value: JSONObject? = null): JSONObject?{
        return try{
            mFields.get(key) as JSONObject
        } catch (e: JSONException){
            return value
        }
    }

    fun getArray(key: String, value: JSONArray? = null): JSONArray?{
        return try{
            mFields.get(key) as JSONArray
        } catch (e: JSONException){
            return value
        }
    }

    /**
     * get NCMBGeoPoint value from given key
     *
     * @param key field name to get the value
     * @return value of specified key or null
     */
    @Throws(NCMBException::class)
    fun getGeo(key : String): NCMBGeoPoint {
        try {
            val geoPoint = mFields.getJSONObject(key)
            if (geoPoint.getString("__type") == "GeoPoint") {
                val lat = geoPoint.getDouble("latitude")
                val lon = geoPoint.getDouble("longitude")
                val geo = NCMBGeoPoint(lat, lon)
                return geo
            }else{
                throw NCMBException(NCMBException.INVALID_TYPE, "type is not GeoPoint.")
            }
        } catch (e: JSONException) {
            throw NCMBException(
                NCMBException.INVALID_TYPE, e.localizedMessage
            )
        }
    }

    /**
     * Update object from Response Data
     *
     * @param response Response Data
     */
    @Throws(JSONException::class)
    internal fun reflectResponse(responseData: JSONObject) {
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
            } else if (isIgnoreKey(key)) {
                continue
            }
            else {
                json.put(key, mFields[key])
            }
        }
        return json
    }

    @Throws(JSONException::class)
    protected fun createRegisterJsonData(): JSONObject {
        val json = JSONObject()
        for (key in mFields.keys()) {
            if (isIgnoreKey(key)) {
                continue
            }
            else {
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
            if (value is NCMBGeoPoint) {
                val locationJson = JSONObject("{'__type':'GeoPoint'}")
                locationJson.put("longitude", value.mlongitude)
                locationJson.put("latitude", value.mlatitude)
                mFields.put(key, locationJson)
            } else if (value is Date) {
                val dateJson = JSONObject()
                dateJson.put("iso", NCMBDateFormat.getIso8601().format(value))
                dateJson.put("__type", "Date")
                mFields.put(key, dateJson)
            }
            else {
                mFields.put(key, value)
            }
            mUpdateKeys.add(key)
            keys.add(key)
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_FORMAT, e.localizedMessage)
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
            throw NCMBException(NCMBException.INVALID_FORMAT, e.localizedMessage)
        }
    }

    @Throws(NCMBException::class)
    fun getDate(key : String, value : Date? = null): Date? {
        try {
            if (!mFields.isNull(key)) {
                val df: SimpleDateFormat = NCMBDateFormat.getIso8601()
                val dateJson = mFields.getJSONObject(key)
                if (dateJson.getString("__type") == "Date" && !dateJson.isNull("iso")) {
                    return df.parse(dateJson.getString("iso"))
                }
            }
            return null
        } catch (e: JSONException) {
            return value
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
    internal fun isIgnoreKey(key: String?): Boolean {
        if (this.mIgnoreKeys.size > 0) {  //mIgnoreKeys設定がある場合
            return mIgnoreKeys.contains(key)
        } else {
            return false
        }
    }

    /**
     * Copy from another JSON
     *
     * @param from JSON that copy from
     */
    @Throws(JSONException::class)
    internal open fun copyFrom(from: JSONObject) {
        for(key in from.keys()){
            this.keys.add(key)
            mFields.put(key, from[key])
            localData.put(key, from[key])
        }
    }

    @Throws(NCMBException::class)
    internal fun setAclFromInternal(acl: NCMBAcl?) {
        try {
            if (acl == null) {
                mFields.put(ACCESS_CONTROL_LIST, null)
            } else {
                mFields.put(ACCESS_CONTROL_LIST, acl.toJson())
            }
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_FORMAT, e.localizedMessage)
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
            throw NCMBException(NCMBException.INVALID_FORMAT, e.localizedMessage)
        }
    }
}
