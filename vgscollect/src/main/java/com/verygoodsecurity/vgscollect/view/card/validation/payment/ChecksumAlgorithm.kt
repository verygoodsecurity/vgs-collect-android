package com.verygoodsecurity.vgscollect.view.card.validation.payment

/**
 * The checksum algorithm to use for validation.
 */
enum class ChecksumAlgorithm {

    /** The Luhn algorithm. */
    LUHN,

    /** Any checksum algorithm. */
    ANY,

    /** No checksum algorithm. */
    NONE
}