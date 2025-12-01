package com.verygoodsecurity.vgscollect.view.card.formatter.date

import android.widget.EditText

internal class StrictExpirationDateFormatter(
        source: EditText? = null
) : StrictDateFormatter(source) {

    companion object {
        private const val YEAR_FULL_REGEX = "^[2-9](\\d{0,1}|(0[2-9]|(?!0)\\d{2})\\d?)$"
        private const val YEAR_REGEX = "^([2-9]|[1-9]\\d)$"
    }

    override val yearLongRegex: String
        get() = YEAR_FULL_REGEX

    override val yearShortRegex: String
        get() = YEAR_REGEX
}