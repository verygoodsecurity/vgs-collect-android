package com.verygoodsecurity.vgscollect.api

import com.verygoodsecurity.vgscollect.core.*
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.api.client.ApiClient
import com.verygoodsecurity.vgscollect.core.model.network.NetworkRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import org.junit.Test
import org.mockito.Mockito

class ApiClientTest {

    @Test
    fun test_api_execute() {
        val client = Mockito.mock(ApiClient::class.java)

        val headers = HashMap<String, String>()
        headers.put("NEW-HEADER", "header")
        val data = HashMap<String, Any>()
        data.put("customData", "dataset")

        val r = NetworkRequest(
            HTTPMethod.POST,
            "https://www.test.com/post",
            headers,
            data,
            false,
            false,
            VGSHttpBodyFormat.JSON,
            60000L
        )

        client.execute(r)

        Mockito.verify(client).execute(r)
    }

    @Test
    fun test_api_enqueue() {
        val client = Mockito.mock(ApiClient::class.java)

        val headers = HashMap<String, String>()
        headers.put("NEW-HEADER", "header")
        val data = HashMap<String, Any>()
        data.put("customData", "dataset")

        val r = NetworkRequest(
            HTTPMethod.POST,
            "https://www.test.com/post",
            headers,
            data,
            false,
            false,
            VGSHttpBodyFormat.JSON,
            60000L
        )

        client.enqueue(r)

        Mockito.verify(client).enqueue(r)
    }

    @Test
    fun getStore() {
        val client = Mockito.mock(ApiClient::class.java)
        client.getTemporaryStorage()

        Mockito.verify(client).getTemporaryStorage()
    }
}