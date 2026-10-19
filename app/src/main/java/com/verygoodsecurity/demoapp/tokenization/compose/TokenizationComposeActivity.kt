package com.verygoodsecurity.demoapp.tokenization.compose

import android.graphics.Color as AndroidColor
import android.os.Bundle
import android.util.Log
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.start.StartActivity.Companion.KEY_BUNDLE_ENVIRONMENT
import com.verygoodsecurity.demoapp.start.StartActivity.Companion.KEY_BUNDLE_ROUTE_ID
import com.verygoodsecurity.demoapp.start.StartActivity.Companion.KEY_BUNDLE_TENANT_ID
import com.verygoodsecurity.demoapp.ui.DemoButton
import com.verygoodsecurity.demoapp.ui.DemoDebugPanel
import com.verygoodsecurity.demoapp.ui.DemoErrorBanner
import com.verygoodsecurity.demoapp.ui.DemoField
import com.verygoodsecurity.demoapp.ui.DemoFieldDefaults
import com.verygoodsecurity.demoapp.ui.DemoFormCard
import com.verygoodsecurity.demoapp.tokenization.settings.SettingsAction
import com.verygoodsecurity.demoapp.tokenization.settings.TokenizationSettings
import com.verygoodsecurity.demoapp.tokenization.settings.TokenizationSettingsActivity
import com.verygoodsecurity.demoapp.ui.DemoPlaceholder
import com.verygoodsecurity.demoapp.ui.DemoResponse
import com.verygoodsecurity.demoapp.ui.DemoScreen
import com.verygoodsecurity.demoapp.ui.DemoTheme
import com.verygoodsecurity.demoapp.ui.errorMessage
import com.verygoodsecurity.demoapp.utils.getStringExtra
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.network.tokenization.VGSTokenizationRequest
import com.verygoodsecurity.vgscollect.widget.compose.date.VgsExpiryDateFormat
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsCardNumberOutlinedTextField
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsCardholderOutlinedTextField
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsCvcOutlinedTextField
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsExpiryOutlinedTextField
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsCardNumberTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsCardholderTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsCvcTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsExpiryTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsCvcTokenizationConfig

private const val TAG = "TokenizationCompose"

/**
 * Demonstrates VGS Collect tokenization flow using Jetpack Compose.
 *
 * This screen shows:
 * - How to initialize and configure [VGSCollect]
 * - How to declare field states with field-specific [com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsTokenizationConfig]
 * - How to sync the CVC state with the detected card brand
 * - How to tokenize collected data using [VGSCollect.tokenize]
 *
 * Each field state carries its own tokenization config with the correct defaults:
 * - Card number: [VgsCardNumberTokenizationConfig] → [com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat.FPE_SIX_T_FOUR]
 * - CVC: [VgsCvcTokenizationConfig] → [com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat.NUM_LENGTH_PRESERVING] / [com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultStorageType.VOLATILE]
 * - Cardholder / Expiry: UUID / PERSISTENT
 *
 * Layout, styling and the debug panel come from the shared demo components in
 * `com.verygoodsecurity.demoapp.ui`, so this file only contains SDK usage.
 *
 * @see VGSCollect
 * @see VGSTokenizationRequest
 * @see VgsCollectResponseListener
 */
class TokenizationComposeActivity : AppCompatActivity(), VgsCollectResponseListener {

    private val tenantId: String by lazy { getStringExtra(KEY_BUNDLE_TENANT_ID) }
    private val env: String by lazy { getStringExtra(KEY_BUNDLE_ENVIRONMENT) }
    private val routeId: String? by lazy { getStringExtra(KEY_BUNDLE_ROUTE_ID).takeIf { it.isNotBlank() } }

    private val collect: VGSCollect by lazy {
        VGSCollect(this, tenantId, env).apply {
            addOnResponseListeners(this@TokenizationComposeActivity)
        }
    }

    /** Last tokenize result, set from [onResponse] and displayed by the debug panel. */
    private var response by mutableStateOf<DemoResponse?>(null)

    /** Options from the settings screen; re-read on resume so changes apply when coming back. */
    private var settings by mutableStateOf<TokenizationSettings?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The top bar is a dark gradient, so the status bar needs light icons.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT))
        VGSCollectLogger.isEnabled = true
        VGSCollectLogger.logLevel = VGSCollectLogger.Level.DEBUG
        settings = TokenizationSettings.read(this)
        setContent {
            // Field configs are fixed when a field state is created, so changed settings rebuild the form.
            settings?.let { settings ->
                key(settings) {
                    DemoTheme {
                        TokenizationScreen(
                            collect = collect,
                            settings = settings,
                            response = response,
                            onBack = ::finish,
                            onOpenSettings = { TokenizationSettingsActivity.start(this) },
                            onTokenize = { states ->
                                collect.tokenize(
                                    request = VGSTokenizationRequest.VGSRequestBuilder()
                                        .apply { routeId?.let { setRouteId(it) } }
                                        .build(),
                                    fieldsStates = states,
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        settings = TokenizationSettings.read(this)
    }

    override fun onResponse(response: VGSResponse?) {
        when (response) {
            is VGSResponse.SuccessResponse -> {
                Log.d(TAG, "Tokenize success: ${response.body}")
                this.response = DemoResponse(isSuccess = true, code = response.successCode, body = response.body)
            }

            is VGSResponse.ErrorResponse -> {
                Log.e(TAG, "Tokenize error: ${response.errorCode}")
                this.response = DemoResponse(
                    isSuccess = false,
                    code = response.errorCode,
                    body = response.localizeMessage
                )
            }

            else -> Unit
        }
    }
}

/**
 * Card tokenization form built with VGS Compose widgets.
 *
 * Each field state is initialized with a field-specific tokenization config so the vault
 * produces the correct alias format for each data type (e.g. FPE for card numbers, VOLATILE
 * storage for CVC). [onTokenize] receives the current states so the caller can pass them
 * directly to [VGSCollect.tokenize].
 */
@Composable
private fun TokenizationScreen(
    collect: VGSCollect,
    settings: TokenizationSettings,
    response: DemoResponse?,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    onTokenize: (List<BaseFieldState>) -> Unit,
) {
    // ==========================================================
    // STEP 1: Initialize field states with tokenization configs
    // ==========================================================
    // Each state carries its own tokenization config. The field-specific
    // subclasses provide the correct alias format / storage defaults for
    // each data type, matching the behavior of the view-based fields.
    var cardHolderState by rememberVgsCardholderTextFieldState(
        collect = collect,
        fieldName = "data.name",
        tokenizationConfig = settings.cardHolderConfig,
    )
    var cardNumberState by rememberVgsCardNumberTextFieldState(
        collect = collect,
        fieldName = "data.card_number",
        tokenizationConfig = settings.cardNumberConfig,
    )
    var cvcState by rememberVgsCvcTextFieldState(
        collect = collect,
        fieldName = "data.cvc",
        tokenizationConfig = VgsCvcTokenizationConfig(),
    )
    var expiryState by rememberVgsExpiryTextFieldState(
        collect = collect,
        fieldName = "data.expiry",
        inputDateFormat = VgsExpiryDateFormat.MonthShortYear,
        outputDateFormat = VgsExpiryDateFormat.MonthShortYear,
        tokenizationConfig = settings.expiryConfig,
    )
    val states = listOf(cardHolderState, cardNumberState, cvcState, expiryState)

    // ==========================================================
    // STEP 2: Sync dependent field states
    // ==========================================================
    LaunchedEffect(cardNumberState.cardBrand) {
        cvcState = cvcState.withCardBrand(cardNumberState.cardBrand)
    }

    // ==========================================================
    // STEP 3: Validate and tokenize
    // ==========================================================
    // Errors are only displayed after the first attempt, and the request is only sent
    // once all fields are valid.
    var showErrors by remember { mutableStateOf(false) }
    fun invalid(state: BaseFieldState) = showErrors && !state.isValid
    fun tokenize() {
        showErrors = true
        if (states.all { it.isValid }) onTokenize(states)
    }

    val fieldColors = DemoFieldDefaults.colors()
    val fieldShape = DemoFieldDefaults.shape

    DemoScreen(
        title = stringResource(R.string.start_tokenization_compose_title),
        onBack = onBack,
        actions = { SettingsAction(onOpenSettings) }
    ) {
        // ======================================================
        // STEP 4: Render fields
        // ======================================================
        DemoFormCard {
            DemoField(
                label = stringResource(R.string.card_holder_title),
                error = cardHolderState.errorMessage().takeIf { invalid(cardHolderState) }
            ) {
                VgsCardholderOutlinedTextField(
                    state = cardHolderState,
                    onStateChange = { cardHolderState = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { DemoPlaceholder(stringResource(R.string.collect_card_holder_hint)) },
                    isError = invalid(cardHolderState),
                    colors = fieldColors,
                    shape = fieldShape,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            DemoField(
                label = stringResource(R.string.card_number_title),
                error = cardNumberState.errorMessage().takeIf { invalid(cardNumberState) }
            ) {
                VgsCardNumberOutlinedTextField(
                    state = cardNumberState,
                    onStateChange = { cardNumberState = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { DemoPlaceholder(stringResource(R.string.collect_card_number_hint)) },
                    isError = invalid(cardNumberState),
                    trailingIcon = {
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(id = cardNumberState.cardBrand.cardIcon),
                            contentDescription = cardNumberState.cardBrand.name,
                        )
                    },
                    colors = fieldColors,
                    shape = fieldShape,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            // Expiry (left) + CVC (right). `Done` on the keyboard tokenizes the form.
            Row(horizontalArrangement = Arrangement.spacedBy(DemoTheme.spacing.m)) {
                DemoField(
                    label = stringResource(R.string.expiry_title),
                    modifier = Modifier.weight(1f),
                    error = expiryState.errorMessage().takeIf { invalid(expiryState) }
                ) {
                    VgsExpiryOutlinedTextField(
                        state = expiryState,
                        onStateChange = { expiryState = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { DemoPlaceholder(stringResource(R.string.collect_expiry_hint)) },
                        isError = invalid(expiryState),
                        colors = fieldColors,
                        shape = fieldShape,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                }
                DemoField(
                    label = stringResource(R.string.cvc_title),
                    modifier = Modifier.weight(1f),
                    error = cvcState.errorMessage().takeIf { invalid(cvcState) }
                ) {
                    VgsCvcOutlinedTextField(
                        state = cvcState,
                        onStateChange = { cvcState = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { DemoPlaceholder(stringResource(R.string.collect_cvc_hint)) },
                        isError = invalid(cvcState),
                        colors = fieldColors,
                        shape = fieldShape,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { tokenize() })
                    )
                }
            }
        }

        val invalidCount = states.count { !it.isValid }
        if (showErrors && invalidCount > 0) {
            DemoErrorBanner(pluralStringResource(R.plurals.collect_invalid_fields_banner, invalidCount, invalidCount))
        }

        // ======================================================
        // STEP 5: Tokenize
        // ======================================================
        // The SDK reads each field's tokenizationConfig, sets
        // is_required_tokenization = true on those fields, and sends
        // the payload to the VGS tokenization endpoint. The response
        // body contains aliases in place of the raw values.
        DemoButton(
            text = stringResource(R.string.tokenize_btn_title),
            onClick = ::tokenize,
            modifier = Modifier.fillMaxWidth()
        )

        DemoDebugPanel(states = states, response = response)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun Preview() {
    DemoTheme {
        TokenizationScreen(
            collect = VGSCollect(LocalContext.current, "", ""),
            settings = TokenizationSettings.read(LocalContext.current),
            response = null,
            onBack = {},
            onOpenSettings = {},
            onTokenize = {}
        )
    }
}
