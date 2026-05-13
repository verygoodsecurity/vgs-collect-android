@file:Suppress("UsingMaterialAndMaterial3Libraries")

package com.verygoodsecurity.demoapp.collect.compose

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.verygoodsecurity.vgscollect.widget.compose.state.VgsCardHolderTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.VgsCardNumberTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.VgsCvcTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.VgsExpiryTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.VgsSsnTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.VgsTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState

private const val TAG = "CollectComposeActivity"

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        VGSCollectLogger.isEnabled = true
        VGSCollectLogger.logLevel = VGSCollectLogger.Level.DEBUG
        setContent {
            Content(
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
private fun Content(onSubmit: (List<BaseFieldState>) -> Unit) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // ==========================================================
            // STEP 1: Initialize field states
            // ==========================================================
            // Each state holds the field name used as the JSON key in the
            // submit payload. Pass a custom validators list to override
            // default validation, or emptyList() to disable it entirely.
            var cardHolderState by remember {
                mutableStateOf(VgsCardHolderTextFieldState(fieldName = "data.name"))
            }
            var cardNumberState by remember {
                mutableStateOf(VgsCardNumberTextFieldState(fieldName = "data.card_number"))
            }
            var cvcState by remember {
                mutableStateOf(VgsCvcTextFieldState(fieldName = "data.cvc"))
            }
            var expiryState by remember {
                mutableStateOf(
                    VgsExpiryTextFieldState(
                        fieldName = "data.expiry",
                        inputDateFormat = VgsExpiryDateFormat.MonthShortYear,
                        outputDateFormat = VgsExpiryDateFormat.LongYearMonth,
                    )
                )
            }
            var cityState by remember {
                mutableStateOf(VgsTextFieldState(fieldName = "data.city", validators = emptyList()))
            }
            var postalCodeState by remember {
                mutableStateOf(
                    VgsTextFieldState(
                        fieldName = "data.postal_code",
                        validators = emptyList()
                    )
                )
            }
            var ssnState by remember {
                mutableStateOf(VgsSsnTextFieldState(fieldName = "data.ssn"))
            }

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
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    FieldLabel("Postal Code")
                    VgsOutlineTextField(
                        state = postalCodeState,
                        modifier = Modifier.fillMaxWidth(),
                        onStateChange = { postalCodeState = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    onClick = { /* TODO: implement file attachment */ }
                ) {
                    Text(
                        text = "Attach".uppercase(),
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
    Content(onSubmit = {})
}
