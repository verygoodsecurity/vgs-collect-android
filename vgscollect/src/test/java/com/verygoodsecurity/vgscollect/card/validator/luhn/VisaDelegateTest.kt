package com.verygoodsecurity.vgscollect.card.validator.luhn

import com.verygoodsecurity.vgscollect.view.card.validation.card.brand.VisaDelegate
import org.junit.Assert
import org.junit.Test

class VisaDelegateTest {

    @Test
    fun detectVisa13() {
        val visa13 = VisaDelegate()

        val state = visa13.isValid("4144411111113")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa16() {
        val visa13 = VisaDelegate()

        val state = visa13.isValid("4144411111113456")
        Assert.assertTrue(state)
    }

    @Test
    fun detectVisa19() {
        val visa13 = VisaDelegate()

        val state = visa13.isValid("4144411111113456789")
        Assert.assertTrue(state)
    }
}