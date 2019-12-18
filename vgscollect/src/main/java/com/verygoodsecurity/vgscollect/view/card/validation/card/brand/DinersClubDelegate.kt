package com.verygoodsecurity.vgscollect.view.card.validation.card.brand

import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator

class DinersClubDelegate : VGSValidator by DefaultCardValidator("^3(?:0[0-5]|[68][0-9])[0-9]{11}\$")