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

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
import org.robolectric.annotation.Config
import org.skyscreamer.jsonassert.JSONAssert
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import androidx.test.core.app.ApplicationProvider


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [27], manifest = Config.NONE)
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
    lateinit var tmpImgFile: File

    @Before
    fun setup() {
        val ncmbDispatcher = NCMBDispatcher("file")
        mServer.dispatcher = ncmbDispatcher
        mServer.start()
        NCMB.initialize(
            ApplicationProvider.getApplicationContext(),
            "appKey",
            "cliKey",
            mServer.url("/").toString(),
            "2013-09-01"
        )

        callbackFlag = false

        try {
            // Create a temporary file.
            tmpFile = tmpFolder.newFile("tempFile.txt")
            // Write something to it.
            tmpFile.appendText("hello world")

            // Create a png file
            tmpImgFile = tmpFolder.newFile("test.png")
            val bitmap = BitmapFactory.decodeFile(tmpImgFile.absolutePath)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            paint.color = Color.BLACK
            paint.textSize = 10F
            canvas.drawText("test", 1F, 1F, paint)
            canvas.save()

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
        Assert.assertEquals("hello world", ncmbFile.fileData!!.readText(Charset.defaultCharset()))
        Assert.assertEquals("hello world", (ncmbFile.mFields.get(NCMBFile.FILE_DATA) as File).readText(Charset.defaultCharset()))
    }

    @Test
    fun fileConstructor_filename_filedata() {
        val ncmbFile = NCMBFile("testfile.txt", tmpFile)
        Assert.assertEquals("testfile.txt", ncmbFile.fileName)
        Assert.assertEquals("hello world", ncmbFile.fileData!!.readText(Charset.defaultCharset()))
        Assert.assertEquals("hello world", (ncmbFile.mFields.get(NCMBFile.FILE_DATA) as File).readText(Charset.defaultCharset()))
    }

    @Test
    fun fileConstructor_filename() {
        val ncmbFile = NCMBFile("testfile.txt")
        Assert.assertEquals("testfile.txt", ncmbFile.fileName)
    }

    @Test
    fun fileSaveInBackGround_success() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val fileObj = NCMBFile("tempFile.txt")
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
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals((inBackgroundHelper["ncmbFile"] as NCMBFile).fileName,"tempFile.txt")
        val date: Date = NCMBDateFormat.getIso8601().parse("2022-02-03T11:28:30.348Z")!!
        Assert.assertNotNull((inBackgroundHelper["ncmbFile"] as NCMBFile).getCreateDate())
        Assert.assertEquals((inBackgroundHelper["ncmbFile"] as NCMBFile).getCreateDate(),date)
    }

    @Test
    fun fileSaveWithSetACL_success() {
        val acl = NCMBAcl()
        val fileObj = NCMBFile("tempFile.txt")
        fileObj.fileData = tmpFile
        acl.publicWriteAccess = true
        acl.publicReadAccess = false
        fileObj.setAcl(acl)
        fileObj.save()
        val date: Date = NCMBDateFormat.getIso8601().parse("2022-02-03T11:28:30.348Z")!!
        Assert.assertEquals(fileObj.getCreateDate(), date)
        JSONAssert.assertEquals(fileObj.getAcl().toJson(), acl.toJson(), false)
    }

    @Test
    fun fileUpdateInBackGround_success() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val fileObj = NCMBFile("tempFile.txt")
        val acl = NCMBAcl()
        acl.publicWriteAccess = false
        acl.publicReadAccess = true
        fileObj.setAcl(acl)
        inBackgroundHelper.start()
        // ファイルストアへの登録を実施
        fileObj.updateInBackground(NCMBCallback { e, ncmbFile ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbFile"] = ncmbFile
            inBackgroundHelper.release() // ブロックをリリース
        })
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        val date: Date = NCMBDateFormat.getIso8601().parse("2014-06-04T11:28:30.348Z")!!
        Assert.assertNotNull((inBackgroundHelper["ncmbFile"] as NCMBFile).getUpdateDate())
        Assert.assertEquals((inBackgroundHelper["ncmbFile"] as NCMBFile).getUpdateDate(),date)
        JSONAssert.assertEquals((inBackgroundHelper["ncmbFile"] as NCMBFile).getAcl().toJson(), acl.toJson(), false)
    }


    @Test
    fun fileUpdateACL_success() {
        val acl = NCMBAcl()
        val fileObj = NCMBFile("tempFile.txt")
        acl.publicWriteAccess = false
        acl.publicReadAccess = true
        fileObj.setAcl(acl)
        fileObj.update()
        val date: Date = NCMBDateFormat.getIso8601().parse("2014-06-04T11:28:30.348Z")!!
        Assert.assertEquals(fileObj.getUpdateDate(), date)
        JSONAssert.assertEquals(fileObj.getAcl().toJson(), acl.toJson(), false)
    }

    @Test
    fun filePNGSaveInBackGround_success() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val fileObj = NCMBFile("test.png")
        fileObj.fileData = tmpImgFile
        inBackgroundHelper.start()
        // ファイルストアへの登録を実施
        fileObj.saveInBackground(NCMBCallback { e, ncmbFile ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbFile"] = ncmbFile
            inBackgroundHelper.release() // ブロックをリリース
        })
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals((inBackgroundHelper["ncmbFile"] as NCMBFile).fileName,"test.png")
        val date: Date = NCMBDateFormat.getIso8601().parse("2022-02-03T11:28:30.348Z")!!
        Assert.assertNotNull((inBackgroundHelper["ncmbFile"] as NCMBFile).getCreateDate())
        Assert.assertEquals((inBackgroundHelper["ncmbFile"] as NCMBFile).getCreateDate(),date)
    }

    @Test
    fun fileSave_success() {
        val fileObj = NCMBFile("tempFile.txt")
        Assert.assertNull(fileObj.getCreateDate())
        fileObj.fileData = tmpFile
        // ファイルストアへの登録を実施
        fileObj.save()
        val date: Date = NCMBDateFormat.getIso8601().parse("2022-02-03T11:28:30.348Z")!!
        Assert.assertEquals(fileObj.getCreateDate(), date)
    }

    @Test
    fun filePNGSave_success() {
        val fileObj = NCMBFile("test.png")
        Assert.assertNull(fileObj.getCreateDate())
        fileObj.fileData = tmpImgFile
        fileObj.save()

        val date: Date = NCMBDateFormat.getIso8601().parse("2022-02-03T11:28:30.348Z")!!
        Assert.assertEquals(fileObj.getCreateDate(), date)
    }

    @Test
    fun fileFetchInBackGround_success() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val fileObj = NCMBFile("tempFileDownload.txt")
        inBackgroundHelper.start()
        // ファイルストアへの登録を実施
        fileObj.fetchInBackground(NCMBCallback { e, ncmbFile ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbFile"] = ncmbFile
            inBackgroundHelper.release() // ブロックをリリース
        })
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        val testFileObj = inBackgroundHelper["ncmbFile"] as NCMBFile
        Assert.assertEquals(testFileObj.fileName,"tempFileDownload.txt")
        //FileByteDataチェック
        Assert.assertNotNull(testFileObj.fileDownloadByte)
        val encodedString = String(testFileObj.fileDownloadByte!!, Charsets.UTF_8)
        Assert.assertEquals(encodedString,"hello world")
    }

    @Test
    fun fileFetch_success() {
        val fileObj = NCMBFile("tempFileDownload.txt")
        // ファイルストアへの登録を実施
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