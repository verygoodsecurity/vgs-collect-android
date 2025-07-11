package com.verygoodsecurity.vgscollect.widget.compose

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import com.verygoodsecurity.vgscollect.widget.compose.card.VgsCardBrand
import com.verygoodsecurity.vgscollect.widget.compose.card.getCardNumberValidators
import com.verygoodsecurity.vgscollect.widget.compose.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.mask.VgsMaskVisualTransformation
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRequiredFieldValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator
import kotlin.math.min

class VgsCardNumberTextFieldState internal constructor(
    text: String,
    fieldName: String,
    validators: List<VgsTextFieldValidator>?,
    val isCardBrandValidationEnabled: Boolean,
    val cardBrand: VgsCardBrand,
) : BaseFieldState(text, fieldName, validators) {

    constructor(
        fieldName: String,
        validators: List<VgsTextFieldValidator>? = null,
        isCardBrandValidationEnabled: Boolean = true,
    ) : this(
        EMPTY,
        fieldName,
        validators,
        isCardBrandValidationEnabled,
        VgsCardBrand.UNKNOWN,
    )

    override fun getOutputText(): String = text

    internal fun copy(text: String): VgsCardNumberTextFieldState {
        val cardBrand = VgsCardBrand.detect(text)
        return VgsCardNumberTextFieldState(
            text = normalizeText(text, cardBrand),
            fieldName = fieldName,
            validators = validators,
            isCardBrandValidationEnabled = isCardBrandValidationEnabled,
            cardBrand = cardBrand,
        )
    }

    override fun validate(): List<VgsTextFieldValidationResult> {
        val completeValidators = with(validators ?: listOf(VgsRequiredFieldValidator())) {
            if (isCardBrandValidationEnabled) {
                this + cardBrand.getCardNumberValidators()
            } else {
                this
            }
        }
        return completeValidators.map { it.validate(text) }
    }

    /**
     * Ensure text does not exceed the maximum card length and contains only digits.
     */
    private fun normalizeText(text: String, cardBrand: VgsCardBrand): String {
        val digits = text.filter(Char::isDigit)
        val length = digits.length
        return digits.substring(0, min(length, cardBrand.length.maxOrNull() ?: length))
    }
}

@ExperimentalComposeUiApi
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
        visualTransformation = VgsMaskVisualTransformation(state.cardBrand.mask),
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