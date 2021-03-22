package com.verygoodsecurity.vgscollect.view.card.oldview

import android.app.Activity
import android.graphics.Typeface
import android.text.InputType
import android.view.View
import com.verygoodsecurity.vgscollect.TestApplication
import com.verygoodsecurity.vgscollect.any
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.view.internal.InputField
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
        assertTrue(child is InputField)
    }

    @Test
    fun test_card_number() {
        view.setFieldType(FieldType.CARD_NUMBER)
        assertEquals(FieldType.CARD_NUMBER, view.getFieldType())
    }

    @Test
    fun test_exp_date() {
        view.setFieldType(FieldType.CARD_EXPIRATION_DATE)
        assertEquals(FieldType.CARD_EXPIRATION_DATE, view.getFieldType())
    }

    @Test
    fun test_holder_name() {
        view.setFieldType(FieldType.CARD_HOLDER_NAME)
        assertEquals(FieldType.CARD_HOLDER_NAME, view.getFieldType())
    }

    @Test
    fun test_cvc() {
        view.setFieldType(FieldType.CVC)
        assertEquals(FieldType.CVC, view.getFieldType())
    }

    @Test
    fun test_cvc_set_text_false() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CVC)
        assertEquals(FieldType.CVC, view.getFieldType())

        view.setText("12f")
        assertEquals("", (view.statePreparer.getView() as BaseInputField).text.toString())
        view.setText("12 333333")
        assertEquals("", (view.statePreparer.getView() as BaseInputField).text.toString())
    }

    @Test
    fun test_cvc_set_text_true() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CVC)

        view.setText("12333333")
        assertEquals("1233", (view.statePreparer.getView() as BaseInputField).text.toString())

        view.setText("123")
        assertEquals("123", (view.statePreparer.getView() as BaseInputField).text.toString())
    }

    @Test
    fun test_info() {
        view.setFieldType(FieldType.INFO)
        assertEquals(FieldType.INFO, view.getFieldType())
    }

    @Test
    fun test_card_number_input_type_none() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_NUMBER)

        view.setInputType(InputType.TYPE_NULL)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_card_number_input_type_number() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_NUMBER)

        view.setInputType(InputType.TYPE_CLASS_NUMBER)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_card_number_input_type_number_password() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_NUMBER)

        val passType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(passType)
        assertEquals(passType, view.getInputType())

        view.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD)
        assertEquals(passType, view.getInputType())
    }

    @Test
    fun test_card_number_input_type_text_password() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_NUMBER)

        val passType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(passType)

        val correctType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        val it = view.getInputType()
        assertEquals(correctType, it)
    }

    @Test
    fun test_card_number_input_type_other() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_NUMBER)

        view.setInputType(InputType.TYPE_CLASS_TEXT)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())

        view.setInputType(InputType.TYPE_CLASS_DATETIME)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_exp_date_input_type_number() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_EXPIRATION_DATE)

        view.setInputType(InputType.TYPE_CLASS_NUMBER)
        assertEquals(InputType.TYPE_CLASS_DATETIME, view.getInputType())
    }

    @Test
    fun test_exp_date_input_type_number_password() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_EXPIRATION_DATE)

        val passType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(passType)
        assertEquals(passType, view.getInputType())
    }

    @Test
    fun test_exp_date_input_type_text() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_EXPIRATION_DATE)

        val passType = InputType.TYPE_CLASS_TEXT
        view.setInputType(passType)
        assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())
    }

    @Test
    fun test_exp_date_input_type_text_password() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_EXPIRATION_DATE)

        val passType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        view.setInputType(passType)
        assertEquals(passType, view.getInputType())
    }

    @Test
    fun test_exp_date_input_type_date() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_EXPIRATION_DATE)

        view.setInputType(InputType.TYPE_CLASS_DATETIME)
        assertEquals(InputType.TYPE_CLASS_DATETIME, view.getInputType())
    }

    @Test
    fun test_exp_date_input_type_other() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_EXPIRATION_DATE)

        view.setInputType(InputType.TYPE_CLASS_PHONE)
        assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())

        view.setInputType(InputType.TYPE_CLASS_PHONE or InputType.TYPE_CLASS_DATETIME)
        assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())

        view.setInputType(InputType.TYPE_MASK_CLASS)
        assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())

    }

    @Test
    fun test_exp_date_input_type_none() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_EXPIRATION_DATE)

        view.setInputType(InputType.TYPE_NULL)
        assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())
    }

    @Test
    fun test_cvc_input_type_number() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CVC)

        view.setInputType(InputType.TYPE_CLASS_NUMBER)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_cvc_input_type_number_password() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CVC)

        val passType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(passType)
        assertEquals(passType, view.getInputType())

        view.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD)
        assertEquals(passType, view.getInputType())
    }

    @Test
    fun test_cvc_input_type_text_password() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CVC)

        val passType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(passType)

        val correctType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        val it = view.getInputType()
        assertEquals(correctType, it)
    }

    @Test
    fun test_cvc_input_type_other() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CVC)

        view.setInputType(InputType.TYPE_CLASS_TEXT)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())

        view.setInputType(InputType.TYPE_CLASS_DATETIME)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_cvc_input_type_none() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CVC)

        view.setInputType(InputType.TYPE_NULL)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_card_holder_input_type_text() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_HOLDER_NAME)

        view.setInputType(InputType.TYPE_CLASS_TEXT)
        assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())
    }

    @Test
    fun test_card_holder_input_type_number() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_HOLDER_NAME)

        view.setInputType(InputType.TYPE_CLASS_NUMBER)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_card_holder_input_type_date() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_HOLDER_NAME)

        view.setInputType(InputType.TYPE_CLASS_DATETIME)
        assertEquals(InputType.TYPE_CLASS_DATETIME, view.getInputType())
    }

    @Test
    fun test_card_holder_input_type_text_password() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_HOLDER_NAME)

        val textPass = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        view.setInputType(textPass)
        assertEquals(textPass, view.getInputType())
    }

    @Test
    fun test_card_holder_input_type_number_password() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_HOLDER_NAME)

        val numPass = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(numPass)
        assertEquals(numPass, view.getInputType())
    }

    @Test
    fun test_card_holder_input_type_none() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_HOLDER_NAME)

        view.setInputType(InputType.TYPE_NULL)
        assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())
    }

    @Test
    fun test_info_input_type_text() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.INFO)

        view.setInputType(InputType.TYPE_CLASS_TEXT)
        assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())
    }

    @Test
    fun test_info_input_type_number() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.INFO)

        view.setInputType(InputType.TYPE_CLASS_NUMBER)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_info_input_type_date() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.INFO)

        view.setInputType(InputType.TYPE_CLASS_DATETIME)
        assertEquals(InputType.TYPE_CLASS_DATETIME, view.getInputType())
    }

    @Test
    fun test_info_input_type_text_password() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.INFO)

        val textPass = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        view.setInputType(textPass)
        assertEquals(textPass, view.getInputType())
    }

    @Test
    fun test_info_input_type_number_password() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.INFO)

        val numPass = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(numPass)
        assertEquals(numPass, view.getInputType())
    }

    @Test
    fun test_info_input_type_none() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.INFO)

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
}