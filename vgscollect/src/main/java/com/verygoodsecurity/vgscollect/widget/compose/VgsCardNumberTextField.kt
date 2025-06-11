package com.verygoodsecurity.vgscollect.widget.compose

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import com.verygoodsecurity.vgscollect.widget.compose.card.VgsCardBrand
import com.verygoodsecurity.vgscollect.widget.compose.card.getValidators
import com.verygoodsecurity.vgscollect.widget.compose.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.mask.VgsMaskVisualTransformation
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRequiredFieldValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator
import kotlin.math.min

class VgsCardNumberTextFieldState internal constructor(
    text: String,
    fieldName: String,
    validators: List<VgsTextFieldValidator>,
    val cardBrand: VgsCardBrand
) : BaseFieldState(text, fieldName, validators) {

    constructor(
        fieldName: String,
        validators: List<VgsTextFieldValidator> = listOf(VgsRequiredFieldValidator())
    ) : this(
        EMPTY,
        fieldName,
        validators,
        VgsCardBrand.detect(EMPTY)
    )

    internal fun copy(text: String = this.text): VgsCardNumberTextFieldState {
        val cardBrand = VgsCardBrand.detect(text)
        return VgsCardNumberTextFieldState(
            text = maxCardLengthSubstring(text), // Ensure text does not exceed the maximum card length
            fieldName = fieldName,
            validators = validators + cardBrand.getValidators(),
            cardBrand = cardBrand
        )
    }

    private fun maxCardLengthSubstring(text: String): String {
        return text.substring(0, min(text.length, cardBrand.length.maxOrNull() ?: text.length))
    }
}

@Composable
fun VgsCardNumberTextField(
    state: VgsCardNumberTextFieldState,
    modifier: Modifier = Modifier,
    onStateChange: (state: VgsCardNumberTextFieldState) -> Unit = {},
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small.copy(
        bottomEnd = ZeroCornerSize, bottomStart = ZeroCornerSize
    ),
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
        visualTransformation = VgsMaskVisualTransformation(state.cardBrand.mask),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors
    )
}