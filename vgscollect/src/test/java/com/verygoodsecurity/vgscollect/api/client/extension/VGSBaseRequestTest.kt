package com.verygoodsecurity.vgscollect.api.client.extension

import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.tokenization.VGSTokenizationRequest
import com.verygoodsecurity.vgscollect.util.extension.toNetworkRequest
import org.junit.Assert
import org.junit.Test

class VGSBaseRequestTest {

    @Test
    fun tokenization_request_to_network_request() {
        val correctRequest = VGSTokenizationRequest.VGSRequestBuilder()
            .setRouteId(ROUTE_ID)
            .build().toNetworkRequest(HOST)

        Assert.assertEquals(
            "https://tnt-$ROUTE_ID.sandbox.vgs.com/tokens",
            correctRequest.url
        )

        val wrongRequest = VGSTokenizationRequest.VGSRequestBuilder()
            .setRouteId("")
            .build().toNetworkRequest(HOST)

        Assert.assertEquals(
            "$HOST/tokens",
            wrongRequest.url
        )
    }

    @Test
    fun collect_request_to_network_request() {
        val correctRequest = VGSRequest.VGSRequestBuilder()
            .setPath(ENDPOINT)
            .setRouteId(ROUTE_ID)
            .build().toNetworkRequest(HOST)

        Assert.assertEquals(
            "https://tnt-$ROUTE_ID.sandbox.vgs.com$ENDPOINT",
            correctRequest.url
        )

        val wrongRequest = VGSRequest.VGSRequestBuilder()
            .setPath(ENDPOINT)
            .setRouteId(ROUTE_ID)
            .setRouteId("")
            .build().toNetworkRequest(HOST)

        Assert.assertEquals(
            HOST + ENDPOINT,
            wrongRequest.url
        )
    }

    companion object {
        private const val HOST = "https://tnt.sandbox.vgs.com"
        private const val ENDPOINT = "/post"
        private const val ROUTE_ID = "1111-2222-3333-4444"
    }
}