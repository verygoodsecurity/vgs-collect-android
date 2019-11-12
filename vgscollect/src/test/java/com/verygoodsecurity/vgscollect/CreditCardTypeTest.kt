package com.verygoodsecurity.vgscollect

import com.verygoodsecurity.vgscollect.view.text.validation.card.CreditCardType
import com.verygoodsecurity.vgscollect.view.text.validation.card.getTypeCredit
import junit.framework.Assert.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class CreditCardTypeTest {

    @Test
    fun testVisa() {
        val card = CreditCardType.Visa
        assertTrue(card.isValid("4111111111111111"))
    }

    @Test
    fun testMastercard() {
        val card = CreditCardType.Mastercard
        assertTrue(card.isValid("5555555555555555"))
    }

    @Test
    fun testAmericanExpress() {
        val card = CreditCardType.AmericanExpress
        assertTrue(card.isValid("378282246310005"))
    }

    @Test
    fun testDinClub() {
        val card = CreditCardType.DinClub
        assertTrue(card.isValid("30043277253249"))
    }

    @Test
    fun testDiscover() {
        val card = CreditCardType.Discover
        assertTrue(card.isValid("6011000000000004"))
    }

    @Test
    fun testJcb() {
        val card = CreditCardType.Jcb
        assertTrue(card.isValid("3566002020360505"))
    }

    @Test
    fun testUnknown() {
        val card = CreditCardType.Visa
        assertFalse(card.isValid(" "))
    }


    @Test
    fun getTypeCreditAmericanExpress() {
        val t = getTypeCredit("378282246310005")
        assertTrue(t is CreditCardType.AmericanExpress)
    }

    @Test
    fun getTypeCreditDinClub() {
        val t = getTypeCredit("30043277253249")
        assertTrue(t is CreditCardType.DinClub)
    }

    @Test
    fun getTypeCreditDiscover() {
        val t = getTypeCredit("6011000000000004")
        assertTrue(t is CreditCardType.Discover)
    }

    @Test
    fun getTypeCreditJcb() {
        val t = getTypeCredit("3566002020360505")
        assertTrue(t is CreditCardType.Jcb)
    }

    @Test
    fun getTypeCreditMastercard() {
        val t = getTypeCredit("5555555555555555")
        assertTrue(t is CreditCardType.Mastercard)
    }

    @Test
    fun getTypeCreditVisa() {
        val t = getTypeCredit("4111111111111111")
        assertTrue(t is CreditCardType.Visa)
    }

}