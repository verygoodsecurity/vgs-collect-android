package com.verygoodsecurity.vgscollect

import com.verygoodsecurity.vgscollect.core.Environment
import com.verygoodsecurity.vgscollect.core.api.UrlBuilder
import com.verygoodsecurity.vgscollect.core.api.isUrlValid
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.regex.Pattern

class UrlBuilderTest {

    companion object {
        private const val PROXY_URL_REGEX =
            "^(https:\\/\\/)+[a-zA-Z0-9]+[.]+((live|sandbox|LIVE|SANDBOX)+((-)+([a-zA-Z0-9]+)|)+)+[.](verygoodproxy.com)\$"
    }

    @Test
    fun buildCollectUrl_sandbox_envAddedToUrl() {
        val url = UrlBuilder.buildCollectUrl("abc", Environment.SANDBOX.rawValue)
        assertTrue(url.contains("sandbox"))
    }

    @Test
    fun buildCollectUrl_live_envAddedToUrl() {
        val url = UrlBuilder.buildCollectUrl("ab2c", Environment.LIVE.rawValue)
        assertTrue(url.contains("live"))
    }

    @Test
    fun buildCollectUrl_sandbox_validUrlReturned() {
        val vault = "tnt234mm"
        val url = UrlBuilder.buildCollectUrl(vault, Environment.SANDBOX.rawValue)
        assertTrue(Pattern.compile(PROXY_URL_REGEX).matcher(url).matches())
    }

    @Test
    fun buildCollectUrl_live_validUrlReturned() {
        val vault = "1239f3hf"
        val url = UrlBuilder.buildCollectUrl(vault, Environment.LIVE.rawValue)

        assertTrue(Pattern.compile(PROXY_URL_REGEX).matcher(url).matches())
    }

    @Test
    fun buildCollectUrl_sandbox_isUrlValidTrue() {
        val vault = "tnt234mm"
        val url = UrlBuilder.buildCollectUrl(vault, Environment.SANDBOX.rawValue)
        assertTrue(url.isUrlValid())
    }

    @Test
    fun buildCollectUrl_live_isUrlValidTrue() {
        val vault = "123456"
        val url = UrlBuilder.buildCollectUrl(vault, Environment.LIVE.rawValue)
        assertTrue(url.isUrlValid())
    }

    @Test
    fun buildCollectUrl_sandboxEnvWithRegions_validUrlReturned() {
        val vault = "acv12das"
        val url0 = UrlBuilder.buildCollectUrl(vault, "sandbox")
        assertTrue(Pattern.compile(PROXY_URL_REGEX).matcher(url0).matches())

        val url1 = UrlBuilder.buildCollectUrl(vault, "sandbox-")
        assertFalse(Pattern.compile(PROXY_URL_REGEX).matcher(url1).matches())

        val url2 = UrlBuilder.buildCollectUrl(vault, "sandbox-eu")
        assertTrue(Pattern.compile(PROXY_URL_REGEX).matcher(url2).matches())

        val url3 = UrlBuilder.buildCollectUrl(vault, "sandbox-eu-")
        assertFalse(Pattern.compile(PROXY_URL_REGEX).matcher(url3).matches())

        val url4 = UrlBuilder.buildCollectUrl(vault, "sandbox-eu-123")
        assertTrue(Pattern.compile(PROXY_URL_REGEX).matcher(url4).matches())

        val url5 = UrlBuilder.buildCollectUrl(vault, "SANDBOX-EU-1")
        assertTrue(Pattern.compile(PROXY_URL_REGEX).matcher(url5).matches())
    }

    @Test
    fun buildCollectUrl_liveEnvWithRegions_validUrlReturned() {
        val vault = "acv12das"
        val url0 = UrlBuilder.buildCollectUrl(vault, "live")
        assertTrue(Pattern.compile(PROXY_URL_REGEX).matcher(url0).matches())

        val url1 = UrlBuilder.buildCollectUrl(vault, "live-")
        assertFalse(Pattern.compile(PROXY_URL_REGEX).matcher(url1).matches())

        val url2 = UrlBuilder.buildCollectUrl(vault, "live-eu")
        assertTrue(Pattern.compile(PROXY_URL_REGEX).matcher(url2).matches())

        val url3 = UrlBuilder.buildCollectUrl(vault, "live-eu-")
        assertFalse(Pattern.compile(PROXY_URL_REGEX).matcher(url3).matches())

        val url4 = UrlBuilder.buildCollectUrl(vault, "live-eu-123")
        assertTrue(Pattern.compile(PROXY_URL_REGEX).matcher(url4).matches())

        val url5 = UrlBuilder.buildCollectUrl(vault, "LIVE-UA-1")
        assertTrue(Pattern.compile(PROXY_URL_REGEX).matcher(url5).matches())
    }

    @Test
    fun buildCardManagerUrl_sandbox_envAddedToUrl() {
        val url = UrlBuilder.buildCardManagerUrl(Environment.SANDBOX.rawValue)
        assertTrue(url.contains("sandbox"))
    }

    @Test
    fun buildCardManagerUrl_live_envNotAddedToUrl() {
        val url = UrlBuilder.buildCardManagerUrl(Environment.LIVE.rawValue)
        assertFalse(url.contains("live"))
    }

    @Test
    fun buildCardManagerUrl_sandbox_validUrlReturned() {
        val url = UrlBuilder.buildCardManagerUrl(Environment.SANDBOX.rawValue)
        assertTrue(url.isUrlValid())
    }

    @Test
    fun buildCardManagerUrl_live_validUrlReturned() {
        val url = UrlBuilder.buildCardManagerUrl(Environment.LIVE.rawValue)
        assertTrue(url.isUrlValid())
    }

    @Test
    fun buildCardManagerUrl_sandboxEnvWithRegions_envNotAddedToUrl() {
        val url = UrlBuilder.buildCardManagerUrl("sandbox-eu-1")
        assertFalse(url.contains("sandbox-eu-1"))
    }

    @Test
    fun buildCardManagerUrl_liveEnvWithRegions_envNotAddedToUrl() {
        val url = UrlBuilder.buildCardManagerUrl("live-eu-1")
        assertFalse(url.contains("live-eu-1"))
    }

    @Test
    fun buildCnameUrl_validationUrl() {
        val test1 = UrlBuilder.buildCnameUrl("www.vgs.com", "tnt")
        assertEquals("https://js.verygoodvault.com/collect-configs/www.vgs.com__tnt.txt", test1)

        val test2 = UrlBuilder.buildCnameUrl("", "")
        assertEquals("https://js.verygoodvault.com/collect-configs/__.txt", test2)
    }
}