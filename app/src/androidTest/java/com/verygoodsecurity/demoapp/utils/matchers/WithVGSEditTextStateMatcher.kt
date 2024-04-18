package com.verygoodsecurity.demoapp.utils.matchers

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.remote.annotation.RemoteMsgConstructor
import com.verygoodsecurity.vgscollect.widget.VGSEditText
import org.hamcrest.Description

class WithVGSEditTextStateMatcher @RemoteMsgConstructor internal constructor(
    var str: String
) : BoundedMatcher<View?, VGSEditText>(VGSEditText::class.java) {

    override fun describeTo(description: Description) {
        description.appendText("VGSEditText input field: ")
    }

    override fun matchesSafely(textView: VGSEditText): Boolean {
        val state = textView.getState()
        return state?.run {
            str.length == contentLength
        }?:false
    }
}