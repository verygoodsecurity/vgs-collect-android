package com.verygoodsecurity.vgscollect.view.card.name

import android.app.Activity
import android.text.InputType
import android.view.View
import com.verygoodsecurity.vgscollect.TestApplication
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.view.internal.PersonNameInputField
import com.verygoodsecurity.vgscollect.widget.PersonNameEditText
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@Ignore
@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class PersonNameEditTextTest {
    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var view: PersonNameEditText

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        view = PersonNameEditText(activity)
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
        Assert.assertTrue(child is PersonNameInputField)
    }

    @Test
    fun test_cvc() {
        Assert.assertEquals(FieldType.CARD_HOLDER_NAME, view.getFieldType())
    }

    @Test
    fun test_set_text_true() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setText("123")
        Assert.assertEquals("123", (view.getView() as BaseInputField).text.toString())
    }


    @Test
    fun test_input_type_text() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)


        view.setInputType(InputType.TYPE_CLASS_TEXT)
        Assert.assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())
    }

    @Test
    fun test_input_type_number() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setInputType(InputType.TYPE_CLASS_NUMBER)
        Assert.assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_input_type_date() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)


        view.setInputType(InputType.TYPE_CLASS_DATETIME)
        Assert.assertEquals(InputType.TYPE_CLASS_DATETIME, view.getInputType())
    }

    @Test
    fun test_input_type_text_password() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        val textPass = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        view.setInputType(textPass)
        Assert.assertEquals(textPass, view.getInputType())
    }

    @Test
    fun test_input_type_number_password() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        val numPass = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(numPass)
        Assert.assertEquals(numPass, view.getInputType())
    }

    @Test
    fun test_input_type_none() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setInputType(InputType.TYPE_NULL)
        Assert.assertEquals(InputType.TYPE_NULL, view.getInputType())
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
        val text = "vasia"
        val stateResult = FieldState.CardHolderNameState()
        stateResult.hasFocus = false
        stateResult.isEmpty = false
        stateResult.isValid = true
        stateResult.isRequired = true
        stateResult.contentLength = text.length
        stateResult.fieldName = "holder"
        stateResult.fieldType = FieldType.CARD_HOLDER_NAME

        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)
        view.setText(text)
        view.setFieldName("holder")

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


}