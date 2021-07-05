package com.verygoodsecurity.demoapp.actions

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.remote.annotation.RemoteMsgField
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.CardBrand
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
import org.hamcrest.Matcher
import java.util.*

class SetCustomBrandAction(
    @RemoteMsgField(order = 0)
    val brand: CardBrand? = null
) : BaseAction() {

    override fun getDescription(): String =
        String.format(Locale.ROOT, "Sets the brand(%s) for input field", brand)

    override fun getConstraints(): Matcher<View> =
        ViewMatchers.isAssignableFrom(InputFieldView::class.java)

    override fun perform(uiController: UiController?, view: View?) {
        uiController?.runAction {
            brand?.apply {
                (view as VGSCardNumberEditText).addCardBrand(brand)
            }
        }
    }
}