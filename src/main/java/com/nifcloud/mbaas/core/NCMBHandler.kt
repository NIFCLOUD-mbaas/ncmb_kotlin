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

/**
 * Internal SDK Handler (After get response from server) Tasks class
 *
 * This class is to process sdk handler after finishing connection to NCMB, and
 * process after-connection tasks such as reflect response, return error, and do callback from user.
 *
 * @param handlerCallback handler callback for sdk after-connection tasks.
 * @constructor Creates a handler to receveive handlerCallback with parameters as callback and response.
 */

internal class NCMBHandler(val handlerCallback: (NCMBCallback, NCMBResponse) -> Unit) {

    /**
     * For NCMBConnection to handler and solve NCMBResponse
     *
     * @param callback NCMBCallback
     * @param response NCMB Response
     */
    fun doneSolveResponse(callback: NCMBCallback, response: NCMBResponse) {
        //do sthing here for handler callback and response
        handlerCallback(callback, response)
    }

}
