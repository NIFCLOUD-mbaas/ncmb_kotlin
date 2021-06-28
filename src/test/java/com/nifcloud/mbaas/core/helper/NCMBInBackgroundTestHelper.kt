package com.nifcloud.mbaas.core.helper

import java.util.concurrent.Semaphore
import kotlin.concurrent.thread

/**
 * 非同期処理メソッドをテストする際のヘルパークラスです。
 *
 * Semaphore を利用し、コールバック内部で release() が呼び出されるまで、
 * ないしは制限時間を超えるまでは await() にて処理をブロックします。
 *
 * 使用例:
 * ```kotlin
 * @Test
 * fun NCMBObjectSaveInBackgroundWithPostDataSaveSuccess() {
 *     val inBackgroundHelper = NCMBInBackgroundTestHelper() // ヘルパーの初期化
 *     val obj = NCMBObject("TestClass")
 *     var callback = NCMBCallback { e, result ->
 *         // コールバック処理内では Assert の結果を反映できないため、検証したい変数などを格納する
 *         inBackgroundHelper["e"] = e
 *         inBackgroundHelper["ncmbObj"] = ncmbObj
 *         inBackgroundHelper.release() // ブロックをリリース
 *     }
 *     obj.put("key", "value")
 *     // start() 以後、release() が呼び出さる、または制限時間まで await() にて処理をブロックする。
 *     inBackgroundHelper.start()
 *     obj.saveInBackground(callback!!) // テスト対象の非同期処理
 *     inBackgroundHelper.await() // ブロック
 *     // 制限時間内に release() が呼び出されブロックが解除された場合、isCalledRelease() が true となる
 *     Assert.assertTrue(inBackgroundHelper.isCalledRelease())
 *     // ブロック解除後にコールバック処理内で受け取った変数の検証を行う
 *     Assert.assertNull(inBackgroundHelper["e"])
 *     Assert.assertEquals(
 *          (inBackgroundHelper["ncmbObj"] as NCMBObject).getObjectId(),
 *          "7FrmPTBKSNtVjajm")
 * }
 * ```
 *
 */
class NCMBInBackgroundTestHelper {
    private val semaphore: Semaphore = Semaphore(1)
    private val record: HashMap<String, Any?> = HashMap<String, Any?>()
    private var isCalledRelease = false

    /**
     * release() が呼び出される、または制限時間を超えるまでは await() にてブロックする処理を開始します。
     */
    fun start() {
        semaphore.acquire()
    }

    /**
     * ブロックを解除します。
     */
    fun release() {
        isCalledRelease = true
        semaphore.release()
    }

    /**
     * release() が呼び出される、または制限時間を超えるまで処理をブロックします。
     *
     * @param limitMills 制限時間(単位:ミリ秒)。デフォルトは 10000msec。
     */
    fun await(limitMills: Long = 10000L) {
        thread {
            Thread.sleep(limitMills)
            semaphore.release()
        }
        semaphore.acquire()
    }

    /**
     * release() が呼び出されているかを返します。
     *
     * @return release() が呼び出されていれば true を、そうでない場合は false。
     */
    fun isCalledRelease(): Boolean {
        return isCalledRelease
    }

    /**
     * コールバック外で評価したい値を格納します。
     *
     * @param key 格納キー（取得時に利用）
     * @param value 格納する値
     */
    operator fun set(key: String, value: Any?) {
        record[key] = value
    }

    /**
     * コールバック内で格納された値を取得します。
     *
     * @param key 格納キー（格納時に設定）
     * @return value 格納された値
     */
    operator fun get(key: String): Any? {
        return record[key]
    }
}

