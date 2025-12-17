package com.verygoodsecurity.vgscollect.view.card.validation

/**
 * ABA Routing Number checksum validation.
 *
 * @param errorMsg The error message to be returned if validation fails.
 */
class ABARoutingNumberValidator(
    override val errorMsg: String = DEFAULT_ERROR_MSG
): VGSValidator {

    /**
     * Validates the given ABA routing number.
     *
     * @param content The content to validate.
     * @return True if the content is a valid ABA routing number, false otherwise.
     */
    override fun isValid(content: String): Boolean {
        if (content.isEmpty() || !content.matches(Regex("^\\d{9}$"))) {
            return false
        }
        val digits = content.map { it.digitToInt() }
        val sum = 3 * (digits[0] + digits[3] + digits[6]) +
                7 * (digits[1] + digits[4] + digits[7]) +
                1 * (digits[2] + digits[5] + digits[8])

        return sum != 0 && sum % 10 == 0
    }

    internal companion object {

        internal const val DEFAULT_ERROR_MSG = "is not a valid ABA routing number"
    }
}