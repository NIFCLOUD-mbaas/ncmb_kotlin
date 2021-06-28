package com.nifcloud.mbaas.core

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nifcloud.mbaas.core.NCMB
import com.nifcloud.mbaas.core.NCMBErrorDispatcher
import com.nifcloud.mbaas.core.NCMBException
import com.nifcloud.mbaas.core.NCMBUser
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


/**
 * 主に通信を行う自動化テストクラス
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27), manifest = Config.NONE)
class NCMBErrorUserTest {

    private var mServer: MockWebServer = MockWebServer()
    //Todo background method
    //private var callbackFlag = false

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()
    @Before
    fun setup() {
        var ncmbDispatcher = NCMBErrorDispatcher()
        mServer.dispatcher = ncmbDispatcher
        mServer.start()
        NCMB.initialize(
            RuntimeEnvironment.application.getApplicationContext(),
            "appKey",
            "cliKey",
            mServer.url("/").toString(),
            "2013-09-01"
        )
        //Todo background method

//        Robolectric.getBackgroundThreadScheduler().pause();
//        Robolectric.getForegroundThreadScheduler().pause();
//
//        callbackFlag = false;
    }

    @Test
    fun logout_failure_connect_error() {
        val loginUser: NCMBUser = NCMBUser().login("Ncmb Tarou", "dummyPassword")
        val currentUser: NCMBUser = loginUser.getCurrentUser()
        Assert.assertNotNull(currentUser.getObjectId())
        Assert.assertNotNull(currentUser.userName)
        Assert.assertNotNull(NCMB.SESSION_TOKEN)
        val user = NCMBUser()
        try {
            user.logout()
        } catch (e: NCMBException) {
            Assert.assertEquals(NCMBException.INTERNAL_SERVER_ERROR, e.code)
        }
        Assert.assertNull(NCMB.SESSION_TOKEN)
        Assert.assertNull(NCMB.USER_ID)
    }

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
}
