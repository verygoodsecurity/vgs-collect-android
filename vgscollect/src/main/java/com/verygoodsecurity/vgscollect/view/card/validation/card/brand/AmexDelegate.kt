package com.verygoodsecurity.vgscollect.view.card.validation.card.brand

import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator

class AmexDelegate : VGSValidator by DefaultCardValidator("^3[47][0-9]{13}\$")