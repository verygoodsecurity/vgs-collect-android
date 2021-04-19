package com.verygoodsecurity.api.nfc.utils.extensions

import com.verygoodsecurity.api.nfc.core.model.Card
import com.verygoodsecurity.vgscollect.app.mapper.VGSCard

// TODO: Add holderName & cvc
internal fun Card.toVGSCard() = VGSCard(number, "", date, "")