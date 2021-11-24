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
import java.util.*

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
            RuntimeEnvironment.application.getApplicationContext(),
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

}
