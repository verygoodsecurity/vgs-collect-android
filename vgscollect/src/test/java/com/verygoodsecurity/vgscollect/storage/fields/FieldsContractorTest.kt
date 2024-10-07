package com.verygoodsecurity.vgscollect.storage.fields

import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.storage.content.field.FieldStateContractor
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FieldsContractorTest {

    private val contractor: FieldStateContractor = FieldStateContractor()

    @Test
    fun test_empty_field_name_true() {
        val state = VGSFieldState(fieldName = "name")
        assertTrue(contractor.checkState(state))
    }

    @Test
    fun test_empty_field_name_false() {
        val state1 = VGSFieldState(fieldName = " ")
        assertFalse(contractor.checkState(state1))

        val state2 = VGSFieldState()
        assertFalse(contractor.checkState(state2))
    }
}