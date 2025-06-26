package com.verygoodsecurity.demoapp.flows

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.StartActivity
import com.verygoodsecurity.demoapp.utils.actions.SetTextAction
import com.verygoodsecurity.demoapp.utils.matchers.withCardCVCState
import com.verygoodsecurity.demoapp.utils.matchers.withCardExpDateState
import com.verygoodsecurity.demoapp.utils.matchers.withCardHolderState
import com.verygoodsecurity.demoapp.utils.matchers.withCardNumberState
import com.verygoodsecurity.demoapp.utils.idling.GlobalIdlingResource
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
import org.hamcrest.core.StringContains
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CollectFragmentTest {

    companion object {
        const val CARD_NUMBER = "4111111111111111"
        const val CARD_HOLDER = "Gohn Galt"
        const val CARD_EXP_DATE= "02/32"
        const val CARD_CVC= "123"

        const val CODE_200= "CODE: 200"
    }

    @get:Rule
    val rule = activityScenarioRule<StartActivity>()

    private lateinit var device: UiDevice

    @Before
    fun prepareDevice() {
        init()
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.setOrientationNatural()
        IdlingRegistry.getInstance().register(GlobalIdlingResource.getResource())
    }

    @After
    fun teardown() {
        release()
        IdlingRegistry.getInstance().unregister(GlobalIdlingResource.getResource())
    }

    @Test
    fun test_add_new_fragment() {
        startMainScreen()

        val responseContainer = interactWithResponseContainer()
        val submitBtn = interactWithSubmitButton()

        val cardInputField = interactWithCardNumber()
        val cardHolderNameInputField = interactWithCardHolderName()
        val cardExpDateInputField = interactWithCardExpDate()
        val cardCVCInputField = interactWithCardCVC()

        cardInputField.perform(SetTextAction(CARD_NUMBER))
        cardHolderNameInputField.perform(SetTextAction(CARD_HOLDER))
        cardExpDateInputField.perform(SetTextAction(CARD_EXP_DATE))
        cardCVCInputField.perform(SetTextAction(CARD_CVC))


        cardInputField.check(matches(withCardNumberState(CARD_NUMBER)))
        cardHolderNameInputField.check(matches(withCardHolderState(CARD_HOLDER)))
        cardExpDateInputField.check(matches(withCardExpDateState(CARD_EXP_DATE)))
        cardCVCInputField.check(matches(withCardCVCState(CARD_CVC)))

        performClick(submitBtn)

        responseContainer.check(matches(withText(containsString(CODE_200))))

        performClick(onView(withId(R.id.details_item)))

        device.pressBack()

        cardInputField.check(matches(withCardNumberState(CARD_NUMBER)))
        cardHolderNameInputField.check(matches(withCardHolderState(CARD_HOLDER)))
        cardExpDateInputField.check(matches(withCardExpDateState(CARD_EXP_DATE)))
        cardCVCInputField.check(matches(withCardCVCState(CARD_CVC)))


        submitBtn.perform(scrollTo())
        performClick(submitBtn)

        responseContainer.check(matches(withText(containsString(CODE_200))))
    }

    @Test
    fun test_submit_flow() {
        startMainScreen()

        val responseContainer  = interactWithResponseContainer()
        val submitBtn  = interactWithSubmitButton()

        val cardInputField = interactWithCardNumber()
        val cardHolderNameInputField = interactWithCardHolderName()
        val cardExpDateInputField = interactWithCardExpDate()
        val cardCVCInputField = interactWithCardCVC()

        cardHolderNameInputField.perform(SetTextAction(CARD_HOLDER))
        cardInputField.perform(SetTextAction(CARD_NUMBER))
        cardCVCInputField.perform(SetTextAction(CARD_CVC))
        cardExpDateInputField.perform(SetTextAction(CARD_EXP_DATE))

        performClick(submitBtn)

        responseContainer.check(matches(withText(StringContains.containsString(CODE_200))))
    }

    private fun interactWithResponseContainer(): ViewInteraction {
        return onView(withId(R.id.responseContainerView))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
    }

    private fun startMainScreen() {
        val startWithActivityBtn = onView(withId(R.id.llCollectFragmentFlow))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        startWithActivityBtn.perform(click())
    }

    private fun interactWithCardCVC(): ViewInteraction {
        val cardCVCField = onView(withId(R.id.cardCVCField)).check(matches(not(isDisplayed())))
        onView(withId(R.id.cardCVCFieldLay)).check(matches(isDisplayed()))

        return cardCVCField
    }

    private fun interactWithCardExpDate(): ViewInteraction {
        val cardExpDateField = onView(withId(R.id.cardExpDateField)).check(matches(not(isDisplayed())))
        onView(withId(R.id.cardExpDateFieldLay)).check(matches(isDisplayed()))

        return cardExpDateField
    }

    private fun interactWithCardHolderName(): ViewInteraction {
        val cardHolderField = onView(withId(R.id.cardHolderField)).check(matches(not(isDisplayed())))
        onView(withId(R.id.cardHolderFieldLay)).check(matches(isDisplayed()))

        return cardHolderField
    }

    private fun interactWithCardNumber(): ViewInteraction {
        val cardInputField = onView(withId(R.id.cardNumberField)).check(matches(not(isDisplayed())))
        onView(withId(R.id.cardNumberFieldLay)).check(matches(isDisplayed()))

        return cardInputField
    }

    private fun interactWithSubmitButton(): ViewInteraction {
        return onView(withId(R.id.submitBtn))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
    }

    private fun performClick(interaction: ViewInteraction) {
        interaction.perform(click())
    }
}