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

import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import androidx.test.core.app.ApplicationProvider

/**
 * 初期化のテスト
 *
 */

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [27], manifest = Config.NONE)
class NCMBTest {
    private var mServer: MockWebServer = MockWebServer()

    @Before
    @Throws(Exception::class)
    fun setup() {
        val ncmbDispatcher = NCMBDispatcher("")
        mServer.dispatcher = ncmbDispatcher
        mServer.start()
    }

    /**
     * シンプルなapikeyの初期化テスト
     */
    @Test
    fun Initialize_test() {
        val applicationKey =  "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"
        val clientKey = "111111111111111111111111111111111111111111111111111111111111111"
        NCMB.initialize(
            ApplicationProvider.getApplicationContext(),
            applicationKey,
            clientKey)
        Assert.assertEquals(applicationKey, NCMB.getApplicationKey())
        Assert.assertEquals(clientKey, NCMB.getClientKey())
        Assert.assertEquals(NCMB.getApiBaseUrl(), "https://mbaas.api.nifcloud.com/2013-09-01/")
        Assert.assertEquals(NCMB.getApiBaseUrl(isScript = true), "https://script.mbaas.api.nifcloud.com/2015-09-01/")
    }

    /**
     * シンプルなapikeyの初期化テスト
     */
    @Test
    fun Initialize_test_domainurl_apiversion() {
        val applicationKey =  "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"
        val clientKey = "111111111111111111111111111111111111111111111111111111111111111"
        val domainUrl = "https://mbaas.api.nifcloud.com/"
        val apiVersion = "2013-09-01"
        NCMB.initialize(
            ApplicationProvider.getApplicationContext(),
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
    fun Initialize_test_domainurlScript_apiversionScript() {
        val applicationKey =  "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"
        val clientKey = "111111111111111111111111111111111111111111111111111111111111111"
        val domainUrl = "https://testdomain.com/"
        val apiVersion = "2013-09-01"
        val scriptDomainUrl = "https://scripttestdomain.com/"
        val scriptApiVersion = "2015-09-01"
        NCMB.initialize(
            ApplicationProvider.getApplicationContext(),
            applicationKey,
            clientKey,
            domainUrl,
            apiVersion,
            scriptDomainUrl,
            scriptApiVersion
        )
        Assert.assertEquals(applicationKey, NCMB.getApplicationKey())
        Assert.assertEquals(clientKey, NCMB.getClientKey())
        Assert.assertEquals("$domainUrl$apiVersion/", NCMB.getApiBaseUrl())
        Assert.assertEquals("$scriptDomainUrl$scriptApiVersion/", NCMB.getApiBaseUrl(isScript = true))
    }

    /**
     * シンプルなapikeyの初期化テスト
     */
    @Test
    fun Initialize_onlyapiclientkey_test() {
        val applicationKey =  "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"
        val clientKey = "111111111111111111111111111111111111111111111111111111111111111"
        NCMB.initialize(
            ApplicationProvider.getApplicationContext(),
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
        val applicationKey1 =  "0123456789"
        val clientKey1 = "111111111"
        val applicationKey2 =  "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef"
        val clientKey2 = "111111111111111111111111111111111111111111111111111111111111111"
        NCMB.initialize(
            ApplicationProvider.getApplicationContext(),
            applicationKey1,
            clientKey1
        )
        NCMB.initialize(
            ApplicationProvider.getApplicationContext(),
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
            ApplicationProvider.getApplicationContext(),
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
