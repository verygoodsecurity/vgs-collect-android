package com.verygoodsecurity.api.nfc.core.content

enum class CommandEnum(
    val cla: Int,
    val ins: Int,
    val p1: Int,
    val p2: Int
) {
    SELECT(0x00, 0xA4, 0x04, 0x00),
    READ_RECORD(0x00, 0xB2, 0x00, 0x00),
    GPO(0x80, 0xA8, 0x00, 0x00)
}