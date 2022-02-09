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


@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27), manifest = Config.NONE)
class NCMBFileTest {

    private var mServer: MockWebServer = MockWebServer()
    private var callbackFlag = false

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()
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
        ncmbFile.fileData = "This is a test file data".toByteArray(Charsets.UTF_8)
        Assert.assertEquals("This is a test file data".toByteArray(Charsets.UTF_8).contentToString(), ncmbFile.fileData!!.contentToString())
        Assert.assertEquals("This is a test file data".toByteArray(Charsets.UTF_8).contentToString(), (ncmbFile.mFields.get(NCMBFile.FILE_DATA) as ByteArray).contentToString())
    }

    @Test
    fun fileConstructor_filename_filedata() {
        val data = "This is a test file data".toByteArray(Charsets.UTF_8)
        val ncmbFile = NCMBFile("testfile.txt", data)
        Assert.assertEquals("testfile.txt", ncmbFile.fileName)
        Assert.assertEquals("This is a test file data".toByteArray(Charsets.UTF_8).contentToString(), ncmbFile.fileData!!.contentToString())
        Assert.assertEquals("This is a test file data".toByteArray(Charsets.UTF_8).contentToString(), (ncmbFile.mFields.get(NCMBFile.FILE_DATA) as ByteArray).contentToString())
    }

    @Test
    fun fileConstructor_filename() {
        val ncmbFile = NCMBFile("testfile.txt")
        Assert.assertEquals("testfile.txt", ncmbFile.fileName)
    }

    @Test
    fun fileSaveInBG_success() {
        var applicationKey =  "APPKEY"
        var clientKey = "CLIKEY"
        NCMB.initialize(RuntimeEnvironment.application.getApplicationContext(),applicationKey, clientKey)
//// クラスのNCMBObjectを作成
//        val obj = NCMBObject("TestClass")
//// オブジェクトの値を設定
//        obj.put("message", "Hello, NCMB!")
//        obj.saveInBackground(NCMBCallback { e, ncmbObj ->
//            if (e != null) {
//                //保存に失敗した場合の処理
//                print("保存に失敗しました : " + e.message)
//            } else {
//                //保存に成功した場合の処理
//                val result = ncmbObj as NCMBObject
//                print("保存に成功しました ObjectID :" + result.getObjectId())
//            }
//        })

        val file = NCMBFile("filename1.txt")
        file.fileData = "This is a test file data".toByteArray(Charsets.UTF_8)
        // ファイルストアへの登録を実施
        file.saveInBackground(NCMBCallback { e, ncmbFile ->
            if (e != null) {
                //保存に失敗した場合の処理
                print("保存に失敗しました : " + e.message)
            } else {
                val fileObj = ncmbFile  as NCMBFile
                //保存に成功した場合の処理
                print("保存に成功しました fileName:" + fileObj.fileName)
            }
        })
    }

}