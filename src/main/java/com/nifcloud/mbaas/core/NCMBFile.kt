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
    var fileToSave: File? = null
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
        const val FILE_DATA = "file"
        const val FILE_ACL = "acl"
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
                print(value)
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
        val responseData: JSONObject
        //new create
        responseData = fileService.saveFile(this)
        mUpdateKeys.clear()
    }

    /**
     * Save installation object in Background
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


}