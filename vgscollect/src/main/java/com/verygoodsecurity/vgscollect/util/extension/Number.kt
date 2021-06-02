package com.verygoodsecurity.vgscollect.util.extension

/** @suppress */
internal fun String.isNumeric(): Boolean = this.toDoubleOrNull() != null