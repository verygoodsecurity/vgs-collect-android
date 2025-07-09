package com.verygoodsecurity.vgscollect.core.api

import androidx.core.util.PatternsCompat
import java.net.MalformedURLException
import java.net.URL
import java.util.regex.Pattern

internal fun String.isIdValid(): Boolean =
    Pattern.compile("^[a-zA-Z0-9]*\$").matcher(this).matches()

internal fun String.isEnvironmentValid(): Boolean =
    Pattern.compile("^(live|sandbox|LIVE|SANDBOX)+((-)+([a-zA-Z0-9]+)|)+\$").matcher(this).matches()

internal fun String?.isUrlValid(): Boolean {
    return when {
        isNullOrBlank() -> false
        else -> PatternsCompat.WEB_URL.matcher(this).matches()
    }
}

internal infix fun String.equalsUrl(name: String?): Boolean {
    return toHost() == name?.toHost()
}

internal fun String.toHttps(): String {
    return when {
        startsWith("http://") -> this
        startsWith("https://") -> this
        else -> "https://$this"
    }
}

internal fun String.toHost(): String {
    return try {
        URL(this.toHttps()).host
    } catch (_: MalformedURLException) {
        ""
    }
}
