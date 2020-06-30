package com.verygoodsecurity.vgscollect.view.card.number

import android.app.Activity
import android.text.InputType
import android.view.Gravity
import android.view.View
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.validation.payment.PaymentCardNumberRule
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.view.internal.CardInputField
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController

@Ignore
@RunWith(RobolectricTestRunner::class)
class VGSCardNumberEditTextTest {

    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var view: VGSCardNumberEditText


    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        view = VGSCardNumberEditText(activity)
    }

    @Test
    fun test_view() {
        view.onAttachedToWindow()
        val internal = view.getView()
        assertNotNull(internal)
    }

    @Test
    fun test_check_internal_view() {
        val internal = view.getView()
        assertNotNull(internal)

        val child = view.getView()
        Assert.assertTrue(child is CardInputField)
    }

    @Test
    fun test_attach_view() {
        view.onAttachedToWindow()

        assertEquals(1, view.childCount)
    }


    @Test
    fun test_field_type() {
        val type = view.getFieldType()
        assertEquals(FieldType.CARD_NUMBER, type)
    }


    @Test
    fun test_set_divider() {
        assertNotNull(view)

        view.setDivider(' ')
        assertEquals(' ', view.getDivider())
    }

    @Test
    fun test_set_card_brand_icon_gravity_start() {
        assertNotNull(view)

        view.setCardBrandIconGravity(Gravity.START)
        assertEquals(Gravity.START, view.getCardPreviewIconGravity())
    }

    @Test
    fun test_set_card_brand_icon_gravity_left() {
        assertNotNull(view)

        view.setCardBrandIconGravity(Gravity.LEFT)
        assertEquals(Gravity.LEFT, view.getCardPreviewIconGravity())
    }

    @Test
    fun test_set_card_brand_icon_gravity_end() {
        assertNotNull(view)

        view.setCardBrandIconGravity(Gravity.END)
        assertEquals(Gravity.END, view.getCardPreviewIconGravity())
    }

    @Test
    fun test_set_card_brand_icon_gravity_right() {
        assertNotNull(view)

        view.setCardBrandIconGravity(Gravity.RIGHT)
        assertEquals(Gravity.RIGHT, view.getCardPreviewIconGravity())
    }

    @Test
    fun test_set_card_brand_icon_gravity_wrong() {
        assertNotNull(view)

        view.setCardBrandIconGravity(Gravity.TOP)
        assertEquals(Gravity.END, view.getCardPreviewIconGravity())
        view.setCardBrandIconGravity(Gravity.BOTTOM)
        assertEquals(Gravity.END, view.getCardPreviewIconGravity())
        view.setCardBrandIconGravity(Gravity.NO_GRAVITY)
        assertEquals(Gravity.NO_GRAVITY, view.getCardPreviewIconGravity())
        view.setCardBrandIconGravity(Gravity.CENTER_VERTICAL)
        assertEquals(Gravity.END, view.getCardPreviewIconGravity())
        view.setCardBrandIconGravity(Gravity.CENTER_HORIZONTAL)
        assertEquals(Gravity.END, view.getCardPreviewIconGravity())
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
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        val listener = mock(OnFieldStateChangeListener::class.java)
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

        val listener = mock(OnFieldStateChangeListener::class.java)
        view.setOnFieldStateChangeListener(listener)

        Mockito.verify(listener, Mockito.times(1)).onStateChange(any())
    }

    @Test
    fun test_on_focus_change_listener() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val listener = mock(View.OnFocusChangeListener::class.java)
        view.onFocusChangeListener = listener
        view.requestFocus()

        Mockito.verify(listener, Mockito.times(1)).onFocusChange(view, true)
    }

    @Test
    fun test_state() {
        val text = "4111111111111111"
        val stateResult = FieldState.CardNumberState()
        stateResult.hasFocus = false
        stateResult.isEmpty = false
        stateResult.isValid = true
        stateResult.isRequired = true
        stateResult.contentLength = text.length
        stateResult.fieldName = "number"
        stateResult.fieldType = FieldType.CARD_NUMBER
        stateResult.bin = "411111"
        stateResult.last = "1111"
        stateResult.number = "411111######1111"
        stateResult.drawableBrandResId = R.drawable.ic_visa_dark

        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)
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

        assertEquals(stateResult.bin, state.bin)
        assertEquals(stateResult.last, state.last)
        assertEquals(stateResult.number, state.number)
        assertEquals(stateResult.drawableBrandResId, state.drawableBrandResId)
    }

    @Test
    fun test_length() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAllowableNumberLength(arrayOf(12, 15))
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
        assertEquals("123123", state2.bin)

        view.setText("123123123123123")
        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state)
        assertEquals(true, state3!!.isValid)
        assertEquals(15, state3.contentLength)
        assertEquals("123123", state3.bin)
    }

    @Test
    fun test_length_min() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = PaymentCardNumberRule.ValidationBuilder()
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
        assertEquals("123123", state2.bin)

        view.setText("1231231231231231231")

        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state)
        assertEquals(true, state3!!.isValid)
        assertEquals(19, state3.contentLength)
        assertEquals("123123", state3.bin)
    }


    @Test
    fun test_length_max() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAllowableMaxLength(17)
            .build()
        view.addRule(rule)
        view.setText("1231231")

        child.refreshInternalState()

        val state = view.getState()
        assertNotNull(state)
        assertEquals(false, state!!.isValid)
        assertEquals(7, state.contentLength)

        view.setText("1231231231233")

        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state)
        assertEquals(true, state2!!.isValid)
        assertEquals(13, state2.contentLength)
        assertEquals("123123", state2.bin)

        view.setText("12312312312312312")

        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state)
        assertEquals(true, state3!!.isValid)
        assertEquals(17, state3.contentLength)
        assertEquals("123123", state3.bin)

        view.setText("123123123123123123")

        child.refreshInternalState()

        val state4 = view.getState()
        assertNotNull(state)
        assertEquals(false, state4!!.isValid)
        assertEquals(18, state4.contentLength)
        assertEquals("", state4.bin)
    }


    @Test
    fun test_length_min_max() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = PaymentCardNumberRule.ValidationBuilder()
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
        assertEquals("123123", state2.bin)

        view.setText("12312312312312312")
        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state)
        assertEquals(true, state3!!.isValid)
        assertEquals(17, state3.contentLength)
        assertEquals("123123", state3.bin)

        view.setText("12312312312312312")
        child.refreshInternalState()

        val state4 = view.getState()
        assertNotNull(state)
        assertEquals(true, state4!!.isValid)
        assertEquals(17, state4.contentLength)
        assertEquals("123123", state4.bin)

        view.setText("123123123123123123")
        child.refreshInternalState()

        val state5 = view.getState()
        assertNotNull(state)
        assertEquals(false, state5!!.isValid)
        assertEquals(18, state5.contentLength)
        assertEquals("", state5.bin)
    }

    @Test
    fun test_luhn() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAlgorithm(ChecksumAlgorithm.LUHN)
            .build()
        view.addRule(rule)

        view.setText("1111111111111117")
        child.refreshInternalState()

        val state = view.getState()
        assertNotNull(state)
        assertEquals(true, state!!.isValid)
        assertEquals(16, state.contentLength)
        assertEquals("111111", state.bin)


        view.setText("1111111111112")
        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state)
        assertEquals(true, state2!!.isValid)
        assertEquals(13, state2.contentLength)
        assertEquals("111111", state2.bin)

        view.setText("1111111111112222331")
        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state)
        assertEquals(true, state3!!.isValid)
        assertEquals(19, state3.contentLength)
        assertEquals("111111", state3.bin)
    }

    @Test
    fun test_length_luhn() {
        val child = view.getView()
        Assert.assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = PaymentCardNumberRule.ValidationBuilder()
            .setAlgorithm(ChecksumAlgorithm.LUHN)
            .setAllowableNumberLength(arrayOf(16))
            .build()
        view.addRule(rule)

        view.setText("1111111111111117")
        child.refreshInternalState()

        val state = view.getState()
        assertNotNull(state)
        assertEquals(true, state!!.isValid)
        assertEquals(16, state.contentLength)
        assertEquals("111111", state.bin)


        view.setText("1111111111112")
        child.refreshInternalState()

        val state2 = view.getState()
        assertNotNull(state)
        assertEquals(false, state2!!.isValid)
        assertEquals(13, state2.contentLength)

        view.setText("1111111111112222331")
        child.refreshInternalState()

        val state3 = view.getState()
        assertNotNull(state)
        assertEquals(false, state3!!.isValid)
        assertEquals(19, state3.contentLength)
    }


    private fun <T> any(): T = Mockito.any<T>()
}