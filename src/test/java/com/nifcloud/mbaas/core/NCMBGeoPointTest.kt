package com.nifcloud.mbaas.core

import android.util.Log
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27), manifest = Config.NONE)
class NCMBGeoPointTest {
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
    fun test_geopoint_put(){
        NCMB.initialize(
            RuntimeEnvironment.application.getApplicationContext(),
            "ac910f7562fc236a520c14000ebf40eb407cd89d5a577ee8ca10e9170d003ba4",
            "35b70813ca144df8eaec9c7d89e10fb52eb43c8ccc28ef0def79fdf6b620e384"
        )
        val latitude : Double = 35.6666269
        val longitude : Double = 139.765607
        val obj = NCMBObject("TestClass")
        val geopoint = NCMBGeoPoint(latitude, longitude)
        obj.put("geo", geopoint)
        obj.saveInBackground(NCMBCallback { e, ncmbObj ->
            if (e != null) {
                //保存に失敗した場合の処理
                Log.d("error","保存に失敗しました : " + e.message)
            } else {
                //保存に成功した場合の処理
                val result = ncmbObj as NCMBObject
                Log.d("success","保存に成功しました ObjectID :" + result.getObjectId())
            }
        })
    }
}
