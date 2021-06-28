/*
 * Copyright 2017-2018 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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

import android.content.Context
import android.content.ContextWrapper
import org.json.JSONException
import org.json.JSONObject
import java.io.*


/**
 * LocalFile class
 */
internal object NCMBLocalFile {
    /**
     * Base folder name
     */
    const val FOLDER_NAME = "NCMB"

    fun create(fileName: String): File {
        return File(NCMB.getCurrentContext()?.getDir(FOLDER_NAME, Context.MODE_PRIVATE), fileName)
    }

    /**
     * Writing to a local file
     *
     * @param writeFile write file instance
     * @param fileData  local file data
     */
    @Throws(NCMBException::class)
    fun writeFile(writeFile: File, fileData: JSONObject) {
        checkNCMBContext()
        try {
            val out = FileOutputStream(writeFile)
            out.write(fileData.toString().toByteArray(charset("utf-8")))
            out.close()
        } catch (e: IOException) {
            throw NCMBException(e)
        }
    }

    /**
     * Reading to a local file
     *
     * @param readFile read file instance
     * @return file data. if file data is empty, return empty json
     */
    @Throws(NCMBException::class)
    fun readFile(readFile: File): JSONObject {
        checkNCMBContext()
        var json = JSONObject()
        json = try {
            val br = BufferedReader(FileReader(readFile))
            val information = br.readLine() ?: return json
            br.close()
            JSONObject(information)
        } catch (e: IOException) {
            throw NCMBException(e)
        } catch (e: JSONException) {
            throw NCMBException(e)
        } catch (e: NullPointerException) {
            throw NCMBException(e)
        }
        return json
    }

    /**
     * Delete the local file
     *
     * @param deleteFile delete file instance
     */
    fun deleteFile(deleteFile: File) {
        checkNCMBContext()
        deleteFile.delete()
    }

    /**
     * null check in NCMBContext
     */
    fun checkNCMBContext() {
        if (NCMB.getCurrentContext() == null) {
            throw RuntimeException("Please run the　NCMB.initialize.")
        }
    }
}
