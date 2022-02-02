package com.nifcloud.mbaas.core

import org.json.JSONException
import java.util.*


/**
 * File features handle class
 *
 * NCMBFilen is used to retrieve and save, update the installation data.
 * Basic features are inherit from NCMBObject and NCMBBase
 */

class NCMBFile: NCMBObject {

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
        const val FILE_NAME = "file"
        const val FILE_ACL = "acl"
    }

    /**
     * Constructor method
     */
    constructor() : super("file") {
        mIgnoreKeys = this.ignoreKeys
    }

    /**
     * File name
     */
    var fileName: String?
        /**
         * Get application name
         *
         * @return application name
         */
        get() = try {
            if (mFields.isNull(NCMBFile.FILE_NAME)) {
                null
            } else mFields.getString(NCMBFile.FILE_NAME)
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
                mFields.put(NCMBFile.FILE_NAME, value)
                mUpdateKeys.add(NCMBFile.FILE_NAME)
            } catch (e: JSONException) {
                throw NCMBException(IllegalArgumentException(e.message))
            }
        }

}