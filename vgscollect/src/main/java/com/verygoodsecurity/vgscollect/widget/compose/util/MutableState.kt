@file:Suppress("UNCHECKED_CAST")

package com.verygoodsecurity.vgscollect.widget.compose.util

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.core.os.BundleCompat
import com.verygoodsecurity.vgscollect.app.BaseTransmitActivity
import com.verygoodsecurity.vgscollect.core.model.VGSHashMapWrapper
import com.verygoodsecurity.vgscollect.widget.compose.state.VgsExpiryTextFieldState
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState
import java.util.Date

@Composable
fun <T : BaseFieldState> MutableState<T>.withScanResult(
    scannerResult: Intent? = null,
): MutableState<T> {
    LaunchedEffect(scannerResult) {
        scannerResult.extractScannedValue(value.fieldName)?.let { scannedValue ->
            val stateValue = value
            when (scannedValue) {
                is String -> value = value.copy(text = scannedValue) as T
                is Long -> (stateValue as? VgsExpiryTextFieldState)?.let {
                    val date = stateValue.inputDateFormat.format(Date(scannedValue))
                    date?.let { text -> value = stateValue.copy(text = text) as T }
                }
                else -> {}
            }
        }
    }
    return this
}

internal fun Intent?.extractScannedValue(fieldName: String): Any? {
    val data = BundleCompat.getParcelable(
        this?.extras ?: return null,
        BaseTransmitActivity.RESULT_DATA,
        VGSHashMapWrapper::class.java,
    ) as? VGSHashMapWrapper<String, Any?>
    return data?.mapOf()?.get(fieldName)
}
