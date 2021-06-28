package com.nifcloud.mbaas.core

import com.nifcloud.mbaas.core.NCMBDateFormat.getIso8601
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * 主に通信を行う自動化テストクラス
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27), manifest = Config.NONE)
class NCMBDateFormatTest {
    /**
     * putテスト
     */
    @Test
    fun UTC_check_test() {
        var format = getIso8601()
        Assert.assertEquals(format.timeZone.rawOffset, 0)
        Assert.assertEquals(format.timeZone.id, "UTC")
    }
}
