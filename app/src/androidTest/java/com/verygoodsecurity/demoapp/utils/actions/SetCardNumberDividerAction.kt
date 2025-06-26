package com.verygoodsecurity.demoapp.utils.actions

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.remote.annotation.RemoteMsgField
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
import org.hamcrest.Matcher
import java.util.*

class SetCardNumberDividerAction(
    @RemoteMsgField(order = 0)
    val divider: Char? = null
) : BaseAction() {

    override fun getDescription(): String = String.format(Locale.ROOT, "Sets the divider(%s) for input field", divider)

    override fun getConstraints(): Matcher<View> =
        ViewMatchers.isAssignableFrom(InputFieldView::class.java)

    override fun perform(uiController: UiController?, view: View?) {
        uiController?.runAction {
            val inputField = view as VGSCardNumberEditText
            inputField.setDivider(divider)
        }
    }
}