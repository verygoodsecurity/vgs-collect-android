package com.verygoodsecurity.vgscollect.view.card.formatter

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgscollect.util.extension.formatToMask
import com.verygoodsecurity.vgscollect.util.extension.replaceIgnoreFilters

class CardNumberFormatter(val editText: EditText) : TextWatcher, Formatter {

    companion object {

        private const val DEFAULT_MASK = "#### #### #### #### ###"
    }

    private var mask: String = DEFAULT_MASK

    private var runtimeData = ""

    override fun setMask(mask: String) {
        this.mask = mask
    }

    fun onSelectionChanged(selStart: Int, selEnd: Int) {
        println("TEST:DD, onSelectionChanged, selStart = $selStart, selEnd = $selEnd")
    }

    override fun afterTextChanged(s: Editable?) {
        println("TEST:DD, afterTextChanged, text = ${s.toString()}, runtime = $runtimeData, position = ${editText.selectionStart}")
        if (s.toString() != runtimeData) {
            println("TEST:DD, replace")
            s?.replaceIgnoreFilters(0, s.length, runtimeData)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        println("TEST:DD, beforeTextChanged, text = $s, position = ${editText.selectionStart}")
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        runtimeData = s.toString().formatToMask(mask)
        println("TEST:DD, onTextChanged, text = $s, runtime = $runtimeData, position = ${editText.selectionStart}")
    }

    override fun getMask(): String = mask

    @VisibleForTesting
    fun getMaskLength(): Int = mask.length
}