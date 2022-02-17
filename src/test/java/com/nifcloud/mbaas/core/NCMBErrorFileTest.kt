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
    fun fileSaveInBackGround_err413001() {
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
    fun fileSave_err413001() {
        val fileObj = NCMBFile("tempFileErr413.txt")
        fileObj.fileData = tmpFile
        // ファイルストアへの登録を実施
        val throwable = assertFails { fileObj.save() } as NCMBException
        Assert.assertNull(fileObj.getCreateDate())
        Assert.assertEquals(NCMBException.FILE_TOO_LARGE, throwable.code)
    }

    @Test
    fun fileSaveInBackGround_err415001() {
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
    fun fileSave_err415001() {
        val fileObj = NCMBFile("tempFileErr415.txt")
        fileObj.fileData = tmpFile
        // ファイルストアへの登録を実施
        val throwable = assertFails { fileObj.save() } as NCMBException
        Assert.assertNull(fileObj.getCreateDate())
        Assert.assertEquals(NCMBException.UNSUPPORT_MEDIA_TYPE, throwable.code)
    }

}
