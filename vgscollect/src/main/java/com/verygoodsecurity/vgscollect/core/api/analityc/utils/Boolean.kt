package com.verygoodsecurity.vgscollect.core.api.analityc.utils

fun Boolean.toAnalyticStatus(): String {
    return if(this) {
        "Ok"
    } else {
        "Failed"
    }
}