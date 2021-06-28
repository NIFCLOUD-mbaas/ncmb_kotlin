package com.nifcloud.mbaas.core

import android.app.Application

/**
 * A class of ncmb_kotlin.
 *
 * Holding the state of the application
 *
 */

class NCMBApplicationController : Application() {
    /**
     * Set application state
     */
    override fun onCreate() {
        super.onCreate()
        applicationState = this
    }

    companion object {
        /**
         * Get application state
         * @return ApplicationController
         */
        var applicationState: NCMBApplicationController? = null
    }
}
