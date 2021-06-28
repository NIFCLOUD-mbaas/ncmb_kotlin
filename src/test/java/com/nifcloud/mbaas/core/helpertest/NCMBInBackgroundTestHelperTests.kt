package com.nifcloud.mbaas.core.helpertest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nifcloud.mbaas.core.helper.NCMBInBackgroundTestHelper
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.concurrent.thread

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(27), manifest = Config.NONE)

/**
 * NCMBInBackgroundTestHelper のテストクラスです。
 */
class NCMBInBackgroundTestHelperTests {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    /**
     * 制限時間内に別スレッドの release() が呼ばれた場合、ブロックが解除される
     */
    @Test
    fun test_call_release_before_await_limit() {
        val sut = NCMBInBackgroundTestHelper()
        sut.start()
        thread {
            Thread.sleep(1000L) // lt 2000L
            sut.release()
        }
        sut.await(2000L)
        Assert.assertTrue(sut.isCalledRelease())
    }

    /**
     * 制限時間より後に別スレッドの release() が呼ばれた場合、制限時間でブロックが解除される
     */
    @Test
    fun test_call_release_after_await_limit() {
        val sut = NCMBInBackgroundTestHelper()
        sut.start()
        thread {
            Thread.sleep(3000L) // gt 2000L
            sut.release()
        }
        sut.await(2000L)
        Assert.assertFalse(sut.isCalledRelease())
    }

    /**
     * 別スレッドの release() が呼び出されない場合、制限時間でブロックが解除される
     */
    @Test
    fun test_dont_call_release_after_await_limit() {
        val sut = NCMBInBackgroundTestHelper()
        sut.start()
        thread {
            Thread.sleep(1000L) // lt 2000L
        }
        sut.await(2000L)
        Assert.assertFalse(sut.isCalledRelease())
    }

    /**
     * 別スレッドで設定された値を await() ブロック処理後に評価できる。
     */
    @Test
    fun test_set_get() {
        val sut = NCMBInBackgroundTestHelper()
        sut.start()
        thread {
            val map: MutableMap<String, String> = HashMap()
            map["takanosan"] = "mobile backend"
            sut["takano-kun"] = map
            sut.release()
        }
        sut.await(2000L)
        Assert.assertEquals(
            (sut["takano-kun"] as MutableMap<*, *>)["takanosan"],
            "mobile backend"
        )
    }
}