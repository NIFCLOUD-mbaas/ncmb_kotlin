package com.nifcloud.mbaas.core

/**
 * A class of ncmb_kotlin.
 *
 * This class is to process sdk handler after finishing connection to NCMB, and
 * process after-connection tasks such as reflect response, return error, and do callback from user.
 *
 * @param handlerCallback handler callback for sdk after-connection tasks.
 * @constructor Creates a handler to receveive handlerCallback with parameters as callback and response.
 */

class NCMBHandler(val handlerCallback: (NCMBCallback?, NCMBResponse?) -> Unit) {

    //For NCMBConnection to solve NCMBResponse
    fun doneSolveResponse(callback: NCMBCallback?, response: NCMBResponse?) {
        //do sthing here for handler callback and response
        handlerCallback(callback, response)
    }

}
