package com.verygoodsecurity.vgscollect

import com.verygoodsecurity.vgscollect.core.*
import com.verygoodsecurity.vgscollect.core.api.ApiClient
import com.verygoodsecurity.vgscollect.core.api.URLConnectionClient
import com.verygoodsecurity.vgscollect.core.model.VGSResponse
import com.verygoodsecurity.vgscollect.core.storage.VgsStore
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito

class ApiClientTest {

    private fun <T> anyObject(): T {
        Mockito.any<T>()
        return uninitialized()
    }
    private fun <T> uninitialized(): T = null as T

    @Test
    fun test_Api_Call() {
        val client = Mockito.mock(ApiClient::class.java)

        val collect = VGSCollect("tennantid", Environment.SANDBOX)

        collect.setClient(client)
        collect.setStorage(Mockito.mock(VgsStore::class.java))

        var res:VGSResponse? = null
        collect.addOnResponseListeners(object : VgsCollectResponseListener {
            override fun onResponse(response: VGSResponse?) {
                res = response
            }
        })

        Mockito.doReturn(VGSResponse.SuccessResponse(successCode = 200))
            .`when`(client).call(Mockito.anyString(),
                anyObject()
                , Mockito.anyMap<String, String>(), Mockito.anyMap<String, String>())


        collect.doMainThreadRequest("/post", HTTPMethod.POST, mutableMapOf(), mutableListOf())

        Mockito.verify(client).call("/post", HTTPMethod.POST, mutableMapOf(), mutableMapOf())

        assertNotNull(res)
        assertEquals(200, res?.code)
    }

    @Test
    fun testTempStoreCustomData() {
        val client = URLConnectionClient()

        assertEquals(0, client.getTemporaryStorage().getCustomData().size)

        val data = HashMap<String, String>()
        data["key"] = "value"
        client.getTemporaryStorage().setCustomData(data)

        assertEquals(1, client.getTemporaryStorage().getCustomData().size)
    }

    @Test
    fun testTempStoreCustomHeaders() {
        val client = URLConnectionClient()

        assertEquals(0, client.getTemporaryStorage().getCustomHeaders().size)

        val headers = HashMap<String, String>()
        headers["key"] = "value"
        client.getTemporaryStorage().setCustomHeaders(headers)

        assertEquals(1, client.getTemporaryStorage().getCustomHeaders().size)
    }

    @Test
    fun testTempStoreCustomDataReset() {
        val client = URLConnectionClient()

        val data = HashMap<String, String>()
        data["key"] = "value"
        client.getTemporaryStorage().setCustomData(data)

        client.getTemporaryStorage().resetCustomData()

        assertEquals(0, client.getTemporaryStorage().getCustomData().size)
    }

    @Test
    fun testTempStoreCustomHeadersReset() {
        val client = URLConnectionClient()

        val data = HashMap<String, String>()
        data["key"] = "value"
        client.getTemporaryStorage().setCustomHeaders(data)

        client.getTemporaryStorage().resetCustomHeaders()

        assertEquals(0, client.getTemporaryStorage().getCustomHeaders().size)
    }

    @Test
    fun testTempStoreGetCustomData() {
        val key = "anyKey"
        val value = "anyValue"

        val client = URLConnectionClient()

        assertEquals(0, client.getTemporaryStorage().getCustomData().size)

        val data = HashMap<String, String>()
        data[key] = value
        client.getTemporaryStorage().setCustomData(data)

        val testValue = client.getTemporaryStorage().getCustomData()[key]

        assertEquals(value, testValue)
    }

    @Test
    fun testTempStoreGetCustomHeaders() {
        val key = "anyKey"
        val value = "anyValue"

        val client = URLConnectionClient()

        assertEquals(0, client.getTemporaryStorage().getCustomHeaders().size)

        val data = HashMap<String, String>()
        data[key] = value
        client.getTemporaryStorage().setCustomHeaders(data)

        val testValue = client.getTemporaryStorage().getCustomHeaders()[key]

        assertEquals(value, testValue)
    }
}