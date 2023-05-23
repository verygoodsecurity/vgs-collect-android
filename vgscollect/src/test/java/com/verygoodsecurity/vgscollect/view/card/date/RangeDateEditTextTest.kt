package com.verygoodsecurity.vgscollect.view.card.date

import android.app.Activity
import android.graphics.Typeface
import android.text.InputType
import android.view.View
import com.verygoodsecurity.vgscollect.TestApplication
import com.verygoodsecurity.vgscollect.any
import com.verygoodsecurity.vgscollect.core.model.VGSDate
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultStorageType
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.formatter.rules.FormatMode
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.view.internal.core.DateInputField
import com.verygoodsecurity.vgscollect.widget.RangeDateEditText
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class RangeDateEditTextTest {

    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var view: RangeDateEditText

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        view = RangeDateEditText(activity)
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
        assertTrue(child is DateInputField)
    }

    @Test
    fun test_field_type() {
        val type = view.getFieldType()
        assertEquals(FieldType.DATE_RANGE, type)
    }

    @Test
    fun test_MM_dd_yyyy() {
        view.setDateRegex("MM/dd/yyyy")
        assertEquals("MM/dd/yyyy", view.getDateRegex())

        view.setDateRegex("MM dd yyyy")
        assertEquals("MM/dd/yyyy", view.getDateRegex())

        view.setDateRegex("MM-dd-yyyy")
        assertEquals("MM/dd/yyyy", view.getDateRegex())
    }

    @Test
    fun test_dd_MM_yyyy() {
        view.setDateRegex("dd/MM/yyyy")
        assertEquals("dd/MM/yyyy", view.getDateRegex())

        view.setDateRegex("dd MM yyyy")
        assertEquals("dd/MM/yyyy", view.getDateRegex())

        view.setDateRegex("dd-MM-yyyy")
        assertEquals("dd/MM/yyyy", view.getDateRegex())
    }

    @Test
    fun test_yyyy_MM_dd() {
        view.setDateRegex("yyyy/MM/dd")
        assertEquals("yyyy/MM/dd", view.getDateRegex())

        view.setDateRegex("yyyy MM dd")
        assertEquals("yyyy/MM/dd", view.getDateRegex())

        view.setDateRegex("yyyy-MM-dd")
        assertEquals("yyyy/MM/dd", view.getDateRegex())
    }

    @Test
    fun test_date_picker_mode() {
        view.setDatePickerMode(DatePickerMode.SPINNER)
        assertEquals(DatePickerMode.SPINNER, view.getDatePickerMode())

        view.setDatePickerMode(DatePickerMode.CALENDAR)
        assertEquals(DatePickerMode.CALENDAR, view.getDatePickerMode())

        view.setDatePickerMode(DatePickerMode.INPUT)
        assertEquals(DatePickerMode.INPUT, view.getDatePickerMode())

        view.setDatePickerMode(DatePickerMode.DEFAULT)
        assertEquals(DatePickerMode.INPUT, view.getDatePickerMode())
    }

    @Test
    fun test_input_picker_mode_failure2() {
        val defaultPattern = "MM/dd/yyyy"

        view.setDatePickerMode(DatePickerMode.INPUT)
        assertEquals(DatePickerMode.INPUT, view.getDatePickerMode())

        view.setDateRegex("dd/MMMM/yy")
        assertEquals(defaultPattern, view.getDateRegex())

        view.setDateRegex("dd/mm/yy")
        assertEquals(defaultPattern, view.getDateRegex())

        view.setDateRegex("dd/mmmm/yy")
        assertEquals(defaultPattern, view.getDateRegex())

        view.setDateRegex("dd0mmTyy")
        assertEquals(defaultPattern, view.getDateRegex())

        view.setDateRegex("dd mm/yy'T'")
        assertEquals(defaultPattern, view.getDateRegex())

        view.setDateRegex("dd mm/yy ")
        assertEquals(defaultPattern, view.getDateRegex())

        view.setDateRegex("dd mm/yyy")
        assertEquals(defaultPattern, view.getDateRegex())
    }

    @Test
    fun test_input_picker_mode_right() {
        view.setDatePickerMode(DatePickerMode.INPUT)
        assertEquals(DatePickerMode.INPUT, view.getDatePickerMode())

        view.setDateRegex("MM/dd/yyyy")
        assertEquals("MM/dd/yyyy", view.getDateRegex())

        view.setDateRegex("dd/MM/yyyy")
        assertEquals("dd/MM/yyyy", view.getDateRegex())

        view.setDateRegex("yyyy/MM/dd")
        assertEquals("yyyy/MM/dd", view.getDateRegex())
    }

    @Test
    fun test_calendar_picker_mode_right() {
        view.setDatePickerMode(DatePickerMode.CALENDAR)
        assertEquals(DatePickerMode.CALENDAR, view.getDatePickerMode())

        view.setDateRegex("dd/MM/yyyy")
        assertEquals("dd/MM/yyyy", view.getDateRegex())
    }

    @Test
    fun test_spinner_picker_mode_right() {
        view.setDatePickerMode(DatePickerMode.SPINNER)
        assertEquals(DatePickerMode.SPINNER, view.getDatePickerMode())

        view.setDateRegex("dd/MM/yyyy")
        assertEquals("dd/MM/yyyy", view.getDateRegex())
    }

    @Test
    fun test_input_type_number() {
        assertNotNull(view)

        view.setInputType(InputType.TYPE_CLASS_NUMBER)
        assertEquals(InputType.TYPE_CLASS_DATETIME, view.getInputType())
    }

    @Test
    fun test_input_type_number_password() {
        assertNotNull(view)

        val passType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(passType)
        assertEquals(passType, view.getInputType())
    }

    @Test
    fun test_input_type_text() {
        assertNotNull(view)

        val passType = InputType.TYPE_CLASS_TEXT
        view.setInputType(passType)
        assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())
    }

    @Test
    fun test_input_type_text_password() {
        assertNotNull(view)

        val passType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        view.setInputType(passType)
        assertEquals(passType, view.getInputType())
    }

    @Test
    fun test_input_type_date() {
        assertNotNull(view)

        view.setInputType(InputType.TYPE_CLASS_DATETIME)
        assertEquals(InputType.TYPE_CLASS_DATETIME, view.getInputType())
    }

    @Test
    fun test_input_type_other() {
        assertNotNull(view)

        view.setInputType(InputType.TYPE_CLASS_PHONE)
        assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())

        view.setInputType(InputType.TYPE_CLASS_PHONE or InputType.TYPE_CLASS_DATETIME)
        assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())

        view.setInputType(InputType.TYPE_MASK_CLASS)
        assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())

        view.setInputType(InputType.TYPE_NULL)
        assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())
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

        verify(listener).onStateChange(any())
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
        view.setDatePickerMode(DatePickerMode.INPUT)
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val listener = mock(View.OnFocusChangeListener::class.java)
        view.onFocusChangeListener = listener
        view.requestFocus()

        verify(listener).onFocusChange(view, true)
    }

    @Test
    fun test_state_valid() {
        val text = "12/03/2023"

        val stateResult = FieldState.CardExpirationDateState()
        stateResult.hasFocus = false
        stateResult.isEmpty = false
        stateResult.isValid = true
        stateResult.isRequired = true
        stateResult.contentLength = text.length
        stateResult.fieldName = "date"
        stateResult.fieldType = FieldType.DATE_RANGE

        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setText(text)
        view.setFieldName("date")

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val state = view.getState()
        assertNotNull(state)
        assertEquals(stateResult.hasFocus, state!!.hasFocus)
        assertEquals(stateResult.isEmpty, state.isEmpty)
        assertEquals(stateResult.isValid, state.isValid)
        assertEquals(stateResult.isRequired, state.isRequired)
        assertEquals(stateResult.contentLength, state.contentLength)
        assertEquals(stateResult.fieldName, state.fieldName)
        assertEquals(stateResult.fieldType, state.fieldType)
    }

    @Test
    fun test_state_invalid_min_date() {
        val text = "01/02/2010"

        val stateResult = FieldState.CardExpirationDateState()
        stateResult.hasFocus = false
        stateResult.isEmpty = false
        stateResult.isValid = false
        stateResult.isRequired = true
        stateResult.contentLength = text.length
        stateResult.fieldName = "date"
        stateResult.fieldType = FieldType.DATE_RANGE

        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setMinDate(VGSDate.create(2, 2, 2010)!!)
        view.setText(text)
        view.setFieldName("date")

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val state = view.getState()
        assertNotNull(state)
        assertEquals(stateResult.hasFocus, state!!.hasFocus)
        assertEquals(stateResult.isEmpty, state.isEmpty)
        assertEquals(stateResult.isValid, state.isValid)
        assertEquals(stateResult.isRequired, state.isRequired)
        assertEquals(stateResult.contentLength, state.contentLength)
        assertEquals(stateResult.fieldName, state.fieldName)
        assertEquals(stateResult.fieldType, state.fieldType)
    }

    @Test
    fun test_state_invalid_max_date() {
        val text = "02/03/2010"

        val stateResult = FieldState.CardExpirationDateState()
        stateResult.hasFocus = false
        stateResult.isEmpty = false
        stateResult.isValid = false
        stateResult.isRequired = true
        stateResult.contentLength = text.length
        stateResult.fieldName = "date"
        stateResult.fieldType = FieldType.DATE_RANGE

        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setMaxDate(VGSDate.create(2, 2, 2010)!!)
        view.setText(text)
        view.setFieldName("date")

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val state = view.getState()
        assertNotNull(state)
        assertEquals(stateResult.hasFocus, state!!.hasFocus)
        assertEquals(stateResult.isEmpty, state.isEmpty)
        assertEquals(stateResult.isValid, state.isValid)
        assertEquals(stateResult.isRequired, state.isRequired)
        assertEquals(stateResult.contentLength, state.contentLength)
        assertEquals(stateResult.fieldName, state.fieldName)
        assertEquals(stateResult.fieldType, state.fieldType)
    }

    @Test
    fun test_formatter_mode() {
        view.setFormatterMode(FormatMode.FLEXIBLE.ordinal)
        assertEquals(FormatMode.FLEXIBLE.ordinal, view.getFormatterMode())

        view.setFormatterMode(FormatMode.STRICT.ordinal)
        assertEquals(FormatMode.STRICT.ordinal, view.getFormatterMode())
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
    fun test_alias_format() {
        view.setVaultAliasFormat(VGSVaultAliasFormat.UUID)

        val child = view.statePreparer.getView()

        assertEquals((child as DateInputField).vaultAliasFormat, VGSVaultAliasFormat.UUID)
    }

    @Test
    fun test_storage_type() {
        view.setVaultStorageType(VGSVaultStorageType.VOLATILE)

        val child = view.statePreparer.getView()
        assertEquals((child as DateInputField).vaultStorage, VGSVaultStorageType.VOLATILE)
    }

    @Test
    fun test_enabled_tokenization() {
        view.setEnabledTokenization(false)

        val child = view.statePreparer.getView()
        assertEquals((child as DateInputField).isEnabledTokenization, false)
    }

    @Test
    fun test_default_tokenization_settings() {
        val child = view.statePreparer.getView()
        assertEquals((child as DateInputField).isEnabledTokenization, true)
        assertEquals(child.vaultAliasFormat, VGSVaultAliasFormat.UUID)
        assertEquals(child.vaultStorage, VGSVaultStorageType.PERSISTENT)
    }
}