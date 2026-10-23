package com.verygoodsecurity.vgscollect.core.api.client.extension

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LogRedactionTest {

    @Test
    fun redactCustomMeta_replacesValuesAndKeepsKeys() {
        val payload = """{"data":{"attributes":{"pan":"4111"},"meta":{"orderId":"secret","_source":"vgs-collect"}}}"""

        val meta = JSONObject(payload.redactCustomMeta()!!).getJSONObject("data").getJSONObject("meta")

        assertEquals("***", meta.getString("orderId"))
        assertEquals("***", meta.getString("_source"))
    }

    @Test
    fun redactCustomMeta_noMeta_returnsPayloadUnchanged() {
        val payload = """{"data":{"attributes":{"pan":"4111"}}}"""

        assertEquals(payload, payload.redactCustomMeta())
    }

    @Test
    fun redactCustomMeta_nonJson_returnsPayloadUnchanged() {
        assertEquals("not json", "not json".redactCustomMeta())
    }

    @Test
    fun redactCustomMeta_nullOrEmpty_returnedAsIs() {
        assertNull((null as String?).redactCustomMeta())
        assertTrue("".redactCustomMeta().isNullOrEmpty())
    }
}
