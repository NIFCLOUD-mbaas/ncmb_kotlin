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

    /**
     * 通信しないテスト
     */
    @Test
    fun no_api_test() {
        //例：put機能など
        val obj = NCMBObject("TestClass")
        obj.put("keyString", "stringValue")
        Assert.assertEquals(obj.get("keyString"), "stringValue")
    }


    /**
     * 通信テスト（同期処理）
     */
    @Test
    fun connection_sync_test() {
        var obj = NCMBObject("TestClass")
        obj.put("key", "value")
        obj = obj.save()
        Assert.assertEquals(obj.getObjectId(), "7FrmPTBKSNtVjajm")
        val date: Date = NCMBDateFormat.getIso8601().parse("2014-06-03T11:28:30.348Z")!!
        Assert.assertEquals(obj.getCreateDate(), date)
    }

    /**
     * 通信テスト（非同期処理）
     */
    @Test
    fun connecction_async_test() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper()
        // ヘルパーの初期化、ヘルパーはテストをするために、スレッド制御し、通信が完了まであえて、Assertをブロック実施
        val obj = NCMBObject("TestClass")
        val callback = NCMBCallback { e, ncmbObj ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbObj"] = ncmbObj
            inBackgroundHelper.release() // ブロックをリリース
        }

        obj.put("key", "value")
        inBackgroundHelper.start()
        obj.saveInBackground(callback)
        inBackgroundHelper.await()

        print("Success saved: ObjectID " + (inBackgroundHelper["ncmbObj"] as NCMBObject).getObjectId() + " | Response data: CreateDate " + (inBackgroundHelper["ncmbObj"] as NCMBObject).getCreateDate())
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).getObjectId(), "7FrmPTBKSNtVjajm")
        val date: Date = NCMBDateFormat.getIso8601().parse("2014-06-03T11:28:30.348Z")!!
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).getCreateDate(), date)
    }


    //DUONG ADD
    @Test
    fun testNCMBObjectPostReal() {
        var applicationKey =  "3c99589bee9dda8184febdf64cdcfe65f84faf3ec5a2b158e477cea807299b30"
        var clientKey = "4f77045784c3d667ccf2557cb31e507a1488e37bf0f88ba042610271f4e3f981"
        NCMB.initialize(RuntimeEnvironment.application.getApplicationContext(),applicationKey, clientKey)
        val obj = NCMBObject("TestClassKotlin")
        obj.put("key1","value1")
        obj.saveInBackground(NCMBCallback { e, ncmbObj ->
            if (e != null) {
                //保存に失敗した場合の処理
                print("保存に失敗しました : " + e.message)
            } else {
                //保存に成功した場合の処理
                val result = ncmbObj as NCMBObject
                print("保存に成功しました ObjectID :" + result.getObjectId())
            }
        })
    }

    @Test
    fun testNCMBObjectDoSearchReal() {
        var applicationKey =  "3c99589bee9dda8184febdf64cdcfe65f84faf3ec5a2b158e477cea807299b30"
        var clientKey = "4f77045784c3d667ccf2557cb31e507a1488e37bf0f88ba042610271f4e3f981"
        NCMB.initialize(RuntimeEnvironment.application.getApplicationContext(),applicationKey, clientKey)
        //GameScoreクラスを検索するクエリを作成
        val query = NCMBQuery<NCMBObject>("GameScore")
        query.whereEqualTo("key", "value");
        query.findInBackground (NCMBCallback { objects, e ->
            if (e != null) {
                //エラー時の処理
                print( "検索に失敗しました。エラー:" + e.message)
            } else {
                //成功時の処理
                print("検索に成功しました。")
//                var i = 0
//                val n = objects.size
//                while (i < n) {
//                    val o = objects[i]
//                    Log.i("NCMB", o.getObjectId())
//                    i++
//                }
            }
        })


        val obj = NCMBObject("TestClassKotlin")
        obj.put("key1","value1")
        obj.saveInBackground(NCMBCallback { e, ncmbObj ->
            if (e != null) {
                //保存に失敗した場合の処理
                print("保存に失敗しました : " + e.message)
            } else {
                //保存に成功した場合の処理
                val result = ncmbObj as NCMBObject
                print("保存に成功しました ObjectID :" + result.getObjectId())
            }
        })
    }
}