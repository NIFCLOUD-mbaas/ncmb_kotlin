package com.nifcloud.mbaas.core

import java.util.Queue
import java.util.LinkedList

internal class DeviceTokenCallbackQueue private constructor() {
    private var queue: Queue<NCMBCallback>? = null
    val isDuringSaveInstallation: Boolean
        get() = queue != null

    fun beginSaveInstallation() {
        if (queue == null) {
            queue = LinkedList<NCMBCallback>()
        }
    }

    fun addQueue(callback: NCMBCallback) {
        beginSaveInstallation()
        queue!!.add(callback)
    }

    fun execQueue(token: String?, e: NCMBException?) {
        if (queue == null) {
            return
        }
        var callback: NCMBCallback? = queue!!.poll()
        while (callback != null) {
            callback.done(e, token)
            callback = queue!!.poll()
        }
        endSaveInstallation()
    }

    fun endSaveInstallation() {
        queue = null
    }

    companion object {
        var instance: DeviceTokenCallbackQueue? = null
            get() {
                if (field == null) {
                    field = DeviceTokenCallbackQueue()
                }
                return field
            }
            private set
    }
}