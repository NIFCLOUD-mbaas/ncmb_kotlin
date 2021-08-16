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
    fun testNCMBObjectDoSearchReal() {
        var applicationKey =  "3c99589bee9dda8184febdf64cdcfe65f84faf3ec5a2b158e477cea807299b30"
        var clientKey = "4f77045784c3d667ccf2557cb31e507a1488e37bf0f88ba042610271f4e3f981"
        NCMB.initialize(RuntimeEnvironment.application.getApplicationContext(),applicationKey, clientKey)
        //TestClassクラスを検索するクエリを作成
        val query = NCMBQuery<NCMBObject>("TestClass")
        query.whereEqualTo("key", "value");
        //objects: List<NCMBObject>
        query.findInBackground (NCMBCallback { e, objects ->
            if (e != null) {
                //エラー時の処理
                println( "検索に失敗しました。エラー:" + e.message)
            } else {
                //成功時の処理
                println("検索に成功しました。")
                if(objects is List<*>) {
                    var i = 0
                    val n = objects.size
                    while (i < n) {
                        val o = objects[i]
                        if(o is NCMBObject) {
                            println(o.getObjectId())
                        }
                        i++
                    }
                }

            }
        })

    }
}