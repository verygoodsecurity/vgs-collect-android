package com.verygoodsecurity.vgscollect.view.card

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.card.discover.VGSCardDetector
import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator
import com.verygoodsecurity.vgscollect.view.text.validation.card.CardType

class InputCardNumberConnection(
    private val id:Int,
    private val validator: VGSValidator?,
    private val detector: VGSCardDetector?,
    private val IcardBrand: IdrawCardBrand?
): InputRunnable {
    private var stateListener: OnVgsViewStateChangeListener? = null

    private var output = VGSFieldState()

    override fun setOutput(state: VGSFieldState) {
        output = state
    }

    override fun getOutput() = output

    override fun setOnVgsViewStateChangeListener(l: OnVgsViewStateChangeListener?) {
        stateListener = l
        run()
    }

    override fun run() {
        val cardtype = detector?.detect(output.content?.data)?: CardType.NONE
        (output.content as FieldContent.CardNumberContent).cardtype = cardtype

        IcardBrand?.drawCardBrandPreview()

        validator?.clearRules()
        validator?.addRule(cardtype.regex)

        val str = output.content?.data
        if(str.isNullOrEmpty() && !output.isRequired) {
            output.isValid = true
        } else {
            val updatedStr = str?.replace(" ", "")?:""

            val isStrValid = validator?.isValid(updatedStr)?:false
            val isLengthAppropriate = checkLength(cardtype, updatedStr.length)
            output.isValid = isStrValid && isLengthAppropriate
        }

        stateListener?.emit(id, output)
    }

    private fun checkLength(
        cardtype: CardType,
        length: Int?
    ): Boolean {
        return cardtype.rangeNumber.contains(length)
    }

    interface IdrawCardBrand {
        fun drawCardBrandPreview()
    }
}