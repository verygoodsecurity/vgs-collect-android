package com.verygoodsecurity.vgscollect.api

import com.verygoodsecurity.vgscollect.core.*
import com.verygoodsecurity.vgscollect.core.api.ApiClient
import org.junit.Test
import org.mockito.Mockito

class ApiClientTest {

    @Test
    fun test_Api_Call() {
        val client = Mockito.mock(ApiClient::class.java)

        val headers = HashMap<String, String>()
        headers.put("NEW-HEADER", "header")
        val data = HashMap<String, Any>()
        data.put("customData", "dataset")

        client.call("/post", HTTPMethod.POST, headers, data)

        Mockito.verify(client).call("/post", HTTPMethod.POST, headers, data)
    }

    @Test
    fun getStore() {
        val client = Mockito.mock(ApiClient::class.java)
        client.getTemporaryStorage()

        Mockito.verify(client).getTemporaryStorage()
    }
}