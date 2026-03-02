package com.verygoodsecurity.demoapp.utils.actions

import android.os.SystemClock
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.remote.annotation.RemoteMsgField
import com.verygoodsecurity.vgscollect.view.InputFieldView
import org.hamcrest.Matcher
import java.util.Locale

class TypeTextAction(
    @RemoteMsgField(order = 0)
    private val text: String? = null,
    @RemoteMsgField(order = 1)
    private val delayMs: Long = 0L
) : BaseAction() {

    override fun getDescription(): String {
        return String.format(Locale.ROOT, "Types text(%s) into input field", text)
    }

    override fun getConstraints(): Matcher<View> = isAssignableFrom(InputFieldView::class.java)

    override fun perform(uiController: UiController?, view: View?) {
        uiController?.runAction {
            val inputField = view as InputFieldView
            inputField.requestFocus()
            val value = text.orEmpty()
            val builder = StringBuilder()
            value.forEach { ch ->
                builder.append(ch)
                inputField.setText(builder.toString())
                uiController.loopMainThreadUntilIdle()
                if (delayMs > 0) {
                    SystemClock.sleep(delayMs)
                }
            }
        }
    }
}

