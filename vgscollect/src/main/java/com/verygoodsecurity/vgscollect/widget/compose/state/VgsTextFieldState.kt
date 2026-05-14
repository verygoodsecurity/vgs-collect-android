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
import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsTextFieldTokenizationConfig
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRequiredFieldValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator

private const val ANALYTICS_FIELD_TYPE = "text"

/**
 * Immutable state for a generic VGS text field (e.g. city, postal code, address).
 *
 * Obtain an instance with [rememberVgsTextFieldState] inside a composable.
 * The state is replaced (not mutated) on every keystroke; pass the latest
 * instance to your `Vgs…TextField` and update it from `onStateChange`:
 *
 * ```
 * var state by rememberVgsTextFieldState(
 *     collect = vgsCollect,
 *     fieldName = "data.city",
 * )
 * VgsOutlineTextField(
 *     state = state,
 *     onStateChange = { state = it },
 * )
 * ```
 *
 * Useful properties:
 * - [isValid] / [validationResult] — current validation outcome.
 * - [fieldName] — JSON key used in the submit payload.
 */
class VgsTextFieldState private constructor(
    text: String,
    fieldName: String,
    validators: List<VgsTextFieldValidator>?,
    override val tokenizationConfig: VgsTextFieldTokenizationConfig? = null,
) : BaseFieldState(text, fieldName, validators) {

    internal constructor(
        fieldName: String,
        validators: List<VgsTextFieldValidator>? = null,
        tokenizationConfig: VgsTextFieldTokenizationConfig? = null,
    ) : this(EMPTY, fieldName, validators, tokenizationConfig)

    override fun validate(): List<VgsTextFieldValidationResult> {
        return (validators ?: listOf(VgsRequiredFieldValidator())).map { it.validate(text) }
    }

    override fun getOutputText(): String = text

    override fun copy(text: String): VgsTextFieldState {
        return VgsTextFieldState(
            text = text,
            fieldName = fieldName,
            validators = validators,
            tokenizationConfig = tokenizationConfig,
        )
    }
}

/**
 * Creates and remembers a [VgsTextFieldState] for use with a `VgsTextField`.
 * This is the only way to obtain an instance — direct construction is internal
 * to the SDK.
 *
 * ```
 * var state by rememberVgsTextFieldState(
 *     collect = vgsCollect,
 *     fieldName = "data.city",
 * )
 * ```
 *
 * @param collect the [VGSCollect] instance the field is bound to.
 * @param fieldName JSON key the value will be sent under by
 *   [VGSCollect.asyncSubmit].
 * @param validators custom validators. If `null`, a default required-field
 *   validator is applied. Pass `emptyList()` to disable validation.
 * @param tokenizationConfig optional; when set, the value is tokenized on
 *   submit and the token is sent in place of the raw text.
 */
@Composable
fun rememberVgsTextFieldState(
    collect: VGSCollect,
    fieldName: String,
    validators: List<VgsTextFieldValidator>? = null,
    tokenizationConfig: VgsTextFieldTokenizationConfig? = null,
): MutableState<VgsTextFieldState> {
    fun newState() = VgsTextFieldState(
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
