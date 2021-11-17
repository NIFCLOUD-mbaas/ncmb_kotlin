package com.nifcloud.mbaas.core

import org.json.JSONObject

interface NCMBServiceInterface<T:NCMBObject> {

    fun find(className: String, query:JSONObject): List<T>

    fun findInBackground( className: String, query:JSONObject, findCallback: NCMBCallback)
}