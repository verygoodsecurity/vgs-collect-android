package com.verygoodsecurity.vgscollect.core.api

import java.util.regex.Pattern

internal fun String.setupURL(env:String):String {
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