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
    fun done(e: NCMBException?, objList: ArrayList<NCMBObject>) {
        //do sthing here for callback
        passCallback(e, objList)
    }

}
