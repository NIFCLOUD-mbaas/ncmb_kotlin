package com.nifcloud.mbaas.core

import org.junit.Assert
import org.junit.Test

class NCMBGeoPointTest {
    @Test
    fun test_geopoint_right_settings(){
        val latitude : Double = 35.6666269
        val longitude : Double = 139.765607
        val geopoint = geoPoint(latitude, longitude)
        Assert.assertEquals(geopoint.latitude, 35.6666269)
        Assert.assertEquals(geopoint.longitude, 139.765607)
    }

    @Test
    fun test_geopoint_init_settings(){
        val geopoint = geoPoint()
        Assert.assertEquals(geopoint.latitude, 0)
        Assert.assertEquals(geopoint.longitude, 0)
    }

    @Test
    fun test_geopoint_wrong_latitude(){

    }
}
