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
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.test.assertFails

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27), manifest = Config.NONE)
class NCMBErrorScriptTest {
    private var mServer: MockWebServer = MockWebServer()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        val ncmbDispatcher = NCMBErrorDispatcher()
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
    }

    @Test
    fun script_execute_fail(){
        val header = HashMap<String, String>()
        val body = JSONObject()
        val query = JSONObject()
        val scriptObj = NCMBScript("errorTestScript.js", NCMBScript.MethodType.GET)

        var response : ByteArray? = null
        try {
            response = scriptObj.execute(header, body, query)
        }
        catch(e : NCMBException){
            Assert.assertNull(response)
            Assert.assertEquals(e.message, "errorTestScript.js not found")
        }
    }

    @Test
    fun scriptExecuteInBackGround_err500() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val header = HashMap<String, String>()
        val body = JSONObject()
        val query = JSONObject()
        val scriptObj = NCMBScript("testScript500.js", NCMBScript.MethodType.GET)

        inBackgroundHelper.start()
        scriptObj.executeInBackground(header, body , query, NCMBCallback { e, responseData ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["responseData"] = responseData
            inBackgroundHelper.release() // ブロックをリリース
        })
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertEquals(NCMBException.INTERNAL_SERVER_ERROR, (inBackgroundHelper["e"] as NCMBException).code)
    }

    @Test
    fun scriptExecuteInBackGround_onlycallback_err500() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val scriptObj = NCMBScript("testScript500.js", NCMBScript.MethodType.GET)

        inBackgroundHelper.start()
        scriptObj.executeInBackground(callback = NCMBCallback { e, responseData ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["responseData"] = responseData
            inBackgroundHelper.release() // ブロックをリリース
        })
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertEquals(NCMBException.INTERNAL_SERVER_ERROR, (inBackgroundHelper["e"] as NCMBException).code)
    }
}
