package com.verygoodsecurity.vgscollect.view.date.connection

import com.verygoodsecurity.vgscollect.view.card.conection.BaseInputConnection
import com.verygoodsecurity.vgscollect.view.card.validation.CompositeValidator

internal class InputDateRangeConnection(
    id: Int,
    validator: CompositeValidator
) : BaseInputConnection(id, validator) {

    override fun getRawContent(content: String?) = content?.trim() ?: ""
}