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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nifcloud.mbaas.core.NCMBDateFormat.getIso8601
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
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
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 主に通信を行う自動化テストクラス
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27), manifest = Config.NONE)
class NCMBPushTest {

    private var mServer: MockWebServer = MockWebServer()
    private var callbackFlag = false

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()
    @Before
    fun setup() {
        val ncmbDispatcher = NCMBDispatcher("push")
        mServer.dispatcher = ncmbDispatcher
        mServer.start()
        NCMB.initialize(
            RuntimeEnvironment.application.getApplicationContext(),
            "appKey",
            "cliKey",
            mServer.url("/").toString(),
            "2013-09-01"
        )

        callbackFlag = false;
    }

    /**
     * putテスト
     */
//    @Test
//    fun put_push_data_test() {
//        var pushObj = NCMBPush()
//        pushObj.title = "title_update"
//        pushObj.message = "message_update"
//        pushObj.immediateDeliveryFlag = true
//        Assert.assertEquals("title_update", pushObj.mFields.get("title"))
//        Assert.assertEquals("message_update", pushObj.mFields.get("message"))
//        Assert.assertEquals(true, pushObj.mFields.get("immediateDeliveryFlag"))
//    }
    /**
     * putテスト
     */
//    @Test
//    fun test_deliveryTime() {
//        val push = NCMBPush()
//        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        try {
//            val date = df.parse("2030-10-10 10:10:10")
//            push.deliveryTime = date
//            Assert.assertEquals(date, push.deliveryTime)
//            val isoDate =  JSONObject("{\"iso\":\"2030-10-10T01:10:10.000Z\", \"__type\":\"Date\"}")
//            Assert.assertEquals(isoDate.toString(), push.mFields.get("deliveryTime").toString())
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }
//    }
    /**
     * putテスト
     */
//    @Test
//    fun test_setDeliveryTimeString() {
//        val push = NCMBPush()
//        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        try {
//            val date = df.parse("2030-10-10 10:10:10")
//            push.setDeliveryTimeString("2030-10-10 10:10:10")
//            Assert.assertEquals(date, push.deliveryTime)
//            val isoDate =  JSONObject("{\"iso\":\"2030-10-10T01:10:10.000Z\", \"__type\":\"Date\"}")
//            Assert.assertEquals(isoDate.toString(), push.mFields.get("deliveryTime").toString())
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }
//    }
    /**
     * putテスト
     */
//    @Test
//    fun test_deliveryExpirationDate() {
//        val push = NCMBPush()
//        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        try {
//            val date = df.parse("2030-10-10 10:10:10")
//            push.deliveryExpirationDate = date
//            Assert.assertEquals(date, push.deliveryExpirationDate)
//            val isoDate =  JSONObject("{\"iso\":\"2030-10-10T01:10:10.000Z\", \"__type\":\"Date\"}")
//            Assert.assertEquals(isoDate.toString(), push.mFields.get("deliveryExpirationDate").toString())
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }
//    }
    /**
     * putテスト
     */
//    @Test
//    fun test_deliveryExpirationTime_day() {
//        val push = NCMBPush()
//        push.deliveryExpirationTime = "3 day"
//        Assert.assertEquals("3 day", push.deliveryExpirationTime)
//        Assert.assertEquals("3 day", push.mFields.get("deliveryExpirationTime"))
//    }

    /**
     * putテスト
     */
//    @Test
//    fun test_deliveryExpirationTime_hour() {
//        val push = NCMBPush()
//        push.deliveryExpirationTime = "3 hour"
//        Assert.assertEquals("3 hour", push.deliveryExpirationTime)
//        Assert.assertEquals("3 hour", push.mFields.get("deliveryExpirationTime"))
//    }

    /**
     * - 内容：send(POST)が成功することを確認する
     * - 結果：同期でプッシュの送信が出来る事
     */
//    @Test
//    @Throws(Exception::class)
//    fun send_post_target() {
//        //post
//        val push = NCMBPush()
//        push.title = "title_update"
//        push.message = "message_update"
//        push.immediateDeliveryFlag = true
//        push.isSendToIOS = true
//        push.save()
//        val TestJSON = JSONObject()
//        TestJSON.put("target",JSONArray(arrayListOf("ios")))
//        Assert.assertEquals(TestJSON.get("target"), push.mFields.get("target"))
//
//        val push2 = NCMBPush()
//        push2.title = "title_update"
//        push2.message = "message_update"
//        push2.immediateDeliveryFlag = true
//        push2.isSendToIOS = true
//        push2.isSendToAndroid = true
//        push2.save()
//        val TestJSON2 = JSONObject()
//        TestJSON2.put("target",JSONArray(arrayListOf("ios", "android")))
//        Assert.assertEquals(TestJSON2.get("target"), push2.mFields.get("target"))
//
//        val push3 = NCMBPush()
//        push3.title = "title_update"
//        push3.message = "message_update"
//        push3.immediateDeliveryFlag = true
//        push3.isSendToIOS = true
//        push3.isSendToAndroid = false
//        push3.save()
//        Assert.assertEquals(TestJSON.get("target"), push3.mFields.get("target"))
//    }

    /**
     * - 内容：send(POST)が成功することを確認する
     * - 結果：同期でプッシュの送信が出来る事
     */
    @Test
    @Throws(Exception::class)
    fun send_post() {
        //post
        var error: NCMBException? = null
        val push = NCMBPush()
        try {
            push.title = "title_update"
            push.message = "message_update"
            push.immediateDeliveryFlag = true
            push.isSendToAndroid = true
            push.save()
        } catch (e: NCMBException) {
            error = e
        }
        val TestJSON = JSONObject()
        TestJSON.put("target",JSONArray(arrayListOf("android")))
        Assert.assertEquals(TestJSON.get("target"), push.mFields.get("target"))
        //check
        Assert.assertNull(error)
        Assert.assertEquals("7FrmPTBKSNtVjajm", push.getObjectId())
        val format: DateFormat = getIso8601()
        Assert.assertEquals(format.parse("2014-06-03T11:28:30.348Z"), push.getCreateDate())
    }
    /**
     * - 内容：send(PUT)が成功することを確認する
     * - 結果：同期でプッシュの更新が出来る事
     */
//    @Test
//    @Throws(Exception::class)
//    fun send_put_sametime() {
//        var error: NCMBException? = null
//        val push = NCMBPush()
//        //put
//        try {
//            push.setObjectId("7FrmPTBKSNtVjajm")
//            push.title = "title_update"
//            push.message = "message_update"
//            push.immediateDeliveryFlag = true
//            push.isSendToAndroid = true
//            push.isSendToIOS = true
//            push.save()
//        } catch (e: NCMBException) {
//            error = e
//        }
//        val TestJSON = JSONObject()
//        TestJSON.put("target",JSONArray(arrayListOf("android", "ios")))
//        Assert.assertEquals(TestJSON.get("target"), push.mFields.get("target"))
//        //check
//        Assert.assertNull(error)
//        Assert.assertEquals("title_update", push.title)
//        Assert.assertEquals("message_update", push.message)
//        val format: DateFormat = getIso8601()
//        Assert.assertEquals(format.parse("2014-06-04T11:28:30.348Z"), push.getUpdateDate())
//    }

    /**
     * - 内容：send(PUT)が成功することを確認する
     * - 結果：同期でプッシュの更新が出来る事
     */
    @Test
    @Throws(Exception::class)
    fun send_put() {
        var error: NCMBException? = null
        val push = NCMBPush()
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            val date = df.parse("2030-10-10 10:10:10")
            push.setObjectId("7FrmPTBKSNtVjajm")
            push.title = "title_update"
            push.message = "message_update"
            push.immediateDeliveryFlag = true
            push.isSendToAndroid = true
            push.isSendToIOS = true
            push.save()
        } catch (e: ParseException) {
            e.printStackTrace()
        } catch (e: NCMBException) {
            error = e
        }
        val TestJSON = JSONObject()
        TestJSON.put("target",JSONArray(arrayListOf("android", "ios")))
        Assert.assertEquals(TestJSON.get("target"), push.mFields.get("target"))
        //check
        Assert.assertNull(error)
        Assert.assertEquals("title_update", push.title)
        Assert.assertEquals("message_update", push.message)
        val format: DateFormat = getIso8601()
        Assert.assertEquals(format.parse("2014-06-04T11:28:30.348Z"), push.getUpdateDate())
    }

    /**
     * - 内容：send(PUT)が成功することを確認する
     * - 結果：同期でプッシュの更新が出来る事
     */
//    @Test
//    @Throws(Exception::class)
//    fun send_post_deliveryExpirationTime() {
//        var error: NCMBException? = null
//        val push = NCMBPush()
//        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        try {
//            val date = df.parse("2030-10-10 10:10:10")
//            push.title = "title_update"
//            push.message = "message_update"
//            push.deliveryTime = date
//            push.deliveryExpirationTime = "3 day"
//            push.isSendToAndroid = true
//            push.isSendToIOS = true
//            push.save()
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        } catch (e: NCMBException) {
//            error = e
//        }
//        val TestJSON = JSONObject()
//        TestJSON.put("target",JSONArray(arrayListOf("android", "ios")))
//        Assert.assertEquals(TestJSON.get("target"), push.mFields.get("target"))
//        //check
//        Assert.assertNull(error)
//        Assert.assertEquals("title_update", push.title)
//        Assert.assertEquals("message_update", push.message)
//        Assert.assertEquals("3 day", push.deliveryExpirationTime)
//        val format: DateFormat = getIso8601()
//        Assert.assertEquals(format.parse("2014-06-04T11:28:30.348Z"), push.getUpdateDate())
//    }
    /**
     * - 内容：send(POST)が成功することを確認する
     * - 結果：deliveryExpirationDateを設定してPush登録
     */
//    @Test
//    @Throws(Exception::class)
//    fun send_post_setdeliveryTimeString() {
//        var error: NCMBException? = null
//        val push = NCMBPush()
//        //put
//        try {
//            push.title = "title_update"
//            push.message = "message_update"
//            push.setDeliveryTimeString("2030-10-10 10:10:10")
//            push.isSendToAndroid = true
//            push.isSendToIOS = true
//            push.save()
//        } catch (e: NCMBException) {
//            error = e
//        }
//        val TestJSON = JSONObject()
//        TestJSON.put("target",JSONArray(arrayListOf("android", "ios")))
//        Assert.assertEquals(TestJSON.get("target"), push.mFields.get("target"))
//        //check
//        Assert.assertNull(error)
//        Assert.assertEquals("title_update", push.title)
//        Assert.assertEquals("message_update", push.message)
//        val format: DateFormat = getIso8601()
//        Assert.assertEquals(format.parse("2030-10-10T01:10:10.000Z"), push.deliveryTime)
//    }

    /**
     * - 内容：send(POST)が成功することを確認する
     * - 結果：deliveryExpirationDateを設定してPush登録
     */
//    @Test
//    @Throws(Exception::class)
//    fun send_post_deliveryExpirationDate() {
//        var error: NCMBException? = null
//        val push = NCMBPush()
//        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        try {
//            val date = df.parse("2030-10-10 10:10:10")
//            push.title = "title_update"
//            push.message = "message_update"
//            push.immediateDeliveryFlag = true
//            push.deliveryExpirationDate = date
//            push.isSendToAndroid = true
//            push.isSendToIOS = true
//            push.save()
//            val TestJSON = JSONObject()
//            TestJSON.put("target",JSONArray(arrayListOf("android", "ios")))
//            Assert.assertEquals(TestJSON.get("target"), push.mFields.get("target"))
//            //check
//            Assert.assertNull(error)
//            Assert.assertEquals("title_update", push.title)
//            Assert.assertEquals("message_update", push.message)
//            Assert.assertEquals(date, push.deliveryExpirationDate)
//            val format: DateFormat = getIso8601()
//            Assert.assertEquals(format.parse("2014-06-04T11:28:30.348Z"), push.getUpdateDate())
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        } catch (e: NCMBException) {
//            error = e
//        }
//    }
}