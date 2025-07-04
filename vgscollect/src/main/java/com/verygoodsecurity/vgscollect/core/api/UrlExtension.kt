package com.verygoodsecurity.vgscollect.core.api

import androidx.core.util.PatternsCompat
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.Environment
import java.net.MalformedURLException
import java.net.URL
import java.util.regex.Pattern

internal fun String.setupCardManagerURL(env: String): String {
    if (this.isBlank()) {
        VGSCollectLogger.warn(message = "Account ID is not valid")
        return ""
    }
    if (env.isBlank() || !env.isEnvironmentValid()) {
        VGSCollectLogger.warn(message = "Environment is not valid")
        return ""
    }
    val scheme = "https://"
    val divider = "."
    val domain = "vgsapi.com"

    val builder = StringBuilder(scheme)

    if (env == Environment.SANDBOX.rawValue) {
        builder.append(env)
        builder.append(divider)
    }

    builder.append(domain)

    return builder.toString()
}

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
