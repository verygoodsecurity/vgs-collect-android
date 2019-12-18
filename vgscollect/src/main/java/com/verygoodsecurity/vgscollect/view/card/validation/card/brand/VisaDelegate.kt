package com.verygoodsecurity.vgscollect.view.card.validation.card.brand

import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator

class VisaDelegate : VGSValidator by DefaultCardValidator("^4[0-9]{12}((?:[0-9]{3})?|(?:[0-9]{6})?)\$")