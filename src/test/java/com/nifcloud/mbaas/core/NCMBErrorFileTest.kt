/*
 * Copyright 2017-2023 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nifcloud.mbaas.core.helper.NCMBInBackgroundTestHelper
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.skyscreamer.jsonassert.JSONAssert
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.test.assertFails

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27), manifest = Config.NONE)
class NCMBErrorFileTest {
    private var mServer: MockWebServer = MockWebServer()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    /* This folder and the files created in it will be deleted after
     * tests are run, even in the event of failures or exceptions.
    */
    @get:Rule
    var tmpFolder = TemporaryFolder()
    lateinit var tmpFile: File

    @Before
    fun setup() {
        val ncmbDispatcher = NCMBErrorDispatcher()
        mServer.dispatcher = ncmbDispatcher
        mServer.start()
        NCMB.initialize(
            RuntimeEnvironment.application.getApplicationContext(),
            "appKey",
            "cliKey",
            mServer.url("/").toString(),
            "2013-09-01"
        )

        try {
            // Create a temporary file.
            tmpFile = tmpFolder.newFile("tempFile.txt")
            // Write something to it.
            tmpFile.appendText("hello world")
        } catch (ioe: IOException) {
            System.err.println(
                "error creating temporary test file in " +
                        this.javaClass.simpleName
            )
        }
    }

    @Test
    fun fileSaveInBackGround_err413() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val fileObj = NCMBFile("tempFileErr413.txt")
        fileObj.fileData = tmpFile
        inBackgroundHelper.start()
        // ファイルストアへの登録を実施
        fileObj.saveInBackground(NCMBCallback { e, ncmbFile ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbFile"] = ncmbFile
            inBackgroundHelper.release() // ブロックをリリース
        })
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertEquals(NCMBException.FILE_TOO_LARGE, (inBackgroundHelper["e"] as NCMBException).code)
    }

    @Test
    fun fileSave_err413() {
        val fileObj = NCMBFile("tempFileErr413.txt")
        fileObj.fileData = tmpFile
        // ファイルストアへの登録を実施
        val throwable = assertFails { fileObj.save() } as NCMBException
        Assert.assertNull(fileObj.getCreateDate())
        Assert.assertEquals(NCMBException.FILE_TOO_LARGE, throwable.code)
    }

    @Test
    fun fileSaveInBackGround_err415() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val fileObj = NCMBFile("tempFileErr415.txt")
        fileObj.fileData = tmpFile
        inBackgroundHelper.start()
        // ファイルストアへの登録を実施
        fileObj.saveInBackground(NCMBCallback { e, ncmbFile ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbFile"] = ncmbFile
            inBackgroundHelper.release() // ブロックをリリース
        })
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertEquals(NCMBException.UNSUPPORT_MEDIA_TYPE, (inBackgroundHelper["e"] as NCMBException).code)
    }

    @Test
    fun fileSave_err415() {
        val fileObj = NCMBFile("tempFileErr415.txt")
        fileObj.fileData = tmpFile
        // ファイルストアへの登録を実施
        val throwable = assertFails { fileObj.save() } as NCMBException
        Assert.assertNull(fileObj.getCreateDate())
        Assert.assertEquals(NCMBException.UNSUPPORT_MEDIA_TYPE, throwable.code)
    }

    @Test
    fun fileFetch_err404() {
        val fileObj = NCMBFile("tempFileErrE404.txt")
        // ファイルストアへの取得を実施
        val throwable = assertFails { fileObj.fetch() } as NCMBException
        Assert.assertNull(fileObj.fileDownloadByte)
        Assert.assertEquals(NCMBException.DATA_NOT_FOUND, throwable.code)
    }

    @Test
    fun fileFetchInBackGround_err404() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val fileObj = NCMBFile("tempFileErrE404.txt")
        inBackgroundHelper.start()
        // ファイルストアへの登録を実施
        fileObj.fetchInBackground(NCMBCallback { e, ncmbFile ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbFile"] = ncmbFile
            inBackgroundHelper.release() // ブロックをリリース
        })
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(fileObj.fileDownloadByte)
        Assert.assertEquals(NCMBException.DATA_NOT_FOUND, (inBackgroundHelper["e"] as NCMBException).code)
    }

    @Test
    fun setNoFileSave_failed() {
        val fileObj = NCMBFile("tempFileUpdate.txt")
        try {
            fileObj.save()
        }catch(e:NCMBException){
            Assert.assertEquals(e.message,"A file need to be set to upload.")
        }
    }

    @Test
    fun fileUpdateSetFile_failed() {
        val acl = NCMBAcl()
        val fileObj = NCMBFile("tempFile.txt")
        fileObj.fileData = tmpFile
        acl.publicWriteAccess = true
        acl.publicReadAccess = false
        fileObj.setAcl(acl)
        try {
            fileObj.update()
        }catch(e:NCMBException){
            Assert.assertEquals(e.message,"Please do not set file data to update. Use save function instead.")
        }
    }

    @Test
    fun fileUpdateInBackgroundSetFile_failed() {
        val acl = NCMBAcl()
        val fileObj = NCMBFile("tempFile.txt")
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        inBackgroundHelper.start()
        fileObj.fileData = tmpFile
        acl.publicWriteAccess = true
        acl.publicReadAccess = false
        fileObj.setAcl(acl)
        try {
            fileObj.updateInBackground(NCMBCallback { e, ncmbFile ->
                inBackgroundHelper["e"] = e
                inBackgroundHelper["ncmbFile"] = ncmbFile
                inBackgroundHelper.release() // ブロックをリリース
            })
        }catch(e:NCMBException){
            Assert.assertEquals(e.message,"Please do not set file data to update. Use save function instead.")
        }
    }
}
