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

import org.json.JSONException
import org.json.JSONObject
import java.io.File

/**
 * NCMBUser is used to sign up and login/logout the user
 */
open class NCMBUser: NCMBObject {

    /**
     * current user
     */
    var currentuser: NCMBUser? = null

    /**
     * currenUser fileName
     */
    val USER_FILENAME = "currentUser"

    /**
     * Get query
     *
     * @return query
     */
    var otherFields: JSONObject = JSONObject()

    val ignoreKeys: List<String> = mutableListOf(
        "objectId", "userName", "password",
        "mailAddress", "mailAddressConfirm",
        "acl", "authData",
        "createDate", "updateDate"
    )

    /**
     * Constructor with class name
     * @param className class name for data store
     */
    constructor() : super("user") {
        mIgnoreKeys = ignoreKeys
    }

    /**
     * Constructor with class name
     * @param className class name for data store
     */
    constructor(params: JSONObject) : super("user", params) {
        mIgnoreKeys = ignoreKeys
        try {
            if (params.has("sessionToken")) {
                NCMB.SESSION_TOKEN = params.getString("sessionToken")
            }
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_JSON, "Invalid user information")
        }
    }

    /**
     * Get user name
     *
     * @return String user name
     */
    /**
     * set user name
     *
     * @param userName user name string
     */
    var userName: String
        get() {
            return getUserInfo("userName")
        }
        set(userName) {
            setUserInfo("userName", userName)
        }

    /**
     * set password
     *
     * @param password password string
     */
    var password: String
        get() {
            return getUserInfo("password")
        }
        set(password) {
            setUserInfo("password", password)
        }

    /**
     * Get sessionToken
     *
     * @return sessionToken
     */
    open fun getSessionToken(): String? {
        return if (getCurrentUser().getString("sessionToken") != null) {
            getCurrentUser().getString("sessionToken")
        } else {
            null
        }
    }

    /**
     * Get mail address
     *
     * @return String mail address
     */
    /**
     * Set mail address
     *
     * @param mailAddress String mail address
     */
    var mailAddress: String
        get() {
            return getUserInfo("mailAddress")
        }
        set(mailAddress) {
            setUserInfo("mailAddress", mailAddress)
        }

    private fun getUserInfo(userKey: String): String {
        try {
            return mFields.getString(userKey)
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_JSON, e.message!!)
        }
    }

    private fun setUserInfo(userKey: String, userValue: String){
        try {
            mFields.put(userKey, userValue)
            mUpdateKeys.add(userKey)
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_JSON, e.message!!)
        }
    }

    // action methods
    /**
     * saveWithoutLogin to NIFCLOUD mobile backend
     *
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    private fun saveWithoutLogin(): NCMBObject {
        val userService = NCMBUserService()
        val params = JSONObject()
        try {
            if(userName == null || password == null){
                throw NCMBException(NCMBException.REQUIRED, "username or password not set")
            }
            for(key in mFields.keys()){
                params.put(key, mFields[key])
            }
            userService.saveUser(this, params, false)
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_JSON, e.message!!)
        }
        return this
    }

    @Throws(NCMBException::class)
    override fun save(): NCMBObject {
        val objectId = getObjectId() ?: return saveWithoutLogin()
        val userService = NCMBUserService()
        try {
            val result: JSONObject = userService.updateUser(this, objectId, createUpdateJsonData())
            if (!result.isNull("updateDate")) {
                mFields.put("updateDate", result.getString("updateDate"))
            }
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_JSON, (e.message)!!)
        }
        return this
    }

    /**
     * login with username and password
     *
     * @param userName user name
     * @param password password
     * @return NCMBUser object that logged-in
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    open fun login(userName: String, password: String): NCMBUser {
        val userService = NCMBUserService()
        return userService.loginByName(userName, password)
    }

    /**
     * Get current user object
     *
     * @return user
     */
    @Throws(NCMBException::class)
    fun getCurrentUser(): NCMBUser {
        NCMBLocalFile.checkNCMBContext()
        try {
            //create currentUser
            if (currentuser == null) {
                currentuser = NCMBUser()
                //ローカルファイルにログイン情報があれば取得、なければ新規作成
                val currentUserFile: File = NCMBLocalFile.create(USER_FILENAME)
                if (currentUserFile.exists()) {
                    //ローカルファイルからログイン情報を取得
                    val localData: JSONObject = NCMBLocalFile.readFile(currentUserFile)
                    currentuser = NCMBUser(localData)
                }
            }
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_JSON, (e.message)!!)
        }
        return currentuser as NCMBUser
    }

    //Todo 匿名認証
    /**
     * Decision whether logged in
     *
     * @return Return true if logged in
     */
//    fun isAuthenticated(): Boolean {
//        val currentUser = getCurrentUser()
//        val sessionToken = getSessionToken()
//        if (currentUser != null && sessionToken != null) {
//            return getObjectId().equals(currentUser.getObjectId())
//        }
//        return false
//    }

//    /**
//     * Check for specified provider's authentication data is linked
//     *
//     * @param provider facebook or twitter or google or anonymous
//     * @return Return true if authentication data is linked
//     */
//    fun isLinkedWith(provider: String): Boolean {
//        return try {
//            mFields.has("authData") && mFields.getJSONObject("authData").has(provider)
//        } catch (e: JSONException) {
//            throw IllegalArgumentException(e.message)
//        }
//    }
//    /**
//     * Get authData
//     *
//     * @return JSONObject or null
//     */
//    fun getAuthData(): JSONObject? {
//        return try {
//            if (mFields.isNull("authData")) {
//                null
//            } else mFields.getJSONObject("authData")
//        } catch (error: JSONException) {
//            throw IllegalArgumentException(error.message)
//        }
//    }
//    /**
//     * Get Specified Authentication Data
//     *
//     * @param provider String "facebook" or "twitter" or "google" or "anonymous"
//     * @return Specified Authentication Data or null
//     */
//    fun getAuthData(provider: String): JSONObject? {
//        return try {
//            if (mFields.isNull("authData") || mFields.getJSONObject("authData").isNull(provider)) {
//                null
//            } else mFields.getJSONObject("authData").getJSONObject(provider)
//        } catch (error: JSONException) {
//            throw IllegalArgumentException(error.message)
//        }
//    }
//    @Throws(JSONException::class)
//    fun createAuthData(params: Any): JSONObject {
//        var authDataJSON: JSONObject
//        if (params.javaClass == NCMBAnonymousParameters::class.java) {
//            authDataJSON = createAnonymousAuthData(params as NCMBAnonymousParameters)
//            authDataJSON.put("type", "anonymous")
//        } else {
//            throw IllegalArgumentException("Parameters must be NCMBFacebookParameters or NCMBTwitterParameters or NCMBGoogleParameters")
//        }
//        return authDataJSON
//    }
//    @Throws(JSONException::class)
//    fun createAnonymousAuthData(params: NCMBAnonymousParameters): JSONObject {
//        val authDataJson = JSONObject()
//        authDataJson.put("id", params.userId)
//        return authDataJson
//    }
//
//    /**
//     * login with parameter that can be obtained after the OAuth authentication
//     *
//     * @param authData NCMBFacebookParameters or NCMBTwitterParameters or NCMBGoogleParameters
//     * @return Authenticated user
//     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
//     */
//    @Throws(NCMBException::class)
//    open fun loginWith(authData: Any): NCMBUser {
//        val objService = NCMBUserService()
//        return try {
//            objService.registerByOauth(createAuthData(authData))
//        } catch (e: JSONException) {
//            throw NCMBException(NCMBException.INVALID_JSON, e.message!!)
//        }
//    }
//
//    /**
//     * Login with anonymous
//     *
//     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
//     */
//    @Throws(NCMBException::class)
//    open fun loginWithAnonymous(): NCMBUser {
//        val anonymousParameters = NCMBAnonymousParameters(createUUID())
//        return loginWith(anonymousParameters)
//    }
//
//    open fun createUUID(): String {
//        return UUID.randomUUID().toString()
//    }

    /**
     * Login with username and password in background
     *
     * @param userName user name
     * @param password password
     * @param callback callback when finished
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun loginInBackground(userName: String, password: String, loginCallback: NCMBCallback) {
        val userService = NCMBUserService()
        userService.loginByNameInBackground(userName, password, loginCallback)
    }

    /**
     * sign up to NIFCLOUD mobile backend
     *
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    open fun signUp(): NCMBUser {
        val userService = NCMBUserService()
        val params = JSONObject()
        var user = NCMBUser()
        try {
            if(userName == null || password == null){
                throw NCMBException(NCMBException.REQUIRED, "username or password not set")
            }
            for(key in mFields.keys()){
                params.put(key, mFields[key])
            }
            user = userService.registerUser(params, false)
            mFields = user.mFields
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_JSON, e.message!!)
        }
        return user
    }

    /**
     * sign up to NIFCLOUD mobile backend
     *
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    open fun signUpInBackground(loginCallback: NCMBCallback) {
        val userService = NCMBUserService()
        val params = JSONObject()
        try {
            if(userName == null || password == null){
                throw NCMBException(NCMBException.REQUIRED, "username or password not set")
            }
            for(key in mFields.keys()){
                params.put(key, mFields[key])
            }
            userService.registerInBackgroundUser(params, false, loginCallback)
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_JSON, e.message!!)
        }
    }

    @Throws(NCMBException::class)
    override fun fetch(): NCMBObject {
        val objectId = getObjectId()
        val userService = NCMBUserService()
        if (objectId != null) {
            val user: NCMBUser = userService.fetchUser(this, objectId)
            mFields = user.mFields
        }
        return this
    }

    @Throws(NCMBException::class)
    override fun fetchInBackground(fetchCallback: NCMBCallback) {
        val objectId = getObjectId()
        val userService = NCMBUserService()
        if (objectId != null) {
            userService.fetchUserInBackground(this, objectId, fetchCallback)
        }
    }

    @Throws(NCMBException::class)
    override fun delete(): NCMBObject? {
        val objectId = getObjectId()
        val userService = NCMBUserService()
        try {
            if (objectId != null) {
                userService.deleteUser(this, objectId)
                mFields = JSONObject()
                mUpdateKeys.clear()
            }
            return null
        } catch (e: NCMBException) {
            throw e
        }
    }

    @Throws(NCMBException::class)
    override fun deleteInBackground(deleteCallback: NCMBCallback) {
        val objectId = getObjectId()
        val userService = NCMBUserService()
        try {
            if (objectId != null) {
                userService.deleteUserInBackground(this, objectId, deleteCallback)
            }
        } catch (e: NCMBException) {
            throw e
        }
    }

    /**
     * logout from NIFCLOUD mobile backend
     *
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    open fun logout() {
        val userService = NCMBUserService()
        userService.logoutUser()
    }

    /**
     * logout from NIFCLOUD mobile backend
     *
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    open fun logoutInBackground(logoutCallback: NCMBCallback) {
        val userService = NCMBUserService()
        userService.logoutUserInBackground(logoutCallback)
    }
}
