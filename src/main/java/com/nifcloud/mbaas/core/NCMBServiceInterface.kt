package com.nifcloud.mbaas.core

import org.json.JSONObject

interface NCMBServiceInterface<T:NCMBObject> {

    fun findObjects(className: String, query:JSONObject): List<T>

    fun findObjectsInBackground( className: String, query:JSONObject, findCallback: NCMBCallback)
}