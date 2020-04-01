package com.verygoodsecurity.vgscollect.util

/** @suppress */
internal fun Boolean.toInt():Int {
    return if(this) {
        1
    } else {
        0
    }
}