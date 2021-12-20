package com.nifcloud.mbaas.core

import org.json.JSONObject

internal interface NCMBServiceInterface<T:NCMBObject> {

    fun find(className: String, query:JSONObject): List<T>

    fun findInBackground( className: String, query:JSONObject, findCallback: NCMBCallback)

    fun count(className: String, query:JSONObject): Int

    fun countInBackground( className: String, query:JSONObject, countCallback: NCMBCallback)
}
