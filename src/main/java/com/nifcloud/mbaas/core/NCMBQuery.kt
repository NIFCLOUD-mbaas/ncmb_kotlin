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
import kotlinx.serialization.json.JSON
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import org.json.JSONArray

/**
 * NCMBQuery is used to search data from NIFCLOUD mobile backend
 */

//プライベートコンストラクターとしてcompanion object内にあるfor〇〇メソッドを用いて、インスタンスを取得する
class NCMBQuery<T : NCMBObject> private constructor(val mClassName: String, val service:NCMBServiceInterface<T>){
  
    private var mWhereConditions: JSONObject = JSONObject()
    private var mCountCondition: Int = 0
    private var order: List<String> = ArrayList()
    
    var limit: Int = 0 // default value is 0 (valid limit value is 1 to 1000)
        set(value) {
            if (value < 1 || value >1000 ) {
                throw NCMBException(
                    NCMBException.GENERIC_ERROR,
                    "Need to set limit value from 1 to 1000"
                )
            }
        }
        
    var skip: Int = 0 // default value is 0 (valid skip value is >0 )
        set(value) {
            if (value < 0 ) {
                throw NCMBException(
                    NCMBException.GENERIC_ERROR,
                    "Need to set skip value > 0"
                )
            }
        }

    companion object {
        fun forObject(className: String): NCMBQuery<NCMBObject> {
            return NCMBQuery<NCMBObject>(className, NCMBObjectService())
        }
    }

    /**
     * search data from NIFCLOUD mobile backend
     * @return NCMBObject(include extend class) list of search result
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun find(): List<T> {
        return service.find(mClassName, query)
    }

    /**
     * search data from NIFCLOUD mobile backend asynchronously
     * @param callback executed callback after data search
     */
    fun findInBackground(findCallback: NCMBCallback) {
        service.findInBackground(mClassName, query, findCallback)
    }

    /**
     * get total number of search result from NIFCLOUD mobile backend
     * @return total number of search result
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun count(): Int {
        mCountCondition = 1
        return service.count(mClassName, query)
    }

    /**
     * get total number of search result from NIFCLOUD mobile backend asynchronously
     * @param callback executed callback after data search
     */
    fun countInBackground(countCallback: NCMBCallback) {
        mCountCondition = 1
        service.countInBackground(mClassName, query, countCallback)
    }

    /**
     * get current search condition
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
                query.put("order", order.joinToString(separator = "," ))
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
        } else {
            value
        }
    }

    /**
     * set the conditions to search the data that matches the value of the specified key.
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
     * set the conditions to search the data that does not match the value of the specified key
     * @param key field name to set the conditions
     * @param value condition value
     */
    fun whereNotEqualTo(key: String, value: Any) {
        try {
            mWhereConditions.put(key, addSearchCondition(key, "\$ne" , value))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }
    
    /**
     * set the conditions to search the data by ascending order with specified field name (key)
     * @param key field name for order by ascending
     */
    fun addOrderByAscending(key: String) {
        if(key != "") {
            order += key
        }
    }

    /**
     * set the conditions to search the data that greater than the value of the specified key
     * @param key field name to set the conditions
     * @param value condition value
     */
    fun whereGreaterThan(key: String, value: Any) {
        try {
            mWhereConditions.put(key,addSearchCondition(key, "\$gt", value))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }

    /**
     * set the conditions to search the data that less than the value of the specified key
     * @param key field name to set the conditions
     * @param value condition value
     */
    fun whereLessThan(key: String, value: Any) {
        try {
            mWhereConditions.put(key,addSearchCondition(key, "\$lt", value))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }

    /**
     * set the conditions to search the data that greater than or equal to the value of the specified key
     * @param key field name to set the conditions
     * @param value condition value
     */
    fun whereGreaterThanOrEqualTo(key: String, value: Any) {
        try {
            mWhereConditions.put(key,addSearchCondition(key, "\$gte", value))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }

    /**
     * set the conditions to search the data that less than or equal to the value of the specified key
     * @param key field name to set the conditions
     * @param value condition value
     */
    fun whereLessThanOrEqualTo(key: String, value: Any) {
        try {
            mWhereConditions.put(key,addSearchCondition(key, "\$lte", value))
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
     * set the conditions to search the data by descending order with specified field name (key)
     * @param key field name for order by ascending
     */
    fun addOrderByDescending(key: String) {
        if(key != "") {
            order += "-"+key
        }
    }

    /**
     * set the conditions to search the data that contains value of the specified key
     * @param key field name to set the conditions
     * @param objects condition objects
     */
    fun whereContainedIn(key: String, objects: Collection<Any>) {
        try {
            mWhereConditions.put(key,addSearchConditionArray(key, "\$in", objects))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }

    /**
     * set the conditions to search the data that contains elements of array in the specified key
     * @param key field name to set the conditions (search field must be Array type field)
     * @param elements condition elements in the specified key array
     */
    fun whereContainedInArray(key: String, objects: Collection<Any>) {
        try {
            mWhereConditions.put(key,addSearchConditionArray(key, "\$inArray", objects))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }


    /**
     * set the conditions to search the data that contains elements of array in the specified key
     * @param key field name to set the conditions (search field must be Array type field)
     * @param elements condition elements in the specified key array
     */
    fun whereNotContainedInArray(key: String, objects: Collection<Any>) {
        try {
            mWhereConditions.put(key,addSearchConditionArray(key, "\$ninArray", objects))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }

    /**
     * set the conditions to search the data that not contains value of the specified key
     * @param key field name to set the conditions
     * @param objects condition objects
     */
    fun whereNotContainedIn(key: String, objects: Collection<Any>) {
        try {
            mWhereConditions.put(key,addSearchConditionArray(key, "\$nin", objects))
        } catch (e: JSONException) {
            throw NCMBException(e)
        }
    }

    /**
     * set the conditions to search the data that contains all elements of array in the specified key
     * @param Arraykey field name to set the conditions (search field must be Array type field)
     * @param elements condition elements in the specified key array
     */
    fun whereContainsAll(key: String, elements: Collection<Any>) {
        try {
            mWhereConditions.put(key,addSearchConditionArray(key, "\$all", elements))
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

