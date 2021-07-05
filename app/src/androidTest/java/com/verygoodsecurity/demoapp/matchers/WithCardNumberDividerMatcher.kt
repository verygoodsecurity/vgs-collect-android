package com.verygoodsecurity.demoapp.matchers

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.remote.annotation.RemoteMsgConstructor
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
import org.hamcrest.Description

class WithCardNumberDividerMatcher @RemoteMsgConstructor internal constructor(
    var divider: Char
) : BoundedMatcher<View?, VGSCardNumberEditText>(VGSCardNumberEditText::class.java) {

    override fun describeTo(description: Description) {
        description.appendText("VGSEditText input field: ")
    }

    override fun matchesSafely(textView: VGSCardNumberEditText): Boolean {
        val state = textView.getState()
        return state?.run {
            divider == textView.getDivider()
        }?:false
    }
}