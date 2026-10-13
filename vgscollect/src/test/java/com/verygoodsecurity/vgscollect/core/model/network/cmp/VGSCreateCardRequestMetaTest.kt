package com.verygoodsecurity.vgscollect.core.model.network.cmp

import com.verygoodsecurity.vgscollect.BuildConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VGSCreateCardRequestMetaTest {

    private fun request(meta: Map<String, String>? = null): VGSCreateCardRequest {
        val builder = VGSCreateCardRequest.VGSRequestBuilder().setAuthToken("token")
        meta?.let { builder.setMeta(it) }
        return builder.build()
    }

    @Test
    fun getMeta_noCustomMeta_returnsOnlySdkMeta() {
        val meta = request().getMeta(null)

        assertEquals(
            mapOf(
                "_source" to "vgs-collect",
                "_medium" to "androidSDK",
                "_version" to BuildConfig.VERSION_NAME,
            ),
            meta
        )
    }

    @Test
    fun getMeta_customMeta_mergedWithSdkMeta() {
        val meta = request(mapOf("orderId" to "order-123", "integration" to "checkout"))
            .getMeta(null)

        assertEquals("order-123", meta["orderId"])
        assertEquals("checkout", meta["integration"])
        assertEquals("vgs-collect", meta["_source"])
        assertEquals("androidSDK", meta["_medium"])
        assertEquals(BuildConfig.VERSION_NAME, meta["_version"])
        assertFalse(meta.containsKey("_formId"))
    }

    @Test
    fun getMeta_reservedKeysSpoofed_sdkValuesWin() {
        val meta = request(
            mapOf(
                "custom" to "value",
                "_source" to "spoofed-source",
                "_medium" to "spoofed-medium",
                "_version" to "spoofed-version",
                "_formId" to "spoofed-form",
            )
        ).getMeta(null)

        assertEquals("value", meta["custom"])
        assertEquals("vgs-collect", meta["_source"])
        assertEquals("androidSDK", meta["_medium"])
        assertEquals(BuildConfig.VERSION_NAME, meta["_version"])
        assertFalse("Caller _formId must be dropped outside session", meta.containsKey("_formId"))
    }

    @Test
    fun getMeta_sessionFormId_overridesSpoofedFormId() {
        val meta = request(mapOf("_formId" to "spoofed-form")).getMeta("session-form")

        assertEquals("session-form", meta["_formId"])
    }

    @Test
    fun updateCardRequest_hasNoCustomMeta() {
        val request = VGSUpdateCardRequest.VGSRequestBuilder("card-id")
            .setAuthToken("token")
            .build()

        assertTrue(request.customMeta.isEmpty())
    }

    @Test
    fun forbiddenMetaKeys_containTypeAndProto() {
        assertEquals(setOf("__type", "__proto__"), FORBIDDEN_META_KEYS)
    }
}
