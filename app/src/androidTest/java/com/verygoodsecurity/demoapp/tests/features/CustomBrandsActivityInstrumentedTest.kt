package com.verygoodsecurity.demoapp.tests.features

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.actions.SetCardNumberDividerAction
import com.verygoodsecurity.demoapp.actions.SetTextAction
import com.verygoodsecurity.demoapp.instrumented.CustomBrandsActivity
import com.verygoodsecurity.demoapp.matchers.withCardBrand
import com.verygoodsecurity.demoapp.matchers.withCardNumberState
import com.verygoodsecurity.vgscollect.view.card.CardType
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CustomBrandsActivityInstrumentedTest {

    @get:Rule
    val rule = activityScenarioRule<CustomBrandsActivity>()

    private lateinit var device: UiDevice

    @Before
    fun prepareDevice() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun test_default_flow() {
        val field = getXMLCardNumberField()

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        pauseTestFor(500)
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_CUSTOM)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.UNKNOWN.name)))
    }

    @Test
    fun test_default_flow_with_set_divider() {
        val field = getXMLCardNumberField()

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetCardNumberDividerAction('-'))

        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        pauseTestFor(500)
        field.check(ViewAssertions.matches(withCardBrand(CardType.UNKNOWN.name)))
    }

    @Test
    fun test_default_custom_brand() {
        val field = getXMLCardNumberField()

        Espresso.onView(ViewMatchers.withId(R.id.addBrandBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        pauseTestFor(500)
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_CUSTOM)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(CustomBrandsActivity.createCardBrand().cardBrandName)
            )
        )
    }

    @Test
    fun test_default_custom_brand_with_set_divider() {
        val field = getXMLCardNumberField()

        Espresso.onView(ViewMatchers.withId(R.id.addBrandBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        pauseTestFor(500)
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_CUSTOM)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(CustomBrandsActivity.createCardBrand().cardBrandName)
            )
        )

        field.perform(SetTextAction(""))

        field.perform(SetCardNumberDividerAction('-'))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        pauseTestFor(500)
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_CUSTOM)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(CustomBrandsActivity.createCardBrand().cardBrandName)
            )
        )
    }

    private fun getXMLCardNumberField(): ViewInteraction {
        return Espresso.onView(ViewMatchers.withId(R.id.xmlCardNumber))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun pauseTestFor(milliseconds: Long) {
        try {
            Thread.sleep(milliseconds)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val NUMBER_VISA = "4111111111111111"
        const val NUMBER_VISA_OVERRIDE = "4111111111111111"
        const val NUMBER_CUSTOM = "777712345678909"
    }
}