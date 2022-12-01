package com.verygoodsecurity.demoapp.tests.flows

import android.app.Activity
import android.app.Instrumentation.ActivityResult
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
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.StartActivity
import com.verygoodsecurity.demoapp.Utils
import com.verygoodsecurity.demoapp.actions.SetTextAction
import com.verygoodsecurity.demoapp.collect_activity.CollectActivity
import com.verygoodsecurity.demoapp.matchers.withCardCVCState
import com.verygoodsecurity.demoapp.matchers.withCardExpDateState
import com.verygoodsecurity.demoapp.matchers.withCardHolderState
import com.verygoodsecurity.demoapp.matchers.withCardNumberState
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.core.StringContains.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ActivityCaseInstrumentedTest {

    companion object {
        const val CARD_NUMBER = "4111111111111111"
        const val CARD_NUMBER_WRONG_1 = "41111111QW"
        const val CARD_NUMBER_WRONG_2 = "41111111111111112"

        const val CARD_HOLDER_WRONG = "Gohn Galt, I."
        const val CARD_HOLDER = "Gohn G"

        const val CARD_EXP_DATE_WRONG = "22/2222"
        const val CARD_EXP_DATE = "02/25"

        const val CARD_CVC_WRONG = "12"
        const val CARD_CVC = "123"

        const val POSTAL_WRONG = "12"
        const val POSTAL = "12345"

        const val CITY = "new city"

        const val CODE_200 = "CODE: 200"
        const val CODE_1001 = "CODE: 1001"
    }

    @get:Rule
    val rule = activityScenarioRule<StartActivity>()

    private lateinit var device: UiDevice

    @Before
    fun prepareDevice() {
        init()
        device = UiDevice.getInstance(getInstrumentation())
        device.setOrientationNatural()
    }

    @After
    fun teardown() {
        release()
    }

    @Test
    fun test_scan_card() {
        startMainScreen()
        intended(hasComponent(CollectActivity::class.qualifiedName))

        interactWithScanner()

        val submitBtn = interactWithSubmitButton()
        performClick(submitBtn)

        pauseTestFor(7000)

        interactWithResponseContainer().check(matches(withText(containsString(CODE_200))))
    }


    fun interactWithScanner() = apply {
        val intent = Intent()
        val card = CreditCard("4111111111111111", 5, 2033, "123", null, "John B")
        intent.putExtra(CardIOActivity.EXTRA_SCAN_RESULT, card)


        intending(hasComponent(CardIOActivity::class.qualifiedName))
            .respondWith(ActivityResult(Activity.RESULT_OK, intent))

        onView(withId(R.id.scan_card)).perform(click())
    }

    @Test
    fun test_rotation() {
        assertThat(device, notNullValue())

        startMainScreen()

        val cardInputField = interactWithCardNumber()
        val cardHolderNameInputField = interactWithCardHolderName()
        val cardExpDateInputField = interactWithCardExpDate()
        val cardCVCInputField = interactWithCardCVC()

        cardInputField.perform(SetTextAction(CARD_NUMBER))
        cardHolderNameInputField.perform(SetTextAction(CARD_HOLDER))
        cardExpDateInputField.perform(SetTextAction(CARD_EXP_DATE))
        cardCVCInputField.perform(SetTextAction(CARD_CVC))

        device.setOrientationLeft()

        cardInputField.check(matches(withCardNumberState(CARD_NUMBER)))
        cardHolderNameInputField.check(matches(withCardHolderState(CARD_HOLDER)))
        cardExpDateInputField.check(matches(withCardExpDateState(CARD_EXP_DATE)))
        cardCVCInputField.check(matches(withCardCVCState(CARD_CVC)))

        device.setOrientationNatural()

        cardInputField.check(matches(withCardNumberState(CARD_NUMBER)))
        cardHolderNameInputField.check(matches(withCardHolderState(CARD_HOLDER)))
        cardExpDateInputField.check(matches(withCardExpDateState(CARD_EXP_DATE)))
        cardCVCInputField.check(matches(withCardCVCState(CARD_CVC)))
    }

    @Test
    fun test_submit_flow() {
        startMainScreen()

        val cardHolderNameInputField = interactWithCardHolderName()
        val cardInputField = interactWithCardNumber()
        val postalCode = interactWithPostalCode()
        val cardExpDateInputField = interactWithCardExpDate()
        val cardCVCInputField = interactWithCardCVC()
        val city = interactWithCity()

        val submitBtn = interactWithSubmitButton()
        val responseContainer = interactWithResponseContainer()

        cardInputField.perform(SetTextAction(CARD_NUMBER_WRONG_1))
        cardHolderNameInputField.perform(SetTextAction(CARD_HOLDER_WRONG))
        cardExpDateInputField.perform(SetTextAction(CARD_EXP_DATE_WRONG))
        cardCVCInputField.perform(SetTextAction(CARD_CVC_WRONG))


        performClick(submitBtn)
        responseContainer.check(matches(withText(containsString(CODE_1001))))
        cardCVCInputField.perform(SetTextAction(CARD_CVC))


        performClick(submitBtn)
        responseContainer.check(matches(withText(containsString(CODE_1001))))
        cardExpDateInputField.perform(SetTextAction(CARD_EXP_DATE))


        performClick(submitBtn)
        responseContainer.check(matches(withText(containsString(CODE_1001))))
        cardHolderNameInputField.perform(SetTextAction(CARD_HOLDER))


        performClick(submitBtn)
        responseContainer.check(matches(withText(containsString(CODE_1001))))
        cardInputField.perform(SetTextAction(CARD_NUMBER_WRONG_2))

        performClick(submitBtn)
        responseContainer.check(matches(withText(containsString(CODE_1001))))
        cardInputField.perform(SetTextAction(CARD_NUMBER))

        postalCode.perform(SetTextAction(POSTAL_WRONG))
        performClick(submitBtn)
        responseContainer.check(matches(withText(containsString(CODE_1001))))

        postalCode.perform(SetTextAction(POSTAL))
        city.perform(SetTextAction(CITY))
        performClick(submitBtn)

        pauseTestFor(10000)
        responseContainer.check(matches(withText(containsString(CODE_200))))
    }

    private fun interactWithResponseContainer(): ViewInteraction {
        return onView(withId(R.id.tvResponseCode))
    }

    private fun startMainScreen() {
        onView(withId(R.id.tiedVaultId)).perform(
            typeText(Utils.DEFAULT_TENANT_ID),
            closeSoftKeyboard()
        )
        onView(withId(R.id.tiedPath)).perform(
            typeText(Utils.DEFAULT_PATH),
            closeSoftKeyboard()
        )

        val startWithActivityBtn = onView(withId(R.id.llCollectActivityFlow))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        performClick(startWithActivityBtn)
    }

    private fun interactWithCardCVC(): ViewInteraction {
        val cardCVCField = onView(withId(R.id.vgsTiedCvc))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.vgsTilCvc))
            .check(matches(isDisplayed()))

        return cardCVCField
    }

    private fun interactWithCardExpDate(): ViewInteraction {
        val cardExpDateField = onView(withId(R.id.vgsTiedExpiry))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.vgsTilExpiry))
            .check(matches(isDisplayed()))

        return cardExpDateField
    }

    private fun interactWithCardHolderName(): ViewInteraction {
        val cardHolderField = onView(withId(R.id.vgsTiedCardHolder))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.vgsTilCardHolder))
            .check(matches(isDisplayed()))

        return cardHolderField
    }

    private fun interactWithCity(): ViewInteraction {
        return onView(withId(R.id.vgsTiedCity))
            .check(matches(not(isDisplayed())))
    }

    private fun interactWithPostalCode(): ViewInteraction {
        return onView(withId(R.id.vgsTiedPostalCode))
            .check(matches(not(isDisplayed())))
    }

    private fun interactWithCardNumber(): ViewInteraction {
        val cardInputField = onView(withId(R.id.vgsTiedCardNumber))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.vgsTilCardNumber))
            .check(matches(isDisplayed()))

        return cardInputField
    }

    private fun interactWithSubmitButton(): ViewInteraction {
        return onView(withId(R.id.mbSubmit))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
    }

    private fun performClick(interaction: ViewInteraction) {
        pauseTestFor(200)
        interaction.perform(scrollTo())
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