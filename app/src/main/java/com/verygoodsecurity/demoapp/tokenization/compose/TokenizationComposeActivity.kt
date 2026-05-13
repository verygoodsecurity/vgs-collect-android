package com.verygoodsecurity.demoapp.tokenization.compose

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
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.start.StartActivity.Companion.KEY_BUNDLE_ENVIRONMENT
import com.verygoodsecurity.demoapp.start.StartActivity.Companion.KEY_BUNDLE_ROUTE_ID
import com.verygoodsecurity.demoapp.start.StartActivity.Companion.KEY_BUNDLE_VAULT_ID
import com.verygoodsecurity.demoapp.utils.getStringExtra
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.network.tokenization.VGSTokenizationRequest
import com.verygoodsecurity.vgscollect.widget.compose.date.VgsExpiryDateFormat
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsCardHolderOutlineTextField
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsCardNumberOutlineTextField
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsCvcOutlineTextField
import com.verygoodsecurity.vgscollect.widget.compose.material.VgsExpiryOutlineTextField
import com.verygoodsecurity.vgscollect.widget.compose.state.VgsCardHolderTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.VgsCardNumberTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.VgsCvcTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.VgsExpiryTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsCardHolderTokenizationConfig
import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsCardNumberTokenizationConfig
import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsCvcTokenizationConfig
import com.verygoodsecurity.vgscollect.widget.compose.tokenization.VgsExpiryTokenizationConfig

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
 * @see VGSCollect
 * @see VGSTokenizationRequest
 * @see VgsCollectResponseListener
 */
class TokenizationComposeActivity : AppCompatActivity(), VgsCollectResponseListener {

    private val vaultId: String by lazy { getStringExtra(KEY_BUNDLE_VAULT_ID) }
    private val env: String by lazy { getStringExtra(KEY_BUNDLE_ENVIRONMENT) }
    private val routeId: String? by lazy { getStringExtra(KEY_BUNDLE_ROUTE_ID).takeIf { it.isNotBlank() } }

    private val collect: VGSCollect by lazy {
        VGSCollect(this, vaultId, env).apply {
            addOnResponseListeners(this@TokenizationComposeActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        VGSCollectLogger.isEnabled = true
        VGSCollectLogger.logLevel = VGSCollectLogger.Level.DEBUG
        setContent {
            Content(
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

    override fun onResponse(response: VGSResponse?) {
        when (response) {
            is VGSResponse.SuccessResponse -> {
                Log.d(TAG, "Tokenize success: ${response.body}")
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            }

            is VGSResponse.ErrorResponse -> {
                Log.e(TAG, "Tokenize error: ${response.errorCode}")
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
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
private fun Content(onTokenize: (List<BaseFieldState>) -> Unit) {
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
            // STEP 1: Initialize field states with tokenization configs
            // ==========================================================
            // Each state carries its own tokenization config. The field-specific
            // subclasses provide the correct alias format / storage defaults for
            // each data type, matching the behavior of the view-based fields.
            var cardHolderState by remember {
                mutableStateOf(
                    VgsCardHolderTextFieldState(
                        fieldName = "data.name",
                        tokenizationConfig = VgsCardHolderTokenizationConfig(),
                    )
                )
            }
            var cardNumberState by remember {
                mutableStateOf(
                    VgsCardNumberTextFieldState(
                        fieldName = "data.card_number",
                        tokenizationConfig = VgsCardNumberTokenizationConfig(),
                    )
                )
            }
            var cvcState by remember {
                mutableStateOf(
                    VgsCvcTextFieldState(
                        fieldName = "data.cvc",
                        tokenizationConfig = VgsCvcTokenizationConfig(),
                    )
                )
            }
            var expiryState by remember {
                mutableStateOf(
                    VgsExpiryTextFieldState(
                        fieldName = "data.expiry",
                        inputDateFormat = VgsExpiryDateFormat.MonthShortYear,
                        outputDateFormat = VgsExpiryDateFormat.MonthShortYear,
                        tokenizationConfig = VgsExpiryTokenizationConfig(),
                    )
                )
            }

            // ==========================================================
            // STEP 2: Sync dependent field states
            // ==========================================================
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

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================================
            // STEP 4: Tokenize
            // ==========================================================
            // The SDK reads each field's tokenizationConfig, sets
            // is_required_tokenization = true on those fields, and sends
            // the payload to the VGS tokenization endpoint. The response
            // body contains aliases in place of the raw values.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .height(IntrinsicSize.Max)
                        .fillMaxWidth(),
                    onClick = {
                        onTokenize(
                            listOf(
                                cardHolderState,
                                cardNumberState,
                                cvcState,
                                expiryState,
                            )
                        )
                    }
                ) {
                    Text(
                        text = "Tokenize".uppercase(),
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
    Content(onTokenize = {})
}
