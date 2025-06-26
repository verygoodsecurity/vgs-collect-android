package com.verygoodsecurity.vgscollect.core.api

import androidx.core.util.PatternsCompat
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import java.net.MalformedURLException
import java.net.URL
import java.util.regex.Pattern

/** @suppress */
internal fun String.setupURL(rawValue: String): String {
    return when {
        this.isEmpty() || !isTennantIdValid() -> {
            VGSCollectLogger.warn(message = "Vault ID is not valid")
            return ""
        }
        rawValue.isEmpty() || !rawValue.isEnvironmentValid() -> {
            VGSCollectLogger.warn(message = "Environment is not valid")
            return ""
        }
        else -> this.buildURL(rawValue)
    }
}

private fun String.buildURL(env: String): String {
    val domain = "verygoodproxy.com"
    val divider = "."
    val scheme = "https://"

    val builder = StringBuilder(scheme)
        .append(this).append(divider)
        .append(env).append(divider)
        .append(domain)

    return builder.toString()
}

internal fun String.isTennantIdValid(): Boolean =
    Pattern.compile("^[a-zA-Z0-9]*\$").matcher(this).matches()

internal fun String.isEnvironmentValid(): Boolean =
    Pattern.compile("^(live|sandbox|LIVE|SANDBOX)+((-)+([a-zA-Z0-9]+)|)+\$").matcher(this).matches()

internal fun String?.isURLValid(): Boolean {
    return when {
        isNullOrBlank() -> false
        else -> PatternsCompat.WEB_URL.matcher(this).matches()
    }
}

internal fun String.isValidIp(): Boolean {
    return when {
        isNullOrEmpty() -> false
        else -> PatternsCompat.IP_ADDRESS.matcher(this).matches()
    }
}

internal fun String.toHostnameValidationUrl(tnt: String): String {
    return String.format(
        "https://js.verygoodvault.com/collect-configs/%s__%s.txt",
        this.toHost(),
        tnt
    )
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
