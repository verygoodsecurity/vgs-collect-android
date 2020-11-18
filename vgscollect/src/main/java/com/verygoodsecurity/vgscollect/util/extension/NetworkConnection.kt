package com.verygoodsecurity.vgscollect.util.extension

import android.content.Context
import android.net.ConnectivityManager

fun Context.isConnectionAvailable(): Boolean {
    val manager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val network = manager?.activeNetworkInfo
    return (network != null)
}