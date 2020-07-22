package com.verygoodsecurity.demoapp.actions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.remote.annotation.RemoteMsgField
import com.verygoodsecurity.vgscollect.view.InputFieldView
import org.hamcrest.Matcher
import java.util.*


class RequestFocusAction: ViewAction {

    override fun getDescription(): String = String.format(Locale.ROOT, "Request focus for input field")

    override fun getConstraints(): Matcher<View> = isAssignableFrom(InputFieldView::class.java)

    override fun perform(uiController: UiController?, view: View?) {
        uiController?.runAction {
            val inputField = view as InputFieldView
            inputField.requestFocus()

        }
    }

    public fun hideSoftKeyboard( view:View ) {
        val softKeyboardService:InputMethodManager = view.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        softKeyboardService.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun UiController.runAction(func:() -> Unit ) {
        loopMainThreadUntilIdle()
        func()
        loopMainThreadUntilIdle()
    }
}