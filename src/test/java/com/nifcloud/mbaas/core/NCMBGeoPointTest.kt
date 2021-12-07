package com.nifcloud.mbaas.core

import android.util.Log
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
import org.robolectric.RuntimeEnvironment
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
        val ncmbDispatcher = NCMBDispatcher()
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
}
