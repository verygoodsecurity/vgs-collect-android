package com.verygoodsecurity.vgscollect.view.card

import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm

internal const val DEFAULT_CARD_MASK_16 = "#### #### #### ####"
internal const val DEFAULT_CARD_MASK_19 = "#### #### #### #### ###"

/**
 * The type of a card.
 *
 * @param regex The regex used to identify the card type.
 * @param resId The drawable resource ID for the card's icon.
 * @param mask The mask used to format the card number.
 * @param algorithm The checksum algorithm used to validate the card number.
 * @param rangeNumber The valid lengths for the card number.
 * @param rangeCVV The valid lengths for the CVC.
 */
enum class CardType(val regex:String,
                    val resId:Int,
                    val mask:String,
                    val algorithm: ChecksumAlgorithm,
                    val rangeNumber:Array<Int>,
                    val rangeCVV:Array<Int>) {

    /**
     * Elo card type.
     */
    ELO(
        "^(4011(78|79)|43(1274|8935)|45(1416|7393|763(1|2))|50(4175|6699|67[0-7][0-9]|9000)|627780|63(6297|6368)|650(03([^4])|04([0-9])|05(0|1)|4(0[5-9]|3[0-9]|8[5-9]|9[0-9])|5([0-2][0-9]|3[0-8])|9([2-6][0-9]|7[0-8])|541|700|720|901)|651652|655000|655021)",
        R.drawable.ic_elo_dark,
        DEFAULT_CARD_MASK_16,
        ChecksumAlgorithm.LUHN,
        arrayOf(16),
        arrayOf(3)
    ),

    /**
     * Visa Electron card type.
     */
    VISA_ELECTRON(
        "^4(026|17500|405|508|844|91[37])",
        R.drawable.ic_visa_electron_dark,
        DEFAULT_CARD_MASK_16,
        ChecksumAlgorithm.LUHN,
        arrayOf(16),
        arrayOf(3)
    ),

    /**
     * Maestro card type.
     */
    MAESTRO(
        "^(5018|5020|5038|6304|6390[0-9]{2}|67[0-9]{4})",
        R.drawable.ic_maestro_dark,
        DEFAULT_CARD_MASK_19,
        ChecksumAlgorithm.LUHN,
        (12..19).toList().toTypedArray(),
        arrayOf(3)
    ),

    /**
     * Forbrugsforeningen card type.
     */
    FORBRUGSFORENINGEN(
        "^600",
        R.drawable.ic_forbrugsforeningen_dark,
        DEFAULT_CARD_MASK_16,
        ChecksumAlgorithm.LUHN,
        arrayOf(16),
        arrayOf(3)
    ),

    /**
     * Dankort card type.
     */
    DANKORT(
        "^5019",
        R.drawable.ic_dankort_dark,
        DEFAULT_CARD_MASK_16,
        ChecksumAlgorithm.LUHN,
        arrayOf(16),
        arrayOf(3)
    ),

    /**
     * Visa card type.
     */
    VISA(
        "^4",
        R.drawable.ic_visa_dark,
        DEFAULT_CARD_MASK_19,
        ChecksumAlgorithm.LUHN,
        arrayOf(13, 16, 19),
        arrayOf(3)
    ),

    /**
     * Mastercard card type.
     */
    MASTERCARD(
        "^(5[1-5][0-9]{4})|^(222[1-9]|22[3-9]|2[3-6]\\d{2}|27[0-1]\\d|2720)([0-9]{2})",
        R.drawable.ic_mastercard_dark,
        DEFAULT_CARD_MASK_16,
        ChecksumAlgorithm.LUHN,
        arrayOf(16),
        arrayOf(3)
    ),

    /**
     * American Express card type.
     */
    AMERICAN_EXPRESS(
        "^3[47]",
        R.drawable.ic_amex_dark,
        "#### ###### #####",
        ChecksumAlgorithm.LUHN,
        arrayOf(15),
        arrayOf(4)
    ),

    /**
     * Hipercard card type.
     */
    HIPERCARD(
        "^(384100|384140|384160|606282|637095|637568|60(?!11))",
        R.drawable.ic_hipercard_dark,
        DEFAULT_CARD_MASK_19,
        ChecksumAlgorithm.LUHN,
        (14..19).toList().toTypedArray(),
        arrayOf(3)
    ),

    /**
     * Diners Club card type.
     */
    DINCLUB(
        "^3(?:[689]|(?:0[059]+))",
        R.drawable.ic_diners_dark,
        "#### ###### #########",
        ChecksumAlgorithm.LUHN,
        arrayOf(14, 16, 17, 18, 19),
        arrayOf(3)
    ),

    /**
     * Discover card type.
     */
    DISCOVER(
        "^(6011|65|64[4-9]|622)",
        R.drawable.ic_discover_dark,
        DEFAULT_CARD_MASK_19,
        ChecksumAlgorithm.LUHN,
        (16..19).toList().toTypedArray(),
        arrayOf(3)
    ),

    /**
     * UnionPay card type.
     */
    UNIONPAY(
        "^(62)",
        R.drawable.ic_union_pay_dark,
        DEFAULT_CARD_MASK_19,
        ChecksumAlgorithm.NONE,
        (16..19).toList().toTypedArray(),
        arrayOf(3)
    ),

    /**
     * JCB card type.
     */
    JCB(
        "^(2131|1800|35)",
        R.drawable.ic_jcb_dark,
        DEFAULT_CARD_MASK_19,
        ChecksumAlgorithm.LUHN,
        (16..19).toList().toTypedArray(),
        arrayOf(3)
    ),

    /**
     * Unknown card type.
     */
    UNKNOWN(
        "^\$a",
        R.drawable.ic_card_front_preview_light,
        DEFAULT_CARD_MASK_19,
        ChecksumAlgorithm.NONE,
        (13..19).toList().toTypedArray(),
        arrayOf(3,4)
    );
}