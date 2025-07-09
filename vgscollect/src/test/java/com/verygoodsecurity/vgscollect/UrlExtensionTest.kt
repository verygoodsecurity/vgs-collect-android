package com.verygoodsecurity.vgscollect

import com.verygoodsecurity.vgscollect.core.api.isEnvironmentValid
import com.verygoodsecurity.vgscollect.core.api.isIdValid
import com.verygoodsecurity.vgscollect.core.api.isUrlValid
import com.verygoodsecurity.vgscollect.core.isLive
import com.verygoodsecurity.vgscollect.core.isSandbox
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UrlExtensionTest {

    @Test
    fun test_is_tennantId_not_valid() {

        val testUrl1 = " "
        assertFalse(testUrl1.isIdValid())

        val testUrl2 = "tnt.com"
        assertFalse(testUrl2.isIdValid())

        val testUrl3 = "tnt com"
        assertFalse(testUrl3.isIdValid())

        val testUrl4 = "2tnt/com"
        assertFalse(testUrl4.isIdValid())

        val testUrl5 = "tnt:com"
        assertFalse(testUrl5.isIdValid())

        val testUrl6 = "tnt*com"
        assertFalse(testUrl6.isIdValid())

        val testUrl7 = "tnt?com"
        assertFalse(testUrl7.isIdValid())
    }
    @Test
    fun test_is_tennantId_valid() {
        val testUrl1 = "tntdldf"
        assertTrue(testUrl1.isIdValid())
        val testUrl = "123"
        assertTrue(testUrl.isIdValid())
    }

    @Test
    fun test_is_URL_valid() {
        val url1 = "google.com"
        assertTrue(url1.isUrlValid())

        val url2 = "https://www.bla"
        assertTrue(url2.isUrlValid())

        val url3 = "https://www.bla-one.com"
        assertTrue(url3.isUrlValid())

        val url4 = "https://www.bla-one.com/post"
        assertTrue(url4.isUrlValid())
    }

    @Test
    fun test_is_url_not_valid() {
        val url1 = "htt://www.bla"
        assertFalse(url1.isUrlValid())

        val url2 = "http:/www.bla"
        assertFalse(url2.isUrlValid())

        val url3 = "http://www.ex ample.com:8800"
        assertFalse(url3.isUrlValid())
    }

    @Test
    fun test_is_environment_valid() {
        assertTrue("live".isEnvironmentValid())
        assertFalse("live-".isEnvironmentValid())
        assertTrue("live-12".isEnvironmentValid())
        assertTrue("live-eu".isEnvironmentValid())
        assertTrue("live-w".isEnvironmentValid())
        assertFalse("live-w-".isEnvironmentValid())
        assertTrue("live-w-12".isEnvironmentValid())
        assertFalse("live-w-12-".isEnvironmentValid())
        assertTrue("live-w-12-abba".isEnvironmentValid())
        assertTrue("LIVE-UA-3".isEnvironmentValid())

        assertTrue("sandbox".isEnvironmentValid())
        assertFalse("sandbox-".isEnvironmentValid())
        assertTrue("sandbox-12".isEnvironmentValid())
        assertTrue("sandbox-eu".isEnvironmentValid())
        assertTrue("sandbox-w".isEnvironmentValid())
        assertFalse("sandbox-w-".isEnvironmentValid())
        assertTrue("sandbox-w-12".isEnvironmentValid())
        assertFalse("sandbox-w-12-".isEnvironmentValid())
        assertTrue("sandbox-w-12-abba".isEnvironmentValid())
        assertTrue("SANDBOX-UA-7".isEnvironmentValid())
    }

    @Test
    fun isLive_trueReturned() {
        // Arrange
        val environmentOne = "live"
        val environmentTwo = "live-eu"
        // Act
        val resultOne = environmentOne.isLive()
        val resultTwo = environmentTwo.isLive()
        // Assert
        assertTrue(resultOne)
        assertTrue(resultTwo)
    }

    @Test
    fun isLive_falseReturned() {
        // Arrange
        val environmentOne = "li-ve-eu"
        val environmentTwo = "sandbox"
        // Act
        val resultOne = environmentOne.isLive()
        val resultTwo = environmentTwo.isLive()
        // Assert
        assertFalse(resultOne)
        assertFalse(resultTwo)
    }

    @Test
    fun isSandbox_trueReturned() {
        // Arrange
        val environmentOne = "sandbox"
        val environmentTwo = "sandbox-eu"
        // Act
        val resultOne = environmentOne.isSandbox()
        val resultTwo = environmentTwo.isSandbox()
        // Assert
        assertTrue(resultOne)
        assertTrue(resultTwo)
    }

    @Test
    fun isSandbox_falseReturned() {
        // Arrange
        val environmentOne = "sand-box-eu"
        val environmentTwo = "live"
        // Act
        val resultOne = environmentOne.isSandbox()
        val resultTwo = environmentTwo.isSandbox()
        // Assert
        assertFalse(resultOne)
        assertFalse(resultTwo)
    }
}