package com.verygoodsecurity.vgscollect

import com.verygoodsecurity.vgscollect.util.extension.isNumeric
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NumberExtensionTest {

    @Test
    fun test1() {
        val s = "123"
        assertTrue(s.isNumeric())
    }

    @Test
    fun test2() {
        val s = "4111111111111111"
        assertTrue(s.isNumeric())
    }

    @Test
    fun test3() {
        val s = "4111-1111-1111-1111"
        assertFalse(s.isNumeric())
    }

    @Test
    fun test4() {
        val s = "4111 1111 1111 1111"
        assertFalse(s.isNumeric())
    }

    @Test
    fun test5() {
        val s = "11a23"
        assertFalse(s.isNumeric())
    }

    @Test
    fun test6() {
        val s = "0x123"
        assertFalse(s.isNumeric())
    }

    @Test
    fun test7() {
        val s = "-200"
        assertTrue(s.isNumeric())
    }

    @Test
    fun test8() {
        val s = ""
        assertFalse(s.isNumeric())
    }

    @Test
    fun test9() {
        val s = "2.99e+8"
        assertTrue(s.isNumeric())
    }

    @Test
    fun test10() {
        val s = "10.0d"
        assertTrue(s.isNumeric())
    }
}