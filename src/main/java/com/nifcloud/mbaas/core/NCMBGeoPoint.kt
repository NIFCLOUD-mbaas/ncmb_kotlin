/*
 * Copyright 2017-2021 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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
package com.nifcloud.mbaas.core;

import org.json.JSONException
import org.json.JSONObject


/**
 * Geolocation handling class
 *
 * This class initialize Geopoint data, do handle geo data tasks.
 *
 */

class NCMBGeoPoint {

    var mlatitude: Double
    var mlongitude: Double

    /**
     * validate input latitude and longitude value. (latitude:-90~90, longitude:-180~180)
     *
     * @param latitude  value set as latitude
     * @param longitude value set as longitude
     */
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

    /**
     * initialize NCMBGeoPoint object with latitude and longitude. (default latitude:0.0, default longitude:0.0)
     *
     * @param latitude  value set as latitude
     * @param longitude value set as longitude
     */
    constructor(latitude: Double = 0.0, longitude: Double = 0.0) {
        val position = validate(latitude, longitude)
        mlatitude = position.first
        mlongitude = position.second
    }
}
