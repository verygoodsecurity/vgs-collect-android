@file:Suppress("UNCHECKED_CAST")

package com.verygoodsecurity.vgscollect.widget.compose.util

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.core.os.BundleCompat
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState

@Composable
fun <T : BaseFieldState> MutableState<T>.withScanResult(
    scannerResult: Intent? = null,
): MutableState<T> {
    LaunchedEffect(scannerResult) {
        scannerResult.extractScannedValue(value.fieldName)?.let { scannedValue ->
            value = value.copy(text = scannedValue) as T
        }
    }
    return this
}

internal fun Intent?.extractScannedValue(fieldName: String): String? {
    val data = BundleCompat.getParcelable(
        this?.extras ?: return null,
        BaseTransmitActivity.RESULT_DATA,
        VGSHashMapWrapper::class.java,
    ) as? VGSHashMapWrapper<String, Any?>
    return data?.mapOf()?.get(fieldName) as? String
}


