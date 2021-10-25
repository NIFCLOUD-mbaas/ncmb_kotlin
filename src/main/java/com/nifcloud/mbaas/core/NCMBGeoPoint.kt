package com.nifcloud.mbaas.core;

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

    constructor(latitude: Double = 0.0, longitude: Double = 0.0) {
        val position = validate(latitude, longitude)
        mlatitude = position.first
        mlongitude = position.second
    }
}
