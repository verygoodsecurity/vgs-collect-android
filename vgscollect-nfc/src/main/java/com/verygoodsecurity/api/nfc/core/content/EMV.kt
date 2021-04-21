package com.verygoodsecurity.api.nfc.core.content

import com.verygoodsecurity.api.nfc.core.utils.matchBitByBitIndex
import com.verygoodsecurity.api.nfc.core.utils.toHexByteArray

enum class EMV(
    val id: String,
    val valueType: EMVFormat
) {
    /**
     * Short File Identifier (SFI)
     */
    SFI("88", EMVFormat.BINARY),
    APPLICATION_LABEL("50", EMVFormat.TEXT),

    APPLICATION_TEMPLATE("61", EMVFormat.BINARY),
    FCI_TEMPLATE("6f", EMVFormat.BINARY),

    DEDICATED_FILE_NAME("84", EMVFormat.BINARY),//-124

    VISA_LOG_ENTRY("df60", EMVFormat.BINARY),
    LOG_ENTRY("9f4d", EMVFormat.BINARY),
    PDOL("df8111", EMVFormat.DOL),
    COMMAND_TEMPLATE("83", EMVFormat.BINARY),
    RESPONSE_MESSAGE_TEMPLATE_1("80", EMVFormat.BINARY),
    APPLICATION_FILE_LOCATOR("94", EMVFormat.BINARY),

    AID_CARD("4f", EMVFormat.BINARY),
    KERNEL_IDENTIFIER("9f2a", EMVFormat.BINARY),

    FCI_PROPRIETARY_TEMPLATE("a5", EMVFormat.BINARY),

    FCI_ISSUER_DISCRETIONARY_DATA("bf0c", EMVFormat.BINARY),

    UNKNOWN("00", EMVFormat.BINARY)
}


fun EMV.isConstructed(): Boolean = id.toHexByteArray()[0].toInt().matchBitByBitIndex(5)