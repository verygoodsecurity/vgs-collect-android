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
import com.verygoodsecurity.vgscollect.widget.compose.state.core.ANALYTICS_UI
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsSsnTokenizationConfig
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRegexValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRequiredFieldValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsTextLengthValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator
import kotlin.math.min

private const val ANALYTICS_FIELD_TYPE = "ssn"

/**
 * Immutable state for a VGS U.S. Social Security Number field.
 *
 * Obtain an instance with [rememberVgsSsnTextFieldState] inside a composable.
 * The state is replaced (not mutated) on every keystroke; pass the latest
 * instance to your `VgsSsn…TextField` and update it from `onStateChange`:
 *
 * ```
 * var state by rememberVgsSsnTextFieldState(
 *     collect = vgsCollect,
 *     fieldName = "data.ssn",
 * )
 * VgsSsnOutlinedTextField(
 *     state = state,
 *     onStateChange = { state = it },
 * )
 * ```
 *
 * Useful properties:
 * - [mask] — input mask the field renders ("###-##-####").
 * - [isValid] / [validationResult] — current validation outcome.
 * - [fieldName] — JSON key used in the submit payload.
 */
class VgsSsnTextFieldState private constructor(
    text: String,
    fieldName: String,
    validators: List<VgsTextFieldValidator>?,
    override val tokenizationConfig: VgsSsnTokenizationConfig? = null,
) : BaseFieldState(text, fieldName, validators) {

    private companion object {

        const val DEFAULT_MASK = "###-##-####"
        const val DEFAULT_LENGTH = 9
        const val DEFAULT_REGEX = "^(?!(000|666|9))\\d{3}(?!(00))\\d{2}(?!(0000))\\d{4}$"
    }

    val mask: String = DEFAULT_MASK

    internal constructor(
        fieldName: String,
        validators: List<VgsTextFieldValidator>? = null,
        tokenizationConfig: VgsSsnTokenizationConfig? = null,
    ) : this(EMPTY, fieldName, validators, tokenizationConfig)

    override fun validate(): List<VgsTextFieldValidationResult> {
        return (validators ?: getDefaultValidators()).map { it.validate(text) }
    }

    override fun getOutputText(): String = text

    override fun copy(text: String): VgsSsnTextFieldState {
        return VgsSsnTextFieldState(
            text = normalizeText(text),
            fieldName = fieldName,
            validators = validators,
            tokenizationConfig = tokenizationConfig,
        )
    }

    private fun getDefaultValidators(): List<VgsTextFieldValidator> {
        return listOf(
            VgsRequiredFieldValidator(),
            VgsTextLengthValidator(arrayOf(DEFAULT_LENGTH)),
            VgsRegexValidator(DEFAULT_REGEX)
        )
    }

    private fun normalizeText(text: String): String {
        val digits = text.filter { it.isDigit() }
        val length = digits.length
        return digits.substring(0, min(length, DEFAULT_LENGTH))
    }
}

/**
 * Creates and remembers a [VgsSsnTextFieldState] for use with a `VgsSsnTextField`.
 * This is the only way to obtain an instance — direct construction is internal
 * to the SDK.
 *
 * ```
 * var state by rememberVgsSsnTextFieldState(
 *     collect = vgsCollect,
 *     fieldName = "data.ssn",
 * )
 * ```
 *
 * @param collect the [VGSCollect] instance the field is bound to.
 * @param fieldName JSON key the value will be sent under by
 *   [VGSCollect.asyncSubmit].
 * @param validators custom validators. If `null`, default required + length +
 *   SSN-pattern validation applies. Pass `emptyList()` to disable validation.
 * @param tokenizationConfig optional; when set, the value is tokenized on
 *   submit and the token is sent in place of the raw value.
 */
@Composable
fun rememberVgsSsnTextFieldState(
    collect: VGSCollect,
    fieldName: String,
    validators: List<VgsTextFieldValidator>? = null,
    tokenizationConfig: VgsSsnTokenizationConfig? = null,
): MutableState<VgsSsnTextFieldState> {
    fun newState() = VgsSsnTextFieldState(
        fieldName,
        validators,
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
