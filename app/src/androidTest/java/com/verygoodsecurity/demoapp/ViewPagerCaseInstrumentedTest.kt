package com.verygoodsecurity.demoapp

import android.widget.DatePicker
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.verygoodsecurity.demoapp.actions.FieldClickAction
import com.verygoodsecurity.demoapp.actions.SetTextAction
import com.verygoodsecurity.demoapp.matchers.withCardCVCState
import com.verygoodsecurity.demoapp.matchers.withCardExpDateState
import org.hamcrest.Matchers
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ViewPagerCaseInstrumentedTest {

    companion object {
        const val CARD_NUMBER = "4111111111111111"
        const val CARD_NUMBER_WRONG_1 = "41111111QW"
        const val CARD_NUMBER_WRONG_2 = "41111111111111112"

        const val CARD_HOLDER = "Gohn Galt"

        const val CARD_EXP_DATE= "02/23"
        const val CARD_EXP_DATE_Y= 2023
        const val CARD_EXP_DATE_M= 2
        const val CARD_EXP_DATE_D= 28

        const val CARD_CVC_WRONG = "1212"
        const val CARD_CVC= "121"
    }

    @get:Rule
    val rule = activityScenarioRule<StartActivity>()


    @Test
    fun test_submit_flow() {
        startMainScreen()
        val next = interactWithNextButton()
        onView(withId(R.id.backBtn)).check(matches(not(isDisplayed())))
        next.check(matches(withText("Next")))

        val cardNumber = interactWithCardNumber()

        cardNumber.perform(SetTextAction(CARD_NUMBER_WRONG_1))
        performClick(next)
        onView(withId(R.id.cardNumberFieldLay)).check(matches(isDisplayed()))

        cardNumber.perform(SetTextAction(CARD_NUMBER_WRONG_2))
        performClick(next)
        onView(withId(R.id.cardNumberFieldLay)).check(matches(isDisplayed()))

        cardNumber.perform(SetTextAction(CARD_NUMBER))
        performClick(next)
        onView(withId(R.id.backBtn)).check(matches(isDisplayed()))
        next.check(matches(withText("Next")))

        val cardHolder = interactWithCardHolderName()
        cardHolder.perform(SetTextAction(CARD_HOLDER))
        performClick(next)
        onView(withId(R.id.backBtn)).check(matches(isDisplayed()))

        next.check(matches(withText("Submit")))

        val expDate = interactWithCardExpDate()
        expDate.perform(SetTextAction(CARD_EXP_DATE))
        expDate.check(matches(withCardExpDateState(CARD_EXP_DATE)))

        val cvc = interactWithCardCVC()
        cvc.perform(SetTextAction(CARD_CVC_WRONG))
        cvc.check(matches(withCardCVCState(CARD_CVC)))

        pauseTestFor(3000)
    }

    @Test
    fun test_submit_flow_with_expiration_date_click() {
        startMainScreen()
        val next = interactWithNextButton()
        onView(withId(R.id.backBtn)).check(matches(not(isDisplayed())))
        next.check(matches(withText("Next")))

        val cardNumber = interactWithCardNumber()

        cardNumber.perform(SetTextAction(CARD_NUMBER_WRONG_1))
        performClick(next)
        onView(withId(R.id.cardNumberFieldLay)).check(matches(isDisplayed()))

        cardNumber.perform(SetTextAction(CARD_NUMBER_WRONG_2))
        performClick(next)
        onView(withId(R.id.cardNumberFieldLay)).check(matches(isDisplayed()))

        cardNumber.perform(SetTextAction(CARD_NUMBER))
        performClick(next)
        onView(withId(R.id.backBtn)).check(matches(isDisplayed()))
        next.check(matches(withText("Next")))

        val cardHolder = interactWithCardHolderName()
        cardHolder.perform(SetTextAction(CARD_HOLDER))
        performClick(next)
        onView(withId(R.id.backBtn)).check(matches(isDisplayed()))

        next.check(matches(withText("Submit")))

        val expDate = interactWithCardExpDate()
        expDate.perform(FieldClickAction())

        pauseTestFor(300)

        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name)))
            .perform(PickerActions.setDate(CARD_EXP_DATE_Y, CARD_EXP_DATE_M, CARD_EXP_DATE_D))

        pauseTestFor(3000)

        val mBtn = onView(Matchers.allOf(withId(android.R.id.button1), withText("Done")))
        performClick(mBtn)

        expDate.check(matches(withCardExpDateState(CARD_EXP_DATE)))

        val cvc = interactWithCardCVC()
        cvc.perform(SetTextAction(CARD_CVC_WRONG))
        cvc.check(matches(withCardCVCState(CARD_CVC)))

        pauseTestFor(3000)
    }

    private fun performClick(interaction: ViewInteraction) {
        pauseTestFor(200)
        interaction.perform(click())
    }


    private fun interactWithCardCVC(): ViewInteraction {
        val cardCVCField = onView(withId(R.id.cardCVCField))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.cardCVCFieldLay))
            .check(matches(isDisplayed()))

        return cardCVCField
    }

    private fun interactWithCardExpDate(): ViewInteraction {
        val cardExpDateField = onView(withId(R.id.cardExpDateField))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.cardExpDateFieldLay))
            .check(matches(isDisplayed()))

        return cardExpDateField
    }

    private fun interactWithCardHolderName(): ViewInteraction {
        val cardHolderField = onView(withId(R.id.cardHolderField))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.cardHolderFieldLay))
            .check(matches(isDisplayed()))

        return cardHolderField
    }

    private fun interactWithCardNumber(): ViewInteraction {
        val cardInputField = onView(withId(R.id.cardNumberField))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.cardNumberFieldLay))
            .check(matches(isDisplayed()))

        return cardInputField
    }

    private fun startMainScreen() {
        val startWithActivityBtn = onView(withId(R.id.startWithViewPagerBtn))
            .check(matches(isDisplayed()))

        startWithActivityBtn.perform(click())
    }

    private fun interactWithNextButton(): ViewInteraction {
        return onView(withId(R.id.nextBtn))
            .check(matches(isDisplayed()))
    }

    private fun interactWithBackButton(): ViewInteraction {
        return onView(withId(R.id.backBtn))
            .check(matches(isDisplayed()))
    }

    private fun pauseTestFor(milliseconds: Long) {
        try {
            Thread.sleep(milliseconds)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}