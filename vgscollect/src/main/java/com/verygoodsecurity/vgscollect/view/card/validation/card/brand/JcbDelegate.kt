package com.verygoodsecurity.vgscollect.view.card.validation.card.brand

import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator

class JcbDelegate : VGSValidator by DefaultCardValidator("^(?:2131|1800|35\\d{3})\\d{11,14}\$")