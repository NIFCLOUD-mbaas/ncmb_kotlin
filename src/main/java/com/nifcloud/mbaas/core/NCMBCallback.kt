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

/**
 * A class of ncmb_kotlin.
 *
 * To do handler callback from user in assynchronous task such as SaveInBackground etc.
 *
 */

class NCMBCallback(val passCallback: (NCMBException?, Any?) -> Unit) {

   //For pass callback from User
    fun done(e: NCMBException?, response: NCMBResponse) {
       //do sthing here for callback
        passCallback(e, response)
    }

    //For pass callback from User
    fun done(e: NCMBException?) {
        //do sthing here for callback
        passCallback(e, null)
    }

    //For pass callback from User
    fun done(e: NCMBException?, obj: NCMBObject) {
        //do sthing here for callback
        passCallback(e, obj)
    }

    //For pass callback from User
    fun done(e: NCMBException?, objList: List<NCMBObject>?) {
        //do sthing here for callback
        passCallback(e, objList)
    }

}
