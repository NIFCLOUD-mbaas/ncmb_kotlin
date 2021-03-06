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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nifcloud.mbaas.core.helper.NCMBInBackgroundTestHelper
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.*


@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27), manifest = Config.NONE)
class NCMBFileTest {

    private var mServer: MockWebServer = MockWebServer()
    private var callbackFlag = false

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
        val ncmbDispatcher = NCMBDispatcher("file")
        mServer.dispatcher = ncmbDispatcher
        mServer.start()
        NCMB.initialize(
            RuntimeEnvironment.application.getApplicationContext(),
            "appKey",
            "cliKey",
            mServer.url("/").toString(),
            "2013-09-01"
        )

        callbackFlag = false;

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
    fun fileName_set_Get_test() {
        val ncmbFile = NCMBFile()
        ncmbFile.fileName = "testfile.txt"
        Assert.assertEquals("testfile.txt", ncmbFile.fileName)
    }


    @Test
    fun fileData_set_Get_test() {
        val ncmbFile = NCMBFile()
        ncmbFile.fileData = tmpFile
        Assert.assertEquals("hello world", ncmbFile.fileData!!.readText(Charset.defaultCharset()).toString())
        Assert.assertEquals("hello world", (ncmbFile.mFields.get(NCMBFile.FILE_DATA) as File).readText(Charset.defaultCharset()).toString())
    }

    @Test
    fun fileConstructor_filename_filedata() {
        val ncmbFile = NCMBFile("testfile.txt", tmpFile)
        Assert.assertEquals("testfile.txt", ncmbFile.fileName)
        Assert.assertEquals("hello world", ncmbFile.fileData!!.readText(Charset.defaultCharset()).toString())
        Assert.assertEquals("hello world", (ncmbFile.mFields.get(NCMBFile.FILE_DATA) as File).readText(Charset.defaultCharset()).toString())
    }

    @Test
    fun fileConstructor_filename() {
        val ncmbFile = NCMBFile("testfile.txt")
        Assert.assertEquals("testfile.txt", ncmbFile.fileName)
    }

    @Test
    fun fileSaveInBackGround_success() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ????????????????????????
        val fileObj = NCMBFile("tempFile.txt")
        fileObj.fileData = tmpFile
        inBackgroundHelper.start()
        // ??????????????????????????????????????????
        fileObj.saveInBackground(NCMBCallback { e, ncmbFile ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbFile"] = ncmbFile
            inBackgroundHelper.release() // ???????????????????????????
        })
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals((inBackgroundHelper["ncmbFile"] as NCMBFile).fileName,"tempFile.txt")
        val date: Date = NCMBDateFormat.getIso8601().parse("2022-02-03T11:28:30.348Z")!!
        Assert.assertNotNull((inBackgroundHelper["ncmbFile"] as NCMBFile).getCreateDate())
        Assert.assertEquals((inBackgroundHelper["ncmbFile"] as NCMBFile).getCreateDate(),date)
    }

    @Test
    fun fileSave_success() {
        val fileObj = NCMBFile("tempFile.txt")
        Assert.assertNull(fileObj.getCreateDate())
        fileObj.fileData = tmpFile
        // ??????????????????????????????????????????
        fileObj.save()
        val date: Date = NCMBDateFormat.getIso8601().parse("2022-02-03T11:28:30.348Z")!!
        Assert.assertEquals(fileObj.getCreateDate(), date)
    }

    @Test
    fun fileFetchInBackGround_success() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ????????????????????????
        val fileObj = NCMBFile("tempFileDownload.txt")
        inBackgroundHelper.start()
        // ??????????????????????????????????????????
        fileObj.fetchInBackground(NCMBCallback { e, ncmbFile ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbFile"] = ncmbFile
            inBackgroundHelper.release() // ???????????????????????????
        })
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        val testFileObj = inBackgroundHelper["ncmbFile"] as NCMBFile
        Assert.assertEquals(testFileObj.fileName,"tempFileDownload.txt")
        //FileByteData????????????
        Assert.assertNotNull(testFileObj.fileDownloadByte)
        val encodedString = String(testFileObj.fileDownloadByte!!, Charsets.UTF_8)
        Assert.assertEquals(encodedString,"hello world")
    }

    @Test
    fun fileFetch_success() {
        val fileObj = NCMBFile("tempFileDownload.txt")
        // ??????????????????????????????????????????
        try {
            fileObj.fetch()
        } catch (e: NCMBException){
            println("Error occured:" + e.message)
        }
        Assert.assertNotNull(fileObj.fileDownloadByte)
        val encodedString = String(fileObj.fileDownloadByte!!, Charsets.UTF_8)
        Assert.assertEquals(encodedString,"hello world")
    }

}