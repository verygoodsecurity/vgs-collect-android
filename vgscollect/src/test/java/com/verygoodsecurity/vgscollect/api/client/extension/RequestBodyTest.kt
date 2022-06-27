package com.verygoodsecurity.vgscollect.api.client.extension

import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.api.client.extension.bodyToString
import com.verygoodsecurity.vgscollect.core.api.client.extension.toRequestBodyOrNull
import org.junit.Assert
import org.junit.Test

class RequestBodyTest {

    @Test
    fun body_to_string() {
        val requestBody = BODY.toRequestBodyOrNull(null, HTTPMethod.POST)

        Assert.assertEquals(BODY, requestBody.bodyToString())
    }

    @Test
    fun null_to_string() {
        val requestBody = null.toRequestBodyOrNull(null, HTTPMethod.POST)

        Assert.assertEquals("", requestBody.bodyToString())
    }

    companion object {
        private const val BODY = "{data:\"data\"}"
    }
}