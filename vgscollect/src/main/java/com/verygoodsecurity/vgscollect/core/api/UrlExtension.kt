package com.verygoodsecurity.vgscollect.core.api

import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.VGSLogger
import java.net.MalformedURLException
import java.net.URL
import java.util.regex.Pattern

/** @suppress */
internal fun String.setupURL(rawValue: String): String {
    return when {
        this.isEmpty() || !isTennantIdValid() -> {
            VGSLogger.warn(VGSCollect.TAG, "tennantId is not valid")
            return ""
        }
        rawValue.isEmpty() || !rawValue.isEnvironmentValid() -> {
            VGSLogger.warn(VGSCollect.TAG, "Environment is not valid")
            return ""
        }
        else -> this.buildURL(rawValue)
    }
}

private fun String.buildURL(env: String): String {
    val DOMEN = "verygoodproxy.com"
    val DIVIDER = "."
    val SCHEME = "https://"

    val builder = StringBuilder(SCHEME)
        .append(this).append(DIVIDER)
        .append(env).append(DIVIDER)
        .append(DOMEN)

    return builder.toString()
}

internal fun String.isTennantIdValid(): Boolean =
    Pattern.compile("^[a-zA-Z0-9]*\$").matcher(this).matches()

internal fun String.isEnvironmentValid(): Boolean =
    Pattern.compile("^(live|sandbox|LIVE|SANDBOX)+((-)+([a-zA-Z0-9]+)|)+\$").matcher(this).matches()

internal fun String.isURLValid(): Boolean {
    return when {
        isNullOrBlank() -> false
        startsWith("http://") -> throw RuntimeException("Cleartext HTTP traffic to * not permitted")
        else -> Pattern.compile(
            "^(?:https?:\\/\\/)?[\\w.-]+(?:\\.[\\w\\/.-]+)+[\\w\\:]+\$"
        ).matcher(this).matches()
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
    } catch (e: MalformedURLException) {
        ""
    }
}
