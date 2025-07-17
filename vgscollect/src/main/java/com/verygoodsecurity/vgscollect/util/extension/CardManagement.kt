package com.verygoodsecurity.vgscollect.util.extension

import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.view.core.serializers.VGSExpDateSeparateSerializer
import com.verygoodsecurity.vgscollect.widget.CardVerificationCodeEditText
import com.verygoodsecurity.vgscollect.widget.ExpirationDateEditText
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText

private const val CMP_CARD_NUMBER_FIELD_NAME = "pan"
private  const val CMP_CARD_CVC_FIELD_NAME = "cvc"
private  const val CMP_CARD_EXPIRY_FIELD_NAME = "exp_date"
private  const val CMP_CARD_EXPIRY_MONTH_FIELD_NAME = "exp_month"
private  const val CMP_CARD_EXPIRY_YEAR_FIELD_NAME = "exp_year"
private  const val CMP_CARD_EXPIRY_OUTPUT_FORMAT = "MM/yy"

/**
 * Binds the card number input view and configures it for Card Management requests.
 *
 * @param view The [VGSCardNumberEditText] instance used to input the card number.
 */

fun VGSCollect.cardNumber(view: VGSCardNumberEditText) {
    view.setFieldName(CMP_CARD_NUMBER_FIELD_NAME)
    view.enableValidation(true)
    view.setIsRequired(true)
    bindView(view)
}

/**
 * Binds the card expiration date input view and configures it for Card Management requests.
 *
 * The expiration date will be serialized into separate `exp_month` and `exp_year` fields.
 * The visible format will be `MM/yy`.
 *
 * @param view The [ExpirationDateEditText] instance used to input the expiration date.
 */
fun VGSCollect.cardExpirationDate(view: ExpirationDateEditText) {
    view.setFieldName(CMP_CARD_EXPIRY_FIELD_NAME)
    view.enableValidation(true)
    view.setIsRequired(true)
    view.setOutputRegex(CMP_CARD_EXPIRY_OUTPUT_FORMAT)
    view.setSerializer(VGSExpDateSeparateSerializer(
        monthFieldName = CMP_CARD_EXPIRY_MONTH_FIELD_NAME,
        yearFieldName = CMP_CARD_EXPIRY_YEAR_FIELD_NAME
    ))
    bindView(view)
}

/**
 * Binds the card CVC input view and configures it for Card Management requests.
 *
 * @param view The [CardVerificationCodeEditText] instance used to input the card's CVC code.
 */
fun VGSCollect.cardCVC(view: CardVerificationCodeEditText) {
    view.setFieldName(CMP_CARD_CVC_FIELD_NAME)
    view.enableValidation(true)
    view.setIsRequired(true)
    bindView(view)
}
