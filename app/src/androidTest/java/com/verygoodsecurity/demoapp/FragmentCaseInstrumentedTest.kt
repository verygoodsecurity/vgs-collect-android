package com.verygoodsecurity.demoapp

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.os.Build
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import com.verygoodsecurity.api.cardio.ScanActivity
import com.verygoodsecurity.demoapp.actions.SetTextAction
import com.verygoodsecurity.demoapp.activity_case.VGSCollectActivity
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard
import org.hamcrest.Matchers
import org.hamcrest.core.StringContains
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FragmentCaseInstrumentedTest {

    companion object {
        const val CARD_NUMBER = "4111111111111111"
        const val CARD_NUMBER_WRONG_1 = "41111111QW"
        const val CARD_NUMBER_WRONG_2 = "41111111111111112"

        const val CARD_HOLDER = "Gohn Galt"

        const val CARD_EXP_DATE_WRONG = "99/9999"
        const val CARD_EXP_DATE= "02/22"

        const val CARD_CVC_WRONG = "12"
        const val CARD_CVC= "123"

        const val CODE_200= "Code: 200"
        const val CODE_1001= "Code: 1001"
    }

    @get:Rule
    val rule = activityScenarioRule<StartActivity>()

    private lateinit var device: UiDevice

    @Before
    fun prepareDevice() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }


    @Test
    fun test_scan_card() {
        Intents.init()

        startMainScreen()

        interactWithScanner()

        val submitBtn  = interactWithSubmitButton()
        submitBtn.perform(ViewActions.click())

        pauseTestFor(7000)

        val responseContainer  = interactWithResponseContainer()
        responseContainer.check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    StringContains.containsString(
                        ActivityCaseInstrumentedTest.CODE_200
                    )
                )
            )
        )

        Intents.release()
    }

    private fun interactWithScanner() = apply {
        val intent = Intent()
        val card = CreditCard("4111111111111111", 5,33, "123", null, "John B")
        intent.putExtra(CardIOActivity.EXTRA_SCAN_RESULT, card)


        Intents.intending(IntentMatchers.hasComponent(CardIOActivity::class.qualifiedName))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, intent))

        Espresso.onView(ViewMatchers.withId(R.id.scan_card))
            .perform(ViewActions.click())
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

        pauseTestFor(300)
        submitBtn.perform(ViewActions.click())

        pauseTestFor(7000)
        responseContainer.check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    StringContains.containsString(
                        CODE_200
                    )
                )
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.details_item))
            .perform(ViewActions.click())


        pauseTestFor(500)

        device.pressBack()

        pauseTestFor(300)
        submitBtn.perform(ViewActions.click())

        pauseTestFor(7000)

        responseContainer.check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    StringContains.containsString(
                        CODE_200
                    )
                )
            )
        )
    }

    private fun allowPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= 23) {
            val allowPermissionsLowercase: UiObject = device.findObject(UiSelector().text("Allow"))
            if (allowPermissionsLowercase.exists()) {
                try {
                    allowPermissionsLowercase.click()
                } catch (e: UiObjectNotFoundException) { }
            }
            val allowPermissionsUpercase: UiObject = device.findObject(UiSelector().text("ALLOW"))
            if (allowPermissionsUpercase.exists()) {
                try {
                    allowPermissionsUpercase.click()
                } catch (e: UiObjectNotFoundException) { }
            }
        }
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

        cardInputField.perform(SetTextAction(CARD_NUMBER_WRONG_1))
        cardHolderNameInputField.perform(SetTextAction(CARD_HOLDER))
        cardExpDateInputField.perform(SetTextAction(CARD_EXP_DATE_WRONG))
        cardCVCInputField.perform(SetTextAction(CARD_CVC_WRONG))


        submitBtn.perform(ViewActions.click())

        pauseTestFor(300)
        responseContainer.check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    StringContains.containsString(
                        CODE_1001
                    )
                )
            )
        )
        cardCVCInputField.perform(SetTextAction(CARD_CVC))

        pauseTestFor(300)
        submitBtn.perform(ViewActions.click())
        responseContainer.check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    StringContains.containsString(
                        CODE_1001
                    )
                )
            )
        )
        cardExpDateInputField.perform(SetTextAction(CARD_EXP_DATE))


        pauseTestFor(300)
        submitBtn.perform(ViewActions.click())
        responseContainer.check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    StringContains.containsString(
                        CODE_1001
                    )
                )
            )
        )
        cardInputField.perform(SetTextAction(CARD_NUMBER_WRONG_2))
        submitBtn.perform(ViewActions.click())
        responseContainer.check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    StringContains.containsString(
                        CODE_1001
                    )
                )
            )
        )
        cardInputField.perform(SetTextAction(CARD_NUMBER))
        pauseTestFor(300)
        submitBtn.perform(ViewActions.click())

        pauseTestFor(7000)
        responseContainer.check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    StringContains.containsString(
                        CODE_200
                    )
                )
            )
        )
    }

    private fun interactWithResponseContainer(): ViewInteraction {
        return Espresso.onView(ViewMatchers.withId(R.id.responseContainerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun startMainScreen() {
        val startWithActivityBtn = Espresso.onView(ViewMatchers.withId(R.id.startWithFragmentBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        startWithActivityBtn.perform(ViewActions.click())
    }

    private fun interactWithCardCVC(): ViewInteraction {
        val cardCVCField = Espresso.onView(ViewMatchers.withId(R.id.cardCVCField))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
        Espresso.onView(ViewMatchers.withId(R.id.cardCVCFieldLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        return cardCVCField
    }

    private fun interactWithCardExpDate(): ViewInteraction {
        val cardExpDateField = Espresso.onView(ViewMatchers.withId(R.id.cardExpDateField))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
        Espresso.onView(ViewMatchers.withId(R.id.cardExpDateFieldLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        return cardExpDateField
    }

    private fun interactWithCardHolderName(): ViewInteraction {
        val cardHolderField = Espresso.onView(ViewMatchers.withId(R.id.cardHolderField))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
        Espresso.onView(ViewMatchers.withId(R.id.cardHolderFieldLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        return cardHolderField
    }

    private fun interactWithCardNumber(): ViewInteraction {
        val cardInputField = Espresso.onView(ViewMatchers.withId(R.id.cardNumberField))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
        Espresso.onView(ViewMatchers.withId(R.id.cardNumberFieldLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        return cardInputField
    }

    private fun interactWithSubmitButton(): ViewInteraction {
        return Espresso.onView(ViewMatchers.withId(R.id.submitBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


    private fun pauseTestFor(milliseconds: Long) {
        try {
            Thread.sleep(milliseconds)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}