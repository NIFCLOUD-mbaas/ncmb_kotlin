package com.nifcloud.mbaas.core;

import org.json.JSONException
import org.json.JSONObject


class NCMBGeoPoint {

    var mlatitude: Double
    var mlongitude: Double

    /// 例外処理
    @Throws(NCMBException::class)
    fun validate(latitude: Double, longitude: Double): Pair<Double, Double> {
        if (latitude < -90.0 || latitude > 90) {
            throw NCMBException(
                NCMBException.NOT_EFFICIENT_VALUE,
                "set the latitude to a value between -90 and 90"
            )
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw NCMBException(
                NCMBException.NOT_EFFICIENT_VALUE,
                "set the longitude to a value between -180 and 180"
            )
        }
        return Pair(latitude, longitude)
    }

    /**
     * put Location value to given key
     *
     * @param key   field name for put the value
     * @param value value to put
     */
    @Throws(NCMBException::class)
    fun put(key: String, value: NCMBGeoPoint) {
        try {
            val locationJson = JSONObject("{'__type':'GeoPoint'}")
            locationJson.put("longitude", value.mlongitude)
            locationJson.put("latitude", value.mlatitude)
            NCMBBase().mFields.put(key, locationJson)
            NCMBBase().mUpdateKeys.add(key)
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_FORMAT, e.message!!)
        }
    }

    constructor(latitude: Double = 0.0, longitude: Double = 0.0) {
        val position = validate(latitude, longitude)
        mlatitude = position.first
        mlongitude = position.second
    }
}
