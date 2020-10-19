package com.verygoodsecurity.vgscollect.core.api

import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.isLocalSandbox
import com.verygoodsecurity.vgscollect.util.Logger
import java.net.MalformedURLException
import java.net.URL
import java.util.regex.Pattern

private const val LOCAL_SANDBOX_URL = "http://10.0.2.2:9098"

/** @suppress */
internal fun String.setupURL(rawValue: String) = if (rawValue.isLocalSandbox()) {
    LOCAL_SANDBOX_URL
} else {
    when {
        this.isEmpty() || !isTennantIdValid() -> {
            Logger.e(VGSCollect::class.java, "tennantId is not valid")
            ""
        }
        rawValue.isEmpty() || !rawValue.isEnvironmentValid() -> {
            Logger.e(VGSCollect::class.java, "Environment is not valid")
            ""
        }
        else -> this.buildURL(rawValue)
    }
}

private fun String.buildURL(env:String):String {
    val DOMEN = "verygoodproxy.com"
    val DIVIDER = "."
    val SCHEME  = "https://"

    val builder = StringBuilder(SCHEME)
        .append(this).append(DIVIDER)
        .append(env).append(DIVIDER)
        .append(DOMEN)

    return builder.toString()
}

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