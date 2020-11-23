package com.verygoodsecurity.vgscollect.utils.extension

import com.verygoodsecurity.vgscollect.util.extension.concatWithDash
import com.verygoodsecurity.vgscollect.util.extension.concatWithSlash
import org.junit.Assert
import org.junit.Test

class StringTest {

    @Test
    fun test_concat_with_dash() {
        Assert.assertEquals("sandbox-eu-1", "sandbox" concatWithDash "eu-1")
        Assert.assertEquals("sandbox-eu-1", "sandbox" concatWithDash "-eu-1")
        Assert.assertEquals("live-eu-", "live" concatWithDash "-eu-")
        Assert.assertEquals("live-eu", "live" concatWithDash "eu")
        Assert.assertEquals("live", "live" concatWithDash "")
    }

    @Test
    fun test_concat_with_slash() {
        Assert.assertEquals("sandbox/path", "sandbox" concatWithSlash "path")
        Assert.assertEquals("sandbox/path", "sandbox" concatWithSlash "/path")
        Assert.assertEquals("live/path/", "live" concatWithSlash "/path/")
        Assert.assertEquals("live/eu", "live" concatWithSlash "eu")
        Assert.assertEquals("live", "live" concatWithSlash "")
    }
}