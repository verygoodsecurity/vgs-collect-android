package com.verygoodsecurity.vgscollect.view.card.info

import android.app.Activity
import android.graphics.Typeface
import android.text.InputType
import android.view.View
import com.verygoodsecurity.vgscollect.TestApplication
import com.verygoodsecurity.vgscollect.any
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultStorageType
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.validation.InfoValidator
import com.verygoodsecurity.vgscollect.view.card.validation.LengthValidator
import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator
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
    fun test_length_custom_error_message() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val errorMessage = "Incorrect length."
        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = VGSInfoRule.ValidationBuilder()
            .setAllowableMinLength(12)
            .setAllowableMaxLength(15, errorMessage)
            .build()
        view.setRule(rule)

        view.setText("12312312312")
        child.refreshInternalState()
        view.getState().validateState(false, 11, listOf(errorMessage))

        view.setText("123123123123")
        child.refreshInternalState()
        view.getState().validateState(true, 12, emptyList())

        view.setText("123123123123123")
        child.refreshInternalState()
        view.getState().validateState(true, 15, emptyList())
    }

    @Test
    fun test_length_min_custom_error_message() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val errorMessage = "Less then min length."
        val rule = VGSInfoRule.ValidationBuilder()
            .setAllowableMinLength(7)
            .build()
        view.setRule(rule)

        view.setText("123123")
        child.refreshInternalState()
        view.getState().validateState(false, 6, listOf(errorMessage))

        view.setText("1231234")
        child.refreshInternalState()
        view.getState().validateState(true, 7, emptyList())

        view.setText("1231231231231231231")
        child.refreshInternalState()
        view.getState().validateState(true, 19, emptyList())
    }


    @Test
    fun test_length_max_with_custom_error_message() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val errorMessage = "Incorrect length."
        val rule = VGSInfoRule.ValidationBuilder()
            .setAllowableMaxLength(17)
            .build()
        view.setRule(rule)

        view.setText("")
        child.refreshInternalState()
        view.getState().validateState(false, 0, listOf(errorMessage))

        view.setText("1")
        child.refreshInternalState()
        view.getState().validateState(true, 1, emptyList())

        view.setText("1231231231233")
        child.refreshInternalState()
        view.getState().validateState(true, 13, emptyList())

        view.setText("12312312312312312")
        child.refreshInternalState()
        view.getState().validateState(true, 17, emptyList())

        view.setText("123123123123123123")
        child.refreshInternalState()
        view.getState().validateState(false, 18, listOf(errorMessage))

    }


    @Test
    fun test_length_min_max_with_default_error_message() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = VGSInfoRule.ValidationBuilder()
            .setAllowableMinLength(15)
            .setAllowableMaxLength(17)
            .build()
        view.setRule(rule)

        view.setText("12312312312312")
        child.refreshInternalState()
        view.getState().validateState(false, 14, listOf(LengthValidator.DEFAULT_ERROR_MSG))

        view.setText("123123123123123")
        child.refreshInternalState()
        view.getState().validateState(true, 15, emptyList())

        view.setText("12312312312312312")
        child.refreshInternalState()
        view.getState().validateState(true, 17, emptyList())

        view.setText("12312312312312312")
        child.refreshInternalState()
        view.getState().validateState(true, 17, emptyList())

        view.setText("123123123123123123")
        child.refreshInternalState()
        view.getState().validateState(false, 18, listOf(LengthValidator.DEFAULT_ERROR_MSG))
    }


    @Test
    fun test_regex_with_default_error_message() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val rule = VGSInfoRule.ValidationBuilder()
            .setRegex("^[0-9]{15}(?:[0-9]{1})?\$")
            .build()
        view.setRule(rule)

        view.setText("")
        child.refreshInternalState()
        view.getState().validateState(false, 0, listOf(RegexValidator.DEFAULT_ERROR_MSG))

        view.setText("011111111111111 ")
        child.refreshInternalState()
        view.getState().validateState(false, 16, listOf(RegexValidator.DEFAULT_ERROR_MSG))

        view.setText("0111111111111111")
        child.refreshInternalState()
        view.getState().validateState(true, 16, emptyList())

        view.setText("0111111111111w111")
        child.refreshInternalState()
        view.getState().validateState(false, 17, listOf(RegexValidator.DEFAULT_ERROR_MSG))

        view.setText("01111111111111")
        child.refreshInternalState()
        view.getState().validateState(false, 14, listOf(RegexValidator.DEFAULT_ERROR_MSG))
    }

    @Test
    fun test_regex_and_length_with_custom_errors() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val regexError = "Regex failed."
        val regexRule = VGSInfoRule.ValidationBuilder()
            .setRegex("\\d+", errorMsg = regexError)
            .build()

        val lengthError = "Incorrect length."
        val lengthRule = VGSInfoRule.ValidationBuilder()
            .setAllowableMinLength(15)
            .setAllowableMaxLength(17, errorMsg = lengthError)
            .build()

        view.setRules(listOf(regexRule, lengthRule))

        view.setText("f")
        child.refreshInternalState()
        view.getState().validateState(false, 1, listOf(regexError, lengthError))


        view.setText("0111111111111111")
        child.refreshInternalState()
        view.getState().validateState(true, 16, emptyList())

        view.setText("011111111111111111")
        child.refreshInternalState()
        view.getState().validateState(false, 18, listOf(lengthError))
    }

    @Test
    fun test_default_validation() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        view.setText("")
        child.refreshInternalState()
        view.getState().validateState(false, 0, listOf(InfoValidator.DEFAULT_ERROR_MSG))

        view.setText("1")
        child.refreshInternalState()
        view.getState().validateState(true, 1)

        view.setText("")
        child.refreshInternalState()
        view.getState().validateState(false, 0, listOf(InfoValidator.DEFAULT_ERROR_MSG))
    }

    @Test
    fun test_default_validation_with_appended_rule() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val regexError = "Regex failed."
        val regexRule = VGSInfoRule.ValidationBuilder()
            .setRegex("\\d+", errorMsg = regexError)
            .build()
        view.appendRule(regexRule)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        view.setText("")
        child.refreshInternalState()
        view.getState().validateState(false, 0, listOf(InfoValidator.DEFAULT_ERROR_MSG, regexError))

        view.setText("d")
        child.refreshInternalState()
        view.getState().validateState(false, 1, listOf(regexError))

        view.setText("1")
        child.refreshInternalState()
        view.getState().validateState(true, 1, emptyList())
    }

    private fun FieldState.InfoState?.validateState(
        isValid: Boolean,
        contentLength: Int,
        errorMessages: List<String>? = null
    ) {
        assertNotNull(this)
        if (this != null) {
            assertEquals(isValid, this.isValid)
            assertEquals(contentLength, this.contentLength)
            if (errorMessages != null) {
                assertEquals(errorMessages, this.validationErrors)
            }
        }
    }

    @Test
    fun test_alias_format() {
        view.setVaultAliasFormat(VGSVaultAliasFormat.FPE_SIX_T_FOUR)

        val child = view.statePreparer.getView()

        assertEquals((child as InfoInputField).vaultAliasFormat, VGSVaultAliasFormat.FPE_SIX_T_FOUR)
    }

    @Test
    fun test_storage_type() {
        view.setVaultStorageType(VGSVaultStorageType.VOLATILE)

        val child = view.statePreparer.getView()
        assertEquals((child as InfoInputField).vaultStorage, VGSVaultStorageType.VOLATILE)
    }

    @Test
    fun test_enabled_tokenization() {
        view.setEnabledTokenization(false)

        val child = view.statePreparer.getView()
        assertEquals((child as InfoInputField).isEnabledTokenization, false)
    }

    @Test
    fun test_default_tokenization_settings() {
        val child = view.statePreparer.getView()
        assertEquals((child as InfoInputField).isEnabledTokenization, true)
        assertEquals(child.vaultAliasFormat, VGSVaultAliasFormat.UUID)
        assertEquals(child.vaultStorage, VGSVaultStorageType.PERSISTENT)
    }


}