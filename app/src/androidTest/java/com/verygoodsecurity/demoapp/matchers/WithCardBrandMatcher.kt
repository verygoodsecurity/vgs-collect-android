package com.verygoodsecurity.demoapp.matchers

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.remote.annotation.RemoteMsgConstructor
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
import org.hamcrest.Description

class WithCardBrandMatcher @RemoteMsgConstructor internal constructor(
    var brand: String?
) : BoundedMatcher<View?, VGSCardNumberEditText>(VGSCardNumberEditText::class.java) {

    override fun describeTo(description: Description) {
        description.appendText("with card number input field state: ")
    }

    override fun matchesSafely(textView: VGSCardNumberEditText): Boolean {
        val state = textView.getState()

        return if (!brand.isNullOrEmpty()) {
            checkWithFullNumber(state, brand!!)
        } else {
            false
        }
    }

    private fun checkWithFullNumber(
        state: FieldState.CardNumberState?,
        brand: String
    ): Boolean {
        return state?.run {
            !cardBrand.isNullOrEmpty() && brand.startsWith(cardBrand!!)
        } ?: false
    }
}

