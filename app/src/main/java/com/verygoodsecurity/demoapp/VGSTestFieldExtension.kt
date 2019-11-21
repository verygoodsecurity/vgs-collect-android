package com.verygoodsecurity.demoapp

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

internal fun brokeViewMethodTest(v: View) {
    if(v is ViewGroup) {
        val count = v.childCount

        for(i in 0..count) {
            val v = v.getChildAt(i)
            when(v) {
                is ViewGroup -> brokeViewMethodTest(v)
                is EditText -> hackView(v)
            }
        }
    }
}

private fun hackView(v: EditText) {
    Log.e("test", "getText: ${v.text}")
    v.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            Log.e("test", "hackedView: $p0")
        }
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    })
}