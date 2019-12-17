package com.verygoodsecurity.vgscollect.card.validator.luhn

import com.verygoodsecurity.vgscollect.view.card.validation.card.brand.JcbDelegate
import org.junit.Assert.assertTrue
import org.junit.Test

class JCBDelegateTest {

    @Test
    fun detectJcb16() {
        val jcb16 = JcbDelegate()

        val state = jcb16.isValid("3566002020360500")

        assertTrue(state)
    }

    @Test
    fun detectJcb17() {
        val jcb16 = JcbDelegate()

        val state = jcb16.isValid("35660020203605001")

        assertTrue(state)
    }

    @Test
    fun detectJcb18() {
        val jcb16 = JcbDelegate()

        val state = jcb16.isValid("356600202036050012")

        assertTrue(state)
    }

    @Test
    fun detectJcb19() {
        val jcb16 = JcbDelegate()

        val state = jcb16.isValid("356600202036050013")

        assertTrue(state)
    }
}