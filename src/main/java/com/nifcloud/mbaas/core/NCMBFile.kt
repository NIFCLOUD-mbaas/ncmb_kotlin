package com.nifcloud.mbaas.core

import org.json.JSONException
import java.util.*


/**
 * File features handle class
 *
 * NCMBFile is used to retrieve and save, update the installation data.
 * Basic features are inherit from NCMBObject and NCMBBase
 */

class NCMBFile: NCMBObject {

    var fileNameToSetURL: String = ""
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
    constructor() : super("file") {
        mIgnoreKeys = this.ignoreKeys
    }

    /**
     * Constructor method with fileName and fileData
     */
    constructor(fileName:String, fileData:ByteArray) : super("file") {
        mIgnoreKeys = this.ignoreKeys
        this.fileName = fileName
        this.fileData = fileData
    }

    /**
     * Constructor method with fileName only
     */
    constructor(fileName:String) : super("file") {
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
     * File data
     */
    var fileData: ByteArray?
        /**
         * Get application name
         *
         * @return application name
         */
        get() = try {
            if (mFields.isNull(NCMBFile.FILE_DATA)) {
                null
            } else mFields.get(NCMBFile.FILE_DATA) as ByteArray?
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




}