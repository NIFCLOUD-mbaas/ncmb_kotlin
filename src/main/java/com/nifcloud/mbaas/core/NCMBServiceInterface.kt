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

import org.json.JSONObject

/**
 * Service interface.
 *
 * Find, count method handling service interface.
 *
 */

internal interface NCMBServiceInterface<T:NCMBObject> {

    fun find(className: String, query:JSONObject): List<T>

    fun findInBackground( className: String, query:JSONObject, findCallback: NCMBCallback)

    fun count(className: String, query:JSONObject): Int

    fun countInBackground( className: String, query:JSONObject, countCallback: NCMBCallback)
}
