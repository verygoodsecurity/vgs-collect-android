package com.verygoodsecurity.vgscollect.util

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat

class NetworkInspector(val context: Context) {

    fun isConnectionAvailable(): Boolean {
        return if (hasAccessNetworkStatePermission()) {
            val manager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            val network = manager?.activeNetworkInfo
            (network != null)
        } else {
            false
        }
    }

    fun hasInternetPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.INTERNET
        ) != PackageManager.PERMISSION_DENIED
    }

    fun hasAccessNetworkStatePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_NETWORK_STATE
        ) != PackageManager.PERMISSION_DENIED
    }
}