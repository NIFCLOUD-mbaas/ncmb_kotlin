package com.nifcloud.mbaas.core;

import java.lang.IllegalArgumentException

//
class NCMBGeoPoint(latitude : Double ,longitude : Double) {

    /// 例外処理
    fun validate (latitude : Double, longitude : Double) : Pair<Double, Double>{
        if(latitude !is Double || longitude !is Double){
            throw IllegalArgumentException("GeoPoint latitude and longitude should be number")
        }
        if(latitude < -90.0 || latitude > 90){
            throw IllegalArgumentException("set the latitude to a value between -90 and 90")
        }
        if(longitude < -180.0 || longitude > 180.0){
            throw IllegalArgumentException("set the longitude to a value between -180 and 180")
        }
        return Pair(latitude, longitude)
    }

    /// オブジェクト化処理
    fun toObject (typename : String, latObject : String, lngObject : String){

    }

    /// メイン処理
    fun geoPoint (lat : Double, lng : Double){
        var position : Pair<Double, Double> = validate(lat, lng)
        var GeoObject = toObject()
        return GeoObject
    }
}
