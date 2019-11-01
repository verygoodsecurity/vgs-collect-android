package com.verygoodsecurity.vgscollect.core.model

import android.net.Uri

fun MutableCollection<VGSFieldState>.mapToEncodedQuery(): String? {
    val builder = Uri.Builder()
    for (entry in this) {
        builder.appendQueryParameter(entry.alias, entry.content)
    }
    return builder.build().encodedQuery
}