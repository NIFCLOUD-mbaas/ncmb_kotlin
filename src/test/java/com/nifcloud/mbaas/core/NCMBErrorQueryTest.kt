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
import kotlin.test.assertFails


/**
 * 主に通信を行う自動化テストクラス
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [27], manifest = Config.NONE)
class NCMBErrorQueryTest {

    private var mServer: MockWebServer = MockWebServer()
    //Todo background method
    //private var callbackFlag = false

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()
    @Before
    fun setup() {
        var ncmbDispatcher = NCMBErrorDispatcher()
        mServer.dispatcher = ncmbDispatcher
        mServer.start()
        NCMB.initialize(
            ApplicationProvider.getApplicationContext(),
            "appKey",
            "cliKey",
            mServer.url("/").toString(),
            "2013-09-01"
        )
        //Todo background method

    }

    @Test
    @Throws(Exception::class)
    fun testNCMBObject_DoSearchSync_503error() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass503")
        val throwable = assertFails{ val objects = query.find() }
        Assert.assertEquals("Service unavailable.",throwable.message)
    }

    @Test
    @Throws(Exception::class)
    fun testNCMBObject_DoSearchSync_500error() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass500")
        val throwable = assertFails{ query.find() }
        Assert.assertEquals("System error.",throwable.message)
    }

    @Test
    @Throws(Exception::class)
    fun testNCMBObject_DoSearchSync_429error() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass429")
        val throwable = assertFails{ query.find() }
        Assert.assertEquals("Too many requests.",throwable.message)
    }

    @Test
    @Throws(Exception::class)
    fun testNCMBObject_DoCountSync_503error() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass503")
        val throwable = assertFails{ query.count() }
        Assert.assertEquals("Service unavailable.",throwable.message)
    }

    @Test
    @Throws(Exception::class)
    fun testNCMBObject_DoCountSync_500error() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass500")
        val throwable = assertFails{ query.count() }
        Assert.assertEquals("System error.",throwable.message)
    }

    @Test
    @Throws(Exception::class)
    fun testNCMBObject_DoCountSync_429error() {
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass429")
        val throwable = assertFails{ query.count() }
        Assert.assertEquals("Too many requests.",throwable.message)
    }

    @Test
    fun testNCMBObject_DoCountInBackground_Equal_Fail503() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass503")
        val callback = NCMBCallback { e, number ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["number"] = number
            inBackgroundHelper.release() // ブロックをリリース
        }
        inBackgroundHelper.start()
        query.countInBackground(callback)
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertEquals(inBackgroundHelper["number"] as Int, 0)
        Assert.assertNotNull(inBackgroundHelper["e"])
        Assert.assertEquals(
            (inBackgroundHelper["e"] as NCMBException).message ,
            "Service unavailable."
        )
    }

    @Test
    fun testNCMBObject_DoCountInBackground_Equal_Fail500() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass500")
        val callback = NCMBCallback { e, number ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["number"] = number
            inBackgroundHelper.release() // ブロックをリリース
        }
        inBackgroundHelper.start()
        query.countInBackground(callback)
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertEquals(inBackgroundHelper["number"] as Int, 0)
        Assert.assertNotNull(inBackgroundHelper["e"])
        Assert.assertEquals(
            (inBackgroundHelper["e"] as NCMBException).message ,
            "System error."
        )
    }

    @Test
    fun testNCMBObject_DoCountInBackground_Equal_Fail429() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery.forObject("TestClass429")
        val callback = NCMBCallback { e, number ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["number"] = number
            inBackgroundHelper.release() // ブロックをリリース
        }
        inBackgroundHelper.start()
        query.countInBackground(callback)
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertEquals(inBackgroundHelper["number"] as Int, 0)
        Assert.assertNotNull(inBackgroundHelper["e"])
        Assert.assertEquals(
            (inBackgroundHelper["e"] as NCMBException).message ,
            "Too many requests."
        )
    }

}
