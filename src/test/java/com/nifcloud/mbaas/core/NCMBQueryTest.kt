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
    fun testNCMBObject_NotEqual_Success() {
        val query = NCMBQuery.forObject("TestClassNotEqual")
        query.whereNotEqualTo("key", "value");
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
    fun testNCMBObject_GreaterThan_Success() {
        val query = NCMBQuery.forObject("TestClassGreaterThan")
        query.whereGreaterThan("key", 2);
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
    fun testNCMBObject_GreaterThanOrEqual_Success() {
        val query = NCMBQuery.forObject("TestClassGreaterThanOrEqual")
        query.whereGreaterThanOrEqualTo("key", 2);
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
    fun testNCMBObject_LessThan_Success() {
        val query = NCMBQuery.forObject("TestClassLessThan")
        query.whereLessThan("key", 100);
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

}
