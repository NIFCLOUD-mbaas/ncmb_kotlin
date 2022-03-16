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

import org.json.JSONObject

/**
 * Callback solving class
 *
 * This class represent callback tasks which is used to be passed to asynchronous methods.
 *
 */

class NCMBCallback(val passCallback: (NCMBException?, Any?) -> Unit) {

    /**
     * This method executes passed callback functions (in general).
     *
     * @param e NCMBException from NCMB
     */
    fun done(e: NCMBException?) {
        passCallback(e, null)
    }

    /**
     * This method executes passed callback functions (Mainly datastore functions).
     *
     * @param e NCMBException from NCMB
     * @param obj NCMBObject which is applied response from NCMB
     */
    fun done(e: NCMBException?, obj: NCMBObject) {
        passCallback(e, obj)
    }

    /**
     * This method executes passed callback functions (Mainly searching datastore functions).
     *
     * @param e NCMBException from NCMB
     * @param objList List of NCMBObject which is reflect search response data from NCMB
     */
    fun done(e: NCMBException?, objList: List<NCMBObject>) {
        passCallback(e, objList)
    }

    /**
     * This method executes passed callback functions (Mainly user login functions).
     *
     * @param e NCMBException from NCMB
     * @param token Session token response data from NCMB
     */
    fun done(e: NCMBException?, token: String?){
        passCallback(e, token)
    }

    /**
     * This method executes passed callback functions (Mainly count data functions).
     *
     * @param e NCMBException from NCMB
     * @param countNumber Count data reflect count response data from NCMB
     */
    fun done(e: NCMBException?, countNumber: Int) {
        passCallback(e, countNumber)
    }

    /**
     * This method executes passed callback functions (Mainly search data functions).
     *
     * @param e NCMBException from NCMB
     * @param responseData Response json object from NCMB
     */
    fun done(e: NCMBException?, responseData: JSONObject) {
        passCallback(e, responseData)
    }

    /**
     * This method executes passed callback functions (Mainly script functions).
     *
     * @param e NCMBException from NCMB
     * @param responseData Response json object from NCMB
     */
    fun done(e: NCMBException?, responseScript: ByteArray) {
        passCallback(e, responseScript)
    }

}
