package com.verygoodsecurity.vgscollect.util.extension

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat

fun Context.isConnectionAvailable(): Boolean {
    return if (hasAccessNetworkStatePermission()) {
        val manager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val network = manager?.activeNetworkInfo
        (network != null)
    } else {
        false
    }
}

fun Context.hasAccessNetworkStatePermission() : Boolean = ContextCompat.checkSelfPermission(
    this,
    android.Manifest.permission.ACCESS_NETWORK_STATE
) != PackageManager.PERMISSION_DENIED

fun Context.hasInternetPermission() = ContextCompat.checkSelfPermission(
    this,
    android.Manifest.permission.INTERNET
) != PackageManager.PERMISSION_DENIED
