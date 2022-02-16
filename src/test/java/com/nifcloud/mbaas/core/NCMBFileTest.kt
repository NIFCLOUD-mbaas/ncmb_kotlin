package com.nifcloud.mbaas.core

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
        val fileObj = NCMBFile("tempFile.txt")
        fileObj.fileData = tmpFile
        // ファイルストアへの登録を実施
        fileObj.saveInBackground(NCMBCallback { e, ncmbFile ->
            if (e != null) {
                //保存に失敗した場合の処理
                println("保存に失敗しました : " + e.code + " " + e.message)
            } else {
                val fileObj = ncmbFile  as NCMBFile
                //保存に成功した場合の処理
                println("保存に成功しました fileName:" + fileObj.fileName)
            }
        })
    }

    @Test
    fun fileSave_success() {
        val fileObj = NCMBFile("tempFile.txt")
        fileObj.fileData = tmpFile
        // ファイルストアへの登録を実施
        try {
            fileObj.save()
        }catch (e:NCMBException) {
            println("保存に失敗しました : " + e.code + " " + e.message)
        }
    }

}