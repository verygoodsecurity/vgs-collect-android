package com.verygoodsecurity.vgscollect.api

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.model.network.*
import com.verygoodsecurity.vgscollect.core.model.network.tokenization.VGSTokenizationRequest
import com.verygoodsecurity.vgscollect.util.extension.toNetworkRequest
import org.junit.Assert
import org.junit.Test

class VGSTokenizationRequestTest {

    @Test
    fun test_create_default_request() {
        val r = VGSTokenizationRequest.VGSRequestBuilder().build()
        Assert.assertEquals(TOKENIZATION_PATH, r.path)
        Assert.assertEquals(HTTPMethod.POST, r.method)
        Assert.assertEquals(HashMap<String, String>(), r.customData)
        Assert.assertEquals(HashMap<String, String>(), r.customHeader)
    }

    @Test
    fun test_to_network_request() {
        val baseUrl = "base.url"
        val method = HTTPMethod.POST
        val path = TOKENIZATION_PATH

        val exampleRequest = NetworkRequest(
            method,
            baseUrl + path,
            emptyMap(),
            "{}",
            fieldsIgnore = false,
            fileIgnore = false,
            format = VGSHttpBodyFormat.JSON,
            requestTimeoutInterval = 60000L,
            requiresTokenization = true
        )

        val r = VGSTokenizationRequest.VGSRequestBuilder()
            .build().toNetworkRequest(baseUrl, emptyMap())

        Assert.assertEquals(exampleRequest, r)
    }

    @Test
    fun test_create_request_with_route_id() {
        val routeId = "route-id"
        val r = VGSTokenizationRequest.VGSRequestBuilder()
            .setRouteId(routeId)
            .build()

        Assert.assertEquals(routeId, r.routeId)
    }


    companion object {
        private const val TOKENIZATION_PATH = "/tokens"
    }

}