package com.verygoodsecurity.vgscollect.view.card.oldview

import android.app.Activity
import android.text.InputType
import android.view.View
import com.verygoodsecurity.vgscollect.TestApplication
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.view.internal.InputField
import com.verygoodsecurity.vgscollect.widget.VGSEditText
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
        Assert.assertNotNull(internal)
    }

    @Test
    fun test_attach_view() {
        view.onAttachedToWindow()

        Assert.assertEquals(1, view.childCount)
    }

    @Test
    fun test_check_internal_view() {
        val internal = view.statePreparer.getView()
        Assert.assertNotNull(internal)

        val child = view.statePreparer.getView()
        Assert.assertTrue(child is InputField)
    }

    @Test
    fun test_card_number() {
        view.setFieldType(FieldType.CARD_NUMBER)
        Assert.assertEquals(FieldType.CARD_NUMBER, view.getFieldType())
    }

    @Test
    fun test_exp_date() {
        view.setFieldType(FieldType.CARD_EXPIRATION_DATE)
        Assert.assertEquals(FieldType.CARD_EXPIRATION_DATE, view.getFieldType())
    }

    @Test
    fun test_holder_name() {
        view.setFieldType(FieldType.CARD_HOLDER_NAME)
        Assert.assertEquals(FieldType.CARD_HOLDER_NAME, view.getFieldType())
    }

    @Test
    fun test_cvc() {
        view.setFieldType(FieldType.CVC)
        Assert.assertEquals(FieldType.CVC, view.getFieldType())
    }

    @Test
    fun test_cvc_set_text_false() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CVC)
        Assert.assertEquals(FieldType.CVC, view.getFieldType())

        view.setText("12f")
        Assert.assertEquals("", (view.statePreparer.getView() as BaseInputField).text.toString())
        view.setText("12 333333")
        Assert.assertEquals("", (view.statePreparer.getView() as BaseInputField).text.toString())
    }

    @Test
    fun test_cvc_set_text_true() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CVC)

        view.setText("12333333")
        Assert.assertEquals("1233", (view.statePreparer.getView() as BaseInputField).text.toString())

        view.setText("123")
        Assert.assertEquals("123", (view.statePreparer.getView() as BaseInputField).text.toString())
    }

    @Test
    fun test_info() {
        view.setFieldType(FieldType.INFO)
        Assert.assertEquals(FieldType.INFO, view.getFieldType())
    }

    @Test
    fun test_card_number_input_type_none() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_NUMBER)

        view.setInputType(InputType.TYPE_NULL)
        Assert.assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_card_number_input_type_number() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_NUMBER)

        view.setInputType(InputType.TYPE_CLASS_NUMBER)
        Assert.assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_card_number_input_type_number_password() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_NUMBER)

        val passType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(passType)
        Assert.assertEquals(passType, view.getInputType())

        view.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD)
        Assert.assertEquals(passType, view.getInputType())
    }

    @Test
    fun test_card_number_input_type_text_password() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_NUMBER)

        val passType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(passType)

        val correctType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        val it = view.getInputType()
        Assert.assertEquals(correctType, it)
    }

    @Test
    fun test_card_number_input_type_other() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_NUMBER)

        view.setInputType(InputType.TYPE_CLASS_TEXT)
        Assert.assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())

        view.setInputType(InputType.TYPE_CLASS_DATETIME)
        Assert.assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_exp_date_input_type_number() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_EXPIRATION_DATE)

        view.setInputType(InputType.TYPE_CLASS_NUMBER)
        Assert.assertEquals(InputType.TYPE_CLASS_DATETIME, view.getInputType())
    }

    @Test
    fun test_exp_date_input_type_number_password() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_EXPIRATION_DATE)

        val passType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(passType)
        Assert.assertEquals(passType, view.getInputType())
    }

    @Test
    fun test_exp_date_input_type_text() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_EXPIRATION_DATE)

        val passType = InputType.TYPE_CLASS_TEXT
        view.setInputType(passType)
        Assert.assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())
    }

    @Test
    fun test_exp_date_input_type_text_password() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_EXPIRATION_DATE)

        val passType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        view.setInputType(passType)
        Assert.assertEquals(passType, view.getInputType())
    }

    @Test
    fun test_exp_date_input_type_date() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_EXPIRATION_DATE)

        view.setInputType(InputType.TYPE_CLASS_DATETIME)
        Assert.assertEquals(InputType.TYPE_CLASS_DATETIME, view.getInputType())
    }

    @Test
    fun test_exp_date_input_type_other() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_EXPIRATION_DATE)

        view.setInputType(InputType.TYPE_CLASS_PHONE)
        Assert.assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())

        view.setInputType(InputType.TYPE_CLASS_PHONE or InputType.TYPE_CLASS_DATETIME)
        Assert.assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())

        view.setInputType(InputType.TYPE_MASK_CLASS)
        Assert.assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())

    }

    @Test
    fun test_exp_date_input_type_none() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_EXPIRATION_DATE)

        view.setInputType(InputType.TYPE_NULL)
        Assert.assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())
    }

    @Test
    fun test_cvc_input_type_number() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CVC)

        view.setInputType(InputType.TYPE_CLASS_NUMBER)
        Assert.assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_cvc_input_type_number_password() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CVC)

        val passType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(passType)
        Assert.assertEquals(passType, view.getInputType())

        view.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD)
        Assert.assertEquals(passType, view.getInputType())
    }

    @Test
    fun test_cvc_input_type_text_password() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CVC)

        val passType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(passType)

        val correctType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        val it = view.getInputType()
        Assert.assertEquals(correctType, it)
    }

    @Test
    fun test_cvc_input_type_other() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CVC)

        view.setInputType(InputType.TYPE_CLASS_TEXT)
        Assert.assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())

        view.setInputType(InputType.TYPE_CLASS_DATETIME)
        Assert.assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_cvc_input_type_none() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CVC)

        view.setInputType(InputType.TYPE_NULL)
        Assert.assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_card_holder_input_type_text() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_HOLDER_NAME)

        view.setInputType(InputType.TYPE_CLASS_TEXT)
        Assert.assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())
    }

    @Test
    fun test_card_holder_input_type_number() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_HOLDER_NAME)

        view.setInputType(InputType.TYPE_CLASS_NUMBER)
        Assert.assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_card_holder_input_type_date() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_HOLDER_NAME)

        view.setInputType(InputType.TYPE_CLASS_DATETIME)
        Assert.assertEquals(InputType.TYPE_CLASS_DATETIME, view.getInputType())
    }

    @Test
    fun test_card_holder_input_type_text_password() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_HOLDER_NAME)

        val textPass = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        view.setInputType(textPass)
        Assert.assertEquals(textPass, view.getInputType())
    }

    @Test
    fun test_card_holder_input_type_number_password() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_HOLDER_NAME)

        val numPass = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(numPass)
        Assert.assertEquals(numPass, view.getInputType())
    }

    @Test
    fun test_card_holder_input_type_none() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.CARD_HOLDER_NAME)

        view.setInputType(InputType.TYPE_NULL)
        Assert.assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())
    }

    @Test
    fun test_info_input_type_text() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.INFO)

        view.setInputType(InputType.TYPE_CLASS_TEXT)
        Assert.assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())
    }

    @Test
    fun test_info_input_type_number() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.INFO)

        view.setInputType(InputType.TYPE_CLASS_NUMBER)
        Assert.assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_info_input_type_date() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.INFO)

        view.setInputType(InputType.TYPE_CLASS_DATETIME)
        Assert.assertEquals(InputType.TYPE_CLASS_DATETIME, view.getInputType())
    }

    @Test
    fun test_info_input_type_text_password() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.INFO)

        val textPass = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        view.setInputType(textPass)
        Assert.assertEquals(textPass, view.getInputType())
    }

    @Test
    fun test_info_input_type_number_password() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.INFO)

        val numPass = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(numPass)
        Assert.assertEquals(numPass, view.getInputType())
    }

    @Test
    fun test_info_input_type_none() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        view.setFieldType(FieldType.INFO)

        view.setInputType(InputType.TYPE_NULL)
        Assert.assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())
    }

    @Test
    fun test_field_state_change_listener_first() {
        val child = view.statePreparer.getView()
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
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val listener = Mockito.mock(OnFieldStateChangeListener::class.java)
        view.setOnFieldStateChangeListener(listener)

        Mockito.verify(listener, Mockito.times(1)).onStateChange(any())
    }


    @Test
    fun test_on_focus_change_listener() {
        val child = view.statePreparer.getView()
        Assert.assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val listener = Mockito.mock(View.OnFocusChangeListener::class.java)
        view.onFocusChangeListener = listener
        view.requestFocus()

        Mockito.verify(listener).onFocusChange(view, true)
    }

    private fun <T> any(): T = Mockito.any<T>()
}