package com.nifcloud.mbaas.core

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nifcloud.mbaas.core.NCMBDateFormat.getIso8601
import com.nifcloud.mbaas.core.helper.NCMBInBackgroundTestHelper
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper
import java.lang.AssertionError
import java.text.SimpleDateFormat
import java.util.*


/**
 * 主に通信を行う自動化テストクラス
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27), manifest = Config.NONE)
class NCMBObjectTest {

    private var mServer: MockWebServer = MockWebServer()
    private var callbackFlag = false

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()
    @Before
    fun setup() {
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

        callbackFlag = false;
    }

    /**
     * putテスト
     */
    @Test
    fun put_string_test() {
        val obj = NCMBObject("TestClass")
        obj.put("keyString", "stringValue")
        Assert.assertEquals(obj.get("keyString"), "stringValue")
    }

    /**
     * putテスト
     */
    @Test
    fun put_int_test_direct() {
        val obj = NCMBBase()
        obj.put("keyNumberInt", 1234)
        Assert.assertEquals(obj.get("keyNumberInt"), 1234)
    }

    /**
     * putテスト
     */
    @Test
    fun put_double_test_direct() {
        val obj = NCMBBase()
        obj.put("keyNumberDouble", 1234.33)
        Assert.assertEquals(obj.get("keyNumberDouble"), 1234.33)
    }

    @Test
    @Throws(NCMBException::class)
    fun save_object_with_post_data_save_success() {
        var obj = NCMBObject("TestClass")
        obj.put("key", "value")
        obj = obj.save()
        Assert.assertEquals(obj.getObjectId(), "7FrmPTBKSNtVjajm")
    }

//    Todo missing save test
//    @Test
//    @Throws(NCMBException::class)
//    fun NCMBObjectSaveWithPostDataSaveFailure() {
//        var obj = NCMBObject("DummyClass")
//        obj.put("key", "value")
//        obj = obj.save()
//        print(obj)
//        print("Success saved: ObjectID " + obj.getObjectId() + " | Response data: CreateDate " + obj.getCreateDate())
//    }

    @Test
    fun saveInBackground_object_with_post_data_save_success() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
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
        val date: Date = getIso8601().parse("2014-06-03T11:28:30.348Z")!!
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).getCreateDate(), date)
    }

//    Todo save inBackground test
//    fun saveInBackground_object_with_post_data_save_failure() {
//        val obj = NCMBObject("DummyClass")
//        val callback = NCMBCallback { e, obj ->
//            if (e != null) {
//                //保存失敗
//                print("Failed" + e.message)
//            } else {
//                //保存成功
//                if(obj is NCMBObject) {
//                    print("Success saved: ObjectID " + obj.getObjectId()+ " | Response data: CreateDate " + obj.getCreateDate())
//                }
//            }
//        }
//        obj.put("key", "value")
//        obj.saveInBackground(callback)
//    }


    @Test
    fun saveInBackground_object_with_post_data_save_success_callback_direct() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val obj = NCMBObject("TestClass")
        obj.put("key", "value")
        inBackgroundHelper.start()
        obj.saveInBackground(NCMBCallback { e, ncmbObj ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbObj"] = ncmbObj
            inBackgroundHelper.release() // ブロックをリリース
        })
        inBackgroundHelper.await()
        print("Success saved: ObjectID " + (inBackgroundHelper["ncmbObj"] as NCMBObject).getObjectId() + " | Response data: CreateDate " + (inBackgroundHelper["ncmbObj"] as NCMBObject).getCreateDate())
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).getObjectId(), "7FrmPTBKSNtVjajm")
        val date: Date = getIso8601().parse("2014-06-03T11:28:30.348Z")!!
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).getCreateDate(), date)
    }

    @Test
    @Throws(Exception::class)
    fun update_object_with_update_value() {
        val obj = NCMBObject("TestClass")
        obj.setObjectId("updateTestObjectId")
        obj.put("updateKey", "updateValue")
        val result = obj.save()
        Assert.assertEquals(obj.get("updateKey"),"updateValue")
        val date: Date = getIso8601().parse("2014-06-04T11:28:30.348Z")!!
        Assert.assertEquals(result.getUpdateDate(),date)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun update_object_non_update_value() {
        val obj = NCMBObject("TestClass")
        obj.setObjectId("updateTestObjectId")
        val result = obj.save()
        val date: Date = getIso8601().parse("2014-06-04T11:28:30.348Z")!!
        Assert.assertEquals(result.getUpdateDate(),date)
    }

    @Test
    @Throws(Exception::class)
    fun update_object_with_multiple_update_value() {
        val obj = NCMBObject("TestClass")
        obj.setObjectId("updateTestObjectId")
        obj.put("updateKey", "updateValue")
        obj.put("updateKey2", "updateValue2")
        val result = obj.save()
        Assert.assertEquals(obj.get("updateKey"),"updateValue")
        Assert.assertEquals(obj.get("updateKey2"),"updateValue2")
        val date: Date = getIso8601().parse("2014-06-04T11:28:30.348Z")!!
        Assert.assertEquals(result.getUpdateDate(),date)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun update_object_in_background_with_update_value() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val obj = NCMBObject("TestClass")
        obj.setObjectId("updateTestObjectId")
        obj.put("updateKey", "updateValue")
        inBackgroundHelper.start()
        obj.saveInBackground(NCMBCallback { e, ncmbObj ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbObj"] = ncmbObj
            inBackgroundHelper.release() // ブロックをリリース
        })
        inBackgroundHelper.await()
        print("Success Updated: Response data: UpdateDate " + (inBackgroundHelper["ncmbObj"] as NCMBObject).getUpdateDate())
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).get("updateKey"),"updateValue")
        val date: Date = getIso8601().parse("2014-06-04T11:28:30.348Z")!!
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).getUpdateDate(), date)
    }

    @Test
    @Throws(NCMBException::class)
    fun fetch_object_with_get_data_success() {
        val obj = NCMBObject("TestClass")
        obj.setObjectId("7FrmPTBKSNtVjajm")
        val result = obj.fetch()
        Assert.assertEquals(result.getObjectId(), "7FrmPTBKSNtVjajm")
        Assert.assertEquals(result.get("key"), "value")
    }

    @Test
    fun fetchInBackground_object_with_get_data_success() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val obj = NCMBObject("TestClass")
        val callback = NCMBCallback { e, ncmbObj ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbObj"] = ncmbObj
            inBackgroundHelper.release() // ブロックをリリース
        }
        obj.setObjectId("7FrmPTBKSNtVjajm")
        inBackgroundHelper.start()
        obj.fetchInBackground(callback)
        inBackgroundHelper.await()
        print("Success fetched: ObjectID " + (inBackgroundHelper["ncmbObj"] as NCMBObject).getObjectId() + " | Response data: CreateDate " + (inBackgroundHelper["ncmbObj"] as NCMBObject).getCreateDate() + " | Get Value: " + (inBackgroundHelper["ncmbObj"] as NCMBObject).get("key"))
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).getObjectId(), "7FrmPTBKSNtVjajm")
        val date: Date = getIso8601().parse("2014-06-03T11:28:30.348Z")!!
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).getCreateDate(), date)
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).get("key"),"value")
    }

    @Test
    fun fetchInBackground_object_with_get_data_success_callback_direct() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val obj = NCMBObject("TestClass")
        obj.setObjectId("7FrmPTBKSNtVjajm")
        inBackgroundHelper.start()
        obj.fetchInBackground(NCMBCallback { e, ncmbObj ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbObj"] = ncmbObj
            inBackgroundHelper.release() // ブロックをリリース
        })
        inBackgroundHelper.await()
        print("Success fetched: ObjectID " + (inBackgroundHelper["ncmbObj"] as NCMBObject).getObjectId() + " | Response data: CreateDate " + (inBackgroundHelper["ncmbObj"] as NCMBObject).getCreateDate() + " | Get Value: " + (inBackgroundHelper["ncmbObj"] as NCMBObject).get("key"))
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).getObjectId(), "7FrmPTBKSNtVjajm")
        val date: Date = getIso8601().parse("2014-06-03T11:28:30.348Z")!!
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).getCreateDate(), date)
        Assert.assertEquals((inBackgroundHelper["ncmbObj"] as NCMBObject).get("key"),"value")
    }

    @Test
    @Throws(NCMBException::class)
    fun delete_object_with_data_success() {
        val sut = NCMBObject("TestClass")
        Assert.assertNull(sut.getObjectId())
        try {
            sut.setObjectId("deleteTestObjectId")
            val obj = sut.delete()
            Assert.assertNull(obj)
        } catch (e: NCMBException) {
            Assert.fail("exception raised:" + e.message)
        }
    }

    //Todo delete Tests
//    @Test
//    @Throws(NCMBException::class)
//    fun delete_object_with_data_success_failure() {
//        val sut = NCMBObject("TestClass")
//        try {
//            sut.setObjectId("nonExistId")
//            val obj = sut.delete()
//        } catch (e: NCMBException) {
//            Assert.assertEquals(NCMBException.DATA_NOT_FOUND, e.code)
//        }
//    }
//    @Test
//    fun deleteInBackground_object_with_data_success() {
//        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
//        val obj = NCMBObject("TestClass")
//        val callback = NCMBCallback { e, ncmbObj ->
//            inBackgroundHelper["e"] = e
//            inBackgroundHelper["ncmbObj"] = ncmbObj
//            inBackgroundHelper.release() // ブロックをリリース
//        }
//        obj.setObjectId("7FrmPTBKSNtVjajm")
//        inBackgroundHelper.start()
//        obj.deleteInBackground(callback)
//        inBackgroundHelper.await()
//        print(inBackgroundHelper["e"])
//        print("Success deleted")
//        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
//        //need to fix
//        Assert.assertNull(inBackgroundHelper["e"])
//        Assert.assertNull((inBackgroundHelper["ncmbObj"] as NCMBObject))
//        Assert.assertNull(obj)
//    }
//    @Test
//    fun deleteInBackground_object_with_data_failure() {
//        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
//        val obj = NCMBObject("TestClass")
//        val callback = NCMBCallback { e, ncmbObj ->
//            inBackgroundHelper["e"] = e?.message
//            inBackgroundHelper["ncmbObj"] = ncmbObj
//            inBackgroundHelper.release() // ブロックをリリース
//        }
//        inBackgroundHelper.start()
//        obj.deleteInBackground(callback)
//        inBackgroundHelper.await()
//        print("Failure deleted")
//        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
//        //need to fix
//        print(inBackgroundHelper["e"])
//        Assert.assertNotNull(inBackgroundHelper["e"])
//        //Assert.assertNull((inBackgroundHelper["ncmbObj"] as NCMBObject))
//        Assert.assertEquals(inBackgroundHelper["e"],"Need to set objectID before delete")
//        //Assert.assertNull(obj)
//    }
//    @Test
//    fun deleteInBackground_with_data_success_callback_direct() {
//        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
//        val obj = NCMBObject("TestClass")
//        obj.setObjectId("7FrmPTBKSNtVjajm")
//        inBackgroundHelper.start()
//        obj.deleteInBackground(NCMBCallback { e, ncmbObj ->
//            inBackgroundHelper["e"] = e!!.message
//            inBackgroundHelper["ncmbObj"] = ncmbObj
//            inBackgroundHelper.release() // ブロックをリリース
//        })
//        inBackgroundHelper.await()
//        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
//        //need to fix
//        print(inBackgroundHelper["e"])
//        Assert.assertNotNull(inBackgroundHelper["e"])
//        //Assert.assertNull((inBackgroundHelper["ncmbObj"] as NCMBObject))
//        Assert.assertEquals(inBackgroundHelper["e"]!!,"Need to set objectID before delete")
//        //Assert.assertNull(obj)
//    }
//
//    @Test
//    fun deleteInBackground_object_With_data_failure_callback_direct() {
//        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
//        val obj = NCMBObject("TestClass")
//        obj.setObjectId("7FrmPTBKSNtVjajm")
//        inBackgroundHelper.start()
//        obj.deleteInBackground(NCMBCallback { e, ncmbObj ->
//            inBackgroundHelper["e"] = e!!.message
//            inBackgroundHelper["ncmbObj"] = ncmbObj
//            inBackgroundHelper.release() // ブロックをリリース
//        })
//        inBackgroundHelper.await()
//        print("Failure deleted")
//        inBackgroundHelper.await()
//        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
//        //need to fix
//        print(inBackgroundHelper["e"])
//        Assert.assertNotNull(inBackgroundHelper["e"])
//        //Assert.assertNull((inBackgroundHelper["ncmbObj"] as NCMBObject))
//        Assert.assertEquals(inBackgroundHelper["e"]!!,"Need to set objectID before delete")
//        //Assert.assertNull(obj)
//    }
}
