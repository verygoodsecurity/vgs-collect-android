package com.verygoodsecurity.demoapp.collect_compose.activity_case

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.verygoodsecurity.demoapp.StartActivity.Companion.KEY_BUNDLE_ENVIRONMENT
import com.verygoodsecurity.demoapp.StartActivity.Companion.KEY_BUNDLE_PATH
import com.verygoodsecurity.demoapp.StartActivity.Companion.KEY_BUNDLE_VAULT_ID
import com.verygoodsecurity.demoapp.getStringExtra
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.widget.compose.VgsCardHolderTextField
import com.verygoodsecurity.vgscollect.widget.compose.VgsCardHolderTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.VgsCardNumberTextField
import com.verygoodsecurity.vgscollect.widget.compose.VgsCardNumberTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.VgsCvcTextField
import com.verygoodsecurity.vgscollect.widget.compose.VgsCvcTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.VgsExpiryTextField
import com.verygoodsecurity.vgscollect.widget.compose.VgsExpiryTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.VgsSsnTextField
import com.verygoodsecurity.vgscollect.widget.compose.VgsSsnTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.core.BaseFieldState
import com.verygoodsecurity.vgscollect.widget.compose.date.VgsExpiryDateFormat
import com.verygoodsecurity.vgscollect.widget.compose.validator.VgsLuhnAlgorithmValidator

class ComposeActivity : AppCompatActivity(), VgsCollectResponseListener {

    private val vaultId: String by lazy { getStringExtra(KEY_BUNDLE_VAULT_ID) }
    private val env: String by lazy { getStringExtra(KEY_BUNDLE_ENVIRONMENT) }
    private val path: String by lazy { getStringExtra(KEY_BUNDLE_PATH) }

    val collect: VGSCollect by lazy {
        VGSCollect(this, vaultId, env).apply {
            addOnResponseListeners(this@ComposeActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VGSCollectLogger.logLevel = VGSCollectLogger.Level.DEBUG
        VGSCollectLogger.isEnabled = true
        setContent {
            Content(
                onSubmit = {
                    collect.asyncSubmit(
                        VGSRequest.VGSRequestBuilder()
                            .setPath(path)
                            .build(),
                        *it.toTypedArray(),
                    )
                },
            )
        }
    }

    override fun onResponse(response: VGSResponse?) {
        Toast.makeText(
            this,
            when (response) {
                is VGSResponse.SuccessResponse -> "Success"
                is VGSResponse.ErrorResponse -> "Error"
                else -> throw IllegalStateException()
            },
            Toast.LENGTH_SHORT
        ).show()

        Log.d("VGS", "response = ${response.body}")
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Content(onSubmit: (List<BaseFieldState>) -> Unit) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Setup initial states
            var cardHolderState: VgsCardHolderTextFieldState by remember {
                mutableStateOf(VgsCardHolderTextFieldState(fieldName = "data.name"))
            }
            var cardNumberFieldState: VgsCardNumberTextFieldState by remember {
                mutableStateOf(VgsCardNumberTextFieldState(fieldName = "data.card_number"))
            }
            var cvcFieldState: VgsCvcTextFieldState by remember {
                mutableStateOf(VgsCvcTextFieldState(fieldName = "data.cvc"))
            }
            var expiryFieldState: VgsExpiryTextFieldState by remember {
                mutableStateOf(
                    VgsExpiryTextFieldState(
                        fieldName = "data.expiry",
                        inputDateFormat = VgsExpiryDateFormat.MonthShortYear(), // Specify input format("MM/yy")
                        outputDateFormat = VgsExpiryDateFormat.LongYearMonth() // Specify input format("yyyy/MM")
                    )
                )
            }
            var ssnFieldState: VgsSsnTextFieldState by remember {
                mutableStateOf(
                    VgsSsnTextFieldState(
                        fieldName = "data.ssn",
                        validators = listOf(
                            VgsLuhnAlgorithmValidator(errorMsg = "Validation failed!")
                        ) // Set empty validators list to disable validation, or provide own validation using predefined types
                    )
                )
            }


            // Update cvc state card brand when card number state changed
            LaunchedEffect(cardNumberFieldState) {
                if (cvcFieldState.cardBrand != cardNumberFieldState.cardBrand) {
                    cvcFieldState = cvcFieldState.withCardBrand(cardNumberFieldState.cardBrand)
                }
            }

            println("TEST:DD:......")
            println("TEST:DD: cardHolderState = ${cardHolderState.validationResult.map { it.errorMsg }}, isValid = ${cardHolderState.isValid}")
            println("TEST:DD: cardNumberFieldState = ${cardNumberFieldState.validationResult.map { it.errorMsg }}, isValid = ${cardNumberFieldState.isValid}")
            println("TEST:DD: cvcFieldState = ${cvcFieldState.validationResult.map { it.errorMsg }}, isValid = ${cvcFieldState.isValid}")
            println("TEST:DD: expiryFieldState = ${expiryFieldState.validationResult.map { it.errorMsg }}, isValid = ${expiryFieldState.isValid}")
            println("TEST:DD: ssnFieldState = ${ssnFieldState.validationResult.map { it.errorMsg }}, isValid = ${ssnFieldState.isValid}")
            println("TEST:DD:......")


            // Add card holder widget
            VgsCardHolderTextField(
                state = cardHolderState,
                modifier = Modifier.fillMaxWidth(),
                onStateChange = { cardHolderState = it },
                label = { Text("Card holder name") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Add card number widget
            VgsCardNumberTextField(
                state = cardNumberFieldState,
                modifier = Modifier.fillMaxWidth(),
                onStateChange = { cardNumberFieldState = it },
                label = { Text("Card number") },
                leadingIcon = {
                    // Handle card icon
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = cardNumberFieldState.cardBrand.cardIcon),
                        contentDescription = cardNumberFieldState.cardBrand.name
                    )
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Add cvc widget
            VgsCvcTextField(
                state = cvcFieldState,
                modifier = Modifier.fillMaxWidth(),
                onStateChange = { cvcFieldState = it },
                label = { Text("Card verification code") },
                leadingIcon = {
                    // Handle cvc icon
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = cvcFieldState.cardBrand.securityCodeIcon),
                        contentDescription = cvcFieldState.cardBrand.name
                    )
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Add expiration date widget
            VgsExpiryTextField(
                state = expiryFieldState,
                modifier = Modifier.fillMaxWidth(),
                onStateChange = { expiryFieldState = it },
                label = { Text("Expiration date") },
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Add ssn widget
            VgsSsnTextField(
                state = ssnFieldState,
                modifier = Modifier.fillMaxWidth(),
                onStateChange = { ssnFieldState = it },
                label = { Text("Social security number") },
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                // Pass states to collect, so collect can get data a make a request
                onSubmit(
                    listOf(
                        cardHolderState,
                        cardNumberFieldState,
                        cvcFieldState,
                        expiryFieldState,
                        ssnFieldState,
                    )
                )
            }
            ) {
                Text(text = "Submit")
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun Preview() {
    Content(
        onSubmit = {}
    )
}
