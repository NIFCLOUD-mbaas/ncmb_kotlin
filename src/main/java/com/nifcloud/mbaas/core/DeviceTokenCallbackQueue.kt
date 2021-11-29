package com.nifcloud.mbaas.core

import java.util.*

internal class DeviceTokenCallbackQueue private constructor() {
    private var queue: Queue<NCMBCallback> = ArrayDeque()
    val isDuringSaveInstallation: Boolean
        get() = queue.isNotEmpty()

    fun beginSaveInstallation() {
        if (queue.isEmpty()) {
            queue = LinkedList<NCMBCallback>()
        }
    }

    fun addQueue(callback: NCMBCallback) {
        beginSaveInstallation()
        queue.add(callback)
    }

    fun execQueue(token: String?, e: NCMBException?) {
        if (queue.isEmpty()) {
            return
        }
        var callback: NCMBCallback? = queue.poll()
        while (callback != null) {
            callback.done(e, token)
            callback = queue.poll()
        }
        endSaveInstallation()
    }

    fun endSaveInstallation() {
        queue = ArrayDeque()
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