package com.verygoodsecurity.vgscollect.view.card.formatter.date

import android.widget.EditText

internal class StrictExpirationDateFormatter(
        source: EditText? = null
) : StrictDateFormatter(source) {

    companion object {
        private const val YEAR_FULL_REGEX = "^([2]|2[0]|20[234]|20[2][123456789]|20[34][0123456789])\$"
        private const val YEAR_REGEX = "^([234]|2[123456789]|[34]\\d)\$"
    }

    override val yearLongRegex: String
        get() = YEAR_FULL_REGEX

    override val yearShortRegex: String
        get() = YEAR_REGEX
}