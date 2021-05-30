package com.verygoodsecurity.vgscollect.util.extension

/** @suppress */
internal fun String.isNumeric(): Boolean = this.toDoubleOrNull() != null

/** @suppress */
internal fun Int.toKb(): Long = this.toLong() * 1024 * 1024