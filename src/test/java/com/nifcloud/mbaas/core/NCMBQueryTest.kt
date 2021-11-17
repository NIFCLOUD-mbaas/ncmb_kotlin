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
        val ncmbDispatcher = NCMBDispatcher()
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
            (objects[0] as NCMBObject).getObjectId(),
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
    fun testSkip_Invalid_value_under0() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass")
        query.whereEqualTo("key", "value");
        val throwable = assertFails { query.skip = -1 }
        Assert.assertEquals("Need to set skip value > 0", throwable.message)
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
    fun testNCMBObject_DoSearchSync_Skip() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClassSkip")
        query.whereEqualTo("key", "value");
        query.skip = 10
        val objects = query.find()
        Assert.assertEquals(2,objects.count())
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
    fun testNCMBObject_DoSearchSync_Limit() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClassLimit")
        query.limit = 3
        val objects = query.find()
        Assert.assertEquals(3,objects.count())
        Assert.assertEquals(
            (objects[0] as NCMBObject).getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            (objects[1] as NCMBObject).getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
        Assert.assertEquals(
            (objects[2] as NCMBObject).getObjectId(),
            "YpfmeOtRkZJeRQWZ"
        )
    }

    @Test
    fun testNCMBObject_DoSearchSync_Asc() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClassAsc")
        query.limit = 3
        query.addOrderByAscending("key")
        val objects = query.find()
        Assert.assertEquals(3,objects.count())
        Assert.assertEquals(
            (objects[0] as NCMBObject).getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
        Assert.assertEquals(
            (objects[0] as NCMBObject).getString("key"),
            "value1"
        )
        Assert.assertEquals(
            (objects[1] as NCMBObject).getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
        Assert.assertEquals(
            (objects[1] as NCMBObject).getString("key"),
            "value2"
        )
        Assert.assertEquals(
            (objects[2] as NCMBObject).getObjectId(),
            "YpfmeOtRkZJeRQWZ"
        )
        Assert.assertEquals(
            (objects[2] as NCMBObject).getString("key"),
            "value3"
        )
    }

    @Test
    fun testNCMBObject_DoSearchSync_Desc() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClassDesc")
        query.limit = 3
        query.addOrderByDescending("key")
        val objects = query.find()
        Assert.assertEquals(3,objects.count())
        Assert.assertEquals(
            (objects[0] as NCMBObject).getObjectId(),
            "YpfmeOtRkZJeRQWZ"
        )
        Assert.assertEquals(
            (objects[1] as NCMBObject).getObjectId(),
            "eQRqoObEZmtrfgzH"
        )
        Assert.assertEquals(
            (objects[2] as NCMBObject).getObjectId(),
            "8FgKqFlH8dZRDrBJ"
        )
    }

//    @Test
//    fun testNCMBObjectDoSearchReal() {
//        var applicationKey =  "3c99589bee9dda8184febdf64cdcfe65f84faf3ec5a2b158e477cea807299b30"
//        var clientKey = "4f77045784c3d667ccf2557cb31e507a1488e37bf0f88ba042610271f4e3f981"
//        NCMB.initialize(RuntimeEnvironment.application.getApplicationContext(),applicationKey, clientKey)
//        //TestClassクラスを検索するクエリを作成
//        val query = NCMBQuery.forObject("TODO")
//        query.limit = 3
//        query.skip = 0
//        query.addOrderByDescending("todo")
//        query.addOrderByDescending("text")
//        //query.whereEqualTo("key", "value");
//        //objects: List<NCMBObject>
//        try {
//            val objects = query.find()
//            println("FIND SUCCESS")
//            for (obj: Any in objects) {
//                if(obj is NCMBObject) {
//                    println(obj.getObjectId() + "|" + obj.getString("todo"))
//                    //println(obj.getObjectId())
//                }
//            }
//        }catch(e: NCMBException) {
//            print("SEARCH SYNC ERROR, EXCEPTION:" + e.message)
//        }
//    }

}
