package com.verygoodsecurity.vgscollect.view.card

/**
 * The enum class represents all available type of VGS input fields.
 *
 * @since 1.0.1
 */
enum class FieldType {

    /** Represents field with Social Security Number (SSN) consists of nine digits,
     * commonly written as three fields separated by hyphens: ###-##-####.
     */
    SSN,

    /**
     * Represents field with card number input in '####-####-####-####' format.
     * The field supports smart detection of different card brands. Some available brands support
     * Luhn algorithm during validation.
     *
     * @see CardType
     */
    CARD_NUMBER,

    /**
     * Represents field with CVC number. CVC input field depends on CARD_NUMBER field.
     * CVC input field applies limitations defined in the CardType class.
     *
     * @see CardType
     */
    CVC,

    /** The input field provides date limitations with format MM/yy . */
    CARD_EXPIRATION_DATE,

    /** The input field applies any characters, digits, and space. */
    CARD_HOLDER_NAME,

    /** The input field has no limitations.  */
    INFO
}