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
import com.verygoodsecurity.vgscollect.core.model.state.allowed8DigitBIN
import com.verygoodsecurity.vgscollect.widget.compose.card.VgsCardBrand
import com.verygoodsecurity.vgscollect.widget.compose.card.getCardNumberValidators
import com.verygoodsecurity.vgscollect.widget.compose.state.core.ANALYTICS_UI
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsCardNumberTokenizationConfig
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRequiredFieldValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator
import kotlin.math.max
import kotlin.math.min

private const val ANALYTICS_FIELD_TYPE = "card-number"

/**
 * Immutable state for a VGS card number field.
 *
 * Obtain an instance with [rememberVgsCardNumberTextFieldState] inside a
 * composable. The state is replaced (not mutated) on every keystroke; pass
 * the latest instance to your `VgsCardNumber…TextField` and update it from
 * the widget's `onStateChange`:
 *
 * ```
 * var state by rememberVgsCardNumberTextFieldState(
 *     collect = vgsCollect,
 *     fieldName = "data.card_number",
 * )
 * VgsCardNumberOutlineTextField(
 *     state = state,
 *     onStateChange = { state = it },
 * )
 * ```
 *
 * Useful properties:
 * - [cardBrand] — detected brand for the current input.
 * - [bin] / [last4] — non-sensitive metadata, populated only when [isValid].
 * - [isValid] / [validationResult] — current validation outcome.
 * - [fieldName] — JSON key used in the submit payload.
 */
class VgsCardNumberTextFieldState private constructor(
    text: String,
    fieldName: String,
    validators: List<VgsTextFieldValidator>?,
    val isCardBrandValidationEnabled: Boolean,
    val supportedCardBrands: List<VgsCardBrand>,
    val cardBrand: VgsCardBrand,
    override val tokenizationConfig: VgsCardNumberTokenizationConfig? = null,
) : BaseFieldState(text, fieldName, validators) {

    val bin: String? = generateBin()

    val last4: String? = generateLast4()

    internal constructor(
        fieldName: String,
        validators: List<VgsTextFieldValidator>? = null,
        isCardBrandValidationEnabled: Boolean = true,
        supportedCardBrands: List<VgsCardBrand> = VgsCardBrand.DEFAULT,
        tokenizationConfig: VgsCardNumberTokenizationConfig? = null,
    ) : this(
        EMPTY,
        fieldName,
        validators,
        isCardBrandValidationEnabled,
        supportedCardBrands,
        VgsCardBrand.UNKNOWN,
        tokenizationConfig,
    )

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

    override fun getOutputText(): String = text

    override fun copy(text: String): VgsCardNumberTextFieldState {
        val cardBrand = VgsCardBrand.detect(text, supportedCardBrands)
        return VgsCardNumberTextFieldState(
            text = normalizeText(text, cardBrand),
            fieldName = fieldName,
            validators = validators,
            isCardBrandValidationEnabled = isCardBrandValidationEnabled,
            supportedCardBrands = supportedCardBrands,
            cardBrand = cardBrand,
            tokenizationConfig = tokenizationConfig,
        )
    }

    private fun normalizeText(text: String, cardBrand: VgsCardBrand): String {
        val digits = text.filter(Char::isDigit)
        val length = digits.length
        return digits.substring(0, min(length, cardBrand.length.maxOrNull() ?: length))
    }

    private fun generateBin(): String? {
        val length = if (allowed8DigitBIN(cardBrand.name, text.length)) 8 else 6
        return if (isValid) text.substring(0, min(text.length, length)) else null
    }

    private fun generateLast4(): String? {
        return if (isValid) text.substring(max(0, text.length - 4), text.length) else null
    }
}

/**
 * Creates and remembers a [VgsCardNumberTextFieldState] for use with a
 * `VgsCardNumberTextField`. This is the only way to obtain an instance —
 * direct construction is reserved for the SDK.
 *
 * Use it inside a composable like any other Compose `remember*` factory:
 *
 * ```
 * var state by rememberVgsCardNumberTextFieldState(
 *     collect = vgsCollect,
 *     fieldName = "data.card_number",
 * )
 * ```
 *
 * @param collect the [VGSCollect] instance the field is bound to. The field
 *   participates in this instance's submit and analytics pipeline.
 * @param fieldName JSON key the value will be sent under by
 *   [VGSCollect.asyncSubmit].
 * @param validators custom validators to apply. If `null`, the SDK applies
 *   default brand-aware validation. Pass `emptyList()` to disable validation.
 * @param isCardBrandValidationEnabled when `true`, the field's required
 *   length/CVC rules adapt to the detected card brand (e.g. 15 digits for Amex).
 * @param supportedCardBrands which brands the field will accept and detect.
 *   Defaults to [VgsCardBrand.DEFAULT].
 * @param tokenizationConfig optional config; when set, the value is tokenized
 *   on submit and the token is sent in place of the raw number.
 */
@Composable
fun rememberVgsCardNumberTextFieldState(
    collect: VGSCollect,
    fieldName: String,
    validators: List<VgsTextFieldValidator>? = null,
    isCardBrandValidationEnabled: Boolean = true,
    supportedCardBrands: List<VgsCardBrand> = VgsCardBrand.DEFAULT,
    tokenizationConfig: VgsCardNumberTokenizationConfig? = null,
): MutableState<VgsCardNumberTextFieldState> {
    fun newState() = VgsCardNumberTextFieldState(
        fieldName,
        validators,
        isCardBrandValidationEnabled,
        supportedCardBrands,
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
