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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nifcloud.mbaas.core.helper.NCMBInBackgroundTestHelper
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import androidx.test.core.app.ApplicationProvider
import org.robolectric.annotation.Config
import org.skyscreamer.jsonassert.JSONAssert
import java.lang.Exception
import java.util.*
import kotlin.test.assertFails

//Android環境のベースにテスト実装するため
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [27], manifest = Config.NONE)
class NCMBQueryTest {

    //mockserver preparation
    private var mServer: MockWebServer = MockWebServer()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setup() {

        //mockserver 制御するNCMBDispatcherを指定し、モックサーバを起動
        val ncmbDispatcher = NCMBDispatcher("query")
        mServer.dispatcher = ncmbDispatcher
        mServer.start()
        NCMB.initialize(
            ApplicationProvider.getApplicationContext(),
            "appKey",
            "cliKey",
            mServer.url("/").toString(),
            "2013-09-01"
        )
    }

    @Test
    fun testNCMBObject_DoSearchInBackground_Equal_OneResult() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass")
        query.whereEqualTo("key", "value");
        val callback = NCMBCallback { e, objects ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["objects"] = objects
            inBackgroundHelper.release() // ブロックをリリース
        }
        inBackgroundHelper.start()
        query.findInBackground(callback)
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals(
            (inBackgroundHelper["objects"] as List<NCMBObject>)[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            (inBackgroundHelper["objects"] as List<NCMBObject>)[0].getString("key"),
            "value"
        )
        Assert.assertEquals(
            (inBackgroundHelper["objects"] as List<NCMBObject>)[0].keys.size,
            5
        )
    }

    @Test
    fun testNCMBObject_DoSearchInBackground_NoResult() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        //TestClassNoDataクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClassNoData")
        val callback = NCMBCallback { e, objects ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["objects"] = objects
            inBackgroundHelper.release() // ブロックをリリース
        }
        inBackgroundHelper.start()
        query.findInBackground(callback)
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals(
            0,
            (inBackgroundHelper["objects"] as List<NCMBObject>).size
        )
    }

    @Test
    fun testNCMBObject_DoSearchInBackground_NoSearchCondition_TwoResults() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass")
        val callback = NCMBCallback { e, objects ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["objects"] = objects
            inBackgroundHelper.release() // ブロックをリリース
        }
        inBackgroundHelper.start()
        query.findInBackground(callback)
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals(
            (inBackgroundHelper["objects"] as List<NCMBObject>)[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            (inBackgroundHelper["objects"] as List<NCMBObject>)[1].getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
    }

    @Test
    fun testNCMBObject_DoSearchSync_Equal_OneResult() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass")
        query.whereEqualTo("key", "value")
        val objects = query.find()
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            objects[0].getString("key"),
            "value"
        )
        Assert.assertEquals(
            objects[0].keys.size,
            5
        )
    }

    @Test
    fun testNCMBObject_DoSearchSync_NoResult() {
        //TestClassNoDataクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClassNoData")
        val objects = query.find()
        Assert.assertEquals(
            0,
            objects.size
        )
    }

    @Test
    fun testNCMBObject_DoSearchSync_NoSearchCondition_TwoResults() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass")
        val objects = query.find()
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            objects[1].getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
    }

    @Test
    @Throws(Exception::class)
    fun testSkip_Invalid_value_under0() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass")
        query.whereEqualTo("key", "value")
        val throwable = assertFails { query.skip = -1 }
        Assert.assertEquals("Need to set skip value > 0", throwable.message)
    }

    @Test
    fun testSkip_valid_value() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass")
        query.whereEqualTo("key", "value")
        query.skip = 20
        Assert.assertEquals(20, query.skip)
    }

    @Test
    @Throws(Exception::class)
    fun testLimit_Invalid_value_under0() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass")
        query.whereEqualTo("key", "value")
        val throwable = assertFails { query.limit = -100 }
        Assert.assertEquals("Need to set limit value from 1 to 1000", throwable.message)
    }

    @Test
    @Throws(Exception::class)
    fun testLimit_Invalid_value_over1000() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass")
        query.whereEqualTo("key", "value")
        val throwable = assertFails { query.limit = 1001 }
        Assert.assertEquals("Need to set limit value from 1 to 1000", throwable.message)
    }

    @Test
    fun testLimit_valid_value() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass")
        query.whereEqualTo("key", "value")
        query.limit = 200
        Assert.assertEquals(200, query.limit)
    }

    @Test
    fun testNCMBObject_DoSearchSync_Skip() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClassSkip")
        query.skip = 3
        query.whereEqualTo("key", "value")

        val objects = query.find()
        Assert.assertEquals(2,objects.count())
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            objects[1].getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
    }

    @Test
    fun testNCMBObject_DoSearchSync_Limit() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClassLimit")
        query.limit = 3
        val objects = query.find()
        Assert.assertEquals(3,objects.count())
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            objects[1].getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
        Assert.assertEquals(
            objects[2].getObjectId(),
            "YpfmeOtRkZJeRQWZ"
        )
    }

    @Test
    fun testNCMBObject_DoCountInBackground_Equal_Success() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClassCount")
        query.whereEqualTo("key", "value")
        val callback = NCMBCallback { e, number ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["number"] = number
            inBackgroundHelper.release() // ブロックをリリース
        }
        inBackgroundHelper.start()
        query.countInBackground(callback)
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals(
            inBackgroundHelper["number"] ,
            50
        )
    }

    @Test
    fun testNCMBObject_DoSearchSync_Asc() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClassAsc")
        query.addOrderByAscending("key")
        val objects = query.find()
        Assert.assertEquals(3,objects.count())
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            objects[0].getString("key"),
            "value1"
        )
        Assert.assertEquals(
            objects[1].getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
        Assert.assertEquals(
            objects[1].getString("key"),
            "value2"
        )
        Assert.assertEquals(
            objects[2].getObjectId(),
            "YpfmeOtRkZJeRQWZ"
        )
        Assert.assertEquals(
            objects[2].getString("key"),
            "value3"
        )
    }

    @Test
    fun testNCMBObject_DoSearchSync_Desc() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClassDesc")
        query.addOrderByDescending("key")
        val objects = query.find()
        Assert.assertEquals(3,objects.count())
        Assert.assertEquals(
            objects[0].getObjectId(),
            "YpfmeOtRkZJeRQWZ"
        )
        Assert.assertEquals(
            objects[0].getString("key"),
            "value3"
        )
        Assert.assertEquals(
            objects[1].getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
        Assert.assertEquals(
            objects[1].getString("key"),
            "value2"
        )
        Assert.assertEquals(
            objects[2].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            objects[2].getString("key"),
            "value1"
        )
    }
    
    @Test    
    fun testNCMBObject_DoCount_Equal_Success() {
        val query = NCMBQuery.forObject("TestClassCount")
        query.whereEqualTo("key", "value")
        val number = query.count()
        Assert.assertEquals(
            50,
            number
        )
    }

    @Test
    fun testNCMBObject_WhereContainedIn_Success() {
        val query = NCMBQuery.forObject("TestClassContainedIn")
        val objs = setOf<Int>(1,2,3)
        query.whereContainedIn("keyArray", objs)
        val objects = query.find()
        Assert.assertEquals(
            2,
            objects.size
        )
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            objects[1].getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
    }
    
    @Test
    fun testNCMBObject_NotEqual_Success() {
        val query = NCMBQuery.forObject("TestClassNotEqual")
        query.whereNotEqualTo("key", "value")
        val objects = query.find()
        Assert.assertEquals(
            2,
            objects.size
        )
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            objects[1].getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
    }

    @Test
    fun testNCMBObject_WhereContainedInArray_Success() {
        val query = NCMBQuery.forObject("TestClassContainedInArray")
        val objs = setOf<Int>(1,2,3)
        query.whereContainedInArray("keyArray", objs)
        val objects = query.find()
        Assert.assertEquals(
            2,
            objects.size
        )
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            objects[1].getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
    }
    
    @Test
    fun testNCMBObject_GreaterThan_Success() {
        val query = NCMBQuery.forObject("TestClassGreaterThan")
        query.whereGreaterThan("key", 2)
        val objects = query.find()
        Assert.assertEquals(
            2,
            objects.size
        )
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            objects[1].getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
    }

    @Test
    fun testNCMBObject_WhereNotContainedInArray_Success() {
        val query = NCMBQuery.forObject("TestClassNotContainedInArray")
        val objs = setOf<Int>(1,2,3)
        query.whereNotContainedInArray("keyArray", objs)
        val objects = query.find()
        Assert.assertEquals(
            2,
            objects.size
        )
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            objects[1].getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
    }        
        
    @Test
    fun testNCMBObject_GreaterThanOrEqual_Success() {
        val query = NCMBQuery.forObject("TestClassGreaterThanOrEqual")
        query.whereGreaterThanOrEqualTo("key", 2)
        val objects = query.find()
        Assert.assertEquals(
            2,
            objects.size
        )
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            objects[1].getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
    }

    @Test    
    fun testNCMBObject_WhereNotContainedIn_Success() {
        val query = NCMBQuery.forObject("TestClassNotContainedIn")
        val objs = setOf<Int>(1,2,3)
        query.whereNotContainedIn("keyArray", objs)
        val objects = query.find()
        Assert.assertEquals(
            2,
            objects.size
        )
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            objects[1].getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
    }      
    
    @Test
    fun testNCMBObject_LessThan_Success() {
        val query = NCMBQuery.forObject("TestClassLessThan")
        query.whereLessThan("key", 100)
        val objects = query.find()
        Assert.assertEquals(
            2,
            objects.size
        )
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            objects[1].getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
    }

    @Test
    fun testNCMBObject_WhereContainedAll_Success() {
        val query = NCMBQuery.forObject("TestClassContainedAll")
        val objs = setOf<Int>(1,2,3)
        query.whereContainsAll("keyArray", objs)
        val objects = query.find()
        Assert.assertEquals(
            2,
            objects.size
        )
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            objects[1].getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
    }       
    
    @Test
    fun testNCMBObject_LessThanOrEqual_Success() {
        val query = NCMBQuery.forObject("TestClassLessThanOrEqual")
        query.whereLessThanOrEqualTo("key", 100)
        val objects = query.find()
        Assert.assertEquals(
            2,
            objects.size
        )
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            objects[1].getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
    }

    @Test
    fun testNCMBObject_WhereContainedIn_StringArray_Success() {
        val query = NCMBQuery.forObject("TestClassContainedIn_String")
        val objs = setOf<String>("1","2","3")
        query.whereContainedIn("keyArray", objs)
        val objects = query.find()
        Assert.assertEquals(
            2,
            objects.size
        )
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            objects[1].getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
    }

    @Test
    @Throws(Exception::class)
    fun testNCMBObject_EqualToAndLessThanOrEqual_Success() {
        val query = NCMBQuery.forObject("TestClassLessThanOrEqual")
        query.whereEqualTo("key", 10)
        val throwable = assertFails {query.whereLessThanOrEqualTo("key", 100) }
        Assert.assertEquals("Cannot set other search condition for key which already set whereEqualTo search condition", throwable.message)
    }

    @Test
    fun testNCMBObject_LessThanOrEqualAndEqual_Success() {
        val query = NCMBQuery.forObject("TestClass")
        query.whereLessThanOrEqualTo("key", 100)
        query.whereEqualTo("key", "value")
        val objects = query.find()
        Assert.assertEquals(
            1,
            objects.size
        )
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
    }

    @Test
    fun testNCMBObject_Or_Success() {
        val query1 = NCMBQuery.forObject("TestClass")
        query1.whereLessThan("keyInt", 100)
        val query2 = NCMBQuery.forObject("TestClass")
        query2.whereGreaterThan("keyInt", 0)
        val queries: List<NCMBQuery<NCMBObject>> = listOf(query1, query2)
        val query = NCMBQuery.forObject("TestClass")
        query.or(queries)
        val objects = query.find()
        Assert.assertEquals(
            1,
            objects.size
        )
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
    }
    
    @Test
    fun testNCMBObject_Or_1query_Success() {
        val query1 = NCMBQuery.forObject("TestClass")
        query1.whereLessThan("keyInt", 100)
        val queries: List<NCMBQuery<NCMBObject>> = listOf(query1)
        val query = NCMBQuery.forObject("TestClass")
        query.or(queries)
        val objects = query.find()
        Assert.assertEquals(
            2,
            objects.size
        )
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            objects[1].getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
    }
    
    @Test
    fun testNCMBObject_Or_3query_Success() {
        val query1 = NCMBQuery.forObject("TestClass")
        query1.whereLessThan("keyInt", 100)
        val query2 = NCMBQuery.forObject("TestClass")
        query2.whereGreaterThan("keyInt", 0)
        val queries: List<NCMBQuery<NCMBObject>> = listOf(query1, query2, query1)
        val query = NCMBQuery.forObject("TestClass")
        query.or(queries)
        val objects = query.find()
        Assert.assertEquals(
            0,
            objects.size
        )
    }

    @Test
    fun testNCMBObject_whereWithinGeoBox_Success() {
        val southwest = NCMBGeoPoint(10.0, 20.0)
        val northeast = NCMBGeoPoint(30.0, 40.0)
        val query = NCMBQuery.forObject("TestClass")
        query.whereWithinGeoBox("geo", southwest, northeast)
        val objects = query.find()
        Assert.assertEquals(
            1,
            objects.size
        )
        Assert.assertEquals(
            objects[0].getObjectId(),
            "ftVV0Zwj6ek0zKfM"
        )
        Assert.assertEquals(objects[0].getGeo("geo").mlatitude, 12.0, 0.0)
        Assert.assertEquals(objects[0].getGeo("geo").mlongitude, 34.0, 0.0)
    }

    @Test
    fun testNCMBObject_whereNearSphereKilometers_Success() {
        val location = NCMBGeoPoint(10.0, 10.1)
        val query = NCMBQuery.forObject("TestClass")
        query.whereNearSphereKilometers("geo", location, 12.0)
        val objects = query.find()
        Assert.assertEquals(
            1,
           objects.size
        )
        Assert.assertEquals(
            objects[0].getObjectId(),
            "ftVV0Zwj6ek0zKfM"
        )
        Assert.assertEquals(objects[0].getGeo("geo").mlatitude, 12.0, 0.0)
        Assert.assertEquals(objects[0].getGeo("geo").mlongitude, 34.0, 0.0)
    }

    @Test
    fun testNCMBObject_whereNearSphereRadians_Success() {
        val location = NCMBGeoPoint(10.0, 10.1)
        val query = NCMBQuery.forObject("TestClass")
        query.whereNearSphereRadians("geo", location, 12.0)
        val objects = query.find()
        Assert.assertEquals(
            1,
            objects.size
        )
        Assert.assertEquals(
            objects[0].getObjectId(),
            "ftVV0Zwj6ek0zKfM"
        )
        Assert.assertEquals(objects[0].getGeo("geo").mlatitude, 12.0, 0.0)
        Assert.assertEquals(objects[0].getGeo("geo").mlongitude, 34.0, 0.0)
    }

    @Test
    fun testNCMBObject_whereNearSphereMiles_Success() {
        val location = NCMBGeoPoint(10.0, 10.1)
        val query = NCMBQuery.forObject("TestClass")
        query.whereNearSphereMiles("geo", location, 12.0)
        val objects = query.find()
        Assert.assertEquals(
            1,
            objects.size
        )
        Assert.assertEquals(
            objects[0].getObjectId(),
            "ftVV0Zwj6ek0zKfM"
        )
        Assert.assertEquals(objects[0].getGeo("geo").mlatitude, 12.0, 0.0)
        Assert.assertEquals(objects[0].getGeo("geo").mlongitude, 34.0, 0.0)
    }

    @Test
    fun testNCMBObject_whereNearSphere_Success() {
        val location = NCMBGeoPoint(10.0, 10.1)
        val query = NCMBQuery.forObject("TestClass")
        query.whereNearSphere("geo", location)
        val objects = query.find()
        Assert.assertEquals(
            1,
            objects.size
        )
        Assert.assertEquals(
            objects[0].getObjectId(),
            "ftVV0Zwj6ek0zKfM"
        )
        Assert.assertEquals(objects[0].getGeo("geo").mlatitude, 12.0, 0.0)
        Assert.assertEquals(objects[0].getGeo("geo").mlongitude, 34.0, 0.0)
    }

    @Test
    fun test_NCMBUser_find_whereEqualTo_success() {
        val query = NCMBQuery.forUser()
        query.whereEqualTo("userName", "Ncmb Tarou")
        val users = query.find()
        Assert.assertEquals(
            1,
            users.size
        )
        Assert.assertEquals(
            users[0].getObjectId(),
            "dummyObjectId01"
        )
        Assert.assertEquals(
            users[0].userName,
            "Ncmb Tarou"
        )
    }

    @Test
    fun test_NCMBUser_findAllUser_success() {
        val query = NCMBQuery.forUser()
        val users = query.find()
        Assert.assertEquals(
            2,
            users.size
        )
        Assert.assertEquals(
            users[0].getObjectId(),
            "dummyObjectId01"
        )
        Assert.assertEquals(
            users[1].getObjectId(),
            "dummyObjectId02"
        )
    }

    @Test
    fun test_NCMBUser_success() {
        val query = NCMBQuery.forUser()
        val number = query.count()
        Assert.assertEquals(
            50,
            number
        )
    }

    @Test
    fun test_NCMBInstallation_find_whereEqualTo_success() {
        val query = NCMBQuery.forInstallation()
        query.whereEqualTo("sdkVersion", "3.0.4")
        val installations = query.find()
        Assert.assertEquals(
            2,
            installations.size
        )
        Assert.assertEquals(
            installations[0].getObjectId(),
            "dummyObjectId01"
        )
        Assert.assertEquals(
            installations[0].sdkVersion,
            "3.0.4"
        )
        Assert.assertEquals(
            installations[1].getObjectId(),
            "dummyObjectId02"
        )
        Assert.assertEquals(
            installations[1].sdkVersion,
            "3.0.4"
        )
    }

    @Test
    fun test_NCMBInstallation_findAll_success() {
        val query = NCMBQuery.forInstallation()
        val installations = query.find()
        Assert.assertEquals(
            2,
            installations.size
        )
        Assert.assertEquals(
            installations[0].getObjectId(),
            "dummyObjectId01"
        )
        Assert.assertEquals(
            installations[1].getObjectId(),
            "dummyObjectId02"
        )
    }

    @Test
    fun test_NCMBInstallation_count_success() {
        val query = NCMBQuery.forInstallation()
        val number = query.count()
        Assert.assertEquals(
            50,
            number
        )
    }

    @Test
    fun test_NCMBFile_find_whereEqualTo_success() {
        val query = NCMBQuery.forFile()
        query.whereEqualTo("fileName", "testFile")
        val acl = NCMBAcl()
        acl.publicWriteAccess = true
        acl.publicReadAccess = true
        val files = query.find()
        Assert.assertEquals(
            1,
            files.size
        )
        Assert.assertEquals(
            files[0].fileName,
            "testFile"
        )
        Assert.assertEquals(
            files[0].fileSize,
            65
        )
        Assert.assertEquals(
            files[0].mimeType,
            "text/plane"
        )
        JSONAssert.assertEquals(
            files[0].getAcl().toJson(),acl.toJson(), false
        )
        val createDateTest: Date = NCMBDateFormat.getIso8601().parse("2013-09-24T00:44:49.245Z")!!
        Assert.assertEquals(
            files[0].getCreateDate(),createDateTest
        )
        val updateDateTest: Date = NCMBDateFormat.getIso8601().parse("2013-09-24T00:44:49.245Z")!!
        Assert.assertEquals(
            files[0].getUpdateDate(),updateDateTest
        )
    }

    @Test
    fun test_NCMBFile_find_success() {
        val query = NCMBQuery.forFile()
        val acl = NCMBAcl()
        acl.publicWriteAccess = true
        acl.publicReadAccess = true
        val files = query.find()
        Assert.assertEquals(
            2,
            files.size
        )
        Assert.assertEquals(
            files[0].fileName,
            "testFile"
        )
        Assert.assertEquals(
            files[0].fileSize,
            65
        )
        Assert.assertEquals(
            files[1].fileName,
            "ttl_mb.png"
        )
        Assert.assertEquals(
            files[1].fileSize,
            6325
        )
    }

    @Test
    fun test_NCMBFile_count_success() {
       val query = NCMBQuery.forFile()
        val number = query.count()
        Assert.assertEquals(
            50,
            number
        )
    }

}
