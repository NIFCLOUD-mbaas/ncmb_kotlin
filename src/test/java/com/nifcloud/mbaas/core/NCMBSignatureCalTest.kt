/*
 * Copyright 2017-2022 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
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

import org.junit.Test
import org.junit.Assert
import java.net.URL

class NCMBSignatureCalTest {

    //Signature test
    @Test
    fun testSignatureCal() {
        val tmpNCMBSignatureCal = NCMBSignatureCal()
        val url = URL("https://mbaas.api.nifcloud.com/2013-09-01/classes/test")
        val queryParamMap = HashMap<String, String>()
        val tmpSignature = tmpNCMBSignatureCal.calSignature("POST",
            url,
            "2020-03-30T05:35:37.974Z",
            "a69ec93ef132f0fdsfdsfds7d24b55d1926bd2c191c0eb054",
            "6afad6056749cb55dfdsggwwcxv54w06bdef688fdsfdsfdsfds3a695d0",
            queryParamMap
        )
        Assert.assertEquals("PypCIPOo1vASUWe1IUT5DWky/niMGqS7HWwFwKvj7NA=", tmpSignature)
    }

    //Signature test
    @Test
    fun testSignatureScriptCal() {
        val tmpNCMBSignatureCal = NCMBSignatureCal()
        val url = URL("https://script.mbaas.api.nifcloud.com/2015-09-01/script/testscript.js")
        val queryParamMap = HashMap<String, String>()
        val tmpSignature = tmpNCMBSignatureCal.calSignature("GET",
            url,
            "2022-03-14T02:44:06.046Z",
            "6145f91061916580c742f806bab67649d10f45920246ff459404c46f00ff3e56",
            "1343d198b510a0315db1c03f3aa0e32418b7a743f8e4b47cbff670601345cf75",
            queryParamMap
        )
        Assert.assertEquals("gvTa2jKaWCNwPvDK0vADjLUrFOYK/AeHuKK1u4UuC0U=", tmpSignature)
    }

    //Signature test with query Document Sample
    @Test
    fun testSignatureCalWithQueryDocumentSample() {
        val tmpNCMBSignatureCal = NCMBSignatureCal()
        val url = URL("https://mbaas.api.nifcloud.com/2013-09-01/classes/TestClass?where={\"testKey\":\"testValue\"}")
        val queryParaMap:HashMap<String, String> = hashMapOf("where" to "{\"testKey\":\"testValue\"}")
        val tmpSignature = tmpNCMBSignatureCal.calSignature("GET",
            url,
            "2013-12-02T02:44:35.452Z",
            "6145f91061916580c742f806bab67649d10f45920246ff459404c46f00ff3e56",
            "1343d198b510a0315db1c03f3aa0e32418b7a743f8e4b47cbff670601345cf75",
            queryParaMap
        )
        Assert.assertEquals("AltGkQgXurEV7u0qMd+87ud7BKuueldoCjaMgVc9Bes=", tmpSignature)
    }

    //Signature test with query with 2 parameters
    @Test
    fun testSignatureCalWithQuery2params() {
        val tmpNCMBSignatureCal = NCMBSignatureCal()
        val url = URL("https://mbaas.api.nifcloud.com/2013-09-01/classes/TestClass?where={\"test\":\"test\"}&order=-createDate")
        val queryParaMap:HashMap<String, String> = hashMapOf("where" to "{\"test\":\"test\"}",
                                                            "order" to "-createDate")
        val tmpSignature = tmpNCMBSignatureCal.calSignature("GET",
            url,
            "2020-06-22T04:58:21.128Z",
            "d2055f34f748bc0da5576ef3bc1bc7c2fea492cb775a1f583c5c183ff0b6ff2d",
            "dbe419dd750daf131aaade194a27fdsfdsfdsfdsfvcvc72001af764d3835cf3812",
            queryParaMap
        )
        Assert.assertEquals("FM/p6H9GXwimzoAQzKFA7kq2w+2zaux2pdw3C/g0Nx8=", tmpSignature)
    }

    //Signature test with query with 2 parameters
    @Test
    fun testSignatureCalWithQuery3params() {
        val tmpNCMBSignatureCal = NCMBSignatureCal()
        val url = URL("https://mbaas.api.nifcloud.com/2013-09-01/classes/TestClass?where={\"message\":\"Hello, NCMB!\"}&order=-createDate&limit=3")
        val queryParaMap:HashMap<String, String> = hashMapOf("where" to "{\"message\":\"Hello\"}",
                                                           "order" to "-createDate",
                                                            "limit" to "3")
        val tmpSignature = tmpNCMBSignatureCal.calSignature("GET",
            url,
            "2020-06-22T04:58:21.128Z",
            "5ae42d1c052d9c5bafdsfdsvcxvcxvseeeeeeexcxc1985c85c49c4054",
            "3e599d0811af4e39fd454dfsgfdgfgfdgfd454775cbec0da8df2d4b396d785",
            queryParaMap
        )

        Assert.assertEquals("HdbNMQsBEm/P/ojx2GpjSPyCTigCvh6Bo/8NZdClaho=", tmpSignature)
    }
}
