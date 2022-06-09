/*
 * Copyright 2017-2022 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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
import java.io.File
import java.util.*


/**
 * File features handle class
 *
 * NCMBFile is used to retrieve and save, update the installation data.
 * Basic features are inherit from NCMBObject and NCMBBase
 */

class NCMBFile: NCMBObject {

    var fileNameToSetURL: String = ""
    var fileDownloadByte: ByteArray? = null
    val ignoreKeys: List<String> = Arrays.asList(
        "fileName",
        "fileData",
        "mimeType",
        "fileSize",
        "createDate",
        "updateDate",
        "acl"
    )

    companion object {
        const val FILE_CLASS_NAME = "files"
        const val FILE_DATA = "file"
        const val FILE_ACL = "acl"
        const val FILE_NAME = "fileName"
        const val FILE_MIMETYPE = "mimeType"
        const val FILE_SIZE = "fileSize"
    }

    /**
     * Constructor method
     */
    constructor() : super("files") {
        mIgnoreKeys = this.ignoreKeys
    }

    /**
     * Constructor method with fileName and fileData
     */
    constructor(fileName:String, fileData:File) : super("files") {
        mIgnoreKeys = this.ignoreKeys
        this.fileName = fileName
        this.fileData = fileData
    }

    /**
     * Constructor method with fileName only
     */
    constructor(fileName:String) : super("files") {
        mIgnoreKeys = this.ignoreKeys
        this.fileName = fileName
    }

    /**
     * Constructor with JSONObject
     * @param className class name for data store
     */
    constructor(params: JSONObject) : super("files")  {
        println("Start to create result NCMBFile")
        try {
            if (params.has("fileName")) this.fileName = params.get("fileName") as String
            if (params.has("mimeType")) this.mimeType = params.get("mimeType") as String
            if (params.has("fileSize")) this.fileSize = params.get("fileSize") as Int
            copyFrom(params)
        } catch (e: JSONException) {
            throw IllegalArgumentException(e.message)
        }
        this.mIgnoreKeys = mutableListOf(
            "acl", "createDate", "updateDate"
        )
    }

    /**
     * File name
     */
    var fileName: String
        /**
         * Get file name
         *
         * @return file name
         */
        get() = fileNameToSetURL
        /**
         * Set file name
         *
         * @param value file Name
         */
        set(value) {
            fileNameToSetURL = value
        }

    var fileSize: Int = 0
    var mimeType: String = ""

    /**
     * File Object
     */
    var fileData: File?
        /**
         * Get application name
         *
         * @return application name
         */
        get() = try {
            if (mFields.isNull(NCMBFile.FILE_DATA)) {
                null
            } else mFields.get(NCMBFile.FILE_DATA) as File
        } catch (e: JSONException) {
            throw NCMBException(IllegalArgumentException(e.message))
        }
        /**
         * Set application name
         *
         * @param value applicationName
         */
        set(value) {
            try {
                mFields.put(NCMBFile.FILE_DATA, value)
                mUpdateKeys.add(NCMBFile.FILE_DATA)
            } catch (e: JSONException) {
                throw NCMBException(IllegalArgumentException(e.message))
            }
        }


    /**
     * Save File object
     *
     * @throws NCMBException exception from NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    override fun save(){
        //connect
        val fileService = NCMBFileService()
        //new create
        fileService.saveFile(this)
        mUpdateKeys.clear()
    }

    /**
     * Save file object in Background
     *
     * @param callback Save Callback
     */
    override fun saveInBackground(saveCallback: NCMBCallback) {
        //connect
        val fileService = NCMBFileService()
        //new create
        fileService.saveFileInBackground(
            this,
            saveCallback
        )
    }

    /**
     * Update File ACL
     * (This function is for ACL information only. To update file data, use save function instead.)
     *
     * @throws NCMBException exception from NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun update(){
        if (fileData != null) {
            throw  NCMBException(NCMBException.GENERIC_ERROR, "Please do not set file data to update. Use save function instead.")
        }
        //connect
        val fileService = NCMBFileService()
        //new create
        fileService.updateFile(this)
        mUpdateKeys.clear()
    }

    /**
     * Update File ACL in Background
     * (This function is for ACL information only. To update file data, use save function instead.)
     *
     * @param callback Save Callback
     *
     * @throws NCMBException exception from NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun updateInBackground(saveCallback: NCMBCallback) {
        if (fileData != null) {
            throw  NCMBException(NCMBException.GENERIC_ERROR, "Please do not set file data to update. Use save function instead.")
        }
        //connect
        val fileService = NCMBFileService()
        //new create
        fileService.updateFileInBackground(
            this,
            saveCallback
        )
    }


    /**
     * Fetch file object in Background
     *
     * @param callback Save Callback
     */
    override fun fetchInBackground(fetchCallback: NCMBCallback) {
        //connect
        val fileService = NCMBFileService()
        //new create
        fileService.fetchFileInBackground(
            this,
            fetchCallback
        )
    }

    /**
     * Fetch File object
     *
     * @throws NCMBException exception from NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    override fun fetch(){
        //connect
        val fileService = NCMBFileService()
        //new create
        fileService.fetchFile(this)
        mUpdateKeys.clear()
    }

    fun reflectResponseFileData(data: ByteArray?) {
        if (data != null) {
            this.fileDownloadByte = data
        }
    }



}