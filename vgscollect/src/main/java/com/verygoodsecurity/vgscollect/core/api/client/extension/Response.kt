package com.verygoodsecurity.vgscollect.core.api.client.extension

fun Int.isCodeSuccessful(): Boolean {
    return this in 200..299
}

fun Int.isHttpStatusCode(): Boolean {
    return this in 200..999
}