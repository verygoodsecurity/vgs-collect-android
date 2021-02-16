package com.verygoodsecurity.vgscollect.view.card

/**
 * The enum class represents all available type of VGS input fields.
 *
 * @since 1.0.1
 */
enum class FieldType(
    val raw:String
) {

    /**
     * Represents field with card number input in '####-####-####-####' format.
     * The field supports smart detection of different card brands. Some available brands support
     * Luhn algorithm during validation.
     *
     * @see CardType
     */
    CARD_NUMBER("cardNumber"),

    /**
     * Represents field with CVC number. CVC input field depends on CARD_NUMBER field.
     * CVC input field applies limitations defined in the CardType class.
     *
     * @see CardType
     */
    CVC("cvc"),

    /** The input field provides date limitations with format MM/yy . */
    CARD_EXPIRATION_DATE("expDate"),

    /** The input field applies any characters, digits, and space. */
    CARD_HOLDER_NAME("cardHolderName"),

    /** The input field has no limitations.  */
    INFO("info"),

    /** Represents field with Social Security Number (SSN) consists of nine digits,
     * commonly written as three fields separated by hyphens: ###-##-####.
     */
    SSN("ssn"),

}

fun FieldType.getAnalyticName():String {
    return when(this) {
        FieldType.CARD_NUMBER -> "card-number"
        FieldType.CVC -> "card-security-code"
        FieldType.CARD_EXPIRATION_DATE -> "card-expiration-date"
        FieldType.CARD_HOLDER_NAME -> "card-holder-name"
        FieldType.SSN -> "ssn"
        FieldType.INFO -> "text"
    }
}
