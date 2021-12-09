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
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.lang.Exception
import kotlin.test.assertFails

//Android環境のベースにテスト実装するため
@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27), manifest = Config.NONE)
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
            RuntimeEnvironment.application.getApplicationContext(),
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
            ((inBackgroundHelper["objects"] as List<Any>)[0] as NCMBObject).getObjectId(),
            "8FgKqFlH8dZRDrBJ"
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
            (inBackgroundHelper["objects"] as List<Any>).size
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
            ((inBackgroundHelper["objects"] as List<Any>)[0] as NCMBObject).getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            ((inBackgroundHelper["objects"] as List<Any>)[1] as NCMBObject).getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
    }

    @Test
    fun testNCMBObject_DoSearchSync_Equal_OneResult() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass")
        query.whereEqualTo("key", "value");
        val objects = query.find()
        Assert.assertEquals(
            objects[0].getObjectId(),
            "8FgKqFlH8dZRDrBJ"
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
        query.whereEqualTo("key", "value");
        val throwable = assertFails { query.skip = -1 }
        Assert.assertEquals("Need to set skip value > 0", throwable.message)
    }

    @Test
    fun testSkip_valid_value() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass")
        query.whereEqualTo("key", "value");
        query.skip = 20
        Assert.assertEquals(20, query.skip)
    }

    @Test
    @Throws(Exception::class)
    fun testLimit_Invalid_value_under0() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass")
        query.whereEqualTo("key", "value");
        val throwable = assertFails { query.limit = -100 }
        Assert.assertEquals("Need to set limit value from 1 to 1000", throwable.message)
    }

    @Test
    @Throws(Exception::class)
    fun testLimit_Invalid_value_over1000() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass")
        query.whereEqualTo("key", "value");
        val throwable = assertFails { query.limit = 1001 }
        Assert.assertEquals("Need to set limit value from 1 to 1000", throwable.message)
    }

    @Test
    fun testLimit_valid_value() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass")
        query.whereEqualTo("key", "value");
        query.limit = 200
        Assert.assertEquals(200, query.limit)
    }

    @Test
    fun testNCMBObject_DoSearchSync_Skip() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClassSkip")
        query.skip = 3
        query.whereEqualTo("key", "value");

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
        query.whereEqualTo("key", "value");
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
        query.whereEqualTo("key", "value");
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
        query.whereNotEqualTo("key", "value");
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
        query.whereGreaterThan("key", 2);
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
        query.whereGreaterThanOrEqualTo("key", 2);
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
        query.whereLessThan("key", 100);
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
            (objects[0] as NCMBObject).getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            (objects[1] as NCMBObject).getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
    }       
    
    @Test
    fun testNCMBObject_LessThanOrEqual_Success() {
        val query = NCMBQuery.forObject("TestClassLessThanOrEqual")
        query.whereLessThanOrEqualTo("key", 100);
        val objects = query.find()
        Assert.assertEquals(
            2,
            objects.size
        )
        Assert.assertEquals(
            (objects[0] as NCMBObject).getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            (objects[1] as NCMBObject).getObjectId(),
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
            (objects[0] as NCMBObject).getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            (objects[1] as NCMBObject).getObjectId(),
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
        query.whereLessThanOrEqualTo("key", 100);
        query.whereEqualTo("key", "value");
        val objects = query.find()
        Assert.assertEquals(
            1,
            objects.size
        )
        Assert.assertEquals(
            (objects[0] as NCMBObject).getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
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
            (users[0] as NCMBUser).getObjectId(),
            "dummyObjectId01"
        )
        Assert.assertEquals(
            (users[0] as NCMBUser).userName,
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
            (users[0] as NCMBUser).getObjectId(),
            "dummyObjectId01"
        )
        Assert.assertEquals(
            (users[1] as NCMBUser).getObjectId(),
            "dummyObjectId02"
        )
    }

    @Test
    fun test_NCMBUser_count_whereEqualTo_success() {
        val query = NCMBQuery.forUser()
        val number = query.count()
        Assert.assertEquals(
            50,
            number
        )
    }


}
