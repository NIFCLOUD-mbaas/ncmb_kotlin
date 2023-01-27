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
import com.nifcloud.mbaas.core.NCMBDateFormat.getIso8601
import com.nifcloud.mbaas.core.helper.NCMBInBackgroundTestHelper
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONArray
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import androidx.test.core.app.ApplicationProvider
import org.robolectric.annotation.Config
import java.util.*

/**
 * 主に通信を行う自動化テストクラス
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27), manifest = Config.NONE)
class NCMBInstallationTest {

    private var mServer: MockWebServer = MockWebServer()
    private var callbackFlag = false

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()
    @Before
    fun setup() {
        val ncmbDispatcher = NCMBDispatcher("installations")
        mServer.dispatcher = ncmbDispatcher
        mServer.start()
        NCMB.initialize(
            ApplicationProvider.getApplicationContext(),
            "appKey",
            "cliKey",
            mServer.url("/").toString(),
            "2013-09-01"
        )

        callbackFlag = false;
        }

    @Test
    @Throws(Exception::class)
    fun saveInBackground_post() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        Assert.assertNull(NCMBInstallation.currentInstallation.getObjectId())
        //post
        val installation = NCMBInstallation()
        installation.deviceToken = "xxxxxxxxxxxxxxxxxxx"
        val channels = JSONArray()
        channels.put("Ch1")
        channels.put("Ch2")
        installation.channels = channels
        inBackgroundHelper.start()
        installation.saveInBackground(NCMBCallback { e, ncmbObj ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbObj"] = ncmbObj
            inBackgroundHelper.release() // ブロックをリリース
        })
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        //check
        Assert.assertEquals("7FrmPTBKSNtVjajm", installation.getObjectId())
        Assert.assertEquals("xxxxxxxxxxxxxxxxxxx", installation.deviceToken)
        Assert.assertEquals("Ch1", installation.channels?.get(0))
        Assert.assertEquals("Ch2", installation.channels?.get(1))
        val date: Date = getIso8601().parse("2014-06-03T11:28:30.348Z")!!
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).getObjectId(), "7FrmPTBKSNtVjajm")
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).getCreateDate(), date)
        Assert.assertNotNull(NCMBInstallation.currentInstallation.getObjectId())
    }

    @Test
    @Throws(Exception::class)
    fun saveInBackground_put() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        Assert.assertNull(NCMBInstallation.currentInstallation.getObjectId())
        val channels = JSONArray()
        channels.put("Ch1")
        channels.put("Ch2")
        //post
        val installation = NCMBInstallation()
        installation.deviceToken = "xxxxxxxxxxxxxxxxxxx"
        installation.put("key", "value1")
        installation.channels = channels
        inBackgroundHelper.start()
        installation.saveInBackground(NCMBCallback { e, ncmbObj ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbObj"] = ncmbObj
            inBackgroundHelper.release() // ブロックをリリース
        })
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        //check
        Assert.assertEquals("7FrmPTBKSNtVjajm", installation.getObjectId())
        Assert.assertEquals("xxxxxxxxxxxxxxxxxxx", installation.deviceToken)
        Assert.assertEquals("value1", installation.getString("key"))
        Assert.assertEquals("Ch1", installation.channels?.get(0))
        Assert.assertEquals("Ch2", installation.channels?.get(1))
        val date: Date = getIso8601().parse("2014-06-03T11:28:30.348Z")!!
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).getObjectId(), "7FrmPTBKSNtVjajm")
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).getCreateDate(), date)
        Assert.assertNotNull(NCMBInstallation.currentInstallation.getObjectId())
    }


    @Test
    @Throws(NCMBException::class)
    fun fetch_installation_with_get_success() {
        val installation = NCMBInstallation()
        installation.setObjectId("7FrmPTBKSNtVjajm")
        installation.put("key", "value")
        installation.fetch()
        Assert.assertEquals(installation.getObjectId(), "7FrmPTBKSNtVjajm")
        Assert.assertEquals(installation.get("key"), "value")
        Assert.assertNotNull(installation)
    }

    @Test
    @Throws(NCMBException::class)
    fun fet_installation_channels() {
        val installation = NCMBInstallation()
        installation.setObjectId("7FrmPTBKchannels")
        installation.fetch()
        Assert.assertEquals(installation.getObjectId(), "7FrmPTBKchannels")
        Assert.assertEquals(installation.channels?.get(0), "Ch1")
        Assert.assertEquals(installation.channels?.get(1), "Ch2")
        Assert.assertNotNull(installation)
    }

    @Test
    @Throws(NCMBException::class)
    fun fetchInBackground_installation_with_get_success() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val installation = NCMBInstallation()
        installation.setObjectId("7FrmPTBKSNtVjajm")
        inBackgroundHelper.start()
        installation.fetchInBackground(NCMBCallback { e, ncmbObj ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbObj"] = ncmbObj
            inBackgroundHelper.release() // ブロックをリリース
        })
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBInstallation).getObjectId(), "7FrmPTBKSNtVjajm")
        Assert.assertNotNull(installation)
    }

    @Test
    @Throws(NCMBException::class)
    fun delete_installation_success() {
        val obj = NCMBInstallation()
        obj.setObjectId("7FrmPTBKSNtVjajm")
        try {
            obj.delete()
            Assert.assertNotNull(obj)
            Assert.assertNull(NCMBInstallation.currentInstallation.getObjectId())
        } catch (e: NCMBException) {
            Assert.fail("exception raised:" + e.message)
        }
    }

    @Test
    @Throws(NCMBException::class)
    fun deleteInBackground_installation_success() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val installation = NCMBInstallation()
        installation.setObjectId("7FrmPTBKSNtVjajm")
        inBackgroundHelper.start()
        installation.deleteInBackground(NCMBCallback { e, ncmbObj ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbObj"] = ncmbObj
            inBackgroundHelper.release() // ブロックをリリース
        })
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertNotNull(installation)
        Assert.assertNull(NCMBInstallation.currentInstallation.getObjectId())
    }
}