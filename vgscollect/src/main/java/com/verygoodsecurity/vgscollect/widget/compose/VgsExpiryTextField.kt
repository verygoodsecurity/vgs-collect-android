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
import com.verygoodsecurity.vgscollect.widget.compose.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.date.VgsExpiryDateFormat
import com.verygoodsecurity.vgscollect.widget.compose.mask.VgsMaskVisualTransformation
import com.verygoodsecurity.vgscollect.widget.compose.util.parse
import com.verygoodsecurity.vgscollect.widget.compose.util.plusYears
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsMinMaxDateValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRequiredFieldValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.min

class VgsExpiryTextFieldState : BaseFieldState {

    internal companion object {

        const val DEFAULT_MAX_YEARS_FROM_NOW = 20
    }

    val inputDateFormat: VgsExpiryDateFormat

    val outputDateFormat: VgsExpiryDateFormat

    constructor(
        fieldName: String,
        validators: List<VgsTextFieldValidator>? = null,
        inputDateFormat: VgsExpiryDateFormat = VgsExpiryDateFormat.MonthShortYear(),
        outputDateFormat: VgsExpiryDateFormat = VgsExpiryDateFormat.MonthShortYear(),
    ) : this(
        EMPTY,
        fieldName,
        validators,
        inputDateFormat,
        outputDateFormat
    )

    internal constructor(
        text: String,
        fieldName: String,
        validators: List<VgsTextFieldValidator>?,
        inputDateFormat: VgsExpiryDateFormat,
        outputDateFormat: VgsExpiryDateFormat
    ) : super(text, fieldName, validators) {
        this.inputDateFormat = inputDateFormat
        this.outputDateFormat = outputDateFormat
    }

    override fun validate(): List<VgsTextFieldValidationResult> {
        return (validators ?: getDefaultValidators(inputDateFormat)).map { it.validate(text) }
    }

    override fun getOutputText(): String {
        val formattedInput = text.format(inputDateFormat.mask)
        val outputDate = formattedInput.parse(inputDateFormat.dateFormat)
        val sdf = SimpleDateFormat(outputDateFormat.dateFormat, Locale.US)
        val output = try {
            outputDate?.let { sdf.format(it) }
        } catch (_: Exception) {
            null
        }
        return output ?: EMPTY
    }

    internal fun copy(text: String): VgsExpiryTextFieldState {
        val normalizedText = normalizeText(text)
        return VgsExpiryTextFieldState(
            text = normalizedText,
            fieldName = fieldName,
            validators = validators,
            inputDateFormat = inputDateFormat,
            outputDateFormat = outputDateFormat
        )
    }

    private fun getDefaultValidators(inputDateFormat: VgsExpiryDateFormat): List<VgsTextFieldValidator> {
        val min = System.currentTimeMillis()
        val max = min.plusYears(DEFAULT_MAX_YEARS_FROM_NOW)
        val minMaxValidator = VgsMinMaxDateValidator(min, max, inputDateFormat)
        return listOf(VgsRequiredFieldValidator(), minMaxValidator)
    }

    /**
     * Ensure text does not exceed the maximum expiration date length and contains only digits.
     */
    private fun normalizeText(text: String): String {
        val digits = text.filter { it.isDigit() }
        val length = digits.length
        return digits.substring(0, min(length, inputDateFormat.maskChartsCount))
    }
}

@Composable
fun VgsExpiryTextField(
    state: VgsExpiryTextFieldState,
    modifier: Modifier = Modifier,
    onStateChange: (state: VgsExpiryTextFieldState) -> Unit = {},
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
        visualTransformation = VgsMaskVisualTransformation(state.inputDateFormat.mask),
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