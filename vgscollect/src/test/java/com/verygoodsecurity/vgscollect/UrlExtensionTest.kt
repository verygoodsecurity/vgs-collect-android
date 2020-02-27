package com.verygoodsecurity.vgscollect

import com.verygoodsecurity.vgscollect.core.Environment
import com.verygoodsecurity.vgscollect.core.api.buildURL
import com.verygoodsecurity.vgscollect.core.api.isTennantIdValid
import com.verygoodsecurity.vgscollect.core.api.isURLValid
import com.verygoodsecurity.vgscollect.core.api.setupURL
import org.junit.Assert.*
import org.junit.Test
import java.util.regex.Pattern

class UrlExtensionTest {

    @Test
    fun test_build_URL_fault() {
        val url1 = "a".buildURL("path")
        assertEquals(null, url1)

        val url2 = "".buildURL("path")
        assertEquals(null, url2)

        val url3 = "a.a".buildURL("path")
        assertEquals(null, url3)
    }

    @Test
    fun test_build_URL_path_right() {
        val url1 = "http://a.a".buildURL("path")
        assertEquals("http://a.a/path", url1.toString())

        val url2 = "http://a.a".buildURL("")
        assertEquals("http://a.a", url2.toString())

        val url3 = "http://a.a".buildURL("/path")
        assertEquals("http://a.a/path", url3.toString())
    }

    @Test
    fun test_build_URL_path_query_right() {
        val url1 = "http://a.a".buildURL("path", "method=EMPTY", "class=android", "param3")
        assertEquals("http://a.a/path?method=EMPTY&class=android&param3", url1.toString())

        val url2 = "http://a.a".buildURL("","method=EMPTY", "class=android", "param3")
        assertEquals("http://a.a?method=EMPTY&class=android&param3", url2.toString())

        val url3 = "http://a.a".buildURL("")
        assertEquals("http://a.a", url3.toString())
    }

    @Test
    fun test_is_tennantId_not_valid() {

        val testUrl1 = " "
        assertFalse(testUrl1.isTennantIdValid())

        val testUrl2 = "tnt.com"
        assertFalse(testUrl2.isTennantIdValid())

        val testUrl3 = "tnt com"
        assertFalse( testUrl3.isTennantIdValid() )

        val testUrl4 = "2tnt/com"
        assertFalse( testUrl4.isTennantIdValid() )

        val testUrl5 = "tnt:com"
        assertFalse( testUrl5.isTennantIdValid() )

        val testUrl6 = "tnt*com"
        assertFalse( testUrl6.isTennantIdValid() )

        val testUrl7 = "tnt?com"
        assertFalse( testUrl7.isTennantIdValid() )
    }
    @Test
    fun test_is_tennantId_valid() {
        val testUrl1 = "tntdldf"
        assertTrue(testUrl1.isTennantIdValid())
        val testUrl = "123"
        assertTrue(testUrl.isTennantIdValid())
    }

    @Test
    fun test_is_URL_valid() {
        val url1 = "http://www.bla"
        assertTrue(url1.isURLValid())

        val url2 = "https://www.bla"
        assertTrue(url2.isURLValid())

        val url3 = "https://www.bla-one.com"
        assertTrue(url3.isURLValid())

        val url4 = "http://www.example.com:8800"
        assertTrue(url4.isURLValid())
    }

    @Test
    fun test_is_url_not_valid() {
        val url1 = "www.bla.com"
        assertFalse(url1.isURLValid())

        val url2 = "google.com"
        assertFalse(url2.isURLValid())

        val url3 = "http://www.ex ample.com:8800"
        assertFalse(url3.isURLValid())
    }

    @Test
    fun test_environment_sandbox_by_default() {
        val url = "abc".setupURL(Environment.SANDBOX.rawValue)
        assertTrue(url.contains("sandbox"))
    }

    @Test
    fun test_environment_live_by_default() {
        val url = "ab2c".setupURL(Environment.LIVE.rawValue)
        assertTrue(url.contains("live"))
    }

    @Test
    fun test_url_sandbox() {
        val regex = "^(https:\\/\\/)+[a-zA-Z0-9 ,]+[.]+[live, sandbox]+[.](verygoodproxy.com)\$"

        val s = "tnt234mm"
        val url = s.setupURL(Environment.SANDBOX.rawValue)

        assertTrue(Pattern.compile(regex).matcher(url).matches())
    }

    @Test
    fun test_url_live() {
        val regex = "^(https:\\/\\/)+[a-zA-Z0-9 ,]+[.]+[live, sandbox]+[.](verygoodproxy.com)\$"

        val s = "1239f3hf"
        val url = s.setupURL(Environment.LIVE.rawValue)

        assertTrue(Pattern.compile(regex).matcher(url).matches())
    }

    @Test
    fun test_setup_url_sandbox() {
        val s = "tnt234mm"
        val url = s.setupURL(Environment.SANDBOX.rawValue)

        assertTrue(url.isURLValid())
    }

    @Test
    fun test_setup_url_live() {
        val s = "123456"
        val url = s.setupURL(Environment.LIVE.rawValue)

        assertTrue(url.isURLValid())
    }
}