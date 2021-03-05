package com.verygoodsecurity.vgscollect.view.card.number

import android.app.Activity
import android.graphics.Typeface
import android.text.InputType
import android.view.View
import com.verygoodsecurity.vgscollect.TestApplication
import com.verygoodsecurity.vgscollect.any
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.view.internal.SSNInputField
import com.verygoodsecurity.vgscollect.widget.SSNEditText
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
class SSNEditTextTest {

    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var view: SSNEditText


    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        view = SSNEditText(activity)
        view.setFieldName("number")
    }

    @Test
    fun test_view() {
        view.onAttachedToWindow()
        val internal = view.statePreparer.getView()
        assertNotNull(internal)
    }

    @Test
    fun test_set_divider() {
        assertNotNull(view)

        view.setDivider('=')
        assertEquals('=', view.getDivider())
        view.setDivider('1')
        assertEquals('-', view.getDivider())
        view.setDivider('#')
        assertEquals('-', view.getDivider())
        view.setDivider('\\')
        assertEquals('-', view.getDivider())
        view.setDivider('/')
        assertEquals('/', view.getDivider())
        view.setDivider(null)
        assertEquals(null, view.getDivider())
        view.setDivider(' ')
        assertEquals(' ', view.getDivider())
    }

    @Test
    fun test_set_output_divider() {
        assertNotNull(view)

        view.setOutputDivider('=')
        assertEquals('=', view.getOutputDivider())
        view.setOutputDivider('1')
        assertEquals('-', view.getOutputDivider())
        view.setOutputDivider('#')
        assertEquals('-', view.getOutputDivider())
        view.setOutputDivider('\\')
        assertEquals('-', view.getOutputDivider())
        view.setOutputDivider('/')
        assertEquals('/', view.getOutputDivider())
        view.setOutputDivider(null)
        assertEquals(null, view.getOutputDivider())
        view.setOutputDivider(' ')
        assertEquals(' ', view.getOutputDivider())
    }

    @Test
    fun test_check_internal_view() {
        val internal = view.statePreparer.getView()
        assertNotNull(internal)

        val child = view.statePreparer.getView()
        assertTrue(child is SSNInputField)
    }

    @Test
    fun test_attach_view() {
        view.onAttachedToWindow()

        assertEquals(1, view.childCount)
    }

    @Test
    fun test_field_type() {
        val type = view.getFieldType()
        assertEquals(FieldType.SSN, type)
    }

    @Test
    fun test_input_type_number() {
        assertNotNull(view)

        view.setInputType(InputType.TYPE_CLASS_NUMBER)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_input_type_number_password() {
        assertNotNull(view)

        val passType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(passType)
        assertEquals(passType, view.getInputType())

        view.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD)
        assertEquals(passType, view.getInputType())
    }

    @Test
    fun test_input_type_text_password() {
        assertNotNull(view)

        val passType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(passType)

        val correctType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        val it = view.getInputType()
        assertEquals(correctType, it)
    }

    @Test
    fun test_input_type_other() {
        assertNotNull(view)

        view.setInputType(InputType.TYPE_CLASS_TEXT)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())

        view.setInputType(InputType.TYPE_CLASS_DATETIME)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
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

        verify(listener, times(1)).onFocusChange(view, true)
    }

    @Test
    fun test_state() {
        val text = "195-75-6789"
        val stateResult = FieldState.SSNNumberState()
        stateResult.hasFocus = false
        stateResult.isEmpty = false
        stateResult.isValid = true
        stateResult.isRequired = true
        stateResult.contentLength = text.length
        stateResult.fieldName = "number"
        stateResult.fieldType = FieldType.SSN
        stateResult.last = "6789"

        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)
        view.setText(text)
        view.setFieldName("number")

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

        assertEquals(stateResult.last, state.last)
    }

    @Test
    fun test_divider() {
        view.setDivider(' ')
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        child.refreshInternalState()
        view.setText("123 12 3123")

        child.refreshInternalState()

        val state = view.getState()
        assertNotNull(state)
        assertEquals(true, state!!.isValid)
        assertEquals(11, state.contentLength)
        assertEquals("3123", state.last)

        view.setText("123 12 3123777")
        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state)
        assertEquals(false, state2!!.isValid)
        assertEquals(14, state2.contentLength)
        assertEquals("", state2.last)

        view.setText("123 12 312")
        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state)
        assertEquals(false, state3!!.isValid)
        assertEquals(10, state3.contentLength)
        assertEquals("", state3.last)
    }

    @Test
    fun test_length() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        child.refreshInternalState()
        view.setText("123-12-3123")

        child.refreshInternalState()

        val state = view.getState()
        assertNotNull(state)
        assertEquals(true, state!!.isValid)
        assertEquals(11, state.contentLength)
        assertEquals("3123", state.last)

        view.setText("123-12-3123777")
        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state)
        assertEquals(false, state2!!.isValid)
        assertEquals(14, state2.contentLength)
        assertEquals("", state2.last)

        view.setText("123-12-312")
        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state)
        assertEquals(false, state3!!.isValid)
        assertEquals(10, state3.contentLength)
        assertEquals("", state3.last)
    }

    @Test
    fun test_not_valid_ssn() {
        val validator = RegexValidator(SSNInputField.VALIDATION_REGEX)
        getNotValidSSNs().forEach {
            assertFalse(validator.isValid(it))
        }
    }

    @Test
    fun test_valid_ssn() {
        val validator = RegexValidator(SSNInputField.VALIDATION_REGEX)
        getValidSSNs().forEach {
            assertTrue(validator.isValid(it))
        }
    }

    private fun getNotValidSSNs(): Array<String> {
        return arrayOf(
            "111-11-1111", "222-22-2222", "555-55-5555",
            "666-66-6666", "999-99-9999", "000-00-0000",
            "000-12-3456", "143-00-4563", "235-23-0000",
            "923-42-3423", "666-12-3456", "111-45-6789",
            "222-09-9999", "555-05-1120", "000-55-5462",
            "123-45-6789", "219-09-9999", "219-09-9999",
            "457-55-5462", "343-43-43", "111-11-111222"
        )
    }

    private fun getValidSSNs(): Array<String> {
        return arrayOf(
            "112-11-1112", "221-23-2222", "455-55-5555",
            "166-66-6666", "899-99-9999", "001-01-0001",
            "100-12-3456", "143-10-4563", "235-23-1000",
            "823-42-3423", "665-12-3455", "123-45-6780",
            "219-09-9998", "078-05-1125", "457-55-5465"
        )
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

}