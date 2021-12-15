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
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.skyscreamer.jsonassert.JSONAssert

/**
 * 主に通信を行うNCMBConnectionテストクラス
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(26), manifest = Config.NONE)
class NCMBAclTest {

    private var mServer: MockWebServer = MockWebServer()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        var ncmbDispatcher = NCMBDispatcher("")
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
     * - 内容：Permission を生成
     * - 結果：正しく初期化される
     */
    @Test
    @Throws(java.lang.Exception::class)
    fun create_Permission() {
        val p1 = NCMBAcl.Permission(true, false)
        JSONAssert.assertEquals(JSONObject("{'read':true}"), p1.toJson(), false)
        val p2 = NCMBAcl.Permission(false, true)
        JSONAssert.assertEquals(JSONObject("{'write':true}"), p2.toJson(), false)
        val p3 = NCMBAcl.Permission(true, true)
        JSONAssert.assertEquals(JSONObject("{'read':true, 'write':true}"), p3.toJson(), false)
    }

    /**
     * - 内容：Permission を JSONOBject から生成
     * - 結果：正しく初期化される
     */
    @Test
    @Throws(java.lang.Exception::class)
    fun create_Permission_from_JSON() {
        val jsonRead = JSONObject()
        jsonRead.put("read", true)
        val p1 = NCMBAcl.Permission(jsonRead)
        Assert.assertTrue(p1.readable)
        Assert.assertFalse(p1.writable)
        JSONAssert.assertEquals(JSONObject("{'read':true}"), p1.toJson(), false)

        val jsonWrite = JSONObject()
        jsonWrite.put("write", true)
        val p2 = NCMBAcl.Permission(jsonWrite)
        Assert.assertFalse(p2.readable)
        Assert.assertTrue(p2.writable)
        JSONAssert.assertEquals(JSONObject("{'write':true}"), p2.toJson(), false)

        val jsonReadWrite = JSONObject()
        jsonReadWrite.put("read", true)
        jsonReadWrite.put("write", true)
        val p3 = NCMBAcl.Permission(jsonReadWrite)
        Assert.assertTrue(p3.readable)
        Assert.assertTrue(p3.writable)
        JSONAssert.assertEquals(JSONObject("{'read':true, 'write':true}"), p3.toJson(), false)
    }

    /**
     * - 内容：NCMBAclを用いて単一ユーザACLを設定する
     * - 結果：JSON出力が正しい
     */
    //Todo fix
    @Test
    @Throws(java.lang.Exception::class)
    fun add_ACL_to_single_user() {
        val userId = "acluser"
        val acl = NCMBAcl()
        acl.setUserReadAccess(userId, true)
        Assert.assertTrue(acl.getUserReadAccess(userId))
        Assert.assertFalse(acl.getUserWriteAccess(userId))
        JSONAssert.assertEquals(JSONObject("{'acluser':{'read':true}}"), acl.toJson(), false)

        val acl2 = NCMBAcl()
        acl2.setUserWriteAccess(userId, true)
        Assert.assertFalse(acl2.getUserReadAccess(userId))
        Assert.assertTrue(acl2.getUserWriteAccess(userId))
        JSONAssert.assertEquals(JSONObject("{'acluser':{'write':true}}"), acl2.toJson(), false)

        val acl3 = NCMBAcl()
        acl3.setUserReadAccess(userId, true)
        acl3.setUserWriteAccess(userId, true)
        Assert.assertTrue(acl3.getUserReadAccess(userId))
        Assert.assertTrue(acl3.getUserWriteAccess(userId))
        JSONAssert.assertEquals(
            JSONObject("{'acluser':{'read':true, 'write':true}}"),
            acl3.toJson(),
            false
        )
    }

    /**
     * - 内容：NCMBAclを用いて単一ロールACLを設定する
     * - 結果：JSON出力が正しい
     */
    @Test
    @Throws(Exception::class)
    fun set_a_single_role_ACL() {
        val roleName = "aclrole"
        val acl = NCMBAcl()
        acl.setRoleReadAccess(roleName, true)
        Assert.assertTrue(acl.getRoleReadAccess(roleName))
        Assert.assertFalse(acl.getRoleWriteAccess(roleName))
        JSONAssert.assertEquals(JSONObject("{'role:aclrole':{'read':true}}"), acl.toJson(), false)

        val acl2 = NCMBAcl()
        acl2.setRoleWriteAccess(roleName, true)
        Assert.assertFalse(acl2.getRoleReadAccess(roleName))
        Assert.assertTrue(acl2.getRoleWriteAccess(roleName))
        JSONAssert.assertEquals(JSONObject("{'role:aclrole':{'write':true}}"), acl2.toJson(), false)

        val acl3 = NCMBAcl()
        acl3.setRoleReadAccess(roleName, true)
        acl3.setRoleWriteAccess(roleName, true)
        Assert.assertTrue(acl3.getRoleReadAccess(roleName))
        Assert.assertTrue(acl3.getRoleWriteAccess(roleName))
        JSONAssert.assertEquals(
            JSONObject("{'role:aclrole':{'read':true, 'write':true}}"),
            acl3.toJson(),
            false
        )
    }

    /**
     * - 内容：NCMBAclを用いてpublic ACLを設定する
     * - 結果：JSON出力が正しい
     */
    @Test
    @Throws(java.lang.Exception::class)
    fun set_publicACL() {
        val acl = NCMBAcl()
        acl.publicReadAccess = true
        Assert.assertTrue(acl.publicReadAccess)
        Assert.assertFalse(acl.publicWriteAccess)
        JSONAssert.assertEquals(JSONObject("{'*':{'read':true}}"), acl.toJson(), false)
        acl.publicReadAccess = false
        acl.publicWriteAccess = true
        Assert.assertFalse(acl.publicReadAccess)
        Assert.assertTrue(acl.publicWriteAccess)
        JSONAssert.assertEquals(JSONObject("{'*':{'write':true}}"), acl.toJson(), false)
        acl.publicReadAccess = true
        Assert.assertTrue(acl.publicReadAccess)
        Assert.assertTrue(acl.publicWriteAccess)
        JSONAssert.assertEquals(
            JSONObject("{'*':{'read':true, 'write':true}}"),
            acl.toJson(),
            false
        )
    }

    /**
     * - 内容：ユーザ、ロール、public を組みあせてACLを設定する
     * - 結果：JSON が実質同等
     */
    @Test
    @Throws(java.lang.Exception::class)
    fun set_up_composite_ACL() {
        val userId = "acluser"
        val roleName = "aclrole"
        val acl = NCMBAcl()
        acl.setUserReadAccess(userId, true)
        acl.setUserWriteAccess(userId, true)
        acl.setRoleReadAccess(roleName, true)
        acl.setRoleWriteAccess(roleName, true)
        acl.publicReadAccess = true
        Assert.assertTrue(acl.getUserReadAccess(userId))
        Assert.assertTrue(acl.getUserWriteAccess(userId))
        Assert.assertTrue(acl.getRoleReadAccess(roleName))
        Assert.assertTrue(acl.getRoleWriteAccess(roleName))
        Assert.assertTrue(acl.publicReadAccess)
        Assert.assertFalse(acl.publicWriteAccess)
        val json = acl.toJson()
        val acluser = json.getJSONObject("acluser")
        val aclrole = json.getJSONObject("role:aclrole")
        val aclpublic = json.getJSONObject("*")
        Assert.assertEquals(acluser.getBoolean("read"), true)
        Assert.assertEquals(acluser.getBoolean("write"), true)
        Assert.assertEquals(aclrole.getBoolean("read"), true)
        Assert.assertEquals(aclrole.getBoolean("write"), true)
        Assert.assertEquals(aclpublic.getBoolean("read"), true)
        val acl2 = NCMBAcl(json)
        //Todo fix jsonでの権限処理以下のwrute accessのTest Trueが反映されない
        Assert.assertTrue(acl2.getUserReadAccess(userId))
        Assert.assertTrue(acl2.getUserWriteAccess(userId))
        Assert.assertTrue(acl2.getRoleReadAccess(roleName))
        Assert.assertTrue(acl2.getRoleWriteAccess(roleName))
        Assert.assertTrue(acl2.publicReadAccess)
        Assert.assertFalse(acl2.publicWriteAccess)
    }

    /**
     * - 内容：ACLの初期値をチェックする
     * - 結果：空状態を判別できる
     */
    @Test
    @Throws(java.lang.Exception::class)
    fun initial_value_of_ACL() {
        val acl = NCMBAcl()
        Assert.assertTrue(acl.isEmpty)
    }

    /**
     * - 内容：未設定のACLにアクセスする
     * - 結果：正しく値を返す
     */
    @Test
    @Throws(java.lang.Exception::class)
    fun access_to_unconfigured_ACLs() {
        val userId = "acluser"
        val roleName = "aclrole"
        val acl = NCMBAcl()
        Assert.assertFalse(acl.getUserReadAccess(userId))
        Assert.assertFalse(acl.getUserWriteAccess(userId))
        Assert.assertFalse(acl.getRoleReadAccess(roleName))
        Assert.assertFalse(acl.getRoleWriteAccess(roleName))
        Assert.assertFalse(acl.publicReadAccess)
        Assert.assertFalse(acl.publicWriteAccess)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun user_Acl_isEmpty() {
        val userId = "acluser"
        val acl = NCMBAcl()
        // set Permission
        Assert.assertTrue(acl.isEmpty)
        acl.setUserReadAccess(userId, true)
        acl.setUserWriteAccess(userId, true)
        JSONAssert.assertEquals(
            JSONObject("{'acluser':{'read':true, 'write':true}}"),
            acl.toJson(),
            false
        )
        Assert.assertFalse(acl.isEmpty)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun role_Acl_isEmpty() {
        val roleName = "aclrole"
        val acl = NCMBAcl()
        Assert.assertTrue(acl.isEmpty)
        acl.setRoleReadAccess(roleName, true)
        acl.setRoleWriteAccess(roleName, true)
        JSONAssert.assertEquals(
            acl.toJson(),
            JSONObject("{'role:aclrole':{'read':true, 'write':true}}"),
            false
        )
        Assert.assertFalse(acl.isEmpty)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun role_and_user_Acl_isEmpty() {
        val roleName = "aclrole"
        val userId = "acluser"

        val acl = NCMBAcl()
        Assert.assertTrue(acl.isEmpty)
        acl.setRoleReadAccess(roleName, true)
        acl.setRoleWriteAccess(roleName, true)
        JSONAssert.assertEquals(
            acl.toJson(),
            JSONObject("{'role:aclrole':{'read':true, 'write':true}}"),
            false
        )
        // set Permission
        acl.setUserReadAccess(userId, true)
        acl.setUserWriteAccess(userId, true)
        JSONAssert.assertEquals(
            JSONObject("{'role:aclrole':{'read':true, 'write':true},'acluser':{'read':true, 'write':true}}"),
            acl.toJson(),
            false
        )
        Assert.assertFalse(acl.isEmpty)
    }

    /**
     * - 内容：Permission の削除
     * - 結果：削除されていること
     */
    @Test
    @Throws(java.lang.Exception::class)
    fun delete_ACL_User_Permission() {
        val userId = "acluser"
        val acl = NCMBAcl()
        // set Permission
        acl.setUserReadAccess(userId, true)
        acl.setUserWriteAccess(userId, true)
        JSONAssert.assertEquals(
            JSONObject("{'acluser':{'read':true, 'write':true}}"),
            acl.toJson(),
            false
        )
        val userId2 = "acluser2"
        // set Permission
        acl.setUserReadAccess(userId2, true)
        acl.setUserWriteAccess(userId2, true)
        JSONAssert.assertEquals(
            JSONObject("{'acluser':{'read':true, 'write':true},'acluser2':{'read':true, 'write':true}}"),
            acl.toJson(),
            false
        )
        Assert.assertTrue(acl.getUserReadAccess(userId))
        Assert.assertTrue(acl.getUserWriteAccess(userId))
        Assert.assertTrue(acl.getUserReadAccess(userId2))
        Assert.assertTrue(acl.getUserWriteAccess(userId2))
        // remove permission
        acl.removeUserPermission(userId)
        Assert.assertFalse(acl.isEmpty)
        acl.removeUserPermission(userId2)
        Assert.assertTrue(acl.isEmpty)
    }

    /**
     * - 内容：Permission の削除
     * - 結果：削除されていること
     */
    @Test
    @Throws(java.lang.Exception::class)
    fun delete_ACL_Role_Permission() {
        val roleName = "aclrole"
        val acl = NCMBAcl()
        acl.setRoleReadAccess(roleName, true)
        acl.setRoleWriteAccess(roleName, true)
        JSONAssert.assertEquals(
            acl.toJson(),
            JSONObject("{'role:aclrole':{'read':true, 'write':true}}"),
            false
        )
        val roleName2 = "aclrole2"
        acl.setRoleReadAccess(roleName2, true)
        acl.setRoleWriteAccess(roleName2, true)
        JSONAssert.assertEquals(
            acl.toJson(),
            JSONObject("{'role:aclrole2':{'read':true,'write':true},'role:aclrole':{'read':true,'write':true}}"),
            false
        )
        Assert.assertTrue(acl.getRoleReadAccess(roleName))
        Assert.assertTrue(acl.getRoleWriteAccess(roleName))
        Assert.assertTrue(acl.getRoleReadAccess(roleName2))
        Assert.assertTrue(acl.getRoleWriteAccess(roleName2))
        // remove permission
        acl.removeRolePermission(roleName)
        Assert.assertFalse(acl.isEmpty)
        acl.removeRolePermission(roleName2)
        Assert.assertTrue(acl.isEmpty)
    }

    //permissionのrole 二つ目に追加してみる
    /**
     * - 内容：Permission の削除
     * - 結果：削除されていること
     */
    @Test
    @Throws(java.lang.Exception::class)
    fun set_same_ACL_Role_Permission() {
        val roleName = "aclrole"
        val roleName2 = "aclrole2"
        val acl = NCMBAcl()
        val p = NCMBAcl.Permission(true, true)
        acl.setRoleReadAccess(roleName, p.readable)
        acl.setRoleWriteAccess(roleName, p.writable)
        acl.setRoleReadAccess(roleName2, p.readable)
        acl.setRoleWriteAccess(roleName2, p.writable)
        JSONAssert.assertEquals(
            acl.toJson(),
            JSONObject("{'role:aclrole':{'read':true, 'write':true},'role:aclrole2':{'read':true, 'write':true}}"),
            false
        )
        acl.setRoleReadAccess(roleName, false)
        acl.setRoleWriteAccess(roleName, false)
        JSONAssert.assertEquals(
            acl.toJson(),
            JSONObject("{'role:aclrole':{'read':false,'write':false},'role:aclrole2':{'read':true,'write':true}}"),
            false
        )
        Assert.assertFalse(acl.getRoleReadAccess(roleName))
        Assert.assertFalse(acl.getRoleWriteAccess(roleName))
        Assert.assertTrue(acl.getRoleReadAccess(roleName2))
        Assert.assertTrue(acl.getRoleWriteAccess(roleName2))
    }
}
