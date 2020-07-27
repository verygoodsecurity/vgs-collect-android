package com.verygoodsecurity.demoapp.matchers

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.remote.annotation.RemoteMsgConstructor
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
import org.hamcrest.Description

class WithCardNumberStateMatcher @RemoteMsgConstructor internal constructor(
    var str: String?,
    var bin: String?,
    var last: String?
) : BoundedMatcher<View?, VGSCardNumberEditText> (VGSCardNumberEditText::class.java) {

    override fun describeTo(description: Description) {
        description.appendText("with card number input field state: ")
    }

    override fun matchesSafely(textView: VGSCardNumberEditText): Boolean {
        val state = textView.getState()

        return when {
            !str.isNullOrEmpty() -> checkWithFullNumber(state, str!!)
            !bin.isNullOrEmpty() && last.isNullOrEmpty() -> checkWithBinAndLast(state, bin?:"", last?:"")
            else -> false
        }
    }

    private fun checkWithBinAndLast(
        state: FieldState.CardNumberState?,
        bin: String,
        last: String
    ): Boolean {
        return state?.run {
            bin.startsWith(this@run.bin!!) && last.endsWith(this@run.last!!)
        }?:false
    }

    private fun checkWithFullNumber(
        state: FieldState.CardNumberState?,
        str: String
    ): Boolean {
        return state?.run {
            !bin.isNullOrEmpty() && !last.isNullOrEmpty() &&
                    str.startsWith(bin!!) &&
                    str.endsWith(last!!)
        }?:false
    }
}

