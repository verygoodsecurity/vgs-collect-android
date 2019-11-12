package com.verygoodsecurity.vgscollect

import com.verygoodsecurity.vgscollect.view.text.validation.card.CreditCardType
import com.verygoodsecurity.vgscollect.view.text.validation.card.VGSTextInputType
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito

class VGSTextInputTypeTest {

    @Test
    fun testCardOwnerName() {
        val type = VGSTextInputType.CardOwnerName
        assertTrue(type.validate("abba"))
    }

    @Test
    fun testCVVCardCode() {
        val type = VGSTextInputType.CVVCardCode
        assertTrue(type.validate("123"))
    }

    @Test
    fun testCardExpDate() {
        val type = VGSTextInputType.CardExpDate
        assertTrue(type.validate("12/23"))
    }

    @Test
    fun testCardNumber() {
        val type = VGSTextInputType.CardNumber()
        val typeCard = Mockito.mock(CreditCardType::class.java)
        Mockito.doReturn("^[a-zA-Z0-9 ,]+\$").`when`(typeCard).validationPattern
        type.card = typeCard
        assertTrue(type.validate("4111111111111111"))
    }
}