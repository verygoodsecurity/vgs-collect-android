package com.verygoodsecurity.vgscollect.util

fun String.isNumeric():Boolean {
    return this.toDoubleOrNull() != null
}