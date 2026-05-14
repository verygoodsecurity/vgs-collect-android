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
import com.verygoodsecurity.vgscollect.widget.compose.card.VgsCardBrand
import com.verygoodsecurity.vgscollect.widget.compose.card.getSecurityCodeValidators
import com.verygoodsecurity.vgscollect.widget.compose.state.core.ANALYTICS_UI
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsCvcTokenizationConfig
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsRequiredFieldValidator
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidationResult
import com.verygoodsecurity.vgscollect.widget.compose.validator.core.VgsTextFieldValidator
import kotlin.math.min

private const val ANALYTICS_FIELD_TYPE = "card-security-code"

/**
 * Immutable state for a VGS card security code (CVC/CVV) field.
 *
 * Obtain an instance with [rememberVgsCvcTextFieldState] inside a composable.
 * Sync the detected brand from your card-number state so length/validation
 * rules adapt automatically:
 *
 * ```
 * var cvcState by rememberVgsCvcTextFieldState(
 *     collect = vgsCollect,
 *     fieldName = "data.cvc",
 * )
 * LaunchedEffect(cardNumberState.cardBrand) {
 *     cvcState = cvcState.withCardBrand(cardNumberState.cardBrand)
 * }
 * VgsCvcOutlineTextField(
 *     state = cvcState,
 *     onStateChange = { cvcState = it },
 * )
 * ```
 *
 * Useful properties:
 * - [cardBrand] — brand currently driving CVC length/validation rules.
 * - [isValid] / [validationResult] — current validation outcome.
 * - [fieldName] — JSON key used in the submit payload.
 */
class VgsCvcTextFieldState private constructor(
    text: String,
    fieldName: String,
    validators: List<VgsTextFieldValidator>?,
    val isCardBrandValidationEnabled: Boolean,
    val cardBrand: VgsCardBrand,
    override val tokenizationConfig: VgsCvcTokenizationConfig? = null,
) : BaseFieldState(text, fieldName, validators) {

    internal constructor(
        fieldName: String,
        validators: List<VgsTextFieldValidator>? = null,
        isCardBrandValidationEnabled: Boolean = true,
        tokenizationConfig: VgsCvcTokenizationConfig? = null,
    ) : this(
        EMPTY,
        fieldName,
        validators,
        isCardBrandValidationEnabled,
        VgsCardBrand.UNKNOWN,
        tokenizationConfig,
    )

    override fun validate(): List<VgsTextFieldValidationResult> {
        val completeValidators = with(validators ?: listOf(VgsRequiredFieldValidator())) {
            if (isCardBrandValidationEnabled) {
                this + cardBrand.getSecurityCodeValidators()
            } else {
                this
            }
        }
        return completeValidators.map { it.validate(text) }
    }

    override fun getOutputText(): String = text

    override fun copy(text: String): VgsCvcTextFieldState {
        return VgsCvcTextFieldState(
            text = normalizeText(text, cardBrand),
            fieldName = fieldName,
            validators = validators,
            isCardBrandValidationEnabled = isCardBrandValidationEnabled,
            cardBrand = cardBrand,
            tokenizationConfig = tokenizationConfig,
        )
    }

    /**
     * Updates the security code field state when a new card brand is detected
     * in the card number field.
     *
     * ### Example usage:
     *
     * ```
     * LaunchedEffect(cardNumberFieldState.cardBrand) {
     *     cvcState = cvcState.withCardBrand(cardNumberFieldState.cardBrand)
     * }
     * ```
     *
     * @param cardBrand The card brand detected in [VgsCardNumberTextFieldState]
     */
    fun withCardBrand(cardBrand: VgsCardBrand) = VgsCvcTextFieldState(
        text = normalizeText(text, cardBrand),
        fieldName = fieldName,
        validators = validators,
        isCardBrandValidationEnabled = isCardBrandValidationEnabled,
        cardBrand = cardBrand,
        tokenizationConfig = tokenizationConfig,
    )

    private fun normalizeText(text: String, cardBrand: VgsCardBrand): String {
        val digits = text.filter { it.isDigit() }
        val length = digits.length
        return digits.substring(0, min(length, cardBrand.securityCodeLength.maxOrNull() ?: length))
    }
}

/**
 * Creates and remembers a [VgsCvcTextFieldState] for use with a `VgsCvcTextField`.
 * This is the only way to obtain an instance — direct construction is internal
 * to the SDK.
 *
 * ```
 * var state by rememberVgsCvcTextFieldState(
 *     collect = vgsCollect,
 *     fieldName = "data.cvc",
 * )
 * ```
 *
 * Sync the brand from your card-number state via [VgsCvcTextFieldState.withCardBrand]
 * so the field's required length adapts (3 digits for most brands, 4 for Amex).
 *
 * @param collect the [VGSCollect] instance the field is bound to.
 * @param fieldName JSON key the value will be sent under by
 *   [VGSCollect.asyncSubmit].
 * @param validators custom validators. If `null`, the SDK applies default
 *   brand-aware length validation. Pass `emptyList()` to disable validation.
 * @param isCardBrandValidationEnabled when `true`, length rules adapt to the
 *   currently synced [VgsCvcTextFieldState.cardBrand].
 * @param tokenizationConfig optional; when set, the value is tokenized on
 *   submit and the token is sent in place of the raw value.
 */
@Composable
fun rememberVgsCvcTextFieldState(
    collect: VGSCollect,
    fieldName: String,
    validators: List<VgsTextFieldValidator>? = null,
    isCardBrandValidationEnabled: Boolean = true,
    tokenizationConfig: VgsCvcTokenizationConfig? = null,
): MutableState<VgsCvcTextFieldState> {
    fun newState() = VgsCvcTextFieldState(
        fieldName,
        validators,
        isCardBrandValidationEnabled,
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
