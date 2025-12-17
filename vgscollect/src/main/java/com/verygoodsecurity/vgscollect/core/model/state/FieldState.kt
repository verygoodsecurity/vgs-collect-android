package com.verygoodsecurity.vgscollect.core.model.state

import com.verygoodsecurity.vgscollect.view.card.FieldType

/**
 * Represents the state of a VGS input field. It contains information about the field's content,
 * validity, and other metadata.
 */
sealed class FieldState {

    /** True if the field has focus, false otherwise. */
    var hasFocus: Boolean = false
        internal set

    /** True if the field's contents are valid, false otherwise. */
    var isValid: Boolean = false
        internal set

    /** A list of validation errors. */
    var validationErrors: List<String> = emptyList()
        internal set

    /** True if the field is empty, false otherwise. */
    var isEmpty: Boolean = false
        internal set

    /** True if the field is required, false otherwise. */
    var isRequired: Boolean = false
        internal set

    /** The length of the content in the field. */
    var contentLength: Int = 0

    /**
     * The name of the field used for data transfer to the VGS proxy.
     */
    var fieldName: String = ""
        internal set

    /** The type of the VGS input field. */
    var fieldType: FieldType = FieldType.INFO
        internal set


    override fun toString(): String {
        return "field name: $fieldName, \n" +
                "field type: $fieldType \n" +
                "isEmpty: $isEmpty \n" +
                "contentLength: $contentLength \n" +
                "hasFocus: $hasFocus \n" +
                "isValid: $isValid \n" +
                "isRequired: $isRequired \n"
    }

    /**
     * The state of a Social Security Number (SSN) field.
     */
    class SSNNumberState : FieldState() {

        /** The last 4 digits of the SSN. */
        var last: String? = ""
            internal set

        /**
         * The length of the raw, unformatted SSN.
         */
        var contentLengthRaw: Int = 0


        override fun toString(): String {
            return super.toString() +
                    "last: $last \n"
        }
    }

    /**
     * The state of a card number field.
     */
    class CardNumberState : FieldState() {

        /** The Bank Identification Number (BIN) of the card. */
        var bin: String? = ""
            internal set

        /** The last 4 digits of the card number. */
        var last: String? = ""
            internal set

        /** The full card number. @suppress */
        var number: String? = ""
            internal set

        /**
         * The length of the raw, unformatted card number.
         */
        var contentLengthRaw: Int = 0

        /** The name of the card brand. */
        var cardBrand: String? = ""
            internal set

        /** The resource ID of the card brand's icon. */
        var drawableBrandResId: Int = 0
            internal set

        override fun toString(): String {
            return super.toString() +
                    "bin: $bin \n" +
                    "last: $last \n" +
                    "number: $number \n" +
                    "card brand: $cardBrand \n"
        }
    }

    /**
     * The state of a CVC field.
     */
    class CVCState : FieldState()

    /**
     * The state of a card holder name field.
     */
    class CardHolderNameState : FieldState()

    /**
     * The state of a date field.
     */
    class DateState : FieldState()

    /**
     * The state of a generic text field.
     */
    class InfoState : FieldState()

}
