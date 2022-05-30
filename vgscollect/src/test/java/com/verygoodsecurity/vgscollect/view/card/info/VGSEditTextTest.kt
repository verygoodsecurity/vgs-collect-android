package com.verygoodsecurity.vgscollect.view.card.info

import android.app.Activity
import android.graphics.Typeface
import android.text.InputType
import android.view.View
import com.verygoodsecurity.vgscollect.TestApplication
import com.verygoodsecurity.vgscollect.any
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.validation.rules.VGSInfoRule
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.view.internal.InfoInputField
import com.verygoodsecurity.vgscollect.widget.VGSEditText
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class VGSEditTextTest {
    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var view: VGSEditText

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        view = VGSEditText(activity)
    }

    @Test
    fun test_view() {
        view.onAttachedToWindow()
        val internal = view.statePreparer.getView()
        assertNotNull(internal)
    }

    @Test
    fun test_attach_view() {
        view.onAttachedToWindow()

        assertEquals(1, view.childCount)
    }

    @Test
    fun test_check_internal_view() {
        val internal = view.statePreparer.getView()
        assertNotNull(internal)

        val child = view.statePreparer.getView()
        assertTrue(child is InfoInputField)
    }

    @Test
    fun test_info() {
        assertEquals(FieldType.INFO, view.getFieldType())
    }

    @Test
    fun test_default_input_type() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())
    }

    @Test
    fun test_info_input_type_text() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setInputType(InputType.TYPE_CLASS_TEXT)
        assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())
    }

    @Test
    fun test_info_input_type_number() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setInputType(InputType.TYPE_CLASS_NUMBER)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_info_input_type_date() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setInputType(InputType.TYPE_CLASS_DATETIME)
        assertEquals(InputType.TYPE_CLASS_DATETIME, view.getInputType())
    }

    @Test
    fun test_info_input_type_text_password() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val textPass = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        view.setInputType(textPass)
        assertEquals(textPass, view.getInputType())
    }

    @Test
    fun test_info_input_type_number_password() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val numPass = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(numPass)
        assertEquals(numPass, view.getInputType())
    }

    @Test
    fun test_info_input_type_none() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setInputType(InputType.TYPE_NULL)
        assertEquals(InputType.TYPE_NULL, view.getInputType())
    }

    @Test
    fun test_field_state_change_listener_first() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val listener = mock(OnFieldStateChangeListener::class.java)
        view.setOnFieldStateChangeListener(listener)
        verify(listener, times(0)).onStateChange(any())

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        verify(listener, times(1)).onStateChange(any())
    }

    @Test
    fun test_field_state_change_listener_last() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val listener = mock(OnFieldStateChangeListener::class.java)
        view.setOnFieldStateChangeListener(listener)

        verify(listener, times(1)).onStateChange(any())
    }


    @Test
    fun test_on_focus_change_listener() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val listener = mock(View.OnFocusChangeListener::class.java)
        view.onFocusChangeListener = listener
        view.requestFocus()

        verify(listener).onFocusChange(view, true)
    }

    @Test
    fun set_typeface() {
        assertEquals(null, view.getTypeface())
        view.getTypeface().let {
            view.setTypeface(it, Typeface.BOLD)
        }

        assertEquals(view.getTypeface(), Typeface.DEFAULT_BOLD)
        view.setTypeface(null, Typeface.NORMAL)
        assertEquals(view.getTypeface(), Typeface.DEFAULT)
    }


    @Test
    fun test_length() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = VGSInfoRule.ValidationBuilder()
            .setAllowableMinLength(12)
            .setAllowableMaxLength(15)
            .build()
        view.addRule(rule)

        view.setText("12312312312")
        child.refreshInternalState()
        view.getState().validateState(false, 11)

        view.setText("123123123123")
        child.refreshInternalState()
        view.getState().validateState(true, 12)

        view.setText("123123123123123")
        child.refreshInternalState()
        view.getState().validateState(true, 15)
    }

    @Test
    fun test_length_min() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = VGSInfoRule.ValidationBuilder()
            .setAllowableMinLength(7)
            .build()
        view.addRule(rule)

        view.setText("123123")
        child.refreshInternalState()
        view.getState().validateState(false, 6)

        view.setText("1231234")
        child.refreshInternalState()
        view.getState().validateState(true, 7)

        view.setText("1231231231231231231")
        child.refreshInternalState()
        view.getState().validateState(true, 19)
    }


    @Test
    fun test_length_max() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = VGSInfoRule.ValidationBuilder()
            .setAllowableMaxLength(17)
            .build()
        view.addRule(rule)

        view.setText("")
        child.refreshInternalState()
        view.getState().validateState(false, 0)

        view.setText("1")
        child.refreshInternalState()
        view.getState().validateState(true, 1)

        view.setText("1231231231233")
        child.refreshInternalState()
        view.getState().validateState(true, 13)

        view.setText("12312312312312312")
        child.refreshInternalState()
        view.getState().validateState(true, 17)

        view.setText("123123123123123123")
        child.refreshInternalState()
        view.getState().validateState(false, 18)

    }


    @Test
    fun test_length_min_max() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = VGSInfoRule.ValidationBuilder()
            .setAllowableMaxLength(17)
            .setAllowableMinLength(15)
            .build()
        view.addRule(rule)

        view.setText("12312312312312")
        child.refreshInternalState()
        view.getState().validateState(false, 14)

        view.setText("123123123123123")
        child.refreshInternalState()
        view.getState().validateState(true, 15)

        view.setText("12312312312312312")
        child.refreshInternalState()
        view.getState().validateState(true, 17)

        view.setText("12312312312312312")
        child.refreshInternalState()
        view.getState().validateState(true, 17)

        view.setText("123123123123123123")
        child.refreshInternalState()
        view.getState().validateState(false, 18)
    }


    @Test
    fun test_regex() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = VGSInfoRule.ValidationBuilder()
            .setRegex("^[0-9]{15}(?:[0-9]{1})?\$")
            .build()
        view.addRule(rule)

        view.setText("")
        child.refreshInternalState()
        view.getState().validateState(false, 0)

        view.setText("011111111111111 ")
        child.refreshInternalState()
        view.getState().validateState(false, 16)

        view.setText("0111111111111111")
        child.refreshInternalState()
        view.getState().validateState(true, 16)

        view.setText("0111111111111w111")
        child.refreshInternalState()
        view.getState().validateState(false, 17)

        view.setText("01111111111111")
        child.refreshInternalState()
        view.getState().validateState(false, 14)
    }

    @Test
    fun test_default_validation() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        view.setText("")
        child.refreshInternalState()
        view.getState().validateState(false, 0)

        view.setText("1")
        child.refreshInternalState()
        view.getState().validateState(true, 1)

        view.setText("")
        child.refreshInternalState()
        view.getState().validateState(false, 0)
    }

    private fun FieldState.InfoState?.validateState(
        isValid: Boolean,
        contentLength: Int
    ) {
        assertNotNull(this)
        if (this != null) {
            assertEquals(isValid, this.isValid)
            assertEquals(contentLength, this.contentLength)
        }
    }

}