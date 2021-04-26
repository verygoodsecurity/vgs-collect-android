package com.verygoodsecurity.api.nfc.core.content

import com.verygoodsecurity.api.nfc.core.utils.ByteUtil

enum class EMV(
    val id: String
) {
    AID_CARD("4f"),
    APPLICATION_LABEL("50"),
    APPLICATION_TEMPLATE("61"),
    FCI_TEMPLATE("6f"),
    DEDICATED_FILE_NAME("84"),
    SFI("88"),
    FCI_PROPRIETARY_TEMPLATE("a5"),
    TRACK_2_EQV_DATA("57"),
    RECORD_TEMPLATE("70"),
    RESPONSE_MESSAGE_TEMPLATE_2("77"),
    RESPONSE_MESSAGE_TEMPLATE_1("80"),
    APPLICATION_INTERCHANGE_PROFILE("82"),
    COMMAND_TEMPLATE("83"),
    ISSUER_PUBLIC_KEY_CERT("90"),
    APPLICATION_FILE_LOCATOR("94"),
    PDOL("df8111"),
    LOG_ENTRY("9f4d"),
    FCI_ISSUER_DISCRETIONARY_DATA("bf0c"),
    VISA_LOG_ENTRY("df60"),
    TRACK1_DATA("56"),
    TERMINAL_TRANSACTION_QUALIFIERS("9f66"),
    TRACK2_DATA("9f6b"),
    KERNEL_IDENTIFIER("9f2a"),
    MAG_STRIPE_APP_VERSION_NUMBER_CARD("9f6c"),
    PCVC3_TRACK1("9f62"),
    PUNTAC_TRACK1("9f63"),
    NATC_TRACK1("9f64"),
    PCVC_TRACK2("9f65"),
    NATC_TRACK2("9f67"),
    TORN_RECORD("ff8101"),
    TAGS_TO_WRITE_BEFORE_GEN_AC("ff8102"),
    TAGS_TO_WRITE_AFTER_GEN_AC("ff8103"),
    DATA_TO_SEND("ff8104"),
    DATA_RECORD("ff8105"),
    DISCRETIONARY_DATA("ff8106"),
    UNKNOWN("00")
}

private fun EMV.matchBitByBitIndex(pVal: Int, pBitIndex: Int): Boolean {
    return if (pBitIndex in 0..31) {
        pVal and 1 shl pBitIndex != 0
    } else {
        throw IllegalArgumentException("parameter \'pBitIndex\' must be between 0 and 31. pBitIndex=$pBitIndex")
    }
}

fun EMV.isConstructed(): Boolean {
    return ByteUtil.matchBitByBitIndex(ByteUtil.fromString(id)[0].toInt(), 5)
}