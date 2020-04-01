package com.verygoodsecurity.vgscollect.view.date.validation

import java.util.regex.Pattern

/** @suppress */
internal fun String.isInputDatePatternValid():Boolean {
    val regex = "^" +
            "((dd)[^a-zA-Z0-9](MM)[^a-zA-Z0-9]((yy)|(yyyy)))|" +
            "((dd)[^a-zA-Z0-9]((yy)|(yyyy))[^a-zA-Z0-9](MM))|" +
            "(((yy)|(yyyy))[^a-zA-Z0-9](dd)[^a-zA-Z0-9](MM))|" +
            "((MM)[^a-zA-Z0-9](dd)[^a-zA-Z0-9]((yy)|(yyyy)))|" +
            "((MM)[^a-zA-Z0-9]((yy)|(yyyy))[^a-zA-Z0-9](dd))|" +
            "(((yy)|(yyyy))[^a-zA-Z0-9](MM)[^a-zA-Z0-9](dd))|" +
            "((MM)[^a-zA-Z0-9]((yy)|(yyyy)))|" +
            "(((yy)|(yyyy))[^a-zA-Z0-9](MM))" +
            "$"
    return Pattern.compile(regex).matcher(this).matches()
}