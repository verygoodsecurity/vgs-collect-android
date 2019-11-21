package com.verygoodsecurity.vgscollect.core.api

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