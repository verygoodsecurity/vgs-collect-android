package com.verygoodsecurity.vgscollect.view.card.validation.rules

import com.verygoodsecurity.vgscollect.view.card.validation.ABARoutingNumberValidator
import com.verygoodsecurity.vgscollect.view.card.validation.LengthValidator

class ABARoutingNumberRule private constructor(
    private val lengthValidator: LengthValidator,
    private val checksumValidator: ABARoutingNumberValidator
) : ValidationRule(null, null, null, null) {

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
            val errorMessage = this.errorMsg ?: "ABA routing number is not valid"
            return ABARoutingNumberRule(
                LengthValidator(ABA_ROUTING_NUMBER_LENGTH, ABA_ROUTING_NUMBER_LENGTH, errorMessage),
                ABARoutingNumberValidator(errorMessage)
            )
        }
    }

    companion object {
        private const val ABA_ROUTING_NUMBER_LENGTH = 9
    }
}