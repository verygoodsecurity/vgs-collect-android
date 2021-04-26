package com.verygoodsecurity.api.nfc.core

import java.util.regex.Matcher
import java.util.regex.Pattern

class PaymentCardParser(
    regex: String = PAYMENT_CARD_FORMAT_REGEX
) {
    private val regexPattern = Pattern.compile(regex)

    var number: String? = null
    var expirationDate: String? = null
    var serviceCode: String? = null
    var lrc: String? = null

    fun parse(source: String): Boolean {
        val m: Matcher = regexPattern.matcher(source)
        return if(m.find()) {
            number = m.group(1)
            expirationDate = with(m) {
                val year = group(2)?.substring(0, 2)
                val month = group(2)?.substring(2, 4)
                "$month/$year"
            }
            serviceCode = m.group(3)
            lrc = m.group(4)
            true
        } else {
            false
        }
    }


    companion object {
        private const val PAYMENT_CARD_FORMAT_REGEX = "(\\d{1,19})D(\\d{4})(\\d{3})(\\S*)"
    }
}