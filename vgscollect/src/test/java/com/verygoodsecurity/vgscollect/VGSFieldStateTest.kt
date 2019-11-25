package com.verygoodsecurity.vgscollect

import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.model.state.mapToFieldState
import com.verygoodsecurity.vgscollect.core.model.state.mapVGSTextInputTypeToFieldState
import com.verygoodsecurity.vgscollect.view.text.validation.card.VGSTextInputType
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito

class VGSFieldStateTest {

    @Test
    fun isValidRequiredTrueCall() {
        val type = Mockito.mock(VGSTextInputType::class.java)
        Mockito.doReturn(true).`when`(type).validate(Mockito.anyString())

        val state = VGSFieldState(isFocusable = true, isRequired = true, type = type, content = "c", fieldName = "a")
        assertTrue(state.isValid())
    }

    @Test
    fun map_VGSTextInputTypeTo_CardNumberState() {
        val cn = VGSTextInputType.CardNumber()

        val cn1 = cn.mapVGSTextInputTypeToFieldState("4111111111111111")
        assertTrue(cn1 is FieldState.CardNumberState)
        if(cn1 is FieldState.CardNumberState) {
            assertTrue(cn1.bin == "411111")
            assertTrue(cn1.last4 == "1111")
        }
    }

    @Test
    fun map_VGSTextInputTypeTo_CardExpirationDate() {
        val ce = VGSTextInputType.CardExpDate
        val ce1 = ce.mapVGSTextInputTypeToFieldState()
        assertTrue(ce1 is FieldState.CardExpirationDate)
    }

    @Test
    fun map_VGSTextInputTypeTo_CardName() {
        val co = VGSTextInputType.CardOwnerName

        val co1 = co.mapVGSTextInputTypeToFieldState()
        assertTrue(co1 is FieldState.CardName)
    }

    @Test
    fun map_VGSTextInputTypeTo_CVCCardCode() {
        val cv = VGSTextInputType.CVCCardCode

        val cv1 = cv.mapVGSTextInputTypeToFieldState()
        assertTrue(cv1 is FieldState.CVCState)
    }

    @Test
    fun mapToFieldState() {
        val type = VGSTextInputType.CVCCardCode
        val oldState = VGSFieldState(isFocusable = true, isRequired = true, type = type, content = "123", fieldName = "a")

        val newState = oldState.mapToFieldState()

        assertTrue(newState.hasFocus == oldState.isFocusable &&
            newState.isRequired == oldState.isRequired &&
            newState.isEmpty == oldState.content.isNullOrEmpty() &&
            newState.isValid == oldState.isValid() &&
            newState.fieldName == oldState.fieldName)
    }
}