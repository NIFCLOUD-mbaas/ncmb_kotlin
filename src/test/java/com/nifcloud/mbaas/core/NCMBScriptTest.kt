package com.nifcloud.mbaas.core

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nifcloud.mbaas.core.helper.NCMBInBackgroundTestHelper
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(26), manifest = Config.NONE)
class NCMBScriptTest {

    private var mServer: MockWebServer = MockWebServer()
    private var callbackFlag = false

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()
    @Before
    fun setup() {
        val ncmbDispatcher = NCMBDispatcher("")
        mServer.dispatcher = ncmbDispatcher
        mServer.start()
        NCMB.initialize(
            RuntimeEnvironment.application.getApplicationContext(),
            "appKey",
            "cliKey",
            mServer.url("/").toString(),
            "2013-09-01"
        )

        callbackFlag = false;
    }

//    @Test
//    fun script_executeInBackground_success(){
//        val inBackgroundHelper = NCMBInBackgroundTestHelper()
//        val script = NCMBScript("testScript.js", NCMBScript.MethodType.GET)
//        val query = null
//        try{
//            inBackgroundHelper.start()
//            script.executeInBackground(null, null, query, NCMBCallback{ e, ncmbScript ->
//                inBackgroundHelper["e"] = e
//                inBackgroundHelper["ncmbScript"] = ncmbScript
//                inBackgroundHelper.release()
//            })
//            inBackgroundHelper.await()
//            Assert.assertTrue(inBackgroundHelper.isCalledRelease())
//            Assert.assertNull(inBackgroundHelper["e"])
//            Assert.assertEquals((inBackgroundHelper["ncmbScript"] as NCMBScript).scriptName, "testScript.js")
//        }
//        catch(e: NCMBException){
//            Assert.assertEquals(e.message, "")
//        }
//    }

    @Test
    fun script_method_test(){
        val scriptObj:NCMBScript = NCMBScript("testscript.js", NCMBScript.MethodType.GET)
        Assert.assertEquals(scriptObj.mScriptName, "testscript.js")
        Assert.assertEquals(scriptObj.mMethod, NCMBScript.MethodType.GET)
    }

}