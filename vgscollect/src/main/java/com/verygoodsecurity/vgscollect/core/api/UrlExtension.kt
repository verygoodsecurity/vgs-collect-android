package com.verygoodsecurity.vgscollect.core.api

import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.util.Logger
import java.net.URL
import java.util.regex.Pattern

internal fun String.setupURL(rawValue:String):String {
    return if(this.isTennantIdValid()) {
        this.buildURL(rawValue)
    } else {
        Logger.e(VGSCollect.TAG, "tennantId is not valid")
        ""
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

internal fun String.isURLValid():Boolean {
    return try {
        URL(this).toURI()
        true
    } catch (e:Exception) {
        false
    }
//    val s = Patterns.WEB_URL.matcher(this).matches()
//    val s = URLUtil.isValidUrl(this)
}