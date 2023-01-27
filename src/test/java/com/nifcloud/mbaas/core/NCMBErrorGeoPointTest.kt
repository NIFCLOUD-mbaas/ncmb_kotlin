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
import com.nifcloud.mbaas.core.helper.NCMBInBackgroundTestHelper
import okhttp3.mockwebserver.MockWebServer
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
import kotlin.test.assertFails

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27), manifest = Config.NONE)
class NCMBErrorGeoPointTest {
    private var mServer: MockWebServer = MockWebServer()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()
    @Before
    fun setup() {
        val ncmbDispatcher = NCMBErrorDispatcher()
        mServer.dispatcher = ncmbDispatcher
        mServer.start()
        NCMB.initialize(
            ApplicationProvider.getApplicationContext(),
            "appKey",
            "cliKey",
            mServer.url("/").toString(),
            "2013-09-01"
        )
    }

    @Test
    fun test_geopoint_saveInBackground_invalid_field(){
        val inBackgroundHelper = NCMBInBackgroundTestHelper()
        val latitude : Double = 35.6666269
        val longitude : Double = 139.765607
        val obj = NCMBObject("TestClassGeo403")
        val geopoint = NCMBGeoPoint(latitude, longitude)
        obj.put("geo", geopoint)
        inBackgroundHelper.start()
        obj.saveInBackground(NCMBCallback { e, ncmbObj ->
            inBackgroundHelper.release()
            Assert.assertEquals(e!!.message, "Invalid GeoPoint value.")
        })
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
    }

    @Test
    fun test_geopoint_getInBackground_wrong_type(){
        val inBackgroundHelper = NCMBInBackgroundTestHelper()
        val obj = NCMBObject("TestClassGeo400")
        obj.setObjectId("7FrmPTBKSNtVjajm")
        inBackgroundHelper.start()
        obj.fetchInBackground(NCMBCallback { e, ncmbObj ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbObj"] = ncmbObj
            inBackgroundHelper.release()
        })
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        val throwable_wrong_type = assertFails { val geo: NCMBGeoPoint = (inBackgroundHelper["ncmbObj"] as NCMBObject).getGeo("geoPoint") }
        val throwable_wrong_key = assertFails { val geo: NCMBGeoPoint = (inBackgroundHelper["ncmbObj"] as NCMBObject).getGeo("geo") }
        Assert.assertEquals("type is not GeoPoint.", throwable_wrong_type.message)
        Assert.assertEquals("No value for geo", throwable_wrong_key.message)
    }
}
