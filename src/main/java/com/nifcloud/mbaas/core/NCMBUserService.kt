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

import com.nifcloud.mbaas.core.NCMBLocalFile.create
import com.nifcloud.mbaas.core.NCMBLocalFile.deleteFile
import org.json.JSONException
import org.json.JSONObject
import java.io.File

/**
 * Service for user api
 */
class NCMBUserService : NCMBService() {

    /**
     * Status code of register success
     */
    val HTTP_STATUS_REGISTERED = 201

    /**
     * Status code of authorize success
     */
    val HTTP_STATUS_AUTHORIZED = 200

    /**
     * service path for API category
     */
    val SERVICE_PATH = "users"

    /**
     * Initialization
     *
     */
    init  {
        this.mServicePath = this.SERVICE_PATH
    }

    /**
     * Login by user name
     *
     * @param userName user name
     * @param password password
     * @return new NCMBUser object that logged-in
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun loginByName(userName: String, password: String): NCMBUser {
        try{
            val params = JSONObject()
            params.put("userName", userName)
            params.put("password", password)
            return loginUser(params)
        } catch (e: JSONException){
            throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, e.message!!)
        }
    }

    /**
     * Internal method to register user
     *
     * @param params parameters
     * @param oauth  use oauth or not
     * @return new NCMBUser object that logged-in
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    fun registerUser(params: JSONObject, oauth: Boolean): NCMBUser {
        val reqParams = registerUserParams(params)
        val response = sendRequest(reqParams)
        val responseData = registerUserCheckResponse(response, oauth)
        return postLoginProcess(responseData)
    }

    /**
     * Internal method to save user
     *
     * @param params parameters
     * @param oauth use oauth or not
     * @return NCMBUser
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    fun saveUser(saveObject: NCMBObject, params: JSONObject, oauth: Boolean) {
        val reqParams = registerUserParams(params)
        val response = sendRequest(reqParams)
        val responseData = registerUserCheckResponse(response, oauth)
        saveObject.reflectResponse(responseData)
    }

    /**
     * Internal method to save user
     *
     * @param params parameters
     * @param oauth use oauth or not
     * @return NCMBUser
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    protected fun loginUser(params: JSONObject): NCMBUser {
        val reqParams = loginByNameParams(params)
        val response = sendRequest(reqParams)
        val responseData = loginByNameCheckResponse(response)
        return postLoginProcess(responseData)
    }

    /**
     * Logout from session
     *
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun logoutUser() {
        val reqParams = logoutParams()
        val response = sendRequest(reqParams)
        // clear login informations
        clearCurrentUser()
        logoutCheckResponse(response)
    }

    /**
     * LogoutInBackground from session
     *
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun logoutUserInBackground(logoutCallback: NCMBCallback) {
        val logoutHandler = NCMBHandler { logoutcallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    // clear login informations
                    clearCurrentUser()
                    val responseData = logoutCheckResponse(response)
                    val user = NCMBUser(responseData)
                    //loginCallback done to object
                    logoutCallback.done(null, user)
                }
                is NCMBResponse.Failure -> {
                    logoutCallback.done(response.resException)
                }
            }
        }
        val reqParams: RequestParamsAsync = logoutParamsInBackground(logoutCallback, logoutHandler)
        sendRequestAsync(reqParams)
    }

    /**
     * Login by user name in background
     *
     * @param userName user name
     * @param password password
     * @param callback callback when process finished
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun loginByNameInBackground(userName: String?, password: String?, loginCallback: NCMBCallback) {
        try{
            val params = JSONObject()
            params.put("userName", userName)
            params.put("password", password)
            return loginUserInBackground(params, loginCallback)
        } catch (e: JSONException){
            throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, e.message!!)
        }
    }

    /**
     * Internal method to register user
     *
     * @param params parameters
     * @param oauth  use oauth or not
     * @return new NCMBUser object that logged-in
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    fun registerInBackgroundUser(params: JSONObject, oauth: Boolean, signUpCallback: NCMBCallback) {
        val signUpHandler = NCMBHandler { logincallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    val responseData = registerUserCheckResponse(response, oauth)
                    val user = postLoginProcess(responseData)
                    NCMBBase().mFields = user.mFields
                    //loginCallback done to object
                    signUpCallback.done(null, user)
                }
                is NCMBResponse.Failure -> {
                    signUpCallback.done(response.resException)
                }
            }
        }
        val reqParams = registerInBackgroundByNameParams(params, signUpCallback, signUpHandler)
        sendRequestAsync(reqParams)
    }

    /**
     * Internal method to save user
     *
     * @param params parameters
     * @param oauth use oauth or not
     * @return NCMBUser
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    protected fun loginUserInBackground(params: JSONObject, loginCallback: NCMBCallback){
        val loginHandler = NCMBHandler { logincallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    val responseData = loginByNameCheckResponse(response)
                    val user = postLoginProcess(responseData)
                    //loginCallback done to object
                    loginCallback.done(null, user);
                }
                is NCMBResponse.Failure -> {
                    loginCallback.done(response.resException)
                }
            }
        }
        val reqParams = loginInBackgroundByNameParams(params, loginCallback, loginHandler)
        sendRequestAsync(reqParams)
    }

    /**
     * Setup params to register new user
     *
     * @param params user parameters
     * @return parameters in object
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    protected fun registerUserParams(params: JSONObject): RequestParams {
        val url = NCMB.getApiBaseUrl() + mServicePath
        val method = NCMBRequest.HTTP_METHOD_POST
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        return RequestParams(url = url, method = method, params = params, contentType = contentType)
    }

    /**
     * Setup params to register new user in background
     *
     * @param params user parameters
     * @return parameters in object
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    fun registerInBackgroundByNameParams(params: JSONObject, loginCallback: NCMBCallback, loginHandler: NCMBHandler): RequestParamsAsync {
        val url = NCMB.getApiBaseUrl() + mServicePath
        val method = NCMBRequest.HTTP_METHOD_POST
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        return RequestParamsAsync(url = url, method = method, params = params, contentType = contentType, callback = loginCallback, handler = loginHandler)
    }

    /**
     * Setup params to login by user name
     *
     * @param userName user name
     * @param password password
     * @return parameters in object
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    fun loginByNameParams(query: JSONObject): RequestParams {
        return try {
            val url = NCMB.getApiBaseUrl() + "login"
            val method = NCMBRequest.HTTP_METHOD_GET
            val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
            RequestParams(url = url, method = method, contentType = contentType, query = query)
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.MISSING_VALUE, "userName/password required")
        }
    }

    /**
     * Setup params to login by user name
     *
     * @param userName user name
     * @param password password
     * @return parameters in object
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    fun loginInBackgroundByNameParams(query: JSONObject, loginCallback: NCMBCallback, loginHandler: NCMBHandler): RequestParamsAsync {
        return try {
            val url = NCMB.getApiBaseUrl() + "login"
            val method = NCMBRequest.HTTP_METHOD_GET
            val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
            RequestParamsAsync(url = url, method = method, contentType = contentType, query = query, callback = loginCallback, handler = loginHandler)
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.MISSING_VALUE, "userName/password required")
        }
    }

    /**
     * Setup parameters to logout
     *
     * @return request params in object
     */
    protected fun logoutParams(): RequestParams {
        val url = NCMB.getApiBaseUrl() + "logout"
        val method = NCMBRequest.HTTP_METHOD_GET
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        return RequestParams(url = url, method = method, contentType = contentType)
    }

    /**
     * Setup parameters to logout
     *
     * @return request params in object
     */
    protected fun logoutParamsInBackground(logoutCallback: NCMBCallback, logoutHandler: NCMBHandler): RequestParamsAsync {
        val url = NCMB.getApiBaseUrl() + "logout"
        val method = NCMBRequest.HTTP_METHOD_GET
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        return RequestParamsAsync(url = url, method = method, contentType = contentType, callback = logoutCallback, handler = logoutHandler)
    }

    /**
     * Update user information
     *
     * @param userId user id
     * @param params update values
     * @return result of update user
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun updateUser(updateObject: NCMBObject, userId: String, params: JSONObject): JSONObject {
        val reqParams = updateUserParams(userId, params)
        val response = sendRequest(reqParams)
        //update currentUser
        try {
            params.put("objectId", userId)
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, e.message!!)
        }
        val responseData = updateUserCheckResponse(response)
        updateObject.reflectResponse(responseData)
        writeCurrentUser(params, responseData)
        return responseData
    }

    /**
     * Set up to update user information
     *
     * @param userId user id
     * @param params update values
     * @return parameters for NCMBRequest
     */
    fun updateUserParams(userId: String, params: JSONObject): RequestParams {
        val url = NCMB.getApiBaseUrl() + this.mServicePath + "/" + userId
        val method = NCMBRequest.HTTP_METHOD_PUT
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        val reqParams = RequestParams(
            url = url,
            method = method,
            params = params,
            contentType = contentType
        )
        return reqParams
    }

    /**
     * Check response to register new user
     *
     * @param response
     * @param oauth    use oauth or not
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    fun registerUserCheckResponse(response: NCMBResponse, oauth: Boolean): JSONObject {
        when(response) {
            is NCMBResponse.Success -> {
                if (response.resCode == HTTP_STATUS_REGISTERED) {
                    return response.data
                }
                if (response.resCode == HTTP_STATUS_AUTHORIZED && !oauth) {
                    throw NCMBException(NCMBException.AUTH_FAILURE, "User registration failed")
                } else {
                    if (oauth) {
                        throw NCMBException(NCMBException.OAUTH_FAILURE, "Oauth failed")
                    } else {
                        throw NCMBException(NCMBException.AUTH_FAILURE, "User registration failed")
                    }
                }
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
    }

    /**
     * Check response to update user information
     *
     * @param response
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    fun updateUserCheckResponse(response: NCMBResponse): JSONObject {
        return when (response) {
            is NCMBResponse.Success -> {
                if (response.resCode !== HTTP_STATUS_AUTHORIZED) {
                    throw NCMBException(
                        NCMBException.NOT_EFFICIENT_VALUE,
                        "Update user info failed"
                    )
                }
                response.data
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
    }

    @Throws(NCMBException::class)
    fun loginByNameCheckResponse(response: NCMBResponse): JSONObject {
        return when(response) {
            is NCMBResponse.Success -> {
                if (response.resCode !== HTTP_STATUS_AUTHORIZED) {
                    throw NCMBException(NCMBException.AUTH_FAILURE, "Login failed")
                }
                response.data
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
    }

    @Throws(NCMBException::class)
    fun logoutCheckResponse(response: NCMBResponse): JSONObject{
        return when(response) {
            is NCMBResponse.Success -> {
                if (response.resCode !== HTTP_STATUS_AUTHORIZED) {
                    throw NCMBException(NCMBException.AUTH_FAILURE, "Logout failed")
                }
                response.data
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
    }

    /**
     * merge the JSONObject
     *
     * @param base    base JSONObject
     * @param compare merge JSONObject
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    fun mergeJSONObject(base: JSONObject, compare: JSONObject) {
        try {
            for(key in compare.keys()){
                base.put(key, compare[key])
            }
        } catch (error: JSONException) {
            throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, error.message!!)
        }
    }

    /**
     * process after login
     *
     * @param response response object
     * @return new NCMBUser object
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    protected fun postLoginProcess(responseData: JSONObject): NCMBUser {
        try {
            val result: JSONObject = responseData
            val userId = result.getString("objectId")
            // register with login, sessionToken updated
            val newSessionToken = result.getString("sessionToken")
            NCMB.SESSION_TOKEN = newSessionToken
            NCMB.USER_ID = userId
            // create currentUser. empty JSONObject for POST
            writeCurrentUser(JSONObject(), responseData)
            return NCMBUser(result)
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_JSON, "Invalid user info")
        }
    }

    /**
     * Run at the time of "POST" and "PUT"
     * write the currentUser data in the file
     *
     * @param responseData user parameters
     */
    @Throws(NCMBException::class)
    fun writeCurrentUser(params: JSONObject, responseData: JSONObject) {
        //merge responseData to the params
        mergeJSONObject(params, responseData)

        val user = NCMBUser()
        //merge params to the currentData
        val currentUser: NCMBUser = user.getCurrentUser()
        val currentData = currentUser.localData
        if (currentData != null) {
            mergeJSONObject(currentData, params)
            //write file
            val file: File = NCMBLocalFile.create(user.USER_FILENAME)
            NCMBLocalFile.writeFile(file, currentData)
            //held in a static
            user.currentuser = NCMBUser(currentData)

            if (currentData.has("sessionToken")) {
                try {
                    NCMB.SESSION_TOKEN = currentData.getString("sessionToken")
                } catch (e: JSONException) {
                    throw NCMBException(NCMBException.INVALID_JSON, e.message!!)
                }
            }
        }
    }

    //Todo 匿名認証
    /**
     * Setup OAuth parameters to register new user with OAuth
     *
     * @param oauthOptions OAuth options
     * @return "authData" params in JSONObject
     * @throws NCMBException
     */
//    @Throws(NCMBException::class)
//    fun registerByOauthSetup(oauthOptions: JSONObject): JSONObject {
//        return try {
//            val authType = oauthOptions.getString("type")
//            val authData = JSONObject()
//            when (authType) {
//                NCMB.OAUTH_ANONYMOUS -> {
//                    val anKeys = arrayOf(
//                        "id"
//                    )
//                    authData.put("anonymous", fillParameters(anKeys, oauthOptions))
//                }
//                else -> throw NCMBException(NCMBException.OAUTH_FAILURE, "Unknown OAuth type")
//            }
//            val params = JSONObject()
//            params.put("authData", authData)
//            params
//        } catch (e: JSONException) {
//            throw NCMBException(NCMBException.MISSING_VALUE, e.message!!)
//        }
//    }

    /**
     * Register new user by OAuth services
     *
     * @param oauthOptions OAuth options
     * @return new NCMBUser object that registered
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
//    @Throws(NCMBException::class)
//    fun registerByOauth(oauthOptions: JSONObject): NCMBUser {
//        val params = registerByOauthSetup(oauthOptions)
//        return registerUser(params, true)
//    }

    /**
     * Create JSONObject and copy values with given keys
     *
     * @param keys String[] keys to copy
     * @param src  JSONObject source of copy
     * @return JSONObject
     */
//    @Throws(NCMBException::class)
//    fun fillParameters(keys: Array<String>, options: JSONObject): JSONObject {
//        val result = JSONObject()
//        for (key in keys) {
//            try {
//                result.put(key, options[key])
//            } catch (e: JSONException) {
//                throw NCMBException(NCMBException.MISSING_VALUE, "Missing value: $key")
//            }
//        }
//        return result
//    }

    /**
     * Get user entity from given id
     *
     * @param userId user id
     * @return NCMBUser instance
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun fetchUser(fetchObject: NCMBObject, userId: String): NCMBUser {
        val reqParams: RequestParams = getUserParams(userId)
        val response = sendRequest(reqParams)
        val responseData = getUserCheckResponse(response)
        fetchObject.reflectResponse(responseData)
        return NCMBUser(responseData)
    }

    /**
     * Get user entity from given id
     *
     * @param userId user id
     * @return NCMBUser instance
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun fetchUserInBackground(fetchObject: NCMBObject, userId: String, fetchCallback: NCMBCallback) {
        val fetchHandler = NCMBHandler { fetchcallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    val responseData = getUserCheckResponse(response)
                    val user = NCMBUser(responseData)
                    fetchObject.reflectResponse(responseData)
                    //loginCallback done to object
                    fetchCallback.done(null, user);
                }
                is NCMBResponse.Failure -> {
                    fetchCallback.done(response.resException)
                }
            }
        }
        val reqParams: RequestParamsAsync = getUserParamsInBackground(userId, fetchCallback, fetchHandler)
        sendRequestAsync(reqParams)
    }

    /**
     * Delete user by given id
     *
     * @param userId user id
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun deleteUser(deleteObject: NCMBObject, userId: String) {
        val reqParams: RequestParams = deleteUserParams(userId)
        val response = sendRequest(reqParams)
        val responseData = deleteUserCheckResponse(response)
        deleteObject.reflectResponse(responseData)

        if (userId == NCMBUser().getCurrentUser().getObjectId()) {
            // unregister login informations
            clearCurrentUser()
        }
    }

    /**
     * Delete user by given id
     *
     * @param userId user id
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun deleteUserInBackground(deleteObject: NCMBObject, userId: String, deleteCallback: NCMBCallback) {
        val deleteHandler = NCMBHandler { deletecallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    val responseData = deleteUserCheckResponse(response)
                    val user = NCMBUser(responseData)
                    deleteObject.reflectResponse(responseData)
                    NCMBBase().mFields = JSONObject()
                    NCMBBase().mUpdateKeys.clear()
                    if (userId == NCMBUser().getCurrentUser().getObjectId()) {
                        // unregister login informations
                        clearCurrentUser()
                    }
                    //loginCallback done to object
                    deleteCallback.done(null, user);
                }
                is NCMBResponse.Failure -> {
                    deleteCallback.done(response.resException)
                }
            }
        }
        val reqParams: RequestParamsAsync = deleteUserInBackgroundParams(userId, deleteCallback, deleteHandler)
        sendRequestAsync(reqParams)
    }

    /**
     * Setup params to get user entity
     *
     * @param userId user id
     * @return parameters for NCMBRequest
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    protected fun getUserParams(userId: String): RequestParams {
        val url = NCMB.getApiBaseUrl() + mServicePath + "/" + userId
        val method = NCMBRequest.HTTP_METHOD_GET
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        return RequestParams(url = url, method = method, contentType = contentType)
    }

    /**
     * Setup params to get user entity
     *
     * @param userId user id
     * @return parameters for NCMBRequest
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    protected fun getUserParamsInBackground(userId: String, fetchCallback: NCMBCallback, fetchHandler: NCMBHandler): RequestParamsAsync {
        val url = NCMB.getApiBaseUrl() + mServicePath + "/" + userId
        val method = NCMBRequest.HTTP_METHOD_GET
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        return RequestParamsAsync(url = url, method = method, contentType = contentType, callback = fetchCallback, handler = fetchHandler)
    }

    /**
     * Setup params to delete user
     *
     * @param userId user id
     * @return parameters in object
     */
    protected fun deleteUserParams(userId: String): RequestParams {
        val url = NCMB.getApiBaseUrl() + mServicePath + "/" + userId
        val method = NCMBRequest.HTTP_METHOD_DELETE
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        return RequestParams(url = url, method = method, contentType = contentType)
    }

    /**
     * Setup params to delete user
     *
     * @param userId user id
     * @return parameters in object
     */
    protected fun deleteUserInBackgroundParams(userId: String, deleteCallback: NCMBCallback, deleteHandler: NCMBHandler): RequestParamsAsync {
        val url = NCMB.getApiBaseUrl() + mServicePath + "/" + userId
        val method = NCMBRequest.HTTP_METHOD_DELETE
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        return RequestParamsAsync(url = url, method = method, contentType = contentType, callback = deleteCallback, handler = deleteHandler)
    }

    /**
     * Check response to get user entity
     *
     * @param response
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    protected fun getUserCheckResponse(response: NCMBResponse): JSONObject {
        return when (response) {
            is NCMBResponse.Success -> {
                if (response.resCode !== HTTP_STATUS_AUTHORIZED) {
                    throw NCMBException(NCMBException.DATA_NOT_FOUND, "Getting user info failure")
                }
                response.data
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
    }

    /**
     * Check responkse to delete user
     *
     * @param response
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    protected fun deleteUserCheckResponse(response: NCMBResponse): JSONObject {
        return when (response) {
            is NCMBResponse.Success -> {
                if (response.resCode !== HTTP_STATUS_AUTHORIZED) {
                    throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, "Delete user failed")
                }
                response.data
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
    }

    /**
     * Run at the time of "Delete" and "Logout" and "E404001 Error"
     */
    fun clearCurrentUser() {
        //delete file
        val file = create(NCMBUser().USER_FILENAME)
        deleteFile(file)
        //discarded from the static
        NCMBUser().currentuser = null
        NCMB.SESSION_TOKEN = null
        NCMB.USER_ID = null
    }

}
