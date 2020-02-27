package com.verygoodsecurity.vgscollect.view.card.date

import com.verygoodsecurity.vgscollect.view.date.validation.isInputDatePatternValid
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpirationDateInputExtensionTest {
    @Test
    fun test_correct() {
        assertTrue("dd/MM/yy".isInputDatePatternValid())
        assertTrue("MM/dd/yy".isInputDatePatternValid())
        assertTrue("dd/yy/MM".isInputDatePatternValid())
        assertTrue("MM/dd/yy".isInputDatePatternValid())
        assertTrue("MM/yy/dd".isInputDatePatternValid())
        assertTrue("yy/dd/MM".isInputDatePatternValid())
        assertTrue("yy/MM/dd".isInputDatePatternValid())

        assertTrue("dd/MM/yyyy".isInputDatePatternValid())
        assertTrue("MM/dd/yyyy".isInputDatePatternValid())
        assertTrue("dd/yyyy/MM".isInputDatePatternValid())
        assertTrue("MM/dd/yyyy".isInputDatePatternValid())
        assertTrue("MM/yyyy/dd".isInputDatePatternValid())
        assertTrue("yyyy/dd/MM".isInputDatePatternValid())
        assertTrue("yyyy/MM/dd".isInputDatePatternValid())

        assertTrue("MM/yyyy".isInputDatePatternValid())
        assertTrue("MM/yy".isInputDatePatternValid())
        assertTrue("yyyy/MM".isInputDatePatternValid())
        assertTrue("yy/MM".isInputDatePatternValid())
    }

    @Test
    fun test_wrong() {
        assertFalse("dd  MM/yy".isInputDatePatternValid())
        assertFalse("MM0dd/yy".isInputDatePatternValid())
        assertFalse("dd/yyaMM".isInputDatePatternValid())
        assertFalse("MMdd/yy".isInputDatePatternValid())
        assertFalse("MM'T'yy/dd".isInputDatePatternValid())
        assertFalse(" yy/dd/MM".isInputDatePatternValid())
        assertFalse("yy/MM/dd ".isInputDatePatternValid())

        assertFalse("dd/MM/YYYY".isInputDatePatternValid())
        assertFalse("MM/dd/YY".isInputDatePatternValid())
        assertFalse("HH:mm dd/yyyy/MM".isInputDatePatternValid())
        assertFalse("MM/dd/yyyy''".isInputDatePatternValid())

        assertFalse("dd/yyyy".isInputDatePatternValid())
        assertFalse("MM/dd".isInputDatePatternValid())
        assertFalse("yyyy/dd".isInputDatePatternValid())
        assertFalse("yy/dd".isInputDatePatternValid())
    }
}