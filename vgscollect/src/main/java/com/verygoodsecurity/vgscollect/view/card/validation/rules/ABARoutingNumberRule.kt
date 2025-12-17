package com.verygoodsecurity.vgscollect.view.card.validation.rules

import com.verygoodsecurity.vgscollect.view.card.validation.ABARoutingNumberValidator

class ABARoutingNumberRule private constructor(
    validator: ABARoutingNumberValidator
) : ValidationRule(null, null, null, null, validator) {

    class ValidationBuilder {

        private var errorMsg: String? = null

        /**
         * Sets the error message to be displayed if the validation fails.
         *
         * @param errorMsg The error message.
         */
        fun setErrorMsg(errorMsg: String) = this.apply {
            this.errorMsg = errorMsg
        }

        /**
         * Builds the validation rule.
         */
        fun build(): ABARoutingNumberRule {
            val validator = ABARoutingNumberValidator(
                errorMsg = errorMsg ?: ABARoutingNumberValidator.DEFAULT_ERROR_MSG
            )
            return ABARoutingNumberRule(validator)
        }
    }

    companion object {
        private const val ABA_ROUTING_NUMBER_LENGTH = 9
    }
}