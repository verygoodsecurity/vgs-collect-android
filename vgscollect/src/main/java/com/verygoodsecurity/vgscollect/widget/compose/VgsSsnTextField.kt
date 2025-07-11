package com.verygoodsecurity.vgscollect.widget.compose

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.verygoodsecurity.vgscollect.widget.compose.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.mask.VgsMaskVisualTransformation
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRegexValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRequiredFieldValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsTextLengthValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator
import kotlin.math.min

class VgsSsnTextFieldState(
    text: String,
    fieldName: String,
    validators: List<VgsTextFieldValidator>?
) : BaseFieldState(text, fieldName, validators) {

    private companion object {

        const val DEFAULT_MASK = "###-##-####"
        const val DEFAULT_LENGTH = 9
        const val DEFAULT_REGEX =
            "^(?!(000|666|9))(\\d{3}(-|\\s)?(?!(00))\\d{2}(-|\\s)?(?!(0000))\\d{4})\$"
    }

    val mask: String = DEFAULT_MASK

    constructor(
        fieldName: String,
        validators: List<VgsTextFieldValidator>? = null
    ) : this(
        EMPTY,
        fieldName,
        validators
    )

    override fun validate(): List<VgsTextFieldValidationResult> {
        return (validators ?: getDefaultValidators()).map { it.validate(text) }
    }

    override fun getOutputText(): String = text

    internal fun copy(text: String): VgsSsnTextFieldState {
        return VgsSsnTextFieldState(
            text = normalizeText(text),
            fieldName = fieldName,
            validators = validators
        )
    }

    private fun getDefaultValidators(): List<VgsTextFieldValidator> {
        return listOf(
            VgsRequiredFieldValidator(),
            VgsTextLengthValidator(arrayOf(DEFAULT_LENGTH)),
            VgsRegexValidator(DEFAULT_REGEX)
        )
    }

    /**
     * Ensure text does not exceed the maximum ssn length and contains only digits.
     */
    private fun normalizeText(text: String): String {
        val digits = text.filter { it.isDigit() }
        val length = digits.length
        return digits.substring(0, min(length, DEFAULT_LENGTH))
    }
}

@ExperimentalComposeUiApi
@Composable
fun VgsSsnTextField(
    state: VgsSsnTextFieldState,
    modifier: Modifier = Modifier,
    onStateChange: (state: VgsSsnTextFieldState) -> Unit = {},
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = TextFieldDefaults.TextFieldShape,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors()
) {
    TextField(
        value = state.text,
        onValueChange = { onStateChange(state.copy(text = it)) },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = VgsMaskVisualTransformation(state.mask),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        keyboardActions = keyboardActions,
        singleLine = true,
        maxLines = 1,
        minLines = 1,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors
    )
}

@ExperimentalComposeUiApi
@Composable
fun VgsSsnOutlineTextField(
    state: VgsSsnTextFieldState,
    modifier: Modifier = Modifier,
    onStateChange: (state: VgsSsnTextFieldState) -> Unit = {},
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = TextFieldDefaults.OutlinedTextFieldShape,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors()
) {
    OutlinedTextField(
        value = state.text,
        onValueChange = { onStateChange(state.copy(text = it)) },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = VgsMaskVisualTransformation(state.mask),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        keyboardActions = keyboardActions,
        singleLine = true,
        maxLines = 1,
        minLines = 1,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors
    )
}