package com.verygoodsecurity.vgscollect.api

import com.verygoodsecurity.vgscollect.core.api.client.ssl.TLSSocketFactory
import org.junit.Assert.assertTrue
import org.junit.Test

class TLSSocketFactoryTest {

    @Test
    fun test_socket_factory() {
        val protocol = "custom_protocols"
        val tls11 = "TLSv1.1"
        val tls12 = "TLSv1.2"

        val connectionProtocols = arrayOf(protocol)

        val protocols = TLSSocketFactory().mergeProtocols(connectionProtocols)

        assertTrue(arrayOf(protocol, tls11, tls12).contentEquals(protocols))
    }

}