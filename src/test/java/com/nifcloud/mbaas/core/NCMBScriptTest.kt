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
import com.nifcloud.mbaas.core.helper.NCMBInBackgroundTestHelper
import kotlinx.serialization.json.JSON
import okhttp3.mockwebserver.MockWebServer
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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(26), manifest = Config.NONE)
class NCMBScriptTest {

    private var mServer: MockWebServer = MockWebServer()
    private var callbackFlag = false

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()
    @Before
    fun setup() {
        val ncmbDispatcher = NCMBDispatcher("script")
        mServer.dispatcher = ncmbDispatcher
        mServer.start()
        NCMB.initialize(
            RuntimeEnvironment.application.getApplicationContext(),
            "appKey",
            "cliKey",
            mServer.url("/").toString(),
            "2013-09-01",
            mServer.url("/").toString(),
            "2015-09-01"
        )
        callbackFlag = false
    }


    @Test
    fun script_method_test(){
        val scriptObj = NCMBScript("testscript.js", NCMBScript.MethodType.GET)
        Assert.assertEquals(scriptObj.mScriptName, "testscript.js")
        Assert.assertEquals(scriptObj.mMethod, NCMBRequest.HTTP_METHOD_GET)
    }

    @Test
    fun script_executeInBackground_success(){
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        inBackgroundHelper.start()

        val header = HashMap<String, String>()
        val body = JSONObject()
        val query = JSONObject()
        val scriptObj = NCMBScript("testScript.js", NCMBScript.MethodType.GET)

        // ファイルストアへの登録を実施
        scriptObj.executeInBackground(header, body , query, NCMBCallback { e, responseData ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["responseData"] = responseData
            inBackgroundHelper.release() // ブロックをリリース
        })
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        val responseData = inBackgroundHelper["responseData"] as ByteArray
        val encodedString = String(responseData, Charsets.UTF_8)
        Assert.assertEquals(encodedString,"this is script result")
    }

    @Test
    fun script_executeInBackground_onlycallback_success(){
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        inBackgroundHelper.start()

        val scriptObj = NCMBScript("testScript.js", NCMBScript.MethodType.GET)
        scriptObj.executeInBackground(callback = NCMBCallback { e, responseData ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["responseData"] = responseData
            inBackgroundHelper.release() // ブロックをリリース
        })
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        val responseData = inBackgroundHelper["responseData"] as ByteArray
        val encodedString = String(responseData, Charsets.UTF_8)
        Assert.assertEquals(encodedString,"this is script result")
    }
}