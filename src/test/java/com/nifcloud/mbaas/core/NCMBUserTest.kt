/*
 * Copyright 2017-2021 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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
import com.nifcloud.mbaas.core.NCMBDateFormat.getIso8601
import com.nifcloud.mbaas.core.helper.NCMBInBackgroundTestHelper
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.model.MultipleFailureException.assertEmpty
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.*


/**
 * 主に通信を行う自動化テストクラス
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27), manifest = Config.NONE)
class NCMBUserTest {

    private var mServer: MockWebServer = MockWebServer()
    //Todo background method
    //private var callbackFlag = false

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()
    @Before
    fun setup() {
        var ncmbDispatcher = NCMBDispatcher()
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
    @Throws(Exception::class)
    fun update() {
        val user = NCMBUser()
        user.setObjectId("dummyUserId")
        user.put("key", "value")
        user.save()
        val date: Date = getIso8601().parse("2014-06-04T11:28:30.348Z")
        Assert.assertEquals(user.getUpdateDate(), date)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun login_no_argument() {
        val user = NCMBUser()
        user.userName = "Ncmb Tarou"
        user.password = "dummyPassword"
        user.login()
        //Assert.assertEquals("dummyObjectId", user.getObjectId())
        Assert.assertEquals("Ncmb Tarou", user.userName)
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.SESSION_TOKEN)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun login() {
        val user: NCMBUser = NCMBUser().login("Ncmb Tarou", "dummyPassword")
        Assert.assertEquals("dummyObjectId", user.getObjectId())
        Assert.assertEquals("2013-08-30T05:32:03.868Z", user.mFields.get("updateDate"))
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", user.mFields.get("sessionToken"))
        Assert.assertEquals("Ncmb Tarou", user.userName)
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.SESSION_TOKEN)
    }

    /**
     * - 内容：CurrentUserの情報を確認する。
     * ユーザーオブジェクトID、ユーザー名、Saveを行ってからセッショントークンの変更がないこと。
     *
     * - 結果：セッショントークンが変更されない
     */
    @Test
    @Throws(java.lang.Exception::class)
    fun login_and_getCurrentUser() {
        val user: NCMBUser = NCMBUser().login("Ncmb Tarou", "dummyPassword")
        Assert.assertEquals("dummyObjectId", NCMBUser().getCurrentUser().getObjectId())
        Assert.assertEquals("Ncmb Tarou", NCMBUser().getCurrentUser().userName)
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.SESSION_TOKEN)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun loginInBackground_no_argument() {
        val user = NCMBUser()
        user.userName = "Ncmb Tarou"
        user.password = "dummyPassword"
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val callback = NCMBCallback { e, ncmbUser ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbUser"] = ncmbUser
            inBackgroundHelper.release() // ブロックをリリース
        }

        inBackgroundHelper.start()
        user.loginInBackground(callback)
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals("dummyObjectId", (inBackgroundHelper["ncmbUser"] as NCMBUser).getObjectId())
        Assert.assertEquals("Ncmb Tarou", (inBackgroundHelper["ncmbUser"] as NCMBUser).userName)
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.SESSION_TOKEN)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun loginInBackground() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val callback = NCMBCallback { e, ncmbUser ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbUser"] = ncmbUser
            inBackgroundHelper.release() // ブロックをリリース
        }

        inBackgroundHelper.start()
        NCMBUser().loginInBackground("Ncmb Tarou", "dummyPassword", callback)
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals("dummyObjectId", (inBackgroundHelper["ncmbUser"] as NCMBUser).getObjectId())
        Assert.assertEquals("Ncmb Tarou", (inBackgroundHelper["ncmbUser"] as NCMBUser).userName)
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.SESSION_TOKEN)
    }

    /**
     * - 内容：loginInBackground 後の CurrentUserの情報を確認する。
     * ユーザーオブジェクトID、ユーザー名、Saveを行ってからセッショントークンの変更がないこと。
     *
     * - 結果：CurrentUserが変更される
     */
    @Test
    @Throws(java.lang.Exception::class)
    fun loginInBackground_and_getCurrentUser() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val callback = NCMBCallback { e, ncmbUser ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbUser"] = ncmbUser
            inBackgroundHelper.release() // ブロックをリリース
        }
        inBackgroundHelper.start()
        NCMBUser().loginInBackground("Ncmb Tarou", "dummyPassword", callback)
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals("dummyObjectId", NCMBUser().getCurrentUser().getObjectId())
        Assert.assertEquals("Ncmb Tarou", NCMBUser().getCurrentUser().userName)
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.SESSION_TOKEN)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun fetch() {
        val user = NCMBUser()
        user.setObjectId("dummyUserId")
        user.fetch()
        Assert.assertEquals("Ncmb Tarou", user.mFields.get("userName"))
        Assert.assertEquals("dummySessionToken", user.mFields.get("sessionToken"))
        Assert.assertEquals("Ncmb Tarou", user.userName)
        Assert.assertEquals("dummySessionToken", NCMB.SESSION_TOKEN)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun fetchInBackground() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val callback = NCMBCallback { e, ncmbUser ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbUser"] = ncmbUser
            inBackgroundHelper.release() // ブロックをリリース
        }
        val user = NCMBUser()
        user.setObjectId("dummyUserId")
        inBackgroundHelper.start()
        user.fetchInBackground(callback)
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])

        Assert.assertEquals("Ncmb Tarou", (inBackgroundHelper["ncmbUser"] as NCMBUser).userName)
        Assert.assertEquals("dummySessionToken", NCMB.SESSION_TOKEN)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun delete_current_user() {
        NCMBUser().login("Ncmb Tarou", "dummyPassword")
        Assert.assertEquals("dummyObjectId", NCMBUser().getCurrentUser().getObjectId())
        val user: NCMBUser = NCMBUser().getCurrentUser()
        user.delete()
        Assert.assertNull(NCMB.SESSION_TOKEN)
        Assert.assertNull(NCMB.USER_ID)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun deleteInBackground_current_user() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val logincallback = NCMBCallback { e, ncmbUser ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbUser"] = ncmbUser
            inBackgroundHelper.release() // ブロックをリリース
        }
        inBackgroundHelper.start()
        NCMBUser().loginInBackground("Ncmb Tarou", "dummyPassword", logincallback)
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])

        val deleteCallback = NCMBCallback { e, ncmbUser ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbUser"] = ncmbUser
            inBackgroundHelper.release() // ブロックをリリース
        }
        val user: NCMBUser = NCMBUser().getCurrentUser()
        user.deleteInBackground(deleteCallback)
        Assert.assertNull(NCMB.SESSION_TOKEN)
        Assert.assertNull(NCMB.USER_ID)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun delete_not_current_user() {
        NCMBUser().login("Ncmb Tarou", "dummyPassword")
        Assert.assertEquals("dummyObjectId", NCMBUser().getCurrentUser().getObjectId())
        val user = NCMBUser()
        user.setObjectId("notCurrentUserId")
        user.delete()
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", NCMB.SESSION_TOKEN)
        Assert.assertEquals("dummyObjectId", NCMB.USER_ID)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun signUp_no_argument() {
        val user = NCMBUser()
        user.userName = "Ncmb Tarou"
        user.password = "Ncmbtarou"
        user.signUp()
        Assert.assertEquals("dummyObjectId", user.mFields.get("objectId"))
        Assert.assertEquals("Ncmb Tarou", user.mFields.get("userName"))
        Assert.assertEquals("dummySessionToken", user.mFields.get("sessionToken"))
        Assert.assertEquals("dummyObjectId", user.getObjectId())
        Assert.assertEquals("Ncmb Tarou", user.userName)
        Assert.assertEquals("dummySessionToken", NCMB.SESSION_TOKEN)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun signUp() {
        val user = NCMBUser()
        user.signUp("Ncmb Tarou", "Ncmbtarou")
        Assert.assertEquals("dummyObjectId", user.getObjectId())
        Assert.assertEquals("Ncmb Tarou", user.userName)
        Assert.assertEquals("dummySessionToken", NCMB.SESSION_TOKEN)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun signUpInBackground_no_argument() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val callback = NCMBCallback { e, ncmbUser ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbUser"] = ncmbUser
            inBackgroundHelper.release() // ブロックをリリース
        }
        inBackgroundHelper.start()
        NCMBUser().signUpInBackground("Ncmb Tarou","Ncmbtarou", callback)
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals("dummyObjectId", (inBackgroundHelper["ncmbUser"] as NCMBUser).getObjectId())
        Assert.assertEquals("Ncmb Tarou", (inBackgroundHelper["ncmbUser"] as NCMBUser).userName)
        Assert.assertEquals("dummySessionToken", NCMB.SESSION_TOKEN)
    }
    @Test
    @Throws(java.lang.Exception::class)
    fun signUpInBackground() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val callback = NCMBCallback { e, ncmbUser ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbUser"] = ncmbUser
            inBackgroundHelper.release() // ブロックをリリース
        }
        val user = NCMBUser()
        user.userName = "Ncmb Tarou"
        user.password = "Ncmbtarou"
        inBackgroundHelper.start()
        user.signUpInBackground(callback)
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertEquals("dummyObjectId", (inBackgroundHelper["ncmbUser"] as NCMBUser).getObjectId())
        Assert.assertEquals("Ncmb Tarou", (inBackgroundHelper["ncmbUser"] as NCMBUser).userName)
        Assert.assertEquals("dummySessionToken", NCMB.SESSION_TOKEN)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun logout() {
        val loginUser: NCMBUser = NCMBUser().login("Ncmb Tarou", "dummyPassword")
        Assert.assertEquals("2013-08-30T05:32:03.868Z", loginUser.mFields.get("updateDate"))
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", loginUser.mFields.get("sessionToken"))
        loginUser.logout()
        Assert.assertNull(NCMB.SESSION_TOKEN)
        Assert.assertNull(NCMB.USER_ID)
        Assert.assertEquals("2013-08-30T05:32:03.868Z", loginUser.mFields.get("updateDate"))
        Assert.assertNull(loginUser.sessionToken)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun logout_check_current_user() {
        val loginUser: NCMBUser = NCMBUser().login("Ncmb Tarou", "dummyPassword")
        val currentUser: NCMBUser = loginUser.getCurrentUser()
        Assert.assertNotNull(currentUser.getObjectId())
        Assert.assertNotNull(currentUser.userName)
        loginUser.logout()
        val logoutUser = NCMBUser().getCurrentUser()
        Assert.assertNull(logoutUser.getObjectId())
        Assert.assertNull(NCMB.SESSION_TOKEN)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun login_to_logout_to_login() {
        val loginUser: NCMBUser = NCMBUser().login("Ncmb Tarou", "dummyPassword")
        loginUser.logout()
        Assert.assertEquals("2013-08-30T05:32:03.868Z", loginUser.mFields.get("updateDate"))
        Assert.assertNull(loginUser.sessionToken)
        val loginUser2: NCMBUser = NCMBUser().login("Ncmb Tarou", "dummyPassword")
        Assert.assertEquals("2013-08-30T05:32:03.868Z", loginUser2.mFields.get("updateDate"))
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", loginUser2.sessionToken)
        Assert.assertEquals("dummyObjectId", loginUser2.getObjectId())
        Assert.assertEquals("Ncmb Tarou", loginUser2.userName)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun login_to_logout_to_login_current_user() {
        val loginUser: NCMBUser = NCMBUser().login("Ncmb Tarou", "dummyPassword")
        loginUser.logout()
        val currentUser: NCMBUser = loginUser.getCurrentUser()
        Assert.assertNull(currentUser.getObjectId())
        Assert.assertNull(loginUser.sessionToken)
        NCMBUser().login("Ncmb Tarou", "dummyPassword")
        val currentUser2: NCMBUser = NCMBUser().getCurrentUser()
        Assert.assertEquals("dummyObjectId", currentUser2.getObjectId())
        Assert.assertEquals("2013-08-30T05:32:03.868Z", currentUser2.mFields.get("updateDate"))
        Assert.assertEquals("ebDH8TtmLoygzjqjaI4EWFfxc", currentUser2.sessionToken)
        Assert.assertEquals("Ncmb Tarou", currentUser2.userName)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun logoutInBackground() {
        val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
        val logincallback = NCMBCallback { e, ncmbUser ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbUser"] = ncmbUser
            inBackgroundHelper.release() // ブロックをリリース
        }
        inBackgroundHelper.start()
        NCMBUser().loginInBackground("Ncmb Tarou", "dummyPassword", logincallback)
        inBackgroundHelper.await()

        val logoutCallback = NCMBCallback { e, ncmbUser ->
            inBackgroundHelper["e"] = e
            inBackgroundHelper["ncmbLogoutUser"] = ncmbUser
            inBackgroundHelper.release() // ブロックをリリース
        }
        val user = NCMBUser()
        inBackgroundHelper.start()
        user.logoutInBackground(logoutCallback)
        inBackgroundHelper.await()
        Assert.assertTrue(inBackgroundHelper.isCalledRelease())
        Assert.assertNull(inBackgroundHelper["e"])
        Assert.assertNull((inBackgroundHelper["ncmbLogoutUser"] as NCMBUser).getCurrentUser().getObjectId())
        Assert.assertNull(NCMB.SESSION_TOKEN)
        Assert.assertNull(NCMB.USER_ID)
    }
}
