package com.nifcloud.mbaas.core

import NCMBQuery
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
import java.util.*

//Android環境のベースにテスト実装するため
@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27), manifest = Config.NONE)
class NCMBQueryTest {

    //mockserver preparation
    private var mServer: MockWebServer = MockWebServer()

    //@get:Rule
    //val rule: TestRule = InstantTaskExecutorRule()
    @Before
    fun setup() {

        //mockserver 制御するNCMBDispatcherを指定し、モックサーバを起動
        val ncmbDispatcher = NCMBDispatcher()
        mServer.dispatcher = ncmbDispatcher
        mServer.start()
        NCMB.initialize(
            RuntimeEnvironment.application.getApplicationContext(),
            "appKey",
            "cliKey",
            mServer.url("/").toString(),
            "2013-09-01"
        )
    }

    @Test
    fun testNCMBObject_DoSearchInBackground_() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery<NCMBObject>("TestClass")
        query.whereEqualTo("key", "value");
        val callback = NCMBCallback { e, objects ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["objects"] = objects
            inBackgroundHelper.release() // ブロックをリリース
        }
        inBackgroundHelper.start()
        query.findInBackground (callback)
        inBackgroundHelper.await()
        print("Success searched:  " + ((inBackgroundHelper["objects"] as List<Any>)[0]as NCMBObject).getObjectId() )
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals(((inBackgroundHelper["objects"] as List<Any>)[0]as NCMBObject).getObjectId() , "8FgKqFlH8dZRDrBJ")

    }
}