package com.nifcloud.mbaas.core

import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert


class NCMBBaseTest {
    private var mServer: MockWebServer = MockWebServer()

    @Before
    @Throws(Exception::class)
    fun setup() {
        mServer = MockWebServer()
        mServer.start()
    }

    /**
     * putテスト
     */
    @Test
    fun put_string_test() {
        var baseObj = NCMBBase()
        baseObj.put("keyString", "stringValue")
        Assert.assertEquals(baseObj.get("keyString"), "stringValue")
    }

    /**
     * putテスト
     */
    @Test
    fun put_int_test() {
        var baseObj = NCMBBase()
        val setNumberValue: Int = 12
        baseObj.put("keyNumberInt", setNumberValue)
        Assert.assertEquals(baseObj.get("keyNumberInt"), setNumberValue)
    }

    /**
     * putテスト
     */
    @Test
    fun put_int_test_direct() {
        var baseObj = NCMBBase()
        baseObj.put("keyNumberInt", 1234)
        Assert.assertEquals(baseObj.get("keyNumberInt"), 1234)
    }

    /**
     * putテスト
     */
    @Test
    fun put_double_test_direct() {
        var baseObj = NCMBBase()
        baseObj.put("keyNumberDouble", 1234.33)
        Assert.assertEquals(baseObj.get("keyNumberDouble"), 1234.33)
    }

    /**
     * copyFromテスト
     */
    @Test
    fun copy_from_test() {
        var baseObj = NCMBBase()
        val json = JSONObject("{\"objectId\":\"xxxxx\",\"userName\":\"YamadaTarou\"}")
        Assert.assertEquals("xxxxx", json.getString("objectId"))
        Assert.assertEquals("YamadaTarou", json.getString("userName"))
        baseObj.copyFrom(json)
        var data = baseObj.mFields
        JSONAssert.assertEquals(json, data,false)
    }
}

