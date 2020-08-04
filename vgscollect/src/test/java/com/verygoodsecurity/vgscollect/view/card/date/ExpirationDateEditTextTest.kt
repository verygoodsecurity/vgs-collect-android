package com.verygoodsecurity.vgscollect.view.card.date

import android.app.Activity
import android.text.InputType
import android.view.View
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.formatter.rules.FormatMode
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.view.internal.DateInputField
import com.verygoodsecurity.vgscollect.widget.ExpirationDateEditText
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController

@Ignore
@RunWith(RobolectricTestRunner::class)
class ExpirationDateEditTextTest {

    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var view: ExpirationDateEditText

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        view = ExpirationDateEditText(activity)
    }

    @Test
    fun test_view() {
        view.onAttachedToWindow()
        val internal = view.getView()
        Assert.assertNotNull(internal)
    }

    @Test
    fun test_attach_view() {
        view.onAttachedToWindow()

        Assert.assertEquals(1, view.childCount)
    }

    @Test
    fun test_check_internal_view() {
        val internal = view.getView()
        Assert.assertNotNull(internal)

        val child = view.getView()
        Assert.assertTrue(child is DateInputField)
    }

    @Test
    fun test_field_type() {
        val type = view.getFieldType()
        Assert.assertEquals(FieldType.CARD_EXPIRATION_DATE, type)
    }

    @Test
    fun test_dd_MM_yy() {
        view.setDateRegex("dd/MM/yy")
        Assert.assertEquals("dd/MM/yy", view.getDateRegex())

        view.setDateRegex("dd MMMM yyyy")
        Assert.assertEquals("dd MMMM yyyy", view.getDateRegex())
    }

    @Test
    fun test_date_picker_mode() {
        view.setDatePickerMode(DatePickerMode.SPINNER)
        Assert.assertEquals(DatePickerMode.SPINNER, view.getDatePickerMode())

        view.setDatePickerMode(DatePickerMode.CALENDAR)
        Assert.assertEquals(DatePickerMode.CALENDAR, view.getDatePickerMode())

        view.setDatePickerMode(DatePickerMode.INPUT)
        Assert.assertEquals(DatePickerMode.INPUT, view.getDatePickerMode())

        view.setDatePickerMode(DatePickerMode.DEFAULT)
        Assert.assertEquals(DatePickerMode.INPUT, view.getDatePickerMode())
    }

    @Test
    fun test_input_picker_mode_failure() {
        val DEFAULT_PATTERN = "MM/yyyy"

        view.setDateRegex("HH:mm dd/yy")
        Assert.assertEquals("HH:mm dd/yy", view.getDateRegex())

        view.setDatePickerMode(DatePickerMode.INPUT)
        Assert.assertEquals(DatePickerMode.INPUT, view.getDatePickerMode())

        Assert.assertEquals(DEFAULT_PATTERN, view.getDateRegex())
    }

        @Test
    fun test_input_picker_mode_failure2() {
        val DEFAULT_PATTERN = "MM/yyyy"

        view.setDatePickerMode(DatePickerMode.INPUT)
        Assert.assertEquals(DatePickerMode.INPUT, view.getDatePickerMode())

        view.setDateRegex("dd/MMMM/yy")
        Assert.assertEquals(DEFAULT_PATTERN, view.getDateRegex())

        view.setDateRegex("dd/mm/yy")
        Assert.assertEquals(DEFAULT_PATTERN, view.getDateRegex())

        view.setDateRegex("dd/mmmm/yy")
        Assert.assertEquals(DEFAULT_PATTERN, view.getDateRegex())

        view.setDateRegex("dd0mmTyy")
        Assert.assertEquals(DEFAULT_PATTERN, view.getDateRegex())

        view.setDateRegex("dd mm/yy'T'")
        Assert.assertEquals(DEFAULT_PATTERN, view.getDateRegex())

        view.setDateRegex("dd mm/yy ")
        Assert.assertEquals(DEFAULT_PATTERN, view.getDateRegex())

        view.setDateRegex("dd mm/yyy")
        Assert.assertEquals(DEFAULT_PATTERN, view.getDateRegex())
    }

    @Test
    fun test_input_picker_mode_right() {
        view.setDatePickerMode(DatePickerMode.INPUT)
        Assert.assertEquals(DatePickerMode.INPUT, view.getDatePickerMode())

        view.setDateRegex("dd/MM/yy")
        Assert.assertEquals("dd/MM/yy", view.getDateRegex())

        view.setDateRegex("dd/MM/yyyy")
        Assert.assertEquals("dd/MM/yyyy", view.getDateRegex())

        view.setDateRegex("MM/yy")
        Assert.assertEquals("MM/yy", view.getDateRegex())

        view.setDateRegex("MM/yy")
        Assert.assertEquals("MM/yy", view.getDateRegex())
    }

    @Test
    fun test_calendar_picker_mode_right() {
        view.setDatePickerMode(DatePickerMode.CALENDAR)
        Assert.assertEquals(DatePickerMode.CALENDAR, view.getDatePickerMode())

        view.setDateRegex("HH:mm dd/MMMM/yy")
        Assert.assertEquals("HH:mm dd/MMMM/yy", view.getDateRegex())
    }

    @Test
    fun test_spinner_picker_mode_right() {
        view.setDatePickerMode(DatePickerMode.SPINNER)
        Assert.assertEquals(DatePickerMode.SPINNER, view.getDatePickerMode())

        view.setDateRegex("HH:mm dd/MMMM/yy")
        Assert.assertEquals("HH:mm dd/MMMM/yy", view.getDateRegex())
    }

    @Test
    fun test_input_type_number() {
        Assert.assertNotNull(view)

        view.setInputType(InputType.TYPE_CLASS_NUMBER)
        Assert.assertEquals(InputType.TYPE_CLASS_DATETIME, view.getInputType())
    }

    @Test
    fun test_input_type_number_password() {
        Assert.assertNotNull(view)

        val passType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(passType)
        Assert.assertEquals(passType, view.getInputType())
    }

    @Test
    fun test_input_type_text() {
        Assert.assertNotNull(view)

        val passType = InputType.TYPE_CLASS_TEXT
        view.setInputType(passType)
        Assert.assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())
    }

    @Test
    fun test_input_type_text_password() {
        Assert.assertNotNull(view)

        val passType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        view.setInputType(passType)
        Assert.assertEquals(passType, view.getInputType())
    }

    @Test
    fun test_input_type_date() {
        Assert.assertNotNull(view)

        view.setInputType(InputType.TYPE_CLASS_DATETIME)
        Assert.assertEquals(InputType.TYPE_CLASS_DATETIME, view.getInputType())
    }

    @Test
    fun test_input_type_other() {
        Assert.assertNotNull(view)

        view.setInputType(InputType.TYPE_CLASS_PHONE)
        Assert.assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())

        view.setInputType(InputType.TYPE_CLASS_PHONE or InputType.TYPE_CLASS_DATETIME)
        Assert.assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())

        view.setInputType(InputType.TYPE_MASK_CLASS)
        Assert.assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())

        view.setInputType(InputType.TYPE_NULL)
        Assert.assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())
    }

    @Test
    fun test_field_state_change_listener_first() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        val listener = Mockito.mock(OnFieldStateChangeListener::class.java)
        view.setOnFieldStateChangeListener(listener)
        Mockito.verify(listener, Mockito.times(0)).onStateChange(any())

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        Mockito.verify(listener).onStateChange(any())
    }

    @Test
    fun test_field_state_change_listener_last() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val listener = Mockito.mock(OnFieldStateChangeListener::class.java)
        view.setOnFieldStateChangeListener(listener)

        Mockito.verify(listener, Mockito.times(1)).onStateChange(any())
    }

    @Test
    fun test_on_focus_change_listener() {
        val child = view.getView()
        view.setDatePickerMode(DatePickerMode.INPUT)
        Assert.assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val listener = Mockito.mock(View.OnFocusChangeListener::class.java)
        view.onFocusChangeListener = listener
        view.requestFocus()

        Mockito.verify(listener).onFocusChange(view, true)
    }

    @Test
    fun test_state_valid() {
        val text = "12/2023"
        val stateResult = FieldState.CardExpirationDateState()
        stateResult.hasFocus = false
        stateResult.isEmpty = false
        stateResult.isValid = true
        stateResult.isRequired = true
        stateResult.contentLength = text.length
        stateResult.fieldName = "date"
        stateResult.fieldType = FieldType.CARD_EXPIRATION_DATE

        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setText(text)
        view.setFieldName("date")

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()


        val state = view.getState()
        Assert.assertNotNull(state)
        Assert.assertEquals(stateResult.hasFocus, state!!.hasFocus)
        Assert.assertEquals(stateResult.isEmpty, state.isEmpty)
        Assert.assertEquals(stateResult.isValid, state.isValid)
        Assert.assertEquals(stateResult.isRequired, state.isRequired)
        Assert.assertEquals(stateResult.contentLength, state.contentLength)
        Assert.assertEquals(stateResult.fieldName, state.fieldName)
        Assert.assertEquals(stateResult.fieldType, state.fieldType)
    }

    @Test
    fun test_state_invalid() {
        val text = "12/9999"
        val stateResult = FieldState.CardExpirationDateState()
        stateResult.hasFocus = false
        stateResult.isEmpty = false
        stateResult.isValid = false
        stateResult.isRequired = true
        stateResult.contentLength = text.length
        stateResult.fieldName = "date"
        stateResult.fieldType = FieldType.CARD_EXPIRATION_DATE

        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setText(text)
        view.setFieldName("date")

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()


        val state = view.getState()
        Assert.assertNotNull(state)
        Assert.assertEquals(stateResult.hasFocus, state!!.hasFocus)
        Assert.assertEquals(stateResult.isEmpty, state.isEmpty)
        Assert.assertEquals(stateResult.isValid, state.isValid)
        Assert.assertEquals(stateResult.isRequired, state.isRequired)
        Assert.assertEquals(stateResult.contentLength, state.contentLength)
        Assert.assertEquals(stateResult.fieldName, state.fieldName)
        Assert.assertEquals(stateResult.fieldType, state.fieldType)
    }

    @Test
    fun test_formatter_mode() {
        view.setFormatterMode(FormatMode.FLEXIBLE.ordinal)
        Assert.assertEquals(FormatMode.FLEXIBLE.ordinal, view.getFormatterMode())

        view.setFormatterMode(FormatMode.STRICT.ordinal)
        Assert.assertEquals(FormatMode.STRICT.ordinal, view.getFormatterMode())
    }


    private fun <T> any(): T = Mockito.any<T>()
}