package com.verygoodsecurity.demoapp.matchers

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.remote.annotation.RemoteMsgConstructor
import com.verygoodsecurity.vgscollect.widget.CardVerificationCodeEditText
import org.hamcrest.Description

class WithCardCVCStateMatcher @RemoteMsgConstructor internal constructor(
    var str: String
) : BoundedMatcher<View?, CardVerificationCodeEditText>(CardVerificationCodeEditText::class.java) {

    override fun describeTo(description: Description) {
        description.appendText("with card holder input field state: ")
    }

    override fun matchesSafely(textView: CardVerificationCodeEditText): Boolean {
        val state = textView.getState()
        return state?.run {
            str.length == contentLength
        }?:false
    }
}