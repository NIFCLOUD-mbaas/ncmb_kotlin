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

import okhttp3.mockwebserver.MockWebServer
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import java.util.Date


class NCMBBaseTest {
    private var mServer: MockWebServer = MockWebServer()

    @Before
    @Throws(Exception::class)
    fun setup() {
        mServer = MockWebServer()
        mServer.start()
    }

    /**
     * putテスト
     */
    @Test
    fun put_string_test() {
        val baseObj = NCMBBase()
        baseObj.put("keyString", "stringValue")
        Assert.assertEquals(baseObj.getString("keyString"), "stringValue")
    }

    @Test
    fun put_string_null_test() {
        val baseObj = NCMBBase()
        baseObj.put("keyString", "stringValue")
        Assert.assertNull(baseObj.getString("key"))
        Assert.assertEquals(baseObj.getString("key", "error"), "error")
    }

    /**
     * putテスト
     */
    @Test
    fun put_int_test() {
        val baseObj = NCMBBase()
        val setNumberValue: Int = 12
        baseObj.put("keyNumberInt", setNumberValue)
        Assert.assertEquals(baseObj.getInt("keyNumberInt"), setNumberValue)
    }

    @Test
    fun put_int_null_test() {
        val baseObj = NCMBBase()
        val setNumberValue: Int = 12
        baseObj.put("keyNumberInt", setNumberValue)
        Assert.assertNull(baseObj.getInt("key"))
        Assert.assertEquals(baseObj.getInt("key", 0), 0)
    }

    /**
     * putテスト
     */
    @Test
    fun put_int_test_direct() {
        val baseObj = NCMBBase()
        baseObj.put("keyNumberInt", 1234)
        Assert.assertEquals(baseObj.getInt("keyNumberInt"), 1234)
    }

    @Test
    fun put_int_null_test_direct(){
        val baseObj = NCMBBase()
        baseObj.put("keyNumberInt", 1234)
        Assert.assertNull(baseObj.getInt("key"))
        Assert.assertEquals(baseObj.getInt("key", 0), 0)
    }

    /**
     * putテスト
     */
    @Test
    fun put_double_test_direct() {
        val baseObj = NCMBBase()
        baseObj.put("keyNumberDouble", 1234.33)
        Assert.assertEquals(baseObj.getDouble("keyNumberDouble"), 1234.33)
    }

    @Test
    fun put_double_null_test_direct(){
        val baseObj = NCMBBase()
        baseObj.put("keyNumberDouble", 1234.33)
        Assert.assertNull(baseObj.getDouble("key"))
        Assert.assertEquals(baseObj.getDouble("key", 0.0), 0.0)
    }

    /**
     * putテスト
     */
    @Test
    fun put_boolean_test() {
        val baseObj = NCMBBase()
        val booleanValue: Boolean = true
        baseObj.put("keyBoolean", booleanValue)
        Assert.assertEquals(baseObj.getBoolean("keyBoolean"), true)
    }

    @Test
    fun put_boolean_null_test() {
        val baseObj = NCMBBase()
        val booleanValue: Boolean = true
        baseObj.put("keyBoolean", booleanValue)
        Assert.assertNull(baseObj.getBoolean("key"))
        Assert.assertEquals(baseObj.getBoolean("key", true), true)
    }

    /**
     * putテスト
     */
    @Test
    fun put_jsonObject_test() {
        val baseObj = NCMBBase()
        val jsonObjectValue = JSONObject()
        val jsonObjectAssert = JSONObject()
        jsonObjectValue.put("a","b")
        jsonObjectAssert.put("a","b")
        baseObj.put("keyJsonObject", jsonObjectValue)
        JSONAssert.assertEquals(baseObj.getJson("keyJsonObject"), jsonObjectAssert, true)
    }

    @Test
    fun put_jsonObject_null_test() {
        val baseObj = NCMBBase()
        val jsonObjectValue = JSONObject()
        val jsonObjectAssert = JSONObject()
        jsonObjectValue.put("a","b")
        jsonObjectAssert.put("a","b")
        baseObj.put("keyJsonObject", jsonObjectValue)
        Assert.assertNull(baseObj.getJson("key"))
        JSONAssert.assertEquals(baseObj.getJson("key", jsonObjectAssert), jsonObjectAssert, true)
    }

    /**
     * put Array テスト
     */
    @Test
    fun put_array_of_int_test() {
        val obj = NCMBBase()
        val testArray = JSONArray()
        testArray.put(1)
        testArray.put(2)

        val testArray2 = JSONArray()
        testArray2.put(1)
        testArray2.put(2)
        obj.put("keyArray", testArray)
        Assert.assertEquals(obj.getArray("keyArray"), testArray2)
        Assert.assertEquals(obj.mFields.get("keyArray"), testArray2)
    }

    @Test
    fun put_array_of_int_null_test() {
        val obj = NCMBBase()
        val testArray = JSONArray()
        testArray.put(1)
        testArray.put(2)

        val testArray2 = JSONArray()
        testArray2.put(1)
        testArray2.put(2)
        obj.put("keyArray", testArray)
        Assert.assertNull(obj.getArray("key"))
        Assert.assertEquals(obj.getArray("key", testArray2), testArray2)
    }

    @Test
    fun put_date_test_direct() {
        val baseObj = NCMBBase()

        val assertDate: Date = NCMBDateFormat.getIso8601().parse("2022-04-14T10:10:10.000Z") as Date
        baseObj.put("keyDate", assertDate)

        val expectedKeyDateJson = JSONObject("{\"__type\":\"Date\",\"iso\":\"2022-04-14T10:10:10.000Z\"}")
        Assert.assertEquals(assertDate,baseObj.getDate("keyDate"))
        JSONAssert.assertEquals(expectedKeyDateJson, baseObj.mFields.getJSONObject("keyDate"), false)
        Assert.assertEquals(1, baseObj.mUpdateKeys.size)
    }

    @Test
    fun put_date_null_test_direct() {
        val baseObj = NCMBBase()

        val assertDate: Date = NCMBDateFormat.getIso8601().parse("2022-04-14T10:10:10.000Z")
        val assertDate2: Date = NCMBDateFormat.getIso8601().parse("2023-01-27T10:10:10.000Z")
        baseObj.put("keyDate", assertDate)

        Assert.assertNull(baseObj.getDate("key"))
        Assert.assertEquals(baseObj.getDate("key", assertDate2), assertDate2);
    }

    /**
     * copyFromテスト
     */
    @Test
    fun copy_from_test() {
        val baseObj = NCMBBase()
        val json = JSONObject("{\"objectId\":\"xxxxx\",\"userName\":\"YamadaTarou\"}")
        val testKeys = hashSetOf("objectId", "userName")
        Assert.assertEquals("xxxxx", json.getString("objectId"))
        Assert.assertEquals("YamadaTarou", json.getString("userName"))
        baseObj.copyFrom(json)
        JSONAssert.assertEquals(json, baseObj.mFields,false)
        JSONAssert.assertEquals(json, baseObj.localData,false)
        Assert.assertEquals(testKeys, baseObj.keys)
    }


    /**
     * reflectResponse テスト
     */
    @Test
    fun reflectResponse_test() {
        val baseObj = NCMBBase()
        val responseJson = JSONObject("{\"objectId\":\"xxxxx\",\"userName\":\"YamadaTarou\"}")
        baseObj.reflectResponse(responseJson)
        Assert.assertEquals("xxxxx", baseObj.mFields.get("objectId"))
        Assert.assertEquals("YamadaTarou", baseObj.mFields.get("userName"))
    }

    /**
     * reflectResponse テスト
     */
    @Test
    fun reflectResponse_object_test() {
        val baseObj = NCMBObject("TestClass")
        val responseJson = JSONObject("{\"objectId\":\"xxxxx\",\"createDate\":\"2013-08-28T03:02:29.970Z\"}")
        baseObj.reflectResponse(responseJson)
        Assert.assertEquals("xxxxx", baseObj.mFields.get("objectId"))
        Assert.assertEquals("2013-08-28T03:02:29.970Z", baseObj.mFields.get("createDate"))
    }

    /**
     * reflectResponse テスト
     */
    @Test
    fun reflectResponse_user_test() {
        val baseObj = NCMBUser()
        val responseJson = JSONObject("{\"objectId\":\"xxxxx\",\"userName\":\"YamadaTarou\",\"createDate\":\"2013-08-28T03:02:29.970Z\",\"sessionToken\":\"xxxxxxxxxx\"}")
        baseObj.reflectResponse(responseJson)
        Assert.assertEquals("xxxxx", baseObj.mFields.get("objectId"))
        Assert.assertEquals("YamadaTarou", baseObj.mFields.get("userName"))
        Assert.assertEquals("xxxxxxxxxx", baseObj.mFields.get("sessionToken"))
    }

    /**
     * setAclテスト
     */
    @Test
    fun setAcl_test() {
        val baseObj = NCMBBase()
        val acl = NCMBAcl()
        acl.publicReadAccess = true
        baseObj.setAcl(acl)
        JSONAssert.assertEquals(baseObj.getAcl().toJson(), acl.toJson(), true)
    }
}
