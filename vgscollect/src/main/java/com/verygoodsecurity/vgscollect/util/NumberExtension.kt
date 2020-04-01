package com.verygoodsecurity.vgscollect.util

/** @suppress */
internal fun String.isNumeric():Boolean {
    return this.toDoubleOrNull() != null
}