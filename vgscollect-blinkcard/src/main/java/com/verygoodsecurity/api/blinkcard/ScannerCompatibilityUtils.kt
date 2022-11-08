package com.verygoodsecurity.api.blinkcard

import android.app.Activity
import com.microblink.blinkcard.util.RecognizerCompatibility
import com.microblink.blinkcard.util.RecognizerCompatibilityStatus

internal fun Activity.isScannerCompatible(notifyFailedStatus: (String) -> Unit): Boolean {
    when (RecognizerCompatibility.getRecognizerCompatibilityStatus(this)) {
        RecognizerCompatibilityStatus.NO_CAMERA ->
            notifyFailedStatus(
                getString(R.string.vgs_bc_warning_direct_api)
            )
        RecognizerCompatibilityStatus.PROCESSOR_ARCHITECTURE_NOT_SUPPORTED ->
            notifyFailedStatus(
                getString(R.string.vgs_bc_warning_arch_not_supported)
            )
        RecognizerCompatibilityStatus.DEVICE_BLACKLISTED -> notifyFailedStatus(
            getString(
                R.string.vgs_bc_warning_blacklisted_device,
                RecognizerCompatibilityStatus.DEVICE_BLACKLISTED.name
            )
        )
        RecognizerCompatibilityStatus.UNSUPPORTED_ANDROID_VERSION -> notifyFailedStatus(
            getString(
                R.string.vgs_bc_warning_unsupported_android_version,
                RecognizerCompatibilityStatus.UNSUPPORTED_ANDROID_VERSION.name
            )
        )
        RecognizerCompatibilityStatus.RECOGNIZER_NOT_SUPPORTED -> notifyFailedStatus(
            getString(
                R.string.vgs_bc_warning_unsupported_recognizer,
                RecognizerCompatibilityStatus.RECOGNIZER_NOT_SUPPORTED.name
            )
        )
        RecognizerCompatibilityStatus.RECOGNIZER_SUPPORTED -> return true
    }
    return false
}