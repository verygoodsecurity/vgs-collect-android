package com.verygoodsecurity.demoapp.tests.flows

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.*
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.StartActivity
import com.verygoodsecurity.demoapp.Utils
import com.verygoodsecurity.demoapp.actions.SetTextAction
import com.verygoodsecurity.demoapp.matchers.withCardCVCState
import com.verygoodsecurity.demoapp.matchers.withCardExpDateState
import com.verygoodsecurity.demoapp.matchers.withCardHolderState
import com.verygoodsecurity.demoapp.matchers.withCardNumberState
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
import org.hamcrest.core.StringContains
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FragmentCaseInstrumentedTest {

    companion object {
        const val CARD_NUMBER = "4111111111111111"
        const val CARD_NUMBER_WRONG_1 = "41111111QW"
        const val CARD_NUMBER_WRONG_BIN_CHECK = "411111"
        const val CARD_NUMBER_WRONG_LAST_CHECK = "1111"
        const val CARD_NUMBER_WRONG_2 = "41111111111111112"

        const val CARD_HOLDER = "Gohn Galt"

        const val CARD_EXP_DATE_WRONG = "99/9999"
        const val CARD_EXP_DATE= "02/32"

        const val CARD_CVC_WRONG = "12"
        const val CARD_CVC= "123"

        const val CODE_200= "CODE: 200"
        const val CODE_1001= "CODE: 1001"
    }

    @get:Rule
    val rule = activityScenarioRule<StartActivity>()

    private lateinit var device: UiDevice

    @Before
    fun prepareDevice() {
        init()
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @After
    fun teardown() {
        release()
    }

    @Test
    fun test_scan_card() {
        startMainScreen()

        interactWithScanner()

        val submitBtn  = interactWithSubmitButton()
        performClick(submitBtn)

        pauseTestFor(7000)

        val responseContainer  = interactWithResponseContainer()
        responseContainer.check(matches(withText(containsString(ActivityCaseInstrumentedTest.CODE_200))))
    }

    private fun interactWithScanner() = apply {
        val intent = Intent()
        val card = CreditCard("4111111111111111", 5,33, "123", null, "John B")
        intent.putExtra(CardIOActivity.EXTRA_SCAN_RESULT, card)


        intending(hasComponent(CardIOActivity::class.qualifiedName))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, intent))

        performClick(onView(withId(R.id.scan_card)))
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


        pauseTestFor(500)
        cardInputField.check(matches(withCardNumberState(CARD_NUMBER)))
        cardHolderNameInputField.check(matches(withCardHolderState(CARD_HOLDER)))
        cardExpDateInputField.check(matches(withCardExpDateState(CARD_EXP_DATE)))
        cardCVCInputField.check(matches(withCardCVCState(CARD_CVC)))

        performClick(submitBtn)

        pauseTestFor(7000)
        responseContainer.check(matches(withText(containsString(CODE_200))))

        performClick(onView(withId(R.id.details_item)))

        pauseTestFor(500)

        device.pressBack()

        pauseTestFor(500)
        cardInputField.check(matches(withCardNumberState(CARD_NUMBER)))
        cardHolderNameInputField.check(matches(withCardHolderState(CARD_HOLDER)))
        cardExpDateInputField.check(matches(withCardExpDateState(CARD_EXP_DATE)))
        cardCVCInputField.check(matches(withCardCVCState(CARD_CVC)))


        submitBtn.perform(scrollTo())
        performClick(submitBtn)

        pauseTestFor(7000)

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

        pauseTestFor(10000)
        responseContainer.check(matches(withText(StringContains.containsString(CODE_200))))
    }

    private fun interactWithResponseContainer(): ViewInteraction {
        return onView(withId(R.id.responseContainerView))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
    }

    private fun startMainScreen() {
        onView(withId(R.id.tiedVaultId)).perform(typeText(Utils.DEFAULT_TENANT_ID))
        onView(withId(R.id.tiedPath)).perform(typeText(Utils.DEFAULT_PATH), closeSoftKeyboard())

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
        pauseTestFor(300)
        interaction.perform(click())
    }

    private fun pauseTestFor(milliseconds: Long) {
        try {
            Thread.sleep(milliseconds)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}