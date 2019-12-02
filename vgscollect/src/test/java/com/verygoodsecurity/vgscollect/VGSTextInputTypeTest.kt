package com.verygoodsecurity.vgscollect

import com.verygoodsecurity.vgscollect.view.text.validation.card.CreditCardType
import com.verygoodsecurity.vgscollect.view.text.validation.card.VGSEditTextFieldType
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito

class VGSTextInputTypeTest {

    @Test
    fun testCardHolderName() {
        val type = VGSEditTextFieldType.CardHolderName
        assertTrue(type.validate("abba"))
    }

    @Test
    fun testCVCCardCode() {
        val type = VGSEditTextFieldType.CVCCardCode
        assertTrue(type.validate("123"))
    }

    @Test
    fun testCardExpDate() {
        val type = VGSEditTextFieldType.CardExpDate
        assertTrue(type.validate("12/23"))
    }

    @Test
    fun testCardNumber() {
        val type = VGSEditTextFieldType.CardNumber()
        val typeCard = Mockito.mock(CreditCardType::class.java)
        Mockito.doReturn("^[a-zA-Z0-9 ,]+\$").`when`(typeCard).validationPattern
        type.card = typeCard
        assertTrue(type.validate("4111111111111111"))
    }
}