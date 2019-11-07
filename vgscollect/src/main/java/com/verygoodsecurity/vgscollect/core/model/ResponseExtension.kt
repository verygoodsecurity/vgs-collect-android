package com.verygoodsecurity.vgscollect.core.model

internal fun String.parseVGSResponse():Map<String, String> {
    //todo parse JSON

    return mapOf(Pair("response",this))
}