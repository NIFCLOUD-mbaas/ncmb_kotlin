package com.nifcloud.mbaas.core

import org.junit.Assert
import org.junit.Test

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

}
