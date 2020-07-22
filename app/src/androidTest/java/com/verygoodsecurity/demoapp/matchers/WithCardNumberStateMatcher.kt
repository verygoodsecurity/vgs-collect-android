package com.verygoodsecurity.demoapp.matchers

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.remote.annotation.RemoteMsgConstructor
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
import org.hamcrest.Description

class WithCardNumberStateMatcher @RemoteMsgConstructor internal constructor(
    var str: String
) : BoundedMatcher<View?, VGSCardNumberEditText> (VGSCardNumberEditText::class.java) {

    override fun describeTo(description: Description) {
        description.appendText("with card number input field state: ")
    }

    override fun matchesSafely(textView: VGSCardNumberEditText): Boolean {
        val state = textView.getState()
        return state?.run {
            !bin.isNullOrEmpty() && !last.isNullOrEmpty() &&
                    str.startsWith(bin!!) &&
                    str.endsWith(last!!)
        }?:false
    }
}

