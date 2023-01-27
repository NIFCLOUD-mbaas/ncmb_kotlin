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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27), manifest = Config.NONE)
class NCMBGeoPointTest {

    private var mServer: MockWebServer = MockWebServer()
    private var callbackFlag = false

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()
    @Before
    fun setup() {
        val ncmbDispatcher = NCMBDispatcher("geopoint")
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
    fun test_geopoint_right_settings(){
        val latitude : Double = 35.6666269
        val longitude : Double = 139.765607
        val geopoint = NCMBGeoPoint(latitude, longitude)
        Assert.assertEquals(geopoint.mlatitude, latitude, 0.0)
        Assert.assertEquals(geopoint.mlongitude, longitude, 0.0)
    }

    @Test
    fun test_geopoint_init_settings(){
        val geopoint = NCMBGeoPoint()
        Assert.assertEquals(geopoint.mlatitude, 0.0, 0.0)
        Assert.assertEquals(geopoint.mlongitude, 0.0, 0.0)
    }

    @Test
    fun test_geopoint_wrong_latitude(){
        try {
            val latitude: Double = 139.765607
            val longitude: Double = 139.765607
            val geopoint = NCMBGeoPoint(latitude, longitude)
        }
        catch(e: NCMBException){
            Assert.assertEquals(e.message, "set the latitude to a value between -90 and 90")
        }
    }

    @Test
    fun test_geopoint_wrong_longitude(){
        try {
            val latitude: Double = 35.6666269
            val longitude: Double = 189.765607
            val geopoint = NCMBGeoPoint(latitude, longitude)
        }
        catch(e: NCMBException){
            Assert.assertEquals(e.message, "set the longitude to a value between -180 and 180")
        }
    }

    @Test
    fun test_geopoint_save(){
        val latitude : Double = 35.6666269
        val longitude : Double = 139.765607
        var obj = NCMBObject("TestClassGeo")
        val geopoint = NCMBGeoPoint(latitude, longitude)
        obj.put("geoPoint", geopoint)
        obj.save()
        Assert.assertEquals(obj.getObjectId(), "7FrmPTBKSNtVjajm")
    }

    @Test
    fun test_geopoint_put(){
        val inBackgroundHelper = NCMBInBackgroundTestHelper()
        val latitude : Double = 35.6666269
        val longitude : Double = 139.765607
        val obj = NCMBObject("TestClassGeo")
        val geopoint = NCMBGeoPoint(latitude, longitude)
        obj.put("geoPoint", geopoint)
        inBackgroundHelper.start()
        obj.saveInBackground(NCMBCallback { e, ncmbObj ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbObj"] = ncmbObj
            inBackgroundHelper.release()
        })
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).getObjectId(), "7FrmPTBKSNtVjajm")
        val date: Date = getIso8601().parse("2014-06-03T11:28:30.348Z")!!
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).getCreateDate(), date)
    }

    @Test
    fun test_geopoint_get(){
        var obj = NCMBObject("TestClassGeo")
        obj.setObjectId("7FrmPTBKSNtVjajm")
        obj.fetch()
        val geo = obj.getGeo("geoPoint")
        Assert.assertEquals(geo.mlatitude, 35.6666269, 0.0)
        Assert.assertEquals(geo.mlongitude, 139.765607, 0.0)
    }

    @Test
    fun test_geopoint_getInBackground(){
        val inBackgroundHelper = NCMBInBackgroundTestHelper()
        val obj = NCMBObject("TestClassGeo")
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
        val geo: NCMBGeoPoint = (inBackgroundHelper["ncmbObj"] as NCMBObject).getGeo("geoPoint")
        Assert.assertEquals(geo.mlatitude, 35.6666269, 0.0)
        Assert.assertEquals(geo.mlongitude, 139.765607, 0.0)
    }
}
