@file:Suppress("UsingMaterialAndMaterial3Libraries")

package com.verygoodsecurity.demoapp.collect.compose

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.verygoodsecurity.demoapp.start.StartActivity.Companion.KEY_BUNDLE_VAULT_ID
import com.verygoodsecurity.demoapp.utils.getStringExtra
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.widget.compose.date.VgsExpiryDateFormat
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsCardHolderOutlineTextField
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsCardNumberOutlineTextField
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsCvcOutlineTextField
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsExpiryOutlineTextField
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsOutlineTextField
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsSsnOutlineTextField
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsCardHolderTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsCardNumberTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsCvcTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsExpiryTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsSsnTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.rememberVgsTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsTokenizationConfig
import com.verygoodsecurity.vgscollect.widget.compose.util.withScanResult

private const val TAG = "CollectComposeActivity"
private const val ATTACHMENT_FIELD_NAME = "data.attachment"

private const val CARDHOLDER_FIELD_NAME = "data.name"
private const val CARD_NUMBER_FIELD_NAME = "data.card_number"
private const val CVC_FIELD_NAME = "data.cvc"
private const val EXPIRY_FIELD_NAME = "data.expiry"

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
 * 📘 Official documentation:
 * https://docs.verygoodsecurity.com/vault/developer-tools/vgs-collect/android-sdk/index
 *
 * @see VGSCollect
 * @see VGSRequest
 * @see VgsCollectResponseListener
 */
class CollectComposeActivity : AppCompatActivity(), VgsCollectResponseListener {

    private val vaultId: String by lazy { getStringExtra(KEY_BUNDLE_VAULT_ID) }
    private val env: String by lazy { getStringExtra(KEY_BUNDLE_ENVIRONMENT) }
    private val path: String by lazy { getStringExtra(KEY_BUNDLE_PATH) }

    /**
     * Lazy initialization of [VGSCollect].
     *
     * Must be initialized with Activity context.
     */
    private val collect: VGSCollect by lazy {
        VGSCollect(this, vaultId, env).apply {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        VGSCollectLogger.isEnabled = true
        VGSCollectLogger.logLevel = VGSCollectLogger.Level.DEBUG
        setContent {
            Content(
                collect = collect,
                title = title.toString(),
                scannerResult = scannerResult,
                isFileAttached = isFileAttached,
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
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            }

            is VGSResponse.ErrorResponse -> {
                Log.e(TAG, "Submit error: ${response.errorCode}")
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
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
private fun Content(
    collect: VGSCollect,
    title: String,
    scannerResult: Intent?,
    isFileAttached: Boolean,
    onAttachFile: () -> Unit,
    onDetachAllFiles: () -> Unit,
    onSubmit: (List<BaseFieldState>) -> Unit,
) {
    MaterialTheme {
        val activity = LocalActivity.current
        val context = LocalContext.current

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                    backgroundColor = colorResource(R.color.fiord),
                    contentColor = Color.White,
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_vgs_logo_white),
                                contentDescription = "Back"
                            )
                            Spacer(modifier = Modifier.size(12.dp))
                            Text(title)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (activity == null) {
                                Toast.makeText(
                                    context,
                                    "Scanning start failed. Activity is null.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@IconButton
                            }
                            val intent = VGSBlinkCardIntentBuilder(
                                activity = activity,
                                settings = BlinkCardScanActivitySettings(
                                    sdkSettings = BlinkCardSdkSettings(
                                        licenseKey = "sRwCABxjb20udmVyeWdvb2RzZWN1cml0eS5kZW1vYXBwAGxleUpEY21WaGRHVmtUMjRpT2pFM09ERXdPRGs1TnpNd05qWXNJa055WldGMFpXUkdiM0lpT2lJeE56TTBaVGcxTXkxbU1HSmpMVFJqT1RRdFltTXlPUzB6WVRNeFptRXpOR016TW1JaWZRPT2plM9QZ/E8AWXEW1aEpfNnvJb5H4S2XHyjd93xu6eLTzG3U5ZFxmMluk1OQybLEer0B+RJ4w+CzQefUw5DcqVF/OBI/7xx2q2Sx9a1OBc36ZPpCggPZFTXCuQI8f4=",
                                    ),
                                    showOnboardingDialog = false,
                                )
                            )
                                .setCardHolderFieldName(CARDHOLDER_FIELD_NAME)
                                .setCardNumberFieldName(CARD_NUMBER_FIELD_NAME)
                                .setExpirationDateFieldName(EXPIRY_FIELD_NAME)
                                .setCVCFieldName(CVC_FIELD_NAME)
                                .build()
                            activity.startActivityForResult(intent, SCANNER_REQUEST_CODE)
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_scan_card),
                                tint = Color.White,
                                contentDescription = "Start scanner"
                            )
                        }
                    }
                )
            },
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // ==========================================================
                // STEP 1: Initialize field states
                // ==========================================================
                // Each state is bound to `collect` so the SDK can wire its
                // analytics pipeline and survive config changes. Pass custom
                // validators to override the defaults, or emptyList() to
                // disable validation entirely.
                var cardHolderState by rememberVgsCardHolderTextFieldState(
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
                    fieldName = "data.city",
                    validators = emptyList(),
                )
                var postalCodeState by rememberVgsTextFieldState(
                    collect = collect,
                    fieldName = "data.postal_code",
                    validators = emptyList(),
                )
                var ssnState by rememberVgsSsnTextFieldState(
                    collect = collect,
                    fieldName = "data.ssn",
                )

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
                // STEP 3: Render fields
                // ==========================================================
                val fieldColors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = colorResource(R.color.fiord_20),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    cursorColor = colorResource(R.color.fiord)
                )

                // Card Holder
                FieldLabel("Card Holder")
                VgsCardHolderOutlineTextField(
                    state = cardHolderState,
                    modifier = Modifier.fillMaxWidth(),
                    onStateChange = { cardHolderState = it },
                    colors = fieldColors,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Card Number
                FieldLabel("Card Number")
                VgsCardNumberOutlineTextField(
                    state = cardNumberState,
                    modifier = Modifier.fillMaxWidth(),
                    onStateChange = { cardNumberState = it },
                    trailingIcon = {
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(id = cardNumberState.cardBrand.cardIcon),
                            contentDescription = cardNumberState.cardBrand.name,
                        )
                    },
                    colors = fieldColors,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Expiry (left) + CVC (right)
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel("Expiry")
                        VgsExpiryOutlineTextField(
                            state = expiryState,
                            modifier = Modifier.fillMaxWidth(),
                            onStateChange = { expiryState = it },
                            colors = fieldColors,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel("CVC")
                        VgsCvcOutlineTextField(
                            state = cvcState,
                            modifier = Modifier.fillMaxWidth(),
                            onStateChange = { cvcState = it },
                            colors = fieldColors,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // City (left) + Postal Code (right)
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel("City")
                        VgsOutlineTextField(
                            state = cityState,
                            modifier = Modifier.fillMaxWidth(),
                            onStateChange = { cityState = it },
                            colors = fieldColors,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        FieldLabel("Postal Code")
                        VgsOutlineTextField(
                            state = postalCodeState,
                            modifier = Modifier.fillMaxWidth(),
                            onStateChange = { postalCodeState = it },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            colors = fieldColors,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // SSN
                FieldLabel("SSN")
                VgsSsnOutlineTextField(
                    state = ssnState,
                    modifier = Modifier.fillMaxWidth(),
                    onStateChange = { ssnState = it },
                    colors = fieldColors,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onSubmit(
                                listOf(
                                    cardHolderState,
                                    cardNumberState,
                                    cvcState,
                                    expiryState,
                                    cityState,
                                    postalCodeState,
                                    ssnState,
                                )
                            )
                        }
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ==========================================================
                // STEP 4: Submit
                // ==========================================================
                // Pass all states to asyncSubmit. The SDK reads the raw values
                // internally — callers never access the sensitive text directly.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    OutlinedButton(
                        modifier = Modifier.height(IntrinsicSize.Max),
                        onClick = { if (isFileAttached) onDetachAllFiles() else onAttachFile() },
                    ) {
                        Text(
                            text = if (isFileAttached) {
                                stringResource(R.string.detach_btn_title).uppercase()
                            } else {
                                stringResource(R.string.attach_btn_title).uppercase()
                            },
                            color = colorResource(R.color.fiord)
                        )
                        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                        Icon(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            painter = painterResource(id = R.drawable.baseline_insert_drive_file_24),
                            tint = colorResource(R.color.fiord),
                            contentDescription = "Attach file",
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    OutlinedButton(
                        modifier = Modifier.height(IntrinsicSize.Max),
                        onClick = {
                            onSubmit(
                                listOf(
                                    cardHolderState,
                                    cardNumberState,
                                    cvcState,
                                    expiryState,
                                    cityState,
                                    postalCodeState,
                                    ssnState,
                                )
                            )
                        }
                    ) {
                        Text(
                            text = "Submit".uppercase(),
                            color = colorResource(R.color.fiord)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.caption,
        fontWeight = FontWeight.Medium,
        color = colorResource(R.color.fiord)
    )
    Spacer(modifier = Modifier.height(4.dp))
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun Preview() {
    Content(
        collect = VGSCollect(LocalContext.current, "", ""),
        title = "Compose Demo (Activity)",
        scannerResult = null,
        isFileAttached = false,
        onAttachFile = {},
        onDetachAllFiles = {},
        onSubmit = {},
    )
}
