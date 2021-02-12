package com.verygoodsecurity.vgscollect.core.model.state

import com.verygoodsecurity.vgscollect.view.card.FieldType

/**
 * Base class definition for a VGS input field state.
 *
 * @since 1.0.1
 */
sealed class FieldState {

    /** The State is true if this input field has focus itself. */
    var hasFocus: Boolean = false
        internal set

    /** The state is true if the content inside the input field is valid. */
    var isValid: Boolean = false
        internal set

    /** The state is true if the input field has no input inside. */
    var isEmpty: Boolean = false
        internal set

    /** The state is true if the input field may be skipped for submitting. */
    var isRequired: Boolean = false
        internal set

    /** The length of this character sequence inside input. */
    var contentLength: Int = 0

    /**
     * The text to be used for data transfer to VGS proxy.
     * Usually, it is similar to field-name in JSON path in your inbound route filters.
     */
    var fieldName: String = ""
        internal set

    /** The type of current VGS input field. */
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

    class SSNNumberState : FieldState() {

        /** The last numbers on card. */
        var last: String? = ""
            internal set

        /** The length of this character sequence inside input.
         * Comparing with contentLength, the contentLengthRaw doesn't count divider symbol.
         */
        var contentLengthRaw: Int = 0


        override fun toString(): String {
            return super.toString() +
                    "last: $last \n"
        }
    }

    /**
     * Class definition for a CardNumber field state.
     *
     * @version 1.0.1
     */
    class CardNumberState : FieldState() {

        /** Bank Identification Number. */
        var bin: String? = ""
            internal set

        /** The last numbers on card. */
        var last: String? = ""
            internal set

        /** The card number. */
        var number: String? = ""
            internal set

        /** The length of this character sequence inside input.
         * Comparing with contentLength, the contentLengthRaw doesn't count divider symbol.
         */
        var contentLengthRaw: Int = 0

        /** The brand of the card. */
        var cardBrand: String? = ""
            internal set

        /** The resource identifier of the detected card brand. */
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
     * Class definition for a CVC field state.
     *
     * @version 1.0.1
     */
    class CVCState : FieldState()

    /**
     * Class definition for a cardHolderName field state.
     *
     * @version 1.0.1
     */
    class CardHolderNameState : FieldState()

    /**
     * Class definition for a Date field state.
     *
     * @version 1.0.1
     */
    class CardExpirationDateState : FieldState()

    /**
     * Class definition for a Info field state.
     *
     * @version 1.0.1
     */
    class InfoState : FieldState()

}
