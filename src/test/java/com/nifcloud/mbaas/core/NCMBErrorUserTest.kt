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
import com.nifcloud.mbaas.core.NCMB
import com.nifcloud.mbaas.core.NCMBErrorDispatcher
import com.nifcloud.mbaas.core.NCMBException
import com.nifcloud.mbaas.core.NCMBUser
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
import kotlin.test.assertFails


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

    /**
     * - 内容：username　が間違っているときの　loginInBackground 後の CurrentUserの情報を確認する。
     * login失敗後、CurrentUserの更新がないこと。
     *
     * - 結果：CurrentUserが変更されない
     */
    @Test
    @Throws(java.lang.Exception::class)
    fun loginInBackground_invalid_username() {
        NCMBUser().clearCachedCurrentUser()
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val callback = NCMBCallback { e, ncmbUser ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbUser"] = ncmbUser
            inBackgroundHelper.release() // ブロックをリリース
        }
        inBackgroundHelper.start()
        NCMBUser().loginInBackground("invalidUser", "Password", callback)
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(NCMBUser().getCurrentUser().getObjectId())
        Assert.assertEquals(NCMBException.AUTH_FAILURE, (inBackgroundHelper["e"] as NCMBException).code)
    }

    /**
     * - 内容：password　が間違っているときの　loginInBackground 後の CurrentUserの情報を確認する。
     * login失敗後、CurrentUserの更新がないこと。
     *
     * - 結果：CurrentUserが変更されない
     */
    @Test
    @Throws(java.lang.Exception::class)
    fun signUpInBackground_invalid_password() {
        NCMBUser().clearCachedCurrentUser()
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val callback = NCMBCallback { e, ncmbUser ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbUser"] = ncmbUser
            inBackgroundHelper.release() // ブロックをリリース
        }
        val user = NCMBUser()
        user.userName = "duplicateUser"
        user.password = "Password"
        inBackgroundHelper.start()
        user.signUpInBackground(callback)
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(NCMBUser().getCurrentUser().getObjectId())
        Assert.assertEquals(NCMBException.DUPLICATE_VALUE, (inBackgroundHelper["e"] as NCMBException).code)
    }

    /**
     * - 内容：password　がnullの時 signUpInBackground 後の CurrentUserの情報を確認する。
     * login失敗後、CurrentUserの更新がないこと。
     *
     * - 結果：CurrentUserが変更されない
     */
    @Test
    @Throws(java.lang.Exception::class)
    fun signUpInBackground_invalid_null_password() {
        NCMBUser().clearCachedCurrentUser()
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val callback = NCMBCallback { e, ncmbUser ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbUser"] = ncmbUser
            inBackgroundHelper.release() // ブロックをリリース
        }
        val user = NCMBUser()
        user.userName = "duplicateUser"
        inBackgroundHelper.start()
        try {
            user.signUpInBackground(callback)
        }
        catch (e:NCMBException){
            Assert.assertEquals(NCMBException.REQUIRED, e.code)
        }
        inBackgroundHelper.await()
        Assert.assertFalse(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(NCMBUser().getCurrentUser().getObjectId())
    }

    /**
     * - 内容：password　がnullの時 signUpInBackground 後の CurrentUserの情報を確認する。
     * login失敗後、CurrentUserの更新がないこと。
     *
     * - 結果：CurrentUserが変更されない
     */
    @Test
    @Throws(java.lang.Exception::class)
    fun logInBackground_invalid_null_password() {
        NCMBUser().clearCachedCurrentUser()
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val callback = NCMBCallback { e, ncmbUser ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbUser"] = ncmbUser
            inBackgroundHelper.release() // ブロックをリリース
        }
        val user = NCMBUser()
        user.userName = "duplicateUser"
        inBackgroundHelper.start()
        try {
            user.loginInBackground(callback)
        }
        catch (e:NCMBException){
            Assert.assertEquals(NCMBException.REQUIRED, e.code)
        }
        inBackgroundHelper.await()
        Assert.assertFalse(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(NCMBUser().getCurrentUser().getObjectId())
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun login_invalid_null_password() {
        NCMBUser().clearCachedCurrentUser()
        val user = NCMBUser()
        user.userName = "duplicateUser"
        try {
            user.login()
        }
        catch (e:NCMBException){
            Assert.assertEquals(NCMBException.REQUIRED, e.code)
        }
        Assert.assertNull(NCMBUser().getCurrentUser().getObjectId())
    }

    @Test
    fun logout_failure_connect_error() {
        NCMB.SESSION_TOKEN = "testToken"
        NCMB.USER_ID = "TestId"
        val user = NCMBUser()
        try {
            user.logout()
        } catch (e: NCMBException) {
            Assert.assertEquals(NCMBException.INTERNAL_SERVER_ERROR, e.code)
        }
        Assert.assertNull(NCMBUser().getCurrentUser().getObjectId())
        Assert.assertNull(NCMB.SESSION_TOKEN)
        Assert.assertNull(NCMB.USER_ID)
    }

}
