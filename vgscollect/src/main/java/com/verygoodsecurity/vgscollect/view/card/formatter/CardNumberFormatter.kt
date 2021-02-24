package com.verygoodsecurity.vgscollect.view.card.formatter

import android.text.Editable
import android.text.TextWatcher
import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgscollect.util.extension.formatToMask

class CardNumberFormatter: TextWatcher, Formatter {

    companion object {
        private const val DEFAULT_MASK = "#### #### #### #### ###"
    }

    private var mask:String = DEFAULT_MASK

    private var runtimeData = ""

    override fun setMask(mask: String) {
        this.mask = mask
    }

    override fun afterTextChanged(s: Editable?) {
        s?.apply {
            if(s.toString() != runtimeData) {
                replace(0, s.length, runtimeData)
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        do {
            val primaryStr = runtimeData
            runtimeData = s.toString().formatToMask(mask)
        } while (primaryStr != runtimeData)
    }

    override fun getMask():String = mask

    @VisibleForTesting
    fun getMaskLength():Int = mask.length
}