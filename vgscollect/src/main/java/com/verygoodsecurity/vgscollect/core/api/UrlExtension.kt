package com.verygoodsecurity.vgscollect.core.api

import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.isLocalSandbox
import com.verygoodsecurity.vgscollect.util.Logger
import java.net.MalformedURLException
import java.net.URL
import java.util.regex.Pattern

/**
 *  10.0.2.2 is special alias to your host loopback interface (i.e., 127.0.0.1 on your development machine)
 *  More read here @see [Documentation](https://developer.android.com/studio/run/emulator-networking)
 */
private const val LOCAL_HOST_ALIAS = "http://10.0.2.2"

/**
 * Default port for @see [VGS-satellite](https://github.com/verygoodsecurity/vgs-satellite)
 */
private const val DEFAULT_LOCAL_HOST_PORT = 9098

const val PORT_MIN_VALUE = 1L
const val PORT_MAX_VALUE = 65535L

/** @suppress */
internal fun String.setupURL(rawValue: String, port: Int? = null): String =
    if (rawValue.isLocalSandbox()) buildLocalUrl(port) else this.buildRemoteUrl(rawValue)

private fun buildLocalUrl(port: Int?): String =
    "$LOCAL_HOST_ALIAS:${port?.takeIf { it.isValidPort()} ?: DEFAULT_LOCAL_HOST_PORT}"

private fun String.buildRemoteUrl(env: String): String = when {
    this.isEmpty() || !this.isTennantIdValid() -> {
        Logger.e(VGSCollect::class.java, "tennantId is not valid")
        ""
    }
    env.isEmpty() || !env.isEnvironmentValid() -> {
        Logger.e(VGSCollect::class.java, "Environment is not valid")
        ""
    }
    else -> {
        val DOMEN = "verygoodproxy.com"
        val DIVIDER = "."
        val SCHEME = "https://"

        val builder = StringBuilder(SCHEME)
            .append(this).append(DIVIDER)
            .append(env).append(DIVIDER)
            .append(DOMEN)

        builder.toString()
    }
}

internal fun Int.isValidPort() = this in (PORT_MIN_VALUE..PORT_MAX_VALUE)

internal fun String.isTennantIdValid():Boolean {
    val m = Pattern.compile("^[a-zA-Z0-9]*\$").matcher(this)

    return m.matches()
}

internal fun String.isEnvironmentValid():Boolean {
    val m = Pattern.compile("^(live|sandbox|local_sandbox|LIVE|SANDBOX|LOCAL_SANDBOX)+((-)+([a-zA-Z0-9]+)|)+\$").matcher(this)

    return m.matches()
}

internal fun String.isURLValid():Boolean {
    return try {
        URL(this).toURI()
        true
    } catch (e:Exception) {
        false
    }
}

internal fun String.buildURL(path: String, vararg getQuery:String):URL? {
    val builder = StringBuilder(this)

    when {
        path.isEmpty() -> {}
        path.length > 1 && path.first().toString() == "/" -> builder.append(path)
        else -> builder.append("/").append(path)
    }

    if(getQuery.isNotEmpty()) {
        builder.append("?")
        getQuery.forEach {
            if(builder.last() != '?') {
                builder.append("&")
            }
            builder.append(it)
        }
    }

    var url:URL? = null
    try {
        url = URL(builder.toString())
    } catch (e: MalformedURLException) { }

    return url
}