package com.verygoodsecurity.vgscollect.view.card.number

import android.app.Activity
import android.text.InputType
import android.view.View
import com.verygoodsecurity.vgscollect.TestApplication
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.view.internal.SSNInputField
import com.verygoodsecurity.vgscollect.widget.SSNEditText
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
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
        val internal = view.getView()
        Assert.assertNotNull(internal)
    }


    @Test
    fun test_check_internal_view() {
        val internal = view.getView()
        Assert.assertNotNull(internal)

        val child = view.getView()
        Assert.assertTrue(child is SSNInputField)
    }

    @Test
    fun test_attach_view() {
        view.onAttachedToWindow()

        Assert.assertEquals(1, view.childCount)
    }

    @Test
    fun test_field_type() {
        val type = view.getFieldType()
        Assert.assertEquals(FieldType.SSN, type)
    }

    @Test
    fun test_input_type_number() {
        Assert.assertNotNull(view)

        view.setInputType(InputType.TYPE_CLASS_NUMBER)
        Assert.assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_input_type_number_password() {
        Assert.assertNotNull(view)

        val passType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(passType)
        Assert.assertEquals(passType, view.getInputType())

        view.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD)
        Assert.assertEquals(passType, view.getInputType())
    }

    @Test
    fun test_input_type_text_password() {
        Assert.assertNotNull(view)

        val passType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(passType)

        val correctType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        val it = view.getInputType()
        Assert.assertEquals(correctType, it)
    }

    @Test
    fun test_input_type_other() {
        Assert.assertNotNull(view)

        view.setInputType(InputType.TYPE_CLASS_TEXT)
        Assert.assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())

        view.setInputType(InputType.TYPE_CLASS_DATETIME)
        Assert.assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
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

        Mockito.verify(listener, Mockito.times(1)).onStateChange(any())
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
        Assert.assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val listener = Mockito.mock(View.OnFocusChangeListener::class.java)
        view.onFocusChangeListener = listener
        view.requestFocus()

        Mockito.verify(listener, Mockito.times(1)).onFocusChange(view, true)
    }

    @Test
    fun test_state() {
        val text = "123-45-6789"
        val stateResult = FieldState.SSNNumberState()
        stateResult.hasFocus = false
        stateResult.isEmpty = false
        stateResult.isValid = true
        stateResult.isRequired = true
        stateResult.contentLength = text.length
        stateResult.fieldName = "number"
        stateResult.fieldType = FieldType.SSN
        stateResult.last = "6789"

        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)
        view.setText(text)
        view.setFieldName("number")

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

        Assert.assertEquals(stateResult.last, state.last)
    }


    @Test
    fun test_length() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        child.refreshInternalState()
        view.setText("123-12-3123")

        child.refreshInternalState()

        val state = view.getState()
        Assert.assertNotNull(state)
        Assert.assertEquals(true, state!!.isValid)
        Assert.assertEquals(11, state.contentLength)
        Assert.assertEquals("3123", state.last)

        view.setText("123-12-3123777")
        child.refreshInternalState()

        val state2 = view.getState()
        Assert.assertNotNull(state)
        Assert.assertEquals(false, state2!!.isValid)
        Assert.assertEquals(14, state2.contentLength)
        Assert.assertEquals("", state2.last)

        view.setText("123-12-312")
        child.refreshInternalState()

        val state3 = view.getState()
        Assert.assertNotNull(state)
        Assert.assertEquals(false, state3!!.isValid)
        Assert.assertEquals(10, state3.contentLength)
        Assert.assertEquals("", state3.last)
    }


    private fun <T> any(): T = Mockito.any<T>()
}