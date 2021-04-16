package com.verygoodsecurity.api.nfc

import com.verygoodsecurity.api.nfc.core.model.Card

/** Provides mapping between views and data. */
class VGSNFCDataMapper private constructor(
    private val cardNumberFieldName: String?,
    private val expirationDateFieldName: String?,
) {

    internal fun map(card: Card): Map<String, String> {
        val result = mutableMapOf<String, String>()
        cardNumberFieldName?.let { result[it] = card.number }
        expirationDateFieldName?.let { result[it] = card.date }
        return result
    }

    /** VGSNFCDataMapper builder class */
    class Builder {

        private var cardNumberFieldName: String? = null

        private var expirationDateFieldName: String? = null

        /**
         * Specify field name of card number view that should be updated.
         */
        fun setCardNumber(fieldName: String?) = this.apply {
            cardNumberFieldName = fieldName
        }

        /**
         * Specify field name of card expiration date view that should be updated.
         */
        fun setExpirationDate(fieldName: String?) = this.apply {
            expirationDateFieldName = fieldName
        }

        /** Create instance of [VGSNFCDataMapper] */
        fun build() = VGSNFCDataMapper(cardNumberFieldName, expirationDateFieldName)
    }
}