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

import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config


/**
 * 初期化のテスト
 *
 * @param applicationKey & clientKey は apikey
 * @property なし .
 * @constructor なし .
 */

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27), manifest = Config.NONE)
class NCMBTest {
    private var mServer: MockWebServer = MockWebServer()

    @Before
    @Throws(Exception::class)
    fun setup() {
        var ncmbDispatcher = NCMBDispatcher("")
        mServer.dispatcher = ncmbDispatcher
        mServer.start()
    }

    /**
     * シンプルなapikeyの初期化テスト
     */
    @Test
    fun Initialize_test() {
        var applicationKey =  "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"
        var clientKey = "111111111111111111111111111111111111111111111111111111111111111"
        NCMB.initialize(
            RuntimeEnvironment.application.getApplicationContext(),
            applicationKey,
            clientKey)
        Assert.assertEquals(applicationKey, NCMB.getApplicationKey())
        Assert.assertEquals(clientKey, NCMB.getClientKey())
    }

    /**
     * シンプルなapikeyの初期化テスト
     */
    @Test
    fun Initialize_test_domainurl_apiversion() {
        var applicationKey =  "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"
        var clientKey = "111111111111111111111111111111111111111111111111111111111111111"
        var domainUrl = "https://mbaas.api.nifcloud.com/"
        var apiVersion = "2013-09-01"
        NCMB.initialize(
            RuntimeEnvironment.application.getApplicationContext(),
            applicationKey,
            clientKey,
            domainUrl,
            apiVersion
        )
        Assert.assertEquals(applicationKey, NCMB.getApplicationKey())
        Assert.assertEquals(clientKey, NCMB.getClientKey())
        Assert.assertEquals("$domainUrl$apiVersion/", NCMB.getApiBaseUrl())
    }

    /**
     * シンプルなapikeyの初期化テスト
     */
    @Test
    fun Initialize_onlyapiclientkey_test() {
        var applicationKey =  "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"
        var clientKey = "111111111111111111111111111111111111111111111111111111111111111"
        NCMB.initialize(
            RuntimeEnvironment.application.getApplicationContext(),
            applicationKey,
            clientKey
        )

        Assert.assertEquals(applicationKey, NCMB.APPLICATION_KEY)
        Assert.assertEquals(clientKey, NCMB.CLIENT_KEY)
    }

    /**
     * 初期化が2度起きた時のテスト
     */
    @Test
    fun Initialize_two_times() {
        var applicationKey1 =  "0123456789"
        var clientKey1 = "111111111"
        var applicationKey2 =  "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"
        var clientKey2 = "111111111111111111111111111111111111111111111111111111111111111"
        NCMB.initialize(
            RuntimeEnvironment.application.getApplicationContext(),
            applicationKey1,
            clientKey1
        )
        NCMB.initialize(
            RuntimeEnvironment.application.getApplicationContext(),
            applicationKey2,
            clientKey2
        )
        Assert.assertEquals(applicationKey2, NCMB.getApplicationKey())
        Assert.assertEquals(clientKey2, NCMB.getClientKey())
    }

    /**
     * 直接apikeyを代入した初期化のテスト.
     */
    @Test
    fun Initialize_direct() {
        NCMB.initialize(
            RuntimeEnvironment.application.getApplicationContext(),
            "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef",
            "111111111111111111111111111111111111111111111111111111111111111"
        )
        Assert.assertEquals(
            "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef",
            NCMB.getApplicationKey()
        )
        Assert.assertEquals(
            "111111111111111111111111111111111111111111111111111111111111111",
            NCMB.getClientKey()
        )
    }

    /**
     * timeout set テスト.
     */
    @Test
    fun timeout_set_test() {
        NCMB.setTimeOut(12345)
        Assert.assertEquals(NCMB.getTimeOut(), 12345)
    }
}
