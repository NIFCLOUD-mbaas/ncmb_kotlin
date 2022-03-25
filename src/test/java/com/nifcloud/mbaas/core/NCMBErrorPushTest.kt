/*
 * Copyright 2017-2022 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * 主に通信を行う自動化テストクラス
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27), manifest = Config.NONE)
class NCMBErrorPushTest {

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
            RuntimeEnvironment.application.getApplicationContext(),
            "appKey",
            "cliKey",
            mServer.url("/").toString(),
            "2013-09-01"
        )
        //Todo background method

    }

    /**
     * - 内容：send(POST)が失敗することを確認する
     * - 結果：targetの指定がないとのエラーが出ること
     */
    @Test
    @Throws(Exception::class)
    fun send_post_no_target() {
        //post
        val push = NCMBPush()
        push.title = "title_update"
        push.message = "message_update"
        push.immediateDeliveryFlag = true
        try {
            push.save()
        }
        catch (e:NCMBException){
            Assert.assertEquals(NCMBException.INVALID_FORMAT, e.code)
            Assert.assertEquals("'target' do not set.", e.message)
        }
    }

    /**
     * - 内容：deliveryExpirationTimeに誤った値を設定
     * - 結果：deliveryExpirationTimeのset時にerrorが出ること
     */
    @Test
    @Throws(Exception::class)
    fun send_post_invalid_deliveryExpirationTime() {
        //post
        val push = NCMBPush()
        push.title = "title"
        push.message = "message"
        push.immediateDeliveryFlag = true
        push.isSendToAndroid = true
        try {
            push.deliveryExpirationTime = "3yay"
        }
        catch (e:NCMBException){
            Assert.assertEquals(NCMBException.INVALID_FORMAT, e.code)
            Assert.assertEquals("deliveryExpirationTime is invalid format. Please set string such as 'XX day' or 'YY hour'.", e.message)
        }
    }

    /**
     * - 内容：send(POST)が失敗することを確認する
     * - 結果：deliveryTimeとimmediateDeliveryFlagが両方設定されている場合、エラーが出ること
     */
    @Test
    @Throws(Exception::class)
    fun send_post_conflict_deliveryTime_and_immediateDeliveryFlag() {
        //post
        var date = Date()
        val push = NCMBPush()
        var df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        df.timeZone = TimeZone.getTimeZone("Etc/UTC")
        try {
            date = df.parse("2030-10-10 10:10:10")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        push.deliveryTime = date
        push.title = "correlation_error"
        push.message = "correlation_error"
        push.immediateDeliveryFlag = true
        push.isSendToAndroid = true
        push.isSendToIOS = true
        try {
            push.save()
        }
        catch (e:NCMBException){
            Assert.assertEquals(NCMBException.INVALID_CORRELATION, e.code)
            Assert.assertEquals("Either deliveryTime or immediateDeliveryFlag.", e.message)
        }
    }
}