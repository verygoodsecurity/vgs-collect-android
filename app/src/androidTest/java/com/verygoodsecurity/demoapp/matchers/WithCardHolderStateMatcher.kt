package com.verygoodsecurity.demoapp.matchers

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.remote.annotation.RemoteMsgConstructor
import com.verygoodsecurity.vgscollect.widget.PersonNameEditText
import org.hamcrest.Description

class WithCardHolderStateMatcher @RemoteMsgConstructor internal constructor(
    var str: String
) : BoundedMatcher<View?, PersonNameEditText>(PersonNameEditText::class.java) {

    override fun describeTo(description: Description) {
        description.appendText("with card holder input field state: ")
    }

    override fun matchesSafely(textView: PersonNameEditText): Boolean {
        val state = textView.getState()
        return state?.run {
            str.length == contentLength
        }?:false
    }
}