package com.verygoodsecurity.vgscollect.view.card

/**
 * The enum class represents all available type of VGS input fields.
 *
 * @version 1.0.1
 */
enum class FieldType {

    /**
     * Represents field with card number input in '0000 0000 0000 0000' format.
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

    /** The input field provides date limitations with format mm/yy . */
    CARD_EXPIRATION_DATE,

    /** The input field applies any characters, digits, and space. */
    CARD_HOLDER_NAME,

    /** The input field has no limitations.  */
    INFO
}