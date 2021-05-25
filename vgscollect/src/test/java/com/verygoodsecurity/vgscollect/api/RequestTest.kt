package com.verygoodsecurity.vgscollect.api

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.model.network.*
import org.junit.Assert.assertEquals
import org.junit.Test

class RequestTest {

    @Test
    fun test_create_default_request() {
        val r = VGSRequest.VGSRequestBuilder().build()
        assertEquals("", r.path)
        assertEquals(HTTPMethod.POST, r.method)
        assertEquals(HashMap<String, String>(), r.customData)
        assertEquals(HashMap<String, String>(), r.customHeader)
    }

    @Test
    fun test_create_request_with_custom_data() {
        val METHOD = HTTPMethod.POST
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
    fun test_create_request_without_custom_data() {
        val METHOD = HTTPMethod.POST
        val PATH = "/some/path"
        val r = VGSRequest.VGSRequestBuilder()
            .setMethod(METHOD)
            .setPath(PATH)
            .build()

        assertEquals(PATH, r.path)
        assertEquals(METHOD, r.method)
    }

    @Test
    fun test_to_network_request() {
        val BASE_URL = "base.url"
        val METHOD = HTTPMethod.POST
        val PATH = "/some/path"

        val exampleRequest = NetworkRequest(
            METHOD,
            BASE_URL+PATH,
            emptyMap(),
            "{}",
            false,
            false,
            VGSHttpBodyFormat.JSON,
            60000L
        )

        val r = VGSRequest.VGSRequestBuilder()
            .setMethod(METHOD)
            .setPath(PATH)
            .build().toNetworkRequest(BASE_URL, emptyMap())

        assertEquals(exampleRequest, r)
    }

    @Test
    fun test_to_response_success() {
        val BODY = "data"
        val CODE = 200

        val exampleRequest = VGSResponse.SuccessResponse(
            rawResponse = BODY,
            successCode = CODE
        )

        val r = NetworkResponse(
            true,
            BODY,
            CODE
        ).toVGSResponse() as VGSResponse.SuccessResponse

        assertEquals(exampleRequest.rawResponse, r.rawResponse)
        assertEquals(exampleRequest.body, r.body)
        assertEquals(exampleRequest.successCode, r.successCode)
    }

    @Test
    fun test_to_response_failed() {
        val MESSAGE = "error text"
        val CODE = 401

        val exampleRequest = VGSResponse.ErrorResponse(
            MESSAGE,
            CODE
        )

        val r = NetworkResponse(
            code = CODE,
            message = MESSAGE
        ).toVGSResponse() as VGSResponse.ErrorResponse

        assertEquals(exampleRequest.localizeMessage, r.localizeMessage)
        assertEquals(exampleRequest.code, r.code)
        assertEquals(exampleRequest.body, r.body)
    }

}