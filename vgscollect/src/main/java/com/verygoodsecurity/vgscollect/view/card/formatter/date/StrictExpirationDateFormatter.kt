package com.verygoodsecurity.vgscollect.view.card.formatter.date

internal class StrictExpirationDateFormatter : StrictDateFormatter() {

    companion object {
        private const val YEAR_FULL_REGEX = "^([2]|2[0]|20[23]|20[23][0123456789])\$"
        private const val YEAR_REGEX = "^([23]|[123]\\d)\$"
    }

    override val yearLongRegex: String
        get() = YEAR_FULL_REGEX

    override val yearShortRegex: String
        get() = YEAR_REGEX
}