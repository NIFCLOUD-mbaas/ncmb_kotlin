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

import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * 主に通信を行うNCMBConnectionテストクラス
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(26), manifest = Config.NONE)
class NCMBConnectionTest {

    private var mServer: MockWebServer = MockWebServer()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()
    @Before
    fun setup() {
        var ncmbDispatcher = NCMBDispatcher("")
        mServer.dispatcher = ncmbDispatcher
        mServer.start()
        NCMB.initialize(
            RuntimeEnvironment.application.getApplicationContext(),
            "appKey",
            "cliKey",
            mServer.url("/").toString(),
            "2013-09-01")
    }

    /**
     * - 内容：POST通信が成功することを確認する
     * - 結果：responseDataが返却されること
     */


    @Test
    fun ncmbConnectionPostObjectInBackgroundValidCase() {
        val url: String = mServer.url("/2013-09-01/classes/TestClass").toString()
        val json = JSONObject()
        json.put("key", "value")
        val tmpRequest = NCMBRequest(url,
            "POST",
            null,
            json,
            "application/json",
            JSONObject(),
            null,
            "appKey",
            "cliKey",
            "2020-03-13T01:27:35.569Z"
        )
        val tmpConnection = NCMBConnection(tmpRequest)

        val callback = NCMBCallback { e, res ->
            if (e != null) {
                //保存失敗
                print("Failed" + e.message)
                Assert.assertNotNull(e)
            } else {
                //保存成功
                print("Success")
                when(res) {
                    is NCMBResponse.Success -> {
                        print(res.data)
                    }
                }
                Assert.assertNull(e)
            }
        }
        val handler = NCMBHandler { callback, res ->
            //Handler Action is set here
            when(res) {
                is NCMBResponse.Failure -> {
                    callback!!.done(res.resException)
                }
            }
        }
        tmpConnection.sendRequestAsynchronously(callback,handler)
    }

}
