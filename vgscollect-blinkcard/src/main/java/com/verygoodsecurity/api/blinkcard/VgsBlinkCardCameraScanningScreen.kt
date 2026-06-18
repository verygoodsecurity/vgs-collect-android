package com.verygoodsecurity.api.blinkcard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.microblink.blinkcard.core.BlinkCardSdk
import com.microblink.blinkcard.core.BlinkCardSdkSettings
import com.microblink.blinkcard.ux.BlinkCardCameraScanningScreen

@Composable
fun VgsBlinkCardCameraScanningScreen(
    key: String,
    onInitializationFailure: (exception: Throwable) -> Unit = {},
) {
    val context = LocalContext.current
    var sdk by remember { mutableStateOf<BlinkCardSdk?>(null) }

    LaunchedEffect(key) {
        BlinkCardSdk.initializeSdk(
            context = context,
            settings = BlinkCardSdkSettings(licenseKey = "")
        ).onSuccess { instance ->
            sdk = instance
        }.onFailure { exception ->
            onInitializationFailure(exception)
        }
    }

    sdk?.let {
        BlinkCardCameraScanningScreen(
            blinkCardSdk = it,
            onScanningSuccess = { result ->
                println("Card number: ${result.cardAccounts[0].cardNumber}")
            },
            onScanningCanceled = { }
        )
    } ?: run {
        println("Failed to initialize BlinkCard SDK")
    }
}

interface Cgs