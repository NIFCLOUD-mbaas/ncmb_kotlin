/*
 * Copyright 2017-2023 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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
 * Service for user class
 */
internal class NCMBUserService : NCMBObjectService() {
    /**
     * Status code of signup success
     */
    val HTTP_STATUS_SIGNUPED = 201

    /**
     * Status code of authorize success
     */
    val HTTP_STATUS_AUTHORIZED = 200

    /**
     * service path for API category
     */
    override  val SERVICE_PATH = "users"

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
     * @return NCMBUser object that logged-in
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun loginByName(userName: String, password: String): NCMBUser {
        try{
            val query = JSONObject()
            query.put("userName", userName)
            query.put("password", password)
            return loginUser(query)
        } catch (e: JSONException){
            throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, e.localizedMessage)
        }
    }

    /**
     * Internal method to signUp user
     *
     * @param params parameters
     * @param oauth  use oauth or not
     * @return NCMBUser object that logged-in
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    fun signUpUser(params: JSONObject, oauth: Boolean): NCMBUser {
        val reqParams = signUpUserParams(null, params, null, null)
        val response = sendRequest(reqParams)
        val responseData = signUpUserCheckResponse(response, oauth)
        return postLoginProcess(responseData)
    }

    /**
     * Internal method to save user
     *
     * @param params parameters
     * @param oauth use oauth or not
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    fun saveUser(saveObject: NCMBObject, params: JSONObject, oauth: Boolean) {
        val reqParams = signUpUserParams(null, params, null, null)
        val response = sendRequest(reqParams)
        val responseData = signUpUserCheckResponse(response, oauth)
        saveObject.reflectResponse(responseData)
    }

    /**
     * Internal method to save user
     *
     * @param params parameters
     * @return NCMBUser object that logged-in
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    protected fun loginUser(query: JSONObject): NCMBUser {
        val reqParams = loginByNameParams(query, null, null)
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
    fun logoutUser(logoutUser: NCMBUser) {
        val reqParams = logoutParams(null, null)
        val response = sendRequest(reqParams)
        // clear login informations
        clearCurrentUser()
        logoutUser.sessionToken = null
        logoutCheckResponse(response)
    }

    /**
     * LogoutInBackground from session
     * @param logoutCallback callback when process finished
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun logoutUserInBackground(logoutUser: NCMBUser, logoutCallback: NCMBCallback) {
        val logoutHandler = NCMBHandler { logoutcallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    // clear login informations
                    clearCurrentUser()
                    val responseData = logoutCheckResponse(response)
                    logoutUser.sessionToken = null
                    val user = NCMBUser(responseData)
                    //loginCallback done to object
                    logoutCallback.done(null, user)
                }
                is NCMBResponse.Failure -> {
                    logoutCallback.done(response.resException)
                }
            }
        }
        val reqParams: RequestParams = logoutParams(logoutCallback, logoutHandler)
        sendRequestAsync(reqParams, logoutCallback,logoutHandler)
    }

    /**
     * Login by user name in background
     *
     * @param userName user name
     * @param password password
     * @param loginCallback callback when process finished
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
     * Internal method to update user
     *
     * @param params parameters
     * @param oauth  use oauth or not
     * @param signUpCallback callback when process finished
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun signUpUserInBackground(params: JSONObject, oauth: Boolean, signUpCallback: NCMBCallback) {
        val signUpHandler = NCMBHandler { signupcallback, response ->
            when (response) {
                is NCMBResponse.Success -> {
                    val responseData = signUpUserCheckResponse(response, oauth)
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
        val reqParams : RequestParams = signUpUserParams(null, params, signUpCallback, signUpHandler)
        sendRequestAsync(reqParams, signUpCallback, signUpHandler)
    }

    /**
     * Internal method to login user
     *
     * @param params parameters
     * @param loginCallback callback when process finished
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    protected fun loginUserInBackground(query: JSONObject, loginCallback: NCMBCallback){
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
        val reqParams : RequestParams = loginByNameParams(query, loginCallback, loginHandler)
        sendRequestAsync(reqParams, loginCallback, loginHandler)
    }

    /**
     * Set up to update user information
     *
     * @param userId user id
     * @param params update values
     * @return parameters for NCMBRequest
     */
    fun updateUserParams(userId: String, params: JSONObject, updateCallback: NCMBCallback?, updateHandler: NCMBHandler?): RequestParams {
        val url = NCMB.getApiBaseUrl() + this.mServicePath + "/" + userId
        val method = NCMBRequest.HTTP_METHOD_PUT
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        val reqParams = RequestParams(
            url = url,
            method = method,
            params = params,
            contentType = contentType,
            callback = updateCallback,
            handler = updateHandler
        )
        return reqParams
    }

    /**
     * Setup params to update new user in background
     *
     * @param params user parameters
     * @return parameters in object
     * @param signUpCallback callback when process finished
     * @param signUpHandler sdk after-connection tasks
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    fun signUpUserParams(userId: String?, params: JSONObject, signUpCallback: NCMBCallback?, signUpHandler: NCMBHandler?): RequestParams {
        val url = if(userId != null) {
            NCMB.getApiBaseUrl() + this.mServicePath + "/" + userId
        } else{
            NCMB.getApiBaseUrl() + this.mServicePath
        }
        val method = if(userId != null) {
            NCMBRequest.HTTP_METHOD_PUT
        } else{
            NCMBRequest.HTTP_METHOD_POST
        }
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        return RequestParams(url = url, method = method, params = params, contentType = contentType, callback = signUpCallback, handler = signUpHandler)
    }

    /**
     * Setup params to login by user name
     *
     * @param userName user name
     * @param password password
     * @return parameters in object
     * @param loginCallback callback when process finished
     * @param loginHandler sdk after-connection tasks
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    fun loginByNameParams(query: JSONObject, loginCallback: NCMBCallback?, loginHandler: NCMBHandler?): RequestParams {
        val url = NCMB.getApiBaseUrl() + "login?" + queryUrlStringGenerate(query)
        val method = NCMBRequest.HTTP_METHOD_GET
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        return RequestParams(url = url, method = method,  contentType = contentType, query = query, callback = loginCallback, handler = loginHandler)
    }

    /**
     * Setup parameters to logout

     * @param logoutCallback callback when process finished
     * @param logoutHandler sdk after-connection tasks
     * @return request params in object
     */
    protected fun logoutParams(logoutCallback: NCMBCallback?, logoutHandler: NCMBHandler?): RequestParams {
        val url = NCMB.getApiBaseUrl() + "logout"
        val method = NCMBRequest.HTTP_METHOD_GET
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        return RequestParams(url = url, method = method, contentType = contentType, callback = logoutCallback, handler = logoutHandler)
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
        val reqParams = updateUserParams(userId, params, null, null)
        val response = sendRequest(reqParams)
        //update currentUser
        try {
            params.put("objectId", userId)
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, e.localizedMessage)
        }
        val responseData = updateUserCheckResponse(response)
        updateObject.reflectResponse(responseData)
        writeCurrentUser(params, responseData)
        return responseData
    }

    /**
     * Check response to signUp new user
     *
     * @param response
     * @param oauth    use oauth or not
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    fun signUpUserCheckResponse(response: NCMBResponse, oauth: Boolean): JSONObject {
        when(response) {
            is NCMBResponse.Success -> {
                if (response.resCode == HTTP_STATUS_SIGNUPED) {
                    return response.data as JSONObject
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
                response.data as JSONObject
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
    }

    /**
     * Check response to login user information
     *
     * @param response
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    fun loginByNameCheckResponse(response: NCMBResponse): JSONObject {
        return when(response) {
            is NCMBResponse.Success -> {
                if (response.resCode !== HTTP_STATUS_AUTHORIZED) {
                    throw NCMBException(NCMBException.AUTH_FAILURE, "Login failed")
                }
                response.data as JSONObject
            }
            is NCMBResponse.Failure -> {
                throw response.resException
            }
        }
    }

    /**
     * Check response to logout user information
     *
     * @param response
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    fun logoutCheckResponse(response: NCMBResponse): JSONObject{
        return when(response) {
            is NCMBResponse.Success -> {
                if (response.resCode !== HTTP_STATUS_AUTHORIZED) {
                    throw NCMBException(NCMBException.AUTH_FAILURE, "Logout failed")
                }
                response.data as JSONObject
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
            throw NCMBException(NCMBException.NOT_EFFICIENT_VALUE, error.localizedMessage)
        }
    }

    /**
     * process after login
     *
     * @param responseData response object
     * @return NCMBUser object
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    fun postLoginProcess(responseData: JSONObject): NCMBUser {
        try {
            val result: JSONObject = responseData
            val userId = result.getString("objectId")
            // signUp with login, sessionToken updated
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
     * @param params update current user values
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
            NCMBUser.currentuser = NCMBUser(currentData)

            if (currentData.has("sessionToken")) {
                try {
                    NCMB.SESSION_TOKEN = currentData.getString("sessionToken")
                } catch (e: JSONException) {
                    throw NCMBException(NCMBException.INVALID_JSON, e.localizedMessage)
                }
            }
        }
    }

    //Todo 匿名認証
    /**
     * Setup OAuth parameters to signUp new user with OAuth
     *
     * @param oauthOptions OAuth options
     * @return "authData" params in JSONObject
     * @throws NCMBException
     */
//    @Throws(NCMBException::class)
//    fun signUpByOauthSetup(oauthOptions: JSONObject): JSONObject {
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
     * signUp new user by OAuth services
     *
     * @param oauthOptions OAuth options
     * @return new NCMBUser object that signUped
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
//    @Throws(NCMBException::class)
//    fun signUpByOauth(oauthOptions: JSONObject): NCMBUser {
//        val params = signUpByOauthSetup(oauthOptions)
//        return signUpUser(params, true)
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
        val reqParams: RequestParams = getUserParams(userId,null, null)
        val response = sendRequest(reqParams)
        val responseData = getUserCheckResponse(response)
        fetchObject.reflectResponse(responseData)
        return NCMBUser(responseData)
    }

    /**
     * Get user entity from given id
     *
     * @param userId user id
     * @param fetchCallback callback when process finished
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
                    NCMBBase().mFields = user.mFields
                    //loginCallback done to object
                    fetchCallback.done(null, user);
                }
                is NCMBResponse.Failure -> {
                    fetchCallback.done(response.resException)
                }
            }
        }
        val reqParams : RequestParams = getUserParams(userId, fetchCallback, fetchHandler)
        sendRequestAsync(reqParams, fetchCallback, fetchHandler)
    }

    /**
     * Delete user by given id
     *
     * @param userId user id
     * @throws NCMBException exception sdk internal or NIFCLOUD mobile backend
     */
    @Throws(NCMBException::class)
    fun deleteUser(deleteObject: NCMBObject, userId: String) {
        val reqParams: RequestParams = deleteUserParams(userId, null, null)
        val response = sendRequest(reqParams)
        val responseData = deleteUserCheckResponse(response)
        deleteObject.reflectResponse(responseData)
        if (userId == NCMBUser().getCurrentUser().getObjectId()) {
            // unsignUp login informations
            clearCurrentUser()
        }
    }

    /**
     * Delete user by given id
     *
     * @param userId user id
     * @param deleteCallback callback when process finished
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

                    //loginCallback done to object
                    deleteCallback.done(null, user);
                }
                is NCMBResponse.Failure -> {
                    deleteCallback.done(response.resException)
                }
            }
        }
        val reqParams: RequestParams = deleteUserParams(userId, deleteCallback, deleteHandler)
        sendRequestAsync(reqParams, deleteCallback, deleteHandler)
        if (userId == NCMBUser().getCurrentUser().getObjectId()) {
            // unupdate login informations
            clearCurrentUser()
        }
    }

    /**
     * Setup params to get user entity
     *
     * @param userId user id
     * @param fetchCallback callback when process finished
     * @param fetchHandler sdk after-connection tasks
     * @return parameters for NCMBRequest
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    protected fun getUserParams(userId: String, fetchCallback: NCMBCallback?, fetchHandler: NCMBHandler?): RequestParams {
        val url = NCMB.getApiBaseUrl() + mServicePath + "/" + userId
        val method = NCMBRequest.HTTP_METHOD_GET
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        return RequestParams(url = url, method = method, contentType = contentType, callback = fetchCallback, handler = fetchHandler)
    }

    /**
     * Setup params to delete user
     *
     * @param userId user id
     * @param deleteCallback callback when process finished
     * @param deleteHandler sdk after-connection tasks
     * @return parameters in object
     */
    protected fun deleteUserParams(userId: String, deleteCallback: NCMBCallback?, deleteHandler: NCMBHandler?): RequestParams {
        val url = NCMB.getApiBaseUrl() + mServicePath + "/" + userId
        val method = NCMBRequest.HTTP_METHOD_DELETE
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        return RequestParams(url = url, method = method, contentType = contentType, callback = deleteCallback, handler = deleteHandler)
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
                response.data as JSONObject
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
                response.data as JSONObject
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
        NCMBUser.currentuser = null
        NCMB.SESSION_TOKEN = null
        NCMB.USER_ID = null
    }

    private fun validateClassName(className: String?): Boolean {
        return (className == null || className.isEmpty())
    }

    private fun validateObjectId(objectId: String?): Boolean {
        return (objectId == null || objectId.isEmpty())
    }

    /**
     * Setup params to do find request for Query search functions
     *
     * @param className Class name
     * @param query JSONObject
     * @return parameters in object
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    override fun findObjectParams(className: String, query:JSONObject): RequestParams {
        var url = NCMB.getApiBaseUrl() + this.mServicePath
        if(query.length() > 0) {
            url = url.plus("?" + queryUrlStringGenerate(query))
        }
        val method = NCMBRequest.HTTP_METHOD_GET
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        return RequestParams(url = url, method = method, contentType = contentType, query=query)
    }

    @Throws(NCMBException::class)
    override fun createSearchResponseList(className: String, responseData: JSONObject): List<NCMBUser> {
        return try {
            val results = responseData.getJSONArray(NCMBQueryConstants.RESPONSE_PARAMETER_RESULTS)
            val array: MutableList<NCMBUser> = ArrayList()
            for (i in 0 until results.length()) {
                val tmpObj = NCMBUser(results.getJSONObject(i))
                array.add(tmpObj)
            }
            array
        } catch (e: JSONException) {
            throw NCMBException(NCMBException.INVALID_JSON, "Invalid JSON format.")
        }
    }

    /**
     * Setup params to do count request for Query search functions
     *
     * @param className Class name
     * @param query JSONObject
     * @return parameters in object
     * @throws NCMBException
     */
    @Throws(NCMBException::class)
    override fun countObjectParams(className: String, query:JSONObject): RequestParams {
        var url = NCMB.getApiBaseUrl() + this.mServicePath
        if(query.length() > 0) {
            url = url.plus("?" + queryUrlStringGenerate(query))
        }
        val method = NCMBRequest.HTTP_METHOD_GET
        val contentType = NCMBRequest.HEADER_CONTENT_TYPE_JSON
        return RequestParams(url = url, method = method, contentType = contentType, query = query)
    }
}
