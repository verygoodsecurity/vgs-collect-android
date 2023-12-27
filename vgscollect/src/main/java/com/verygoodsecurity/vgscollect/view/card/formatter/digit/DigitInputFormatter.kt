package com.verygoodsecurity.vgscollect.view.card.formatter.digit

import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import com.verygoodsecurity.vgscollect.util.extension.formatDigits
import com.verygoodsecurity.vgscollect.util.extension.replaceIgnoreFilters
import com.verygoodsecurity.vgscollect.view.card.formatter.Formatter

class DigitInputFormatter(private var mask: String) : TextWatcher, Formatter {

    private var formatted: Pair<String, Int?>? = null

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        formatted = s?.toString()?.formatDigits(mask, Selection.getSelectionStart(s))
    }

    override fun afterTextChanged(s: Editable?) {
        formatted?.let {
            if (s?.toString() != it.first) {
                s?.replaceIgnoreFilters(0, s.length, it.first)
                it.second?.let { position -> Selection.setSelection(s, position) }
            }
        }
    }

    override fun setMask(mask: String) {
        this.mask = mask
    }

    override fun getMask(): String = mask
}