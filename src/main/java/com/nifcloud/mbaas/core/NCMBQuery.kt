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

    companion object {
        fun forObject(className: String): NCMBQuery<NCMBObject> {
            return NCMBQuery<NCMBObject>(className, NCMBObjectService())
        }
    }


    /**　TODO
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

    /**　TODO
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
     * set the conditions to search the data that matches the value of the specified key
     * @param key field name to set the conditions
     * @param value condition value
     */
    fun whereEqualTo(key: String, value: Any) {
        try {
            mWhereConditions.put(key, convertConditionValue(value) )
        } catch (e: JSONException) {
            throw IllegalArgumentException(e.message)
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

