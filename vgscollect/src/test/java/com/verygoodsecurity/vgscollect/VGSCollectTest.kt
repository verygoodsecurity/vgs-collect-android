package com.verygoodsecurity.vgscollect

import com.verygoodsecurity.vgscollect.core.*
import com.verygoodsecurity.vgscollect.core.api.ApiClient
import com.verygoodsecurity.vgscollect.core.api.VgsApiTemporaryStorageImpl
import com.verygoodsecurity.vgscollect.core.storage.VgsStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito
import java.util.regex.Pattern

class VGSCollectTest {

    @Test
    fun testInvalidTennantIdURL() {
        val testUrl1 = " "
        assertEquals("", VGSCollect(testUrl1).baseURL )

        val testUrl2 = "tnt.com"
        assertEquals("", VGSCollect(testUrl2).baseURL )

        val testUrl3 = "tnt com"
        assertEquals("", VGSCollect(testUrl3).baseURL )

        val testUrl4 = "2tnt/com"
        assertEquals("", VGSCollect(testUrl4).baseURL )

        val testUrl5 = "tnt:com"
        assertEquals("", VGSCollect(testUrl5).baseURL )

        val testUrl6 = "tnt*com"
        assertEquals("", VGSCollect(testUrl6).baseURL )

        val testUrl7 = "tnt?com"
        assertEquals("", VGSCollect(testUrl7).baseURL )
    }

    @Test
    fun testValidTennantIdURL() {
        val c = VGSCollect("tntTennId123")
        assertTrue(c.baseURL == "https://tntTennId123.sandbox.verygoodproxy.com")
    }

    @Test
    fun testEnvironmentByDefault() {
        val c = VGSCollect("tntabc")
        assertTrue(c.baseURL.contains("sandbox"))
    }

    @Test
    fun testEnvironmentSandboxByDefault() {
        val c = VGSCollect("abc", Environment.SANDBOX)
        assertTrue(c.baseURL.contains("sandbox"))
    }

    @Test
    fun testEnvironmentLiveByDefault() {
        val c = VGSCollect("tntabc", Environment.LIVE)
        assertTrue(c.baseURL.contains("live"))
    }

    @Test
    fun testUrl() {
        val regex = "^(https:\\/\\/)+[a-zA-Z0-9 ,]+[.]+[live, sandbox]+[.](verygoodproxy.com)\$"
        val c = VGSCollect("abc")

        assertTrue(Pattern.compile(regex).matcher(c.baseURL).matches())
    }

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