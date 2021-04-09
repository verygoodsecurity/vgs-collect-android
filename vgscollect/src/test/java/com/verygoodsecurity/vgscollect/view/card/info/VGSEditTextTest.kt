package com.verygoodsecurity.vgscollect.view.card.info

import android.app.Activity
import android.graphics.Typeface
import android.text.InputType
import android.view.View
import com.verygoodsecurity.vgscollect.TestApplication
import com.verygoodsecurity.vgscollect.any
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

        val state = view.getState()
        assertNotNull(state)
        assertEquals(false, state!!.isValid)
        assertEquals(11, state.contentLength)

        view.setText("123123123123")
        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state)
        assertEquals(true, state2!!.isValid)
        assertEquals(12, state2.contentLength)

        view.setText("123123123123123")
        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state)
        assertEquals(true, state3!!.isValid)
        assertEquals(15, state3.contentLength)
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

        val state = view.getState()
        assertNotNull(state)
        assertEquals(false, state!!.isValid)
        assertEquals(6, state.contentLength)

        view.setText("1231234")

        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state)
        assertEquals(true, state2!!.isValid)
        assertEquals(7, state2.contentLength)

        view.setText("1231231231231231231")

        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state)
        assertEquals(true, state3!!.isValid)
        assertEquals(19, state3.contentLength)
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

        val state = view.getState()
        assertNotNull(state)
        assertEquals(false, state!!.isValid)
        assertEquals(0, state.contentLength)

        view.setText("1")

        child.refreshInternalState()

        val state1 = view.getState()
        assertNotNull(state)
        assertEquals(true, state1!!.isValid)
        assertEquals(1, state1.contentLength)

        view.setText("1231231231233")

        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state)
        assertEquals(true, state2!!.isValid)
        assertEquals(13, state2.contentLength)

        view.setText("12312312312312312")

        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state)
        assertEquals(true, state3!!.isValid)
        assertEquals(17, state3.contentLength)

        view.setText("123123123123123123")

        child.refreshInternalState()

        val state4 = view.getState()
        assertNotNull(state)
        assertEquals(false, state4!!.isValid)
        assertEquals(18, state4.contentLength)
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

        val state = view.getState()
        assertNotNull(state)
        assertEquals(false, state!!.isValid)
        assertEquals(14, state.contentLength)


        view.setText("123123123123123")
        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state)
        assertEquals(true, state2!!.isValid)
        assertEquals(15, state2.contentLength)

        view.setText("12312312312312312")
        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state)
        assertEquals(true, state3!!.isValid)
        assertEquals(17, state3.contentLength)

        view.setText("12312312312312312")
        child.refreshInternalState()

        val state4 = view.getState()
        assertNotNull(state)
        assertEquals(true, state4!!.isValid)
        assertEquals(17, state4.contentLength)

        view.setText("123123123123123123")
        child.refreshInternalState()

        val state5 = view.getState()
        assertNotNull(state)
        assertEquals(false, state5!!.isValid)
        assertEquals(18, state5.contentLength)
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

        val state0 = view.getState()
        assertNotNull(state0)
        assertEquals(false, state0!!.isValid)
        assertEquals(0, state0.contentLength)


        view.setText("0111111111111111")
        child.refreshInternalState()

        val state1 = view.getState()
        assertNotNull(state1)
        assertEquals(true, state1!!.isValid)
        assertEquals(16, state1.contentLength)

        view.setText("0111111111111w111")
        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state2)
        assertEquals(false, state2!!.isValid)
        assertEquals(17, state2.contentLength)

        view.setText("01111111111111")
        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state3)
        assertEquals(false, state3!!.isValid)
        assertEquals(14, state3.contentLength)
    }

}