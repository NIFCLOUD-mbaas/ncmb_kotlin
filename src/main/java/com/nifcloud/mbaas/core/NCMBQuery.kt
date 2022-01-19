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
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import org.json.JSONArray

/**
 * Query handling class.
 *
 * NCMBQuery is used to set search conditions, to search data from NIFCLOUD mobile backend.
 */

//プライベートコンストラクターとしてcompanion object内にあるfor〇〇メソッドを用いて、インスタンスを取得する
class NCMBQuery<T : NCMBObject> private constructor(val mClassName: String, val service:NCMBServiceInterface<T>){
  
    private var mWhereConditions: JSONObject = JSONObject()
    private var mCountCondition: Int = 0
    private var order: List<String> = ArrayList()

    /**
     * Limit search operator (default value is 0 (valid limit value is 1 to 1000))
     */
    var limit: Int = 0
        set(value) {
            if (value < 1 || value >1000 ) {
                throw NCMBException(
                    NCMBException.GENERIC_ERROR,
                    "Need to set limit value from 1 to 1000"
                )
            }else{
                field = value
            }
        }

    /**
     * Skip search operator (default value is 0 (valid skip value is >0 ))
     */
    var skip: Int = 0
        set(value) {
            if (value < 0 ) {
                throw NCMBException(
                    NCMBException.GENERIC_ERROR,
                    "Need to set skip value > 0"
                )
            }else{
                field = value
            }
        }

    companion object {
        fun forObject(className: String): NCMBQuery<NCMBObject> {
            return NCMBQuery(className, NCMBObjectService())
        }

        fun forUser(): NCMBQuery<NCMBUser> {
            return NCMBQuery("user", NCMBUserService()) as NCMBQuery<NCMBUser>
        }

        fun forInstallation(): NCMBQuery<NCMBInstallation> {
            return NCMBQuery("installation", NCMBInstallationService()) as NCMBQuery<NCMBInstallation>
        }
    }

    /**
     * Search data from NIFCLOUD mobile backend
     * @return NCMBObject(include extend class) list of search result
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun find(): List<T> {
        return service.find(mClassName, query)
    }

    /**
     * Search data from NIFCLOUD mobile backend asynchronously
     * @param callback executed callback after data search
     */
    fun findInBackground(findCallback: NCMBCallback) {
        service.findInBackground(mClassName, query, findCallback)
    }

    /**
     * Get total number of search result from NIFCLOUD mobile backend
     * @return total number of search result
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun count(): Int {
        mCountCondition = 1
        return service.count(mClassName, query)
    }

    /**
     * Get total number of search result from NIFCLOUD mobile backend asynchronously
     * @param callback executed callback after data search
     */
    fun countInBackground(countCallback: NCMBCallback) {
        mCountCondition = 1
        service.countInBackground(mClassName, query, countCallback)
    }

    /**
     * Get current search condition
     * @return current search condition
     */
    val query: JSONObject
        get() {
            val query = JSONObject()
            if (mWhereConditions.length() > 0) {
                query.put(NCMBQueryConstants.REQUEST_PARAMETER_WHERE, mWhereConditions)
            }
            if (mCountCondition > 0) {
                query.put(NCMBQueryConstants.REQUEST_PARAMETER_COUNT,1)
            }
            if (limit > 0 ) {
                query.put(NCMBQueryConstants.REQUEST_PARAMETER_LIMIT, limit)
            }
            if (skip > 0 ) {
                query.put(NCMBQueryConstants.REQUEST_PARAMETER_SKIP, skip)
            }

            if (order.size > 0) {
                query.put(NCMBQueryConstants.REQUEST_PARAMETER_ORDER, order.joinToString(separator = "," ))
            }
            return  query
        }

    @Throws(JSONException::class)
    private fun convertConditionValue(value: Any): Any {
        return if (value is Date) {
            val dateJson = JSONObject("{'__type':'Date'}")
            val df: SimpleDateFormat = getIso8601()
            dateJson.put("iso", df.format(value as Date))
            dateJson
        }
        else if (value is NCMBGeoPoint) {
               value.convertToJson()
        }
        else {
            value
        }
    }

    /**
     * Set the conditions to search the data that matches the value of the specified key.
     * NOTICE that if this search condition is set, you can not set other search condition for this key.
     * OR if this search condition is set last, other set search condition for same key will be overwrite.
     * @param key field name to set the conditions
     * @param value condition value
     */
    fun whereEqualTo(key: String, value: Any) {
        try {
            mWhereConditions.put(key, convertConditionValue(value) )
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }

    /**
     * Set the conditions to search the data that does not match the value of the specified key
     * @param key field name to set the conditions
     * @param value condition value
     */
    fun whereNotEqualTo(key: String, value: Any) {
        try {
            mWhereConditions.put(key, addSearchCondition(key, "\$" + NCMBQueryConstants.QUERY_OPERATOR_NE , value))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }
    
    /**
     * Set the conditions to search the data by ascending order with specified field name (key)
     * @param key field name for order by ascending
     */
    fun addOrderByAscending(key: String) {
        if(key != "") {
            order += key
        }
    }

    /**
     * Set the conditions to search the data that greater than the value of the specified key
     * @param key field name to set the conditions
     * @param value condition value
     */
    fun whereGreaterThan(key: String, value: Any) {
        try {
            mWhereConditions.put(key,addSearchCondition(key, "\$" + NCMBQueryConstants.QUERY_OPERATOR_GT, value))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }

    /**
     * Set the conditions to search the data that less than the value of the specified key
     * @param key field name to set the conditions
     * @param value condition value
     */
    fun whereLessThan(key: String, value: Any) {
        try {
            mWhereConditions.put(key,addSearchCondition(key, "\$" + NCMBQueryConstants.QUERY_OPERATOR_LT, value))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }

    /**
     * Set the conditions to search the data that greater than or equal to the value of the specified key
     * @param key field name to set the conditions
     * @param value condition value
     */
    fun whereGreaterThanOrEqualTo(key: String, value: Any) {
        try {
            mWhereConditions.put(key,addSearchCondition(key, "\$" + NCMBQueryConstants.QUERY_OPERATOR_GTE, value))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }

    /**
     * Set the conditions to search the data that less than or equal to the value of the specified key
     * @param key field name to set the conditions
     * @param value condition value
     */
    fun whereLessThanOrEqualTo(key: String, value: Any) {
        try {
            mWhereConditions.put(key,addSearchCondition(key, "\$" + NCMBQueryConstants.QUERY_OPERATOR_LTE, value))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }

    /**
     * Set the conditions to search the data with location information
     * @param key field name that contains Geolocation data to search
     * @param southwest NCMBGeoPoint object  lower left location information for search area
     * @param northeast NCMBGeoPoint objectUpper right location information for search area
     */
    fun whereWithinGeoBox(key: String, southwest: NCMBGeoPoint, northeast: NCMBGeoPoint) {
        try {
            val boxArray = JSONArray()
            boxArray.put(convertConditionValue(southwest))
            boxArray.put(convertConditionValue(northeast))
            val boxJson = JSONObject()
            boxJson.put("\$" + NCMBQueryConstants.QUERY_OPERATOR_BOX, boxArray)
            mWhereConditions.put(key, addSearchCondition(key, "\$" + NCMBQueryConstants.QUERY_OPERATOR_WITHIN, boxJson))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }

    /**
     * Set the conditions to search the data with location information
     * @param key field name that contains location information
     * @param centerLocation center location for data searching
     * @param maxDistance search radius distance from center point in kilometers
     */
    fun whereWithinKilometers(key: String, centerLocation:NCMBGeoPoint, maxDistance: Double) {
        try {
            mWhereConditions.put(key, addSearchCondition(key, "\$" + NCMBQueryConstants.QUERY_OPERATOR_NEARSPHERE, centerLocation))
            mWhereConditions.put(key, addSearchCondition(key, "\$" + NCMBQueryConstants.QUERY_OPERATOR_KM, maxDistance))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }


    //Add new search condition (new 'operand' and 'value') for 'key', and return added search Condition for key
    internal fun addSearchCondition(key: String, operand: String, value: Any):JSONObject {
        var newCondition = JSONObject()
        if (mWhereConditions.has(key)) {
            val currentCondition = mWhereConditions[key]
            if (currentCondition is JSONObject) {
                newCondition = currentCondition
            }
            else {
                throw NCMBException(NCMBException.GENERIC_ERROR, "Cannot set other search condition for key which already set whereEqualTo search condition")
            }
        }
        newCondition.put(operand, convertConditionValue(value))
        return newCondition
    }
    
    
    /**
     * Set the conditions to search the data by descending order with specified field name (key)
     * @param key field name for order by ascending
     */
    fun addOrderByDescending(key: String) {
        if(key != "") {
            order += "-"+key
        }
    }

    /**
     * Set the conditions to search the data that contains value of the specified key
     * @param key field name to set the conditions
     * @param objects condition objects
     */
    fun whereContainedIn(key: String, objects: Collection<Any>) {
        try {
            mWhereConditions.put(key,addSearchConditionArray(key, "\$" + NCMBQueryConstants.QUERY_OPERATOR_IN, objects))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }

    /**
     * Set the conditions to search the data that contains elements of array in the specified key
     * @param key field name to set the conditions (search field must be Array type field)
     * @param elements condition elements in the specified key array
     */
    fun whereContainedInArray(key: String, objects: Collection<Any>) {
        try {
            mWhereConditions.put(key,addSearchConditionArray(key, "\$" + NCMBQueryConstants.QUERY_OPERATOR_INARRAY, objects))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }


    /**
     * Set the conditions to search the data that contains elements of array in the specified key
     * @param key field name to set the conditions (search field must be Array type field)
     * @param elements condition elements in the specified key array
     */
    fun whereNotContainedInArray(key: String, objects: Collection<Any>) {
        try {
            mWhereConditions.put(key,addSearchConditionArray(key, "\$" + NCMBQueryConstants.QUERY_OPERATOR_NINARRAY, objects))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }

    /**
     * Set the conditions to search the data that not contains value of the specified key
     * @param key field name to set the conditions
     * @param objects condition objects
     */
    fun whereNotContainedIn(key: String, objects: Collection<Any>) {
        try {
            mWhereConditions.put(key,addSearchConditionArray(key, "\$" + NCMBQueryConstants.QUERY_OPERATOR_NIN, objects))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }

    /**
     * Set the conditions to search the data that contains all elements of array in the specified key
     * @param Arraykey field name to set the conditions (search field must be Array type field)
     * @param elements condition elements in the specified key array
     */
    fun whereContainsAll(key: String, elements: Collection<Any>) {
        try {
            mWhereConditions.put(key,addSearchConditionArray(key, "\$" + NCMBQueryConstants.QUERY_OPERATOR_ALL, elements))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }


    /**
     * Constructor
     * @param className class name string for search data
     */
    init {
        mWhereConditions = JSONObject()
    }

    //Add new search condition (new 'operand' and 'value') for 'key', and return added search Condition for key
    internal fun addSearchConditionArray(key: String, operand: String, objects: Collection<Any>):JSONObject {
        var newCondition = JSONObject()
        if (mWhereConditions.has(key)) {
            val currentCondition = mWhereConditions[key]
            if (currentCondition is JSONObject) {
                        newCondition = currentCondition
            }
            else {
                throw NCMBException(NCMBException.GENERIC_ERROR, "Cannot set other search condition for key which already set whereEqualTo search condition")
            }
        }
        val array = JSONArray()
        for (value in objects) {
            array.put(convertConditionValue(value))
        }
        newCondition.put(operand, array)
        return newCondition
    }

}

