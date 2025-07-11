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
import androidx.compose.ui.text.input.VisualTransformation
import com.verygoodsecurity.vgscollect.widget.compose.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRegexValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRequiredFieldValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator

class VgsCardHolderTextFieldState(
    text: String,
    fieldName: String,
    validators: List<VgsTextFieldValidator>?
) : BaseFieldState(text, fieldName, validators) {

    private companion object {

        const val VALIDATION_REGEX = "^[a-zA-Z0-9 ,\\'.-]+$"
    }

    constructor(
        fieldName: String,
        validators: List<VgsTextFieldValidator>? = null
    ) : this(
        EMPTY,
        fieldName,
        validators
    )

    override fun validate(): List<VgsTextFieldValidationResult> {
        return (validators ?: listOf(
            VgsRequiredFieldValidator(),
            VgsRegexValidator(VALIDATION_REGEX)
        )).map { it.validate(text) }
    }

    override fun getOutputText(): String = text

    internal fun copy(text: String): VgsCardHolderTextFieldState {
        return VgsCardHolderTextFieldState(
            text = text,
            fieldName = fieldName,
            validators = validators
        )
    }
}

@ExperimentalComposeUiApi
@Composable
fun VgsCardHolderTextField(
    state: VgsCardHolderTextFieldState,
    modifier: Modifier = Modifier,
    onStateChange: (state: VgsCardHolderTextFieldState) -> Unit = {},
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
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
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = true,
        maxLines = 1,
        minLines = 1,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors
    )
}