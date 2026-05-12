package com.verygoodsecurity.vgscollect.widget.compose.state

import com.verygoodsecurity.vgscollect.widget.compose.date.VgsExpiryDateFormat
import com.verygoodsecurity.vgscollect.widget.compose.date.VgsExpirySerializer
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.util.plusYears
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsMinMaxDateValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRequiredFieldValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator
import kotlin.math.min

class VgsExpiryTextFieldState internal constructor(
    text: String,
    fieldName: String,
    validators: List<VgsTextFieldValidator>?,
    val inputDateFormat: VgsExpiryDateFormat,
    val outputDateFormat: VgsExpiryDateFormat,
    val serializer: VgsExpirySerializer?,
) : BaseFieldState(text, fieldName, validators) {

    internal companion object {

        const val DEFAULT_MAX_YEARS_FROM_NOW = 20
    }

    constructor(
        fieldName: String,
        validators: List<VgsTextFieldValidator>? = null,
        inputDateFormat: VgsExpiryDateFormat = VgsExpiryDateFormat.MonthShortYear,
        outputDateFormat: VgsExpiryDateFormat = VgsExpiryDateFormat.MonthShortYear,
        serializer: VgsExpirySerializer? = null,
    ) : this(
        EMPTY,
        fieldName,
        validators,
        inputDateFormat,
        outputDateFormat,
        serializer
    )

    override fun validate(): List<VgsTextFieldValidationResult> {
        return (validators ?: getDefaultValidators(inputDateFormat)).map { it.validate(text) }
    }

    override fun getOutputText(): String {
        return outputDateFormat.format(inputDateFormat.parse(text)) ?: EMPTY
    }

    override fun copy(text: String): VgsExpiryTextFieldState {
        return VgsExpiryTextFieldState(
            text = normalizeText(text),
            fieldName = fieldName,
            validators = validators,
            inputDateFormat = inputDateFormat,
            outputDateFormat = outputDateFormat,
            serializer = serializer
        )
    }

    private fun getDefaultValidators(inputDateFormat: VgsExpiryDateFormat): List<VgsTextFieldValidator> {
        val min = System.currentTimeMillis()
        val max = min.plusYears(DEFAULT_MAX_YEARS_FROM_NOW)
        return listOf(
            VgsRequiredFieldValidator(),
            VgsMinMaxDateValidator(min, max, inputDateFormat)
        )
    }

    private fun normalizeText(text: String): String {
        val digits = text.filter { it.isDigit() }
        val length = digits.length
        return digits.substring(0, min(length, inputDateFormat.maskChartsCount))
    }
}