package com.verygoodsecurity.vgscollect.utils.extension

import com.verygoodsecurity.vgscollect.core.api.equalsUrl
import com.verygoodsecurity.vgscollect.core.api.toHost
import com.verygoodsecurity.vgscollect.core.api.toHostnameValidationUrl
import com.verygoodsecurity.vgscollect.core.api.toHttps
import com.verygoodsecurity.vgscollect.util.extension.concatWithDash
import com.verygoodsecurity.vgscollect.util.extension.concatWithSlash
import org.junit.Assert.*
import org.junit.Test

class StringTest {

    @Test
    fun test_concat_with_dash() {
        assertEquals("sandbox-eu-1", "sandbox" concatWithDash "eu-1")
        assertEquals("sandbox-eu-1", "sandbox" concatWithDash "-eu-1")
        assertEquals("live-eu-", "live" concatWithDash "-eu-")
        assertEquals("live-eu", "live" concatWithDash "eu")
        assertEquals("live", "live" concatWithDash "")
    }

    @Test
    fun test_concat_with_slash() {
        assertEquals("sandbox/path", "sandbox" concatWithSlash "path")
        assertEquals("sandbox/path", "sandbox" concatWithSlash "/path")
        assertEquals("live/path/", "live" concatWithSlash "/path/")
        assertEquals("live/eu", "live" concatWithSlash "eu")
        assertEquals("live", "live" concatWithSlash "")
    }

    @Test
    fun test_to_hostname_validation_url() {
        val test1 = "www.vgs.com".toHostnameValidationUrl("tnt")
        assertEquals("https://js.verygoodvault.com/collect-configs/www.vgs.com__tnt.txt", test1)

        val test2 = "".toHostnameValidationUrl("")
        assertEquals("https://js.verygoodvault.com/collect-configs/__.txt", test2)
    }

    @Test
    fun test_equals_url() {
        assertTrue("www.vgs.com" equalsUrl "www.vgs.com")
        assertTrue("http://www.vgs.com" equalsUrl "www.vgs.com")
        assertTrue("www.vgs.com" equalsUrl "http://www.vgs.com")
        assertTrue("http://www.vgs.com" equalsUrl "http://www.vgs.com")
        assertTrue("https://www.vgs.com" equalsUrl "www.vgs.com")
        assertTrue("www.vgs.com" equalsUrl "https://www.vgs.com")
        assertTrue("https://www.vgs.com" equalsUrl "https://www.vgs.com")

        assertFalse("vgs.com" equalsUrl "https://www.vgs.com")
        assertFalse("vgs.com" equalsUrl "http://www.vgs.com")
        assertFalse("vgs.com" equalsUrl "www.vgs.com")
    }

    @Test
    fun test_to_https() {
        assertEquals("https://www.vgs.io", "https://www.vgs.io".toHttps())
        assertEquals("http://www.vgs.io", "http://www.vgs.io".toHttps())
        assertEquals("https://www.vgs.io", "www.vgs.io".toHttps())
    }

    @Test
    fun test_to_host() {
        assertEquals("www.vgs.io", "https://www.vgs.io".toHost())
        assertEquals("www.vgs.io", "http://www.vgs.io".toHost())
        assertEquals("www.vgs.io", "www.vgs.io".toHost())
    }
}