package com.verygoodsecurity.vgscollect.api

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.model.network.*
import com.verygoodsecurity.vgscollect.util.extension.toNetworkRequest
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
        val method = HTTPMethod.POST
        val path = "/some/path"
        val headers = HashMap<String, String>()
        headers["HEADER-S"] = "some-data"
        val data = HashMap<String, String>()
        data["customdata"] = "some-data2"

        val r = VGSRequest.VGSRequestBuilder()
            .setMethod(method)
            .setPath(path)
            .setCustomData(data)
            .setCustomHeader(headers)
            .build()

        assertEquals(path, r.path)
        assertEquals(method, r.method)
        assertEquals(headers, r.customHeader)
        assertEquals(data, r.customData)
    }

    @Test
    fun test_create_request_with_route_id() {
        val routeId = "route-id"
        val method = HTTPMethod.POST
        val path = "/some/path"
        val r = VGSRequest.VGSRequestBuilder()
            .setMethod(method)
            .setPath(path)
            .setRouteId(routeId)
            .build()

        assertEquals(path, r.path)
        assertEquals(method, r.method)
        assertEquals(routeId, r.routeId)
    }

    @Test
    fun test_create_request_without_custom_data() {
        val method = HTTPMethod.POST
        val path = "/some/path"
        val r = VGSRequest.VGSRequestBuilder()
            .setMethod(method)
            .setPath(path)
            .build()

        assertEquals(path, r.path)
        assertEquals(method, r.method)
    }

    @Test
    fun test_to_network_request() {
        val baseUrl = "base.url"
        val method = HTTPMethod.POST
        val path = "/some/path"

        val exampleRequest = NetworkRequest(
            method,
            baseUrl+path,
            emptyMap(),
            "{}",
            false,
            fileIgnore = false,
            format = VGSHttpBodyFormat.JSON,
            requestTimeoutInterval = 60000L,
            requiresTokenization = false
        )

        val r = VGSRequest.VGSRequestBuilder()
            .setMethod(method)
            .setPath(path)
            .build().toNetworkRequest(baseUrl, emptyMap())

        assertEquals(exampleRequest, r)
    }

    @Test
    fun test_to_response_success() {
        val body = "data"
        val code = 200

        val exampleRequest = VGSResponse.SuccessResponse(
            rawResponse = body,
            successCode = code
        )

        val r = NetworkResponse(
            true,
            body,
            code
        ).toVGSResponse() as VGSResponse.SuccessResponse

        assertEquals(exampleRequest.rawResponse, r.rawResponse)
        assertEquals(exampleRequest.body, r.body)
        assertEquals(exampleRequest.successCode, r.successCode)
    }

    @Test
    fun test_to_response_failed() {
        val message = "error text"
        val code = 401

        val exampleRequest = VGSResponse.ErrorResponse(
            message,
            code
        )

        val r = NetworkResponse(
            code = code,
            message = message
        ).toVGSResponse() as VGSResponse.ErrorResponse

        assertEquals(exampleRequest.localizeMessage, r.localizeMessage)
        assertEquals(exampleRequest.code, r.code)
        assertEquals(exampleRequest.body, r.body)
    }

}