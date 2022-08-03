package com.verygoodsecurity.vgscollect.view.card.conection

import com.verygoodsecurity.vgscollect.view.card.validation.CompositeValidator

/** @suppress */
internal class InputCardExpDateConnection(
    id: Int,
    validator: CompositeValidator
) : BaseInputConnection(id, validator) {

    override fun getRawContent(content: String?) = content?.trim() ?: ""
}