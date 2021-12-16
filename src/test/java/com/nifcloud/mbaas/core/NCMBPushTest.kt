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
            push.send()
        } catch (e: NCMBException) {
            error = e
        }

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
    @Test
    @Throws(Exception::class)
    fun send_put() {
        var error: NCMBException? = null
        //post
        val push = NCMBPush()
        //put
        try {
            push.setObjectId("7FrmPTBKSNtVjajm")
            push.title = "title_update"
            push.message = "message_update"
            push.send()
        } catch (e: NCMBException) {
            error = e
        }

        //check
        Assert.assertNull(error)
        Assert.assertEquals("title_update", push.title)
        Assert.assertEquals("message_update", push.message)
        val format: DateFormat = getIso8601()
        Assert.assertEquals(format.parse("2014-06-04T11:28:30.348Z"), push.getUpdateDate())
    }
}