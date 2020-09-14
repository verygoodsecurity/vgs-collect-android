package com.verygoodsecurity.vgscollect.storage.fields

import android.app.Activity
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.storage.content.field.FieldStateContractor
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FieldsContractorTest {

    private lateinit var contractor: FieldStateContractor

    @Before
    fun setUp() {
        val activityController = Robolectric.buildActivity(Activity::class.java)
        val activity = activityController.get()

        contractor = FieldStateContractor(activity)
    }

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