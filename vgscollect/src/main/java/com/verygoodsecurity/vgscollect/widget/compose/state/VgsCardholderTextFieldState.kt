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
import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsCardholderTokenizationConfig
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRegexValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRequiredFieldValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator

private const val ANALYTICS_FIELD_TYPE = "card-holder-name"

/**
 * Immutable state for a VGS cardholder name field.
 *
 * Obtain an instance with [rememberVgsCardholderTextFieldState] inside a
 * composable. The state is replaced (not mutated) on every keystroke; pass
 * the latest instance to your `VgsCardholder…TextField` and update it from
 * `onStateChange`:
 *
 * ```
 * var state by rememberVgsCardholderTextFieldState(
 *     collect = vgsCollect,
 *     fieldName = "data.name",
 * )
 * VgsCardholderOutlinedTextField(
 *     state = state,
 *     onStateChange = { state = it },
 * )
 * ```
 *
 * Useful properties:
 * - [isValid] / [validationResult] — current validation outcome.
 * - [fieldName] — JSON key used in the submit payload.
 */
class VgsCardholderTextFieldState private constructor(
    text: String,
    fieldName: String,
    validators: List<VgsTextFieldValidator>?,
    override val tokenizationConfig: VgsCardholderTokenizationConfig? = null,
) : BaseFieldState(text, fieldName, validators) {

    private companion object {

        const val VALIDATION_REGEX = "^[a-zA-Z0-9 ,\\'.-]+$"
    }

    internal constructor(
        fieldName: String,
        validators: List<VgsTextFieldValidator>? = null,
        tokenizationConfig: VgsCardholderTokenizationConfig? = null,
    ) : this(EMPTY, fieldName, validators, tokenizationConfig)

    override fun validate(): List<VgsTextFieldValidationResult> {
        return (validators ?: listOf(
            VgsRequiredFieldValidator(),
            VgsRegexValidator(VALIDATION_REGEX)
        )).map { it.validate(text) }
    }

    override fun getOutputText(): String = text

    override fun copy(text: String): VgsCardholderTextFieldState {
        return VgsCardholderTextFieldState(
            text = text,
            fieldName = fieldName,
            validators = validators,
            tokenizationConfig = tokenizationConfig,
        )
    }
}

/**
 * Creates and remembers a [VgsCardholderTextFieldState] for use with a
 * `VgsCardholderTextField`. This is the only way to obtain an instance —
 * direct construction is internal to the SDK.
 *
 * ```
 * var state by rememberVgsCardholderTextFieldState(
 *     collect = vgsCollect,
 *     fieldName = "data.name",
 * )
 * ```
 *
 * @param collect the [VGSCollect] instance the field is bound to.
 * @param fieldName JSON key the value will be sent under by
 *   [VGSCollect.asyncSubmit].
 * @param validators custom validators. If `null`, the SDK applies default
 *   required + name-pattern validation. Pass `emptyList()` to disable validation.
 * @param tokenizationConfig optional; when set, the value is tokenized on
 *   submit and the token is sent in place of the raw value.
 */
@Composable
fun rememberVgsCardholderTextFieldState(
    collect: VGSCollect,
    fieldName: String,
    validators: List<VgsTextFieldValidator>? = null,
    tokenizationConfig: VgsCardholderTokenizationConfig? = null,
): MutableState<VgsCardholderTextFieldState> {
    fun newState() = VgsCardholderTextFieldState(
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
