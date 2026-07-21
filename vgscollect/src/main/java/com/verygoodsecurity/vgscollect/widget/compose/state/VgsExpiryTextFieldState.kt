package com.verygoodsecurity.vgscollect.widget.compose.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.verygoodsecurity.sdk.analytics.model.VGSAnalyticsEvent
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.widget.compose.date.VgsExpiryDateFormat
import com.verygoodsecurity.vgscollect.widget.compose.date.VgsExpirySerializer
import com.verygoodsecurity.vgscollect.widget.compose.state.core.ANALYTICS_UI
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsExpiryTokenizationConfig
import com.verygoodsecurity.vgscollect.widget.compose.util.plusYears
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsMinMaxDateValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRequiredFieldValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator
import kotlin.math.min

private const val ANALYTICS_FIELD_TYPE = "card-expiration-date"

/**
 * Immutable state for a VGS card expiration date field.
 *
 * Obtain an instance with [rememberVgsExpiryTextFieldState] inside a
 * composable. The state is replaced (not mutated) on every keystroke; pass
 * the latest instance to your `VgsExpiry…TextField` and update it from
 * `onStateChange`:
 *
 * ```
 * var state by rememberVgsExpiryTextFieldState(
 *     collect = vgsCollect,
 *     fieldName = "data.expiry",
 *     inputDateFormat = VgsExpiryDateFormat.MonthShortYear,
 *     outputDateFormat = VgsExpiryDateFormat.LongYearMonth,
 * )
 * VgsExpiryOutlineTextField(
 *     state = state,
 *     onStateChange = { state = it },
 * )
 * ```
 *
 * Useful properties:
 * - [inputDateFormat] / [outputDateFormat] — how the field reads input and
 *   serializes for submission.
 * - [isValid] / [validationResult] — current validation outcome.
 * - [fieldName] — JSON key used in the submit payload.
 */
class VgsExpiryTextFieldState private constructor(
    text: String,
    fieldName: String,
    validators: List<VgsTextFieldValidator>?,
    val inputDateFormat: VgsExpiryDateFormat,
    val outputDateFormat: VgsExpiryDateFormat,
    val serializer: VgsExpirySerializer?,
    override val tokenizationConfig: VgsExpiryTokenizationConfig? = null,
) : BaseFieldState(text, fieldName, validators) {

    internal companion object {

        const val DEFAULT_MAX_YEARS_FROM_NOW = 20
    }

    internal constructor(
        fieldName: String,
        validators: List<VgsTextFieldValidator>? = null,
        inputDateFormat: VgsExpiryDateFormat = VgsExpiryDateFormat.MonthShortYear,
        outputDateFormat: VgsExpiryDateFormat = VgsExpiryDateFormat.MonthShortYear,
        serializer: VgsExpirySerializer? = null,
        tokenizationConfig: VgsExpiryTokenizationConfig? = null,
    ) : this(
        EMPTY,
        fieldName,
        validators,
        inputDateFormat,
        outputDateFormat,
        serializer,
        tokenizationConfig,
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
            serializer = serializer,
            tokenizationConfig = tokenizationConfig,
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

/**
 * Creates and remembers a [VgsExpiryTextFieldState] for use with a
 * `VgsExpiryTextField`. This is the only way to obtain an instance — direct
 * construction is internal to the SDK.
 *
 * ```
 * var state by rememberVgsExpiryTextFieldState(
 *     collect = vgsCollect,
 *     fieldName = "data.expiry",
 * )
 * ```
 *
 * @param collect the [VGSCollect] instance the field is bound to.
 * @param fieldName JSON key the value will be sent under by
 *   [VGSCollect.asyncSubmit].
 * @param validators custom validators. If `null`, default required + min/max
 *   date validation applies. Pass `emptyList()` to disable validation.
 * @param inputDateFormat the format the user types in. Defaults to
 *   [VgsExpiryDateFormat.MonthShortYear] (MM/YY).
 * @param outputDateFormat the format used on submit. Defaults to
 *   [VgsExpiryDateFormat.MonthShortYear] (MM/YY).
 * @param serializer optional; splits the date into separate month/year JSON
 *   fields on submit.
 * @param tokenizationConfig optional; when set, the value is tokenized on
 *   submit and the token is sent in place of the raw value.
 */
@Composable
fun rememberVgsExpiryTextFieldState(
    collect: VGSCollect,
    fieldName: String,
    validators: List<VgsTextFieldValidator>? = null,
    inputDateFormat: VgsExpiryDateFormat = VgsExpiryDateFormat.MonthShortYear,
    outputDateFormat: VgsExpiryDateFormat = VgsExpiryDateFormat.MonthShortYear,
    serializer: VgsExpirySerializer? = null,
    tokenizationConfig: VgsExpiryTokenizationConfig? = null,
): MutableState<VgsExpiryTextFieldState> {
    fun newState() = VgsExpiryTextFieldState(
        fieldName,
        validators,
        inputDateFormat,
        outputDateFormat,
        serializer,
        tokenizationConfig,
    )
    val state = rememberSaveable(
        fieldName,
        saver = Saver(
            save = { it.value.text },
            restore = { savedText -> mutableStateOf(newState().copy(text = savedText)) },
        ),
    ) {
        mutableStateOf(newState())
    }
    LaunchedEffect(fieldName) {
        collect.analyticsHandler.capture(
            VGSAnalyticsEvent.FieldAttach(
                fieldType = ANALYTICS_FIELD_TYPE,
                ui = ANALYTICS_UI,
            )
        )
    }
    DisposableEffect(fieldName) {
        onDispose {
            collect.analyticsHandler.capture(
                VGSAnalyticsEvent.FieldDetach(ANALYTICS_FIELD_TYPE)
            )
        }
    }
    return state
}
