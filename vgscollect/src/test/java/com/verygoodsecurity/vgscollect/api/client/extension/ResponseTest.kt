package com.verygoodsecurity.vgscollect.api.client.extension

import com.verygoodsecurity.vgscollect.core.api.client.extension.isCodeSuccessful
import com.verygoodsecurity.vgscollect.core.api.client.extension.isHttpStatusCode
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ResponseTest {

    @Test
    fun test_is_code_successful() {
        assertTrue(200.isCodeSuccessful())
        assertTrue(250.isCodeSuccessful())
        assertTrue(299.isCodeSuccessful())
        assertFalse(300.isCodeSuccessful())
        assertFalse(350.isCodeSuccessful())
        assertFalse(399.isCodeSuccessful())
        assertFalse(199.isCodeSuccessful())
        assertFalse(400.isCodeSuccessful())
        assertFalse(1500.isCodeSuccessful())
    }

    @Test
    fun test_is_http_status_code() {
        assertTrue(200.isHttpStatusCode())
        assertTrue(300.isHttpStatusCode())
        assertTrue(400.isHttpStatusCode())
        assertTrue(500.isHttpStatusCode())
        assertTrue(600.isHttpStatusCode())
        assertTrue(700.isHttpStatusCode())
        assertTrue(800.isHttpStatusCode())
        assertTrue(900.isHttpStatusCode())
        assertTrue(999.isHttpStatusCode())
        assertFalse(1000.isHttpStatusCode())
        assertFalse(100.isHttpStatusCode())
        assertFalse(0.isHttpStatusCode())
        assertFalse(199.isHttpStatusCode())
    }
}