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

/**
 * 主に通信を行う自動化テストクラス
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27), manifest = Config.NONE)
class NCMBErrorInstallationTest {

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

    @Test
    @Throws(Exception::class)
    fun saveInBackground_post_no_deviceToken() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        //post
        val installation = NCMBInstallation()
        inBackgroundHelper.start()
        val callback = NCMBCallback { e, ncmbObj ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbObj"] = ncmbObj
            inBackgroundHelper.release() // ブロックをリリース
        }
        val throwable = assertFails { installation.saveInBackground(callback)}
        inBackgroundHelper.await()
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals("registrationId is must not be null.", throwable.message)

//        installation.saveInBackground(callback)
//        inBackgroundHelper.await()
//        Assert.assertNull(inBackgroundHelper["e"])
//        Assert.assertEquals("registrationId is must not be null.", (inBackgroundHelper["e"] as NCMBException).message)
    }

    @Test
    @Throws(Exception::class)
    fun saveInBackground_post_duplicate_deviceToken() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        //post
        val installation = NCMBInstallation()
        installation.deviceToken = "duplicateDeviceToken"
        inBackgroundHelper.start()
        val callback = NCMBCallback { e, ncmbObj ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbObj"] = ncmbObj
            inBackgroundHelper.release() // ブロックをリリース
        }
        installation.saveInBackground(callback)
        inBackgroundHelper.await()
        Assert.assertEquals("Duplication Error.", (inBackgroundHelper["e"] as NCMBException).message)
    }
}