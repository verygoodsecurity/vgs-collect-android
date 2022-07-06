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
        val BASE_URL = "base.url"
        val METHOD = HTTPMethod.POST
        val PATH = TOKENIZATION_PATH

        val exampleRequest = NetworkRequest(
            METHOD,
            BASE_URL + PATH,
            emptyMap(),
            "{}",
            false,
            false,
            VGSHttpBodyFormat.JSON,
            60000L,
            true
        )

        val r = VGSTokenizationRequest.VGSRequestBuilder()
            .build().toNetworkRequest(BASE_URL, emptyMap())

        Assert.assertEquals(exampleRequest, r)
    }

    @Test
    fun test_create_request_with_route_id() {
        val ROUTE_ID = "route-id"
        val r = VGSTokenizationRequest.VGSRequestBuilder()
            .setRouteId(ROUTE_ID)
            .build()

        Assert.assertEquals(ROUTE_ID, r.routeId)
    }


    companion object {
        private const val TOKENIZATION_PATH = "/tokens"
    }

}