package com.verygoodsecurity.demoapp.collect.compose

import android.content.Intent
import android.graphics.Color as AndroidColor
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.microblink.blinkcard.core.BlinkCardSdkSettings
import com.microblink.blinkcard.ux.contract.BlinkCardScanActivitySettings
import com.verygoodsecurity.api.blinkcard.VGSBlinkCardIntentBuilder
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.start.StartActivity.Companion.KEY_BUNDLE_ENVIRONMENT
import com.verygoodsecurity.demoapp.start.StartActivity.Companion.KEY_BUNDLE_PATH
import com.verygoodsecurity.demoapp.start.StartActivity.Companion.KEY_BUNDLE_TENANT_ID
import com.verygoodsecurity.demoapp.ui.DemoButton
import com.verygoodsecurity.demoapp.ui.DemoDebugPanel
import com.verygoodsecurity.demoapp.ui.DemoErrorBanner
import com.verygoodsecurity.demoapp.ui.DemoField
import com.verygoodsecurity.demoapp.ui.DemoFieldDefaults
import com.verygoodsecurity.demoapp.ui.DemoFormCard
import com.verygoodsecurity.demoapp.ui.DemoIconButton
import com.verygoodsecurity.demoapp.ui.DemoPlaceholder
import com.verygoodsecurity.demoapp.ui.DemoResponse
import com.verygoodsecurity.demoapp.ui.DemoScreen
import com.verygoodsecurity.demoapp.ui.DemoTheme
import com.verygoodsecurity.demoapp.ui.errorMessage
import com.verygoodsecurity.demoapp.utils.getStringExtra
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.widget.compose.date.VgsExpiryDateFormat
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsCardNumberOutlinedTextField
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsCardholderOutlinedTextField
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsCvcOutlinedTextField
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsExpiryOutlinedTextField
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsOutlinedTextField
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsSsnOutlinedTextField
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsCardNumberTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsCardholderTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsCvcTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsExpiryTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsSsnTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.transformation.VgsChainedVisualTransformation
import com.verygoodsecurity.vgscollect.widget.compose.transformation.VgsMaskVisualTransformation
import com.verygoodsecurity.vgscollect.widget.compose.transformation.VgsStyleVisualTransformation
import com.verygoodsecurity.vgscollect.widget.compose.util.withScanResult

private const val TAG = "CollectComposeActivity"
private const val ATTACHMENT_FIELD_NAME = "data.attachment"

private const val CARDHOLDER_FIELD_NAME = "data.name"
private const val CARD_NUMBER_FIELD_NAME = "data.card_number"
private const val CVC_FIELD_NAME = "data.cvc"
private const val EXPIRY_FIELD_NAME = "data.expiry"
private const val CITY_FIELD_NAME = "data.city"
private const val POSTAL_CODE_FIELD_NAME = "data.postal_code"
private const val SSN_FIELD_NAME = "data.ssn"

private const val SCANNER_REQUEST_CODE = 1001

/**
 * Demonstrates how to integrate VGS Collect SDK using Jetpack Compose.
 *
 * This screen shows:
 * - How to initialize and configure [VGSCollect]
 * - How to declare and manage Compose field states
 * - How to sync related fields (e.g. CVC card brand from card number)
 * - How to submit collected data securely to a VGS proxy
 *
 * Field states are immutable — every user input produces a new state instance.
 * Raw text is never exposed; validation and submission are handled by the SDK.
 *
 * Layout, styling and the debug panel come from the shared demo components in
 * `com.verygoodsecurity.demoapp.ui`, so this file only contains SDK usage.
 *
 * 📘 Official documentation:
 * https://docs.verygoodsecurity.com/vault/developer-tools/vgs-collect/android-sdk/index
 *
 * @see VGSCollect
 * @see VGSRequest
 * @see VgsCollectResponseListener
 */
class CollectComposeActivity : AppCompatActivity(), VgsCollectResponseListener {

    private val tenantId: String by lazy { getStringExtra(KEY_BUNDLE_TENANT_ID) }
    private val env: String by lazy { getStringExtra(KEY_BUNDLE_ENVIRONMENT) }
    private val path: String by lazy { getStringExtra(KEY_BUNDLE_PATH) }

    /**
     * Lazy initialization of [VGSCollect].
     *
     * Must be initialized with Activity context.
     */
    private val collect: VGSCollect by lazy {
        VGSCollect(this, tenantId, env).apply {
            addOnResponseListeners(this@CollectComposeActivity)
        }
    }

    /**
     * Stores the last scanner activity result (card scanning or file attachment).
     *
     * Hoisted into the Activity so it can be updated from [onActivityResult]
     * after the SDK's scanner returns. Compose reads it to trigger
     * field population and UI updates.
     */
    private var scannerResult by mutableStateOf<Intent?>(null)

    /**
     * Whether a file is currently attached via [com.verygoodsecurity.vgscollect.core.storage.content.file.VGSFileProvider].
     *
     * Hoisted into the Activity so it can be updated from [onActivityResult]
     * after the SDK's file picker returns. Compose reads it to toggle the
     * Attach/Detach button label.
     */
    private var isFileAttached by mutableStateOf(false)

    /** Last submit result, set from [onResponse] and displayed by the debug panel. */
    private var response by mutableStateOf<DemoResponse?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The top bar is a dark gradient, so the status bar needs light icons.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT))
        VGSCollectLogger.isEnabled = true
        VGSCollectLogger.logLevel = VGSCollectLogger.Level.DEBUG
        setContent {
            DemoTheme {
                CollectScreen(
                    collect = collect,
                    scannerResult = scannerResult,
                    isFileAttached = isFileAttached,
                    response = response,
                    onBack = ::finish,
                    onScan = ::startScanner,
                    onAttachFile = {
                        collect.getFileProvider().attachFile(this, ATTACHMENT_FIELD_NAME)
                    },
                    onDetachAllFiles = {
                        collect.getFileProvider().detachAll()
                        isFileAttached = false
                    },
                    onSubmit = { states ->
                        collect.asyncSubmit(
                            VGSRequest.VGSRequestBuilder()
                                .setPath(path)
                                .build(),
                            states,
                        )
                    },
                )
            }
        }
    }

    /**
     * Starts the BlinkCard scanner and maps the scanned values to the fields by name.
     * The result is delivered to [onActivityResult] and applied with `withScanResult`.
     */
    private fun startScanner() {
        val intent = VGSBlinkCardIntentBuilder(
            activity = this,
            settings = BlinkCardScanActivitySettings(
                sdkSettings = BlinkCardSdkSettings(licenseKey = ""),
                showOnboardingDialog = false,
            )
        )
            .setCardHolderFieldName(CARDHOLDER_FIELD_NAME)
            .setCardNumberFieldName(CARD_NUMBER_FIELD_NAME)
            .setExpirationDateFieldName(EXPIRY_FIELD_NAME)
            .setCVCFieldName(CVC_FIELD_NAME)
            .build()
        @Suppress("DEPRECATION")
        startActivityForResult(intent, SCANNER_REQUEST_CODE)
    }

    /**
     * Forwards the file picker result to [VGSCollect] so the selected file is
     * stored in the temporary file storage before submission.
     */
    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        collect.onActivityResult(requestCode, resultCode, data)
        isFileAttached = collect.getFileProvider().getAttachedFiles().isNotEmpty()
        if (requestCode == SCANNER_REQUEST_CODE && resultCode == RESULT_OK) {
            scannerResult = data
        }
    }

    /**
     * Handles the VGS proxy response after [VGSCollect.asyncSubmit].
     *
     * - [VGSResponse.SuccessResponse]: data was accepted by the vault route.
     * - [VGSResponse.ErrorResponse]: submission failed; inspect [VGSResponse.ErrorResponse.errorCode].
     */
    override fun onResponse(response: VGSResponse?) {
        when (response) {
            is VGSResponse.SuccessResponse -> {
                Log.d(TAG, "Submit success: ${response.body}")
                this.response = DemoResponse(isSuccess = true, code = response.successCode, body = response.body)
            }

            is VGSResponse.ErrorResponse -> {
                Log.e(TAG, "Submit error: ${response.errorCode}")
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
 * Payment form built with VGS Compose widgets.
 *
 * Each field owns an immutable state object. [onSubmit] receives the current
 * states so the caller can pass them directly to [VGSCollect.asyncSubmit].
 */
@Composable
private fun CollectScreen(
    collect: VGSCollect,
    scannerResult: Intent?,
    isFileAttached: Boolean,
    response: DemoResponse?,
    onBack: () -> Unit,
    onScan: () -> Unit,
    onAttachFile: () -> Unit,
    onDetachAllFiles: () -> Unit,
    onSubmit: (List<BaseFieldState>) -> Unit,
) {
    // ==========================================================
    // STEP 1: Initialize field states
    // ==========================================================
    // Each state is bound to `collect` so the SDK can wire its
    // analytics pipeline and survive config changes. Pass custom
    // validators to override the defaults, or emptyList() to
    // disable validation entirely.
    var cardHolderState by rememberVgsCardholderTextFieldState(
        collect = collect,
        fieldName = CARDHOLDER_FIELD_NAME,
    ).withScanResult(scannerResult)
    var cardNumberState by rememberVgsCardNumberTextFieldState(
        collect = collect,
        fieldName = CARD_NUMBER_FIELD_NAME,
    ).withScanResult(scannerResult)
    var cvcState by rememberVgsCvcTextFieldState(
        collect = collect,
        fieldName = CVC_FIELD_NAME,
    ).withScanResult(scannerResult)
    var expiryState by rememberVgsExpiryTextFieldState(
        collect = collect,
        fieldName = EXPIRY_FIELD_NAME,
        inputDateFormat = VgsExpiryDateFormat.MonthShortYear,
        outputDateFormat = VgsExpiryDateFormat.LongYearMonth,
    ).withScanResult(scannerResult)
    var cityState by rememberVgsTextFieldState(
        collect = collect,
        fieldName = CITY_FIELD_NAME,
        validators = emptyList(),
    )
    var postalCodeState by rememberVgsTextFieldState(
        collect = collect,
        fieldName = POSTAL_CODE_FIELD_NAME,
        validators = emptyList(),
    )
    var ssnState by rememberVgsSsnTextFieldState(
        collect = collect,
        fieldName = SSN_FIELD_NAME,
    )
    val states = listOf(cardHolderState, cardNumberState, cvcState, expiryState, cityState, postalCodeState, ssnState)

    // ==========================================================
    // STEP 2: Sync dependent field states
    // ==========================================================
    // CVC validation rules depend on the detected card brand (e.g.
    // Amex requires 4 digits). Re-sync whenever the card number
    // state changes and the brand has been updated.
    LaunchedEffect(cardNumberState.cardBrand) {
        cvcState = cvcState.withCardBrand(cardNumberState.cardBrand)
    }

    // ==========================================================
    // STEP 3: Validate and submit
    // ==========================================================
    // Every state exposes `isValid` and `validationResult` (never the raw text).
    // Errors are only displayed after the first submit attempt, and the request is only
    // sent once all fields are valid.
    var showErrors by remember { mutableStateOf(false) }
    fun invalid(state: BaseFieldState) = showErrors && !state.isValid
    fun submit() {
        showErrors = true
        if (states.all { it.isValid }) onSubmit(states)
    }

    val fieldColors = DemoFieldDefaults.colors()
    val fieldShape = DemoFieldDefaults.shape

    DemoScreen(
        title = stringResource(R.string.start_collect_compose_title),
        onBack = onBack,
        actions = {
            DemoIconButton(painterResource(R.drawable.ic_scan_card), stringResource(R.string.scan_item), onScan)
        }
    ) {
        // ======================================================
        // STEP 4: Render fields
        // ======================================================
        // Fields come in two flavours: `Vgs…TextField` and `Vgs…OutlinedTextField`
        // (Material 2), with Material 3 variants in `widget.compose.material3`.
        // They take the same `colors`, `shape`, `isError` and `keyboardOptions`
        // arguments as the Compose Material fields.
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

            // Card Number
            // Formats the digits with the brand mask, then highlights the first 6
            // positions with a rainbow gradient via `VgsStyleVisualTransformation` — the
            // style rule only ever sees character positions, never the card number itself.
            // `SpanStyle` accepts any Compose text styling.
            //
            // To hide the digits, add `VgsPasswordVisualTransformation()` before the mask
            // in the chain, otherwise it would also mask the mask's own separators.
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
                        // The detected brand is part of the state and updates as the user types.
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(id = cardNumberState.cardBrand.cardIcon),
                            contentDescription = cardNumberState.cardBrand.name,
                        )
                    },
                    visualTransformation =  VgsMaskVisualTransformation(cardNumberState.cardBrand.mask),
                    colors = fieldColors,
                    shape = fieldShape,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            // Expiry (left) + CVC (right)
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
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                }
            }

            // City (left) + Postal Code (right). A generic text field with no validators.
            Row(horizontalArrangement = Arrangement.spacedBy(DemoTheme.spacing.m)) {
                DemoField(
                    label = stringResource(R.string.collect_city_title),
                    modifier = Modifier.weight(1f),
                    optional = true
                ) {
                    VgsOutlinedTextField(
                        state = cityState,
                        onStateChange = { cityState = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { DemoPlaceholder(stringResource(R.string.collect_city_hint)) },
                        colors = fieldColors,
                        shape = fieldShape,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                }
                DemoField(
                    label = stringResource(R.string.collect_postal_code_title),
                    modifier = Modifier.weight(1f),
                    optional = true
                ) {
                    VgsOutlinedTextField(
                        state = postalCodeState,
                        onStateChange = { postalCodeState = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { DemoPlaceholder(stringResource(R.string.collect_postal_code_hint)) },
                        colors = fieldColors,
                        shape = fieldShape,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                    )
                }
            }

            // SSN. `Done` on the keyboard submits the form.
            DemoField(
                label = stringResource(R.string.collect_ssn_title),
                error = ssnState.errorMessage().takeIf { invalid(ssnState) }
            ) {
                VgsSsnOutlinedTextField(
                    state = ssnState,
                    onStateChange = { ssnState = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { DemoPlaceholder(stringResource(R.string.collect_ssn_hint)) },
                    isError = invalid(ssnState),
                    colors = fieldColors,
                    shape = fieldShape,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submit() })
                )
            }
        }

        val invalidCount = states.count { !it.isValid }
        if (showErrors && invalidCount > 0) {
            DemoErrorBanner(pluralStringResource(R.plurals.collect_invalid_fields_banner, invalidCount, invalidCount))
        }

        // ======================================================
        // STEP 5: Submit
        // ======================================================
        // Pass all states to asyncSubmit. The SDK reads the raw values
        // internally — callers never access the sensitive text directly.
        // Files are attached separately through `collect.getFileProvider()`.
        Row(horizontalArrangement = Arrangement.spacedBy(DemoTheme.spacing.m)) {
            DemoButton(
                text = stringResource(if (isFileAttached) R.string.detach_btn_title else R.string.attach_btn_title),
                onClick = { if (isFileAttached) onDetachAllFiles() else onAttachFile() },
                modifier = Modifier.weight(1f),
                icon = painterResource(R.drawable.baseline_insert_drive_file_24),
                primary = false
            )
            DemoButton(
                text = stringResource(R.string.collect_submit_btn_title),
                onClick = ::submit,
                modifier = Modifier.weight(1f)
            )
        }

        DemoDebugPanel(states = states, response = response)
    }
}

private val RAINBOW = listOf(Color.Red, Color.Magenta, Color.Blue, Color.Cyan, Color.Green, Color.Yellow)

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun Preview() {
    DemoTheme {
        CollectScreen(
            collect = VGSCollect(LocalContext.current, "", ""),
            scannerResult = null,
            isFileAttached = false,
            response = null,
            onBack = {},
            onScan = {},
            onAttachFile = {},
            onDetachAllFiles = {},
            onSubmit = {},
        )
    }
}
