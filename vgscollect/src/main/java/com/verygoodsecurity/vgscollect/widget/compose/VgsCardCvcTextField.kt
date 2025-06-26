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
import androidx.compose.ui.text.input.KeyboardType
import com.verygoodsecurity.vgscollect.widget.compose.card.VgsCardBrand
import com.verygoodsecurity.vgscollect.widget.compose.card.getSecurityCodeValidators
import com.verygoodsecurity.vgscollect.widget.compose.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.mask.VgsVisualTransformation
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRequiredFieldValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator
import kotlin.math.min

class VgsCardCvcTextFieldState : BaseFieldState {

    val cardBrand: VgsCardBrand
    val validators: List<VgsTextFieldValidator>
    val isCardBrandValidationEnabled: Boolean

    private val userValidators: List<VgsTextFieldValidator>?

    constructor(
        fieldName: String,
        validators: List<VgsTextFieldValidator>? = null,
        isCardBrandValidationEnabled: Boolean = true,
    ) : this(
        EMPTY,
        fieldName,
        validators,
        isCardBrandValidationEnabled,
        VgsCardBrand.UNKNOWN
    )

    internal constructor(
        text: String,
        fieldName: String,
        userValidators: List<VgsTextFieldValidator>?,
        isCardBrandValidationEnabled: Boolean,
        cardBrand: VgsCardBrand
    ) : super(text, fieldName) {
        this.cardBrand = cardBrand
        this.userValidators = userValidators
        this.validators = prepareValidators(userValidators)
        this.isCardBrandValidationEnabled = isCardBrandValidationEnabled
    }

    override fun isValid(): Boolean {
        return validate().all { it.isValid }
    }

    override fun getOutputText(): String = text

    override fun validate(): List<VgsTextFieldValidationResult> {
        return validators.map { it.validate(text) }
    }

    /**
     * Updates the security code field state when a new card brand is detected
     * in the card number field.
     *
     * This ensures that the security code field reflects the correct requirements
     * (e.g., CVV length) for the currently detected card brand.
     *
     * ### Example usage:
     *
     * ```
     * LaunchedEffect(cardNumberFieldState) {
     *     if (securityCodeFieldState.cardBrand != cardNumberFieldState.cardBrand) {
     *         securityCodeFieldState = securityCodeFieldState.withCardBrand(cardNumberFieldState.cardBrand)
     *     }
     * }
     * ```
     *
     * @param cardBrand The card brand detected in [VgsCardNumberTextFieldState]
     */
    fun withCardBrand(cardBrand: VgsCardBrand) = this.copy(cardBrand)

    internal fun copy(cardBrand: VgsCardBrand): VgsCardCvcTextFieldState {
        return VgsCardCvcTextFieldState(
            text = normalizeText(text, cardBrand),
            fieldName = fieldName,
            userValidators = validators,
            isCardBrandValidationEnabled = isCardBrandValidationEnabled,
            cardBrand = cardBrand
        )
    }

    internal fun copy(text: String): VgsCardCvcTextFieldState {
        return VgsCardCvcTextFieldState(
            text = normalizeText(text),
            fieldName = fieldName,
            userValidators = validators,
            isCardBrandValidationEnabled = isCardBrandValidationEnabled,
            cardBrand = cardBrand
        )
    }

    private fun prepareValidators(validators: List<VgsTextFieldValidator>?): List<VgsTextFieldValidator> {
        return with(validators ?: listOf(VgsRequiredFieldValidator())) {
            if (isCardBrandValidationEnabled) {
                this + cardBrand.getSecurityCodeValidators()
            } else {
                this
            }
        }
    }

    /**
     * Ensure text does not exceed the maximum card security code length and contains only digits.
     */
    private fun normalizeText(text: String, cardBrand: VgsCardBrand = this.cardBrand): String {
        val digits = text.filter { it.isDigit() }
        val length = digits.length
        return digits.substring(0, min(length, cardBrand.securityCodeLength.maxOrNull() ?: length))
    }
}

@Composable
fun VgsCvcTextField(
    state: VgsCardCvcTextFieldState,
    modifier: Modifier = Modifier,
    onStateChange: (state: VgsCardCvcTextFieldState) -> Unit = {},
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
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
        visualTransformation = VgsVisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors
    )
}