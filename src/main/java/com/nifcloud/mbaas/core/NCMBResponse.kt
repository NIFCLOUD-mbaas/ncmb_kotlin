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

import okhttp3.Headers
import org.json.JSONObject

/**
 * A class of ncmb_kotlin.
 *
 * NCMBResponse contains response data from NIFCLOUD mobile backend
 *
 */
internal sealed class NCMBResponse {
    data class Success(val resCode: Int, val resHeaders: Headers, val data: Any?) : NCMBResponse()
    //data class SuccessFile(val resCode: Int, val resHeaders: Headers, val data: ByteArray?) : NCMBResponse()
    data class Failure(val resException: NCMBException) : NCMBResponse()
}
