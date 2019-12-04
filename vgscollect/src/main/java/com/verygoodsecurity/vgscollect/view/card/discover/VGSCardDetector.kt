package com.verygoodsecurity.vgscollect.view.card.discover

import com.verygoodsecurity.vgscollect.view.text.validation.card.CardType

interface VGSCardDetector {
    fun detect(str:String?):CardType
}