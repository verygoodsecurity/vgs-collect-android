package com.verygoodsecurity.vgscollect

import com.verygoodsecurity.vgscollect.core.*
import com.verygoodsecurity.vgscollect.core.api.ApiClient
import com.verygoodsecurity.vgscollect.core.api.VgsApiTemporaryStorageImpl
import com.verygoodsecurity.vgscollect.core.storage.VgsStore
import org.junit.Test
import org.mockito.Mockito

class VGSCollectTest {

    @Test
    fun testOnDestroyTest() {
        val store = Mockito.mock(VgsStore::class.java)

        val collect = VGSCollect("tnts")
        collect.setStorage(store)

        collect.onDestroy()
        Mockito.verify(store).clear()
    }


    @Test
    fun testGetAllStates() {
        val store = Mockito.mock(VgsStore::class.java)

        val collect = VGSCollect("tnts")
        collect.setStorage(store)

        collect.getAllStates()

        Mockito.verify(store).getStates()
    }

    @Test
    fun testSetCustomData() {
        val client = Mockito.mock(ApiClient::class.java)
        Mockito.doReturn(VgsApiTemporaryStorageImpl())
            .`when`(client).getTemporaryStorage()

        val collect = VGSCollect("tnts")
        collect.setClient(client)

        val data = HashMap<String, String>()
        data["key"] = "value"
        collect.setCustomHeaders(data)

        Mockito.verify(client).getTemporaryStorage()
    }

    @Test
    fun testSetCustomHeaders() {
        val client = Mockito.mock(ApiClient::class.java)
        Mockito.doReturn(VgsApiTemporaryStorageImpl())
            .`when`(client).getTemporaryStorage()

        val collect = VGSCollect("tnts")
        collect.setClient(client)

        val data = HashMap<String, String>()
        data["key"] = "value"
        collect.setCustomHeaders(data)

        Mockito.verify(client).getTemporaryStorage()
    }

    @Test
    fun testResetCustomData() {
        val client = Mockito.mock(ApiClient::class.java)
        Mockito.doReturn(VgsApiTemporaryStorageImpl())
            .`when`(client).getTemporaryStorage()

        val collect = VGSCollect("tnts")
        collect.setClient(client)

        collect.resetCustomData()

        Mockito.verify(client).getTemporaryStorage()
    }

    @Test
    fun testResetCustomHeaders() {
        val client = Mockito.mock(ApiClient::class.java)
        Mockito.doReturn(VgsApiTemporaryStorageImpl())
            .`when`(client).getTemporaryStorage()

        val collect = VGSCollect("tnts")
        collect.setClient(client)

        collect.resetCustomHeaders()

        Mockito.verify(client).getTemporaryStorage()
    }
}