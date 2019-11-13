package com.verygoodsecurity.vgscollect

import com.verygoodsecurity.vgscollect.core.AbstractVgsCollect
import com.verygoodsecurity.vgscollect.core.Environment
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.api.ApiClient
import com.verygoodsecurity.vgscollect.core.model.VGSResponse
import com.verygoodsecurity.vgscollect.core.storage.VgsStore
import com.verygoodsecurity.vgscollect.widget.VGSEditText
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
    fun test() {
        val client = Mockito.mock(ApiClient::class.java)

        val collect = object : AbstractVgsCollect("", Environment.SANDBOX) {
            override var storage: VgsStore = Mockito.mock(VgsStore::class.java)
            override var client: ApiClient = client
            override fun bindView(view: VGSEditText?) {}
        }

        var res:VGSResponse? = null
        collect.onResponseListener = object : VgsCollectResponseListener {
            override fun onResponse(response: VGSResponse?) {
                res = response
            }
        }

        Mockito.doReturn(VGSResponse.SuccessResponse(successCode = 200))
            .`when`(client).call(Mockito.anyString(),
                anyObject()
                , Mockito.anyMap<String, String>(), Mockito.anyMap<String, String>())


        collect.doMainThreadRequest("/post", HTTPMethod.POST, mutableMapOf(), mutableListOf())

        Mockito.verify(client).call("/post", HTTPMethod.POST, mutableMapOf(), mutableMapOf())

        assertNotNull(res)
        assertEquals(200, res?.code)
    }
}