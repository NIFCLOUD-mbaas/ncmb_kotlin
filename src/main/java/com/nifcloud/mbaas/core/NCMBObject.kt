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

package com.nifcloud.mbaas.core

import org.json.JSONException
import org.json.JSONObject

/**
 * A class of ncmb_kotlin.
 *
 * Main class of datastore features. Inherits from NCMBBase.
 */

open class NCMBObject : NCMBBase {
    protected var mClassName: String = ""

    /**
     * Constructor with class name
     * @param className class name for data store
     */
    constructor(className: String){
        mClassName = className
        mFields = JSONObject()
        mUpdateKeys = HashSet()
        mIgnoreKeys = ArrayList()
    }

    /**
     * Constructor with class name
     * @param className class name for data store
     */
    constructor(className: String, params: JSONObject) : this(className) {
        try {
            copyFrom(params)
        } catch (e: JSONException) {
            throw IllegalArgumentException(e.message)
        }
        this.mClassName = className
        this.mIgnoreKeys = mutableListOf(
            "objectId", "acl",
            "createDate", "updateDate"
        )
    }

    /**
     * save current NCMBObject to data store
     * @throws NCMBException exception from NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    open fun save(): NCMBObject {
        val objectId = getObjectId()
        val className = this.mClassName
        val objService = NCMBObjectService()
        if (objectId == null) {
            //Object save
            objService.saveObject(
                this,
                className,
                this.mFields
            )
            return this
        } else {
            //Object update
            try {
                val updateJson = createUpdateJsonData()
                objService.updateObject(
                    this,
                    className,
                    objectId,
                    updateJson
                )
            } catch (e: JSONException) {
                throw NCMBException(
                    NCMBException.INVALID_JSON,
                    e.message!!
                )
            }
            return this
        }
    }

    /**
     * save current NCMBObject to data store asynchronously
     * @param callback callback after object save
     */
    fun saveInBackground(saveCallback: NCMBCallback) {
        val objectId = getObjectId()
        val className = this.mClassName
        val objService = NCMBObjectService()
        if (objectId == null) {
            // 保存後に実施するsaveCallbackを渡す
            objService.saveObjectInBackground(
                this,
                className,
                this.mFields,
                saveCallback
            )
        }
        else {
            //Object update
            try {
                val updateJson = createUpdateJsonData()
                objService.updateObjectInBackground(
                    this, className,
                    objectId,
                    updateJson,
                    saveCallback
                )
            } catch (e: JSONException) {
                saveCallback.done(
                    NCMBException(
                        NCMBException.INVALID_JSON,
                        e.message!!
                    )
                )
            }
        }
    }

    @Throws(NCMBException::class)
    open fun fetch(): NCMBObject {
        val objectId = getObjectId()
        val className = this.mClassName
        val objService = NCMBObjectService()
        if (objectId != null) {
            // 保存後に実施するsaveCallbackを渡す
            objService.fetchObject(
                this,
                className, objectId
            )
        }
        return this
    }

    open fun fetchInBackground(fetchCallback: NCMBCallback) {
        val objectId = getObjectId()
        val className = this.mClassName
        val objService = NCMBObjectService()
        if (objectId != null) {
            objService.fetchObjectInBackground(
                this,
                className,
                objectId,
                fetchCallback
            )
        } else {
            fetchCallback.done(
                NCMBException(
                    NCMBException.GENERIC_ERROR,
                    "Need to set objectID before fetch"
                )
            )
        }
    }

    /**
     * save current NCMBObject to data store
     * @throws NCMBException exception from NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    open fun delete(): NCMBObject? {
        val objectId = getObjectId()
        val className = this.mClassName
        val objService = NCMBObjectService()
        if (objectId != null) {
            // 保存後に実施するsaveCallbackを渡す
            objService.deleteObject(this, className, objectId)
        }
        return null
    }

    /**
     * delete current NCMBObject from data store asynchronously
     * @param callback callback after delete object
     */
    open fun deleteInBackground(deleteCallback: NCMBCallback) {
        val objectId = getObjectId()
        val className = this.mClassName
        val objService = NCMBObjectService()
        if (objectId != null) {
            objService.deleteObjectInBackground(
                this,
                className, objectId, deleteCallback
            )
        } else {
            val ex = NCMBException(
                NCMBException.GENERIC_ERROR,
                "Need to set objectID before delete"
            )
            deleteCallback.done(ex)
        }
    }
}
