package com.verygoodsecurity.vgscollect.view.card.validation.payment

/**
 * The set of actions for checkSum validation.
 */
enum class ChecksumAlgorithm {

    /** Luhn validation algorithm. */
    LUHN,

    /** Any validation algorithm */
    ANY,

    /** Number will be accepted without any algorithm */
    NONE
}