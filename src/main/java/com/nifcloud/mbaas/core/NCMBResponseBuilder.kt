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

import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject

internal class NCMBResponseBuilder {

    companion object {
        /** http status for success  */
        const val HTTP_STATUS_OK = 200
        const val HTTP_STATUS_CREATED = 201

        fun build(response: Response): NCMBResponse {

            //通信結果文字列
            var responseDataJson= JSONObject()
            var responseError: NCMBException

            /**
             * API response
             *
             * @param responseCode   statusCode
             * @param responseHeaders responseHeaders
             * @param responseDataString String
             */
            //Connecting success and received response data from Server, start to solve response data to create
            //通信結果ステータスコード
            var statusCode = response.code
            var responseHeader = response.headers
            var responseDataString = response.body?.string()
            val contentType = response.headers["Content-Type"].toString()
            if (contentType == "application/json" || contentType == "application/json;charset=UTF-8") {
                // Set response json data
                try {
                    if(!responseDataString.isNullOrEmpty()) {
                        responseDataJson = JSONObject(responseDataString)
                    }
                } catch (e: JSONException) {
                    responseError = NCMBException(e)
                    return NCMBResponse.Failure(responseError)
                }
            }
            //Set error
            if (statusCode != HTTP_STATUS_OK &&
                statusCode != HTTP_STATUS_CREATED
            ) {
                /** mobile backendへのAPIリクエスト結果から取得したエラーコード  */
                var mbErrorCode = responseDataJson.getString("code")
                var mbErrorMessage = responseDataJson.getString("error")
                responseError = NCMBException(mbErrorCode, mbErrorMessage)
                //Checking invalid sessionToken
                invalidSessionToken(mbErrorCode)
                return NCMBResponse.Failure(responseError)
            }
            return NCMBResponse.Success(statusCode, responseHeader, responseDataJson)
        }


        //For File and script feature
        fun buildFileScriptResponse(response: Response): NCMBResponse {

            //通信結果文字列
            //var responseByteArray:ByteArray?
            var responseError: NCMBException
            var responseDataJson = JSONObject()

            //Connecting success and received response data from Server, start to solve response data to create
            //通信結果ステータスコード
            var statusCode = response.code
            var responseHeader = response.headers

            val contentType = response.headers["Content-Type"].toString()
            //println("Content type:" + contentType)
            //println("status Code:"  + statusCode)

            //Error case, json string response show error
            if (contentType == "application/json" || contentType == "application/json;charset=UTF-8") {
                var responseDataString = response.body?.string()
                // Set response json data
                try {
                    if(!responseDataString.isNullOrEmpty()) {
                        responseDataJson = JSONObject(responseDataString)
                        //Set error
                        if (statusCode != HTTP_STATUS_OK &&
                            statusCode != HTTP_STATUS_CREATED
                        ) {
                            /** mobile backendへのAPIリクエスト結果から取得したエラーコード  */
                            var mbErrorCode = responseDataJson.getString("code")
                            var mbErrorMessage = responseDataJson.getString("error")
                            responseError = NCMBException(mbErrorCode, mbErrorMessage)
                            //Checking invalid sessionToken
                            invalidSessionToken(mbErrorCode)
                            return NCMBResponse.Failure(responseError)
                        }
                        return NCMBResponse.Success(statusCode, responseHeader, responseDataJson)
                    }
                } catch (e: JSONException) {
                    responseError = NCMBException(e)
                    return NCMBResponse.Failure(responseError)
                }
            }
            var responseByteArray = response.body?.bytes()
            return NCMBResponse.Success(statusCode, responseHeader, responseByteArray)
        }

        /**
         * check invalid sessionToken
         * automatic logout when 'E404001' error
         *
         * @param code statusCode
         */
        fun invalidSessionToken(code: String) {
            if (NCMBException.INVALID_AUTH_HEADER == code) {
                NCMBUserService().clearCurrentUser()
            }
        }
    }
}
