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

import android.util.Log

class NCMBInstallationUtils {
    //Todo
//    private val THIS_DEVICE_NOT_SUPPORTED_MESSAGE = "This device is not supported google-play-services-APK."
//    protected fun saveInstallation(installationCustomFields: Map<String?, String?>?) {
//        DeviceTokenCallbackQueue.instance?.beginSaveInstallation()
//        try {
////            //端末にAPKがインストールされていない場合は処理を終了
////            if (!checkPlayServices(NCMB.getCurrentContext().context)) {
////                DeviceTokenCallbackQueue.getInstance().execQueue(
////                    null, NCMBException(
////                        IllegalArgumentException(THIS_DEVICE_NOT_SUPPORTED_MESSAGE)
////                    )
////                )
////                return
////            }
//            val installation: NCMBInstallation? = NCMBInstallation.getCurrentInstallation()
//            if (installationCustomFields != null) {
//                for ((key, value) in installationCustomFields) {
//                    if (key != null && value != null) {
//                        installation?.put(key, value)
//                    }
//                }
//            }
//
//            installation?.getDeviceTokenInternalProcess() {
//                fun done(token: String?, e: NCMBException?) {
//                    if (e == null) {
//                        installation.deviceToken = token
//                        //端末情報をデータストアに登録
//                        installation.saveInBackground(object : DoneCallback() {
//                            fun done(saveErr: NCMBException?) {
//                                if (saveErr == null) {
//                                    //保存成功
//                                    DeviceTokenCallbackQueue.instance?.execQueue(token, null)
////                                } else if (NCMBException.DUPLICATE_VALUE == saveErr.code) {
////                                    //保存失敗 : registrationID重複
////                                    updateInstallation(installation)
////                                } else if (NCMBException.DATA_NOT_FOUND == saveErr.code) {
////                                    //保存失敗 : 端末情報の該当データがない
////                                    reRegistInstallation(installation)
//                                } else {
//                                    DeviceTokenCallbackQueue.instance?.execQueue(token, saveErr)
//                                }
//                            }
//                        })
//                    } else {
//                        DeviceTokenCallbackQueue.instance?.execQueue(token, e)
//                    }
//                }
//            })
//        } catch (e: NoClassDefFoundError) {
//            Log.i(
//                "INFO",
//                "For Push Notification function, you must be install Google Play Services in SDK Manager and add the FCM dependency. More information: https://mbaas.nifcloud.com/doc/current/push/basic_usage_android.html"
//            )
//            DeviceTokenCallbackQueue.instance?.execQueue(
//                null, NCMBException(
//                    IllegalArgumentException(THIS_DEVICE_NOT_SUPPORTED_MESSAGE)
//                )
//            )
//        } catch (e: Exception) {
//            Log.e("Error", e.toString())
//            DeviceTokenCallbackQueue.instance?.execQueue(
//                null, NCMBException(
//                    IllegalArgumentException(THIS_DEVICE_NOT_SUPPORTED_MESSAGE)
//                )
//            )
//        }
//    }
}