package com.verygoodsecurity.vgscollect.api

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import org.junit.Assert.assertEquals
import org.junit.Test

class RequestTest {

    @Test
    fun test_create_default_VGSRequest() {
        val r = VGSRequest.VGSRequestBuilder().build()
        assertEquals("/post", r.path)
        assertEquals(HTTPMethod.POST, r.method)
        assertEquals(HashMap<String, String>(), r.customData)
        assertEquals(HashMap<String, String>(), r.customHeader)
    }

    @Test
    fun test_create_VGSRequest_with_custom_data() {
        val METHOD = HTTPMethod.GET
        val PATH = "/some/path"
        val headers = HashMap<String, String>()
        headers["HEADER-S"] = "some-data"
        val data = HashMap<String, String>()
        data["customdata"] = "some-data2"

        val r = VGSRequest.VGSRequestBuilder()
            .setMethod(METHOD)
            .setPath(PATH)
            .setCustomData(data)
            .setCustomHeader(headers)
            .build()

        assertEquals(PATH, r.path)
        assertEquals(METHOD, r.method)
        assertEquals(headers, r.customHeader)
        assertEquals(data, r.customData)
    }

    @Test
    fun test_create_VGSRequest_without_custom_data() {
        val METHOD = HTTPMethod.GET
        val PATH = "/some/path"
        val r = VGSRequest.VGSRequestBuilder()
            .setMethod(METHOD)
            .setPath(PATH)
            .build()

        assertEquals(PATH, r.path)
        assertEquals(METHOD, r.method)
    }

}