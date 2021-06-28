package com.nifcloud.mbaas.core

import okhttp3.Headers
import org.json.JSONObject

/**
 * A class of ncmb_kotlin.
 *
 * NCMBResponse contains response data from NIFCLOUD mobile backend
 *
 */
sealed class NCMBResponse {
    data class Success(val resCode: Int, val resHeaders: Headers, val data: JSONObject) : NCMBResponse()
    data class Failure(val resException: NCMBException) : NCMBResponse()
}
