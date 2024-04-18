package com.verygoodsecurity.demoapp.utils.matchers.validation

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.remote.annotation.RemoteMsgConstructor
import com.verygoodsecurity.vgscollect.widget.VGSEditText
import org.hamcrest.Description

class WithValidationStateMatcher @RemoteMsgConstructor internal constructor() :
    BoundedMatcher<View?, VGSEditText>(VGSEditText::class.java) {

    override fun describeTo(description: Description) {
        description.appendText("with editText input field state: ")
    }

    override fun matchesSafely(textView: VGSEditText): Boolean {
        val state = textView.getState()
        return state?.isValid ?: false
    }
}