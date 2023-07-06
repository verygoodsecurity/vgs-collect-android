package com.verygoodsecurity.demoapp.tests.features

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.actions.SetCardNumberDividerAction
import com.verygoodsecurity.demoapp.actions.SetCustomBrandAction
import com.verygoodsecurity.demoapp.actions.SetTextAction
import com.verygoodsecurity.demoapp.instrumented.CustomBrandsActivity
import com.verygoodsecurity.demoapp.matchers.WithCardNumberDividerMatcher
import com.verygoodsecurity.demoapp.matchers.withCardBrand
import com.verygoodsecurity.demoapp.matchers.withCardNumberState
import com.verygoodsecurity.vgscollect.view.card.BrandParams
import com.verygoodsecurity.vgscollect.view.card.CardBrand
import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class CustomBrandsActivityInstrumentedTest {

    @get:Rule
    val rule = activityScenarioRule<CustomBrandsActivity>()

    private lateinit var device: UiDevice

    private val visaCustomCardBrand: CardBrand by lazy {
        val params = BrandParams(
            "###### ##### ########",
            ChecksumAlgorithm.LUHN,
            arrayOf(16, 19),
            arrayOf(3, 5)
        )

        CardBrand(
            "^41111",
            "newVisa-Brand",
            com.verygoodsecurity.vgscollect.R.drawable.ic_card_back_preview_dark,
            params
        )
    }

    private val customCardBrand: CardBrand by lazy {
        val params = BrandParams(
            "###### ##### ########",
            ChecksumAlgorithm.LUHN,
            arrayOf(15, 19),
            arrayOf(3, 5)
        )
        CardBrand(
            "^777",
            "newBrand",
            com.verygoodsecurity.vgscollect.R.drawable.ic_card_back_preview_dark_4,
            params
        )
    }

    @Before
    fun prepareDevice() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    //    previously inflated View
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
        field.check(ViewAssertions.matches(withCardBrand(CardType.UNKNOWN.name)))
    }

    @Test
    fun test_default_custom_brand() {
        val field = getXMLCardNumberField()

        field.perform(SetCustomBrandAction(customCardBrand))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_CUSTOM)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(customCardBrand.cardBrandName)
            )
        )
    }

    @Test
    fun test_default_custom_brand_with_set_divider() {
        val field = getXMLCardNumberField()

        field.perform(SetCustomBrandAction(customCardBrand))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_CUSTOM)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(customCardBrand.cardBrandName)
            )
        )

        field.perform(SetTextAction(""))

        field.perform(SetCardNumberDividerAction('-'))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_CUSTOM)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(customCardBrand.cardBrandName)
            )
        )
    }

    @Test
    fun test_default_override_custom_brand() {
        val field = getXMLCardNumberField()

        field.perform(SetCustomBrandAction(visaCustomCardBrand))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(visaCustomCardBrand.cardBrandName)
            )
        )

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardBrand(CardType.UNKNOWN.name)))
    }

    @Test(timeout = 10000L)
    fun test_default_override_custom_brand_with_set_divider() {
        val field = getXMLCardNumberField()

        field.perform(SetCustomBrandAction(visaCustomCardBrand))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(visaCustomCardBrand.cardBrandName)
            )
        )

        field.perform(SetTextAction(""))
        field.perform(SetCardNumberDividerAction('-'))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(visaCustomCardBrand.cardBrandName)
            )
        )

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardBrand(CardType.UNKNOWN.name)))
    }

    //    programmatically created View
    @Test
    fun test_custom_view_default_flow() {
        Espresso.onView(ViewMatchers.withId(R.id.createCardNumber))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.attachInflatedCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        val field = Espresso.onView(ViewMatchers.withId(CustomBrandsActivity.VIEW_ID))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardBrand(CardType.UNKNOWN.name)))
    }

    @Test
    fun test_custom_view_default_flow_with_set_divider_before_attach() {
        Espresso.onView(ViewMatchers.withId(R.id.createCardNumber))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.applyDividerBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.attachInflatedCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        val field = Espresso.onView(ViewMatchers.withId(CustomBrandsActivity.VIEW_ID))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.check(ViewAssertions.matches(WithCardNumberDividerMatcher('-')))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardBrand(CardType.UNKNOWN.name)))
    }

    @Test
    fun test_custom_view_flow_with_set_divider_after_attach() {
        Espresso.onView(ViewMatchers.withId(R.id.createCardNumber))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.attachInflatedCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        val field = Espresso.onView(ViewMatchers.withId(CustomBrandsActivity.VIEW_ID))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.perform(SetCardNumberDividerAction('-'))

        field.check(ViewAssertions.matches(WithCardNumberDividerMatcher('-')))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardBrand(CardType.UNKNOWN.name)))
    }

    @Test(timeout = 10000L)
    fun test_custom_view_custom_brand() {
        Espresso.onView(ViewMatchers.withId(R.id.createCardNumber))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.addBrandBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.attachInflatedCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        val field = Espresso.onView(ViewMatchers.withId(CustomBrandsActivity.VIEW_ID))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.perform(SetCustomBrandAction(customCardBrand))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_CUSTOM)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(customCardBrand.cardBrandName)
            )
        )
    }

    @Test(timeout = 10000L)
    fun test_custom_view_custom_brand_set_divider_before_attach() {
        Espresso.onView(ViewMatchers.withId(R.id.createCardNumber))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.addBrandBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.applyDividerBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.attachInflatedCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        val field = Espresso.onView(ViewMatchers.withId(CustomBrandsActivity.VIEW_ID))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.check(ViewAssertions.matches(WithCardNumberDividerMatcher('-')))

        field.perform(SetCustomBrandAction(customCardBrand))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_CUSTOM)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(customCardBrand.cardBrandName)
            )
        )
    }

    @Test(timeout = 10000L)
    fun test_custom_view_custom_brand_set_divider_after_attach() {
        Espresso.onView(ViewMatchers.withId(R.id.createCardNumber))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.addBrandBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.attachInflatedCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        val field = Espresso.onView(ViewMatchers.withId(CustomBrandsActivity.VIEW_ID))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.perform(SetCardNumberDividerAction('-'))

        field.check(ViewAssertions.matches(WithCardNumberDividerMatcher('-')))

        field.perform(SetCustomBrandAction(customCardBrand))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_CUSTOM)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(customCardBrand.cardBrandName)
            )
        )
    }

    @Test(timeout = 10000L)
    fun test_custom_view_override_custom_brand() {
        Espresso.onView(ViewMatchers.withId(R.id.createCardNumber))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.overrideExistedBrandBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.attachInflatedCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        val field = Espresso.onView(ViewMatchers.withId(CustomBrandsActivity.VIEW_ID))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.perform(SetCustomBrandAction(visaCustomCardBrand))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(visaCustomCardBrand.cardBrandName)
            )
        )

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardBrand(CardType.UNKNOWN.name)))
    }


    @Test(timeout = 10000L)
    fun test_custom_view_override_custom_brand_set_divider_before_attach() {
        Espresso.onView(ViewMatchers.withId(R.id.createCardNumber))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.overrideExistedBrandBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.applyDividerBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.attachInflatedCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        val field = Espresso.onView(ViewMatchers.withId(CustomBrandsActivity.VIEW_ID))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.check(ViewAssertions.matches(WithCardNumberDividerMatcher('-')))

        field.perform(SetCustomBrandAction(visaCustomCardBrand))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(visaCustomCardBrand.cardBrandName)
            )
        )

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardBrand(CardType.UNKNOWN.name)))
    }

    @Test(timeout = 10000L)
    fun test_custom_view_override_custom_brand_set_divider_after_attach() {
        Espresso.onView(ViewMatchers.withId(R.id.createCardNumber))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.overrideExistedBrandBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.attachInflatedCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        val field = Espresso.onView(ViewMatchers.withId(CustomBrandsActivity.VIEW_ID))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.perform(SetCardNumberDividerAction('-'))

        field.check(ViewAssertions.matches(WithCardNumberDividerMatcher('-')))

        field.perform(SetCustomBrandAction(visaCustomCardBrand))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(visaCustomCardBrand.cardBrandName)
            )
        )

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardBrand(CardType.UNKNOWN.name)))
    }


    //    programmatically created View
    @Test
    fun test_inflated_programmatically_default_flow() {
        Espresso.onView(ViewMatchers.withId(R.id.inflateCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.attachInflatedCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        val field = Espresso.onView(ViewMatchers.withId(CustomBrandsActivity.VIEW_ID))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardBrand(CardType.UNKNOWN.name)))
    }

    @Test
    fun test_inflated_programmatically_default_flow_with_set_divider_before_attach() {
        Espresso.onView(ViewMatchers.withId(R.id.inflateCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.applyDividerBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.attachInflatedCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        val field = Espresso.onView(ViewMatchers.withId(CustomBrandsActivity.VIEW_ID))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.check(ViewAssertions.matches(WithCardNumberDividerMatcher('-')))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardBrand(CardType.UNKNOWN.name)))
    }

    @Test
    fun test_inflated_programmatically_flow_with_set_divider_after_attach() {
        Espresso.onView(ViewMatchers.withId(R.id.inflateCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.attachInflatedCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        val field = Espresso.onView(ViewMatchers.withId(CustomBrandsActivity.VIEW_ID))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.perform(SetCardNumberDividerAction('-'))

        field.check(ViewAssertions.matches(WithCardNumberDividerMatcher('-')))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardBrand(CardType.UNKNOWN.name)))
    }

    @Test
    fun test_inflated_programmatically_custom_brand() {
        Espresso.onView(ViewMatchers.withId(R.id.inflateCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.addBrandBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.attachInflatedCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        val field = Espresso.onView(ViewMatchers.withId(CustomBrandsActivity.VIEW_ID))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.perform(SetCustomBrandAction(customCardBrand))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_CUSTOM)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(customCardBrand.cardBrandName)
            )
        )
    }

    @Test(timeout = 10000L)
    fun test_inflated_programmatically_custom_brand_set_divider_before_attach() {
        Espresso.onView(ViewMatchers.withId(R.id.inflateCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.addBrandBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.applyDividerBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.attachInflatedCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        val field = Espresso.onView(ViewMatchers.withId(CustomBrandsActivity.VIEW_ID))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.check(ViewAssertions.matches(WithCardNumberDividerMatcher('-')))

        field.perform(SetCustomBrandAction(customCardBrand))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_CUSTOM)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(customCardBrand.cardBrandName)
            )
        )
    }

    @Test(timeout = 10000L)
    fun test_inflated_programmatically_custom_brand_set_divider_after_attach() {
        Espresso.onView(ViewMatchers.withId(R.id.inflateCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.addBrandBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.attachInflatedCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        val field = Espresso.onView(ViewMatchers.withId(CustomBrandsActivity.VIEW_ID))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.perform(SetCardNumberDividerAction('-'))

        field.check(ViewAssertions.matches(WithCardNumberDividerMatcher('-')))

        field.perform(SetCustomBrandAction(customCardBrand))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_CUSTOM)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(customCardBrand.cardBrandName)
            )
        )
    }

    @Test
    fun test_inflated_programmatically_override_custom_brand() {
        Espresso.onView(ViewMatchers.withId(R.id.inflateCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.overrideExistedBrandBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.attachInflatedCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        val field = Espresso.onView(ViewMatchers.withId(CustomBrandsActivity.VIEW_ID))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.perform(SetCustomBrandAction(visaCustomCardBrand))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(visaCustomCardBrand.cardBrandName)
            )
        )

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardBrand(CardType.UNKNOWN.name)))
    }

    @Test(timeout = 10000L)
    fun test_inflated_programmatically_override_custom_brand_set_divider_before_attach() {
        Espresso.onView(ViewMatchers.withId(R.id.inflateCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.overrideExistedBrandBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.applyDividerBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.attachInflatedCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        val field = Espresso.onView(ViewMatchers.withId(CustomBrandsActivity.VIEW_ID))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.check(ViewAssertions.matches(WithCardNumberDividerMatcher('-')))

        field.perform(SetCustomBrandAction(visaCustomCardBrand))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(""))
        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(visaCustomCardBrand.cardBrandName)
            )
        )

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardBrand(CardType.UNKNOWN.name)))
    }

    @Test(timeout = 10000L)
    fun test_inflated_programmatically_override_custom_brand_set_divider_after_attach() {
        Espresso.onView(ViewMatchers.withId(R.id.inflateCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.overrideExistedBrandBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.attachInflatedCardNumberLay))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        val field = Espresso.onView(ViewMatchers.withId(CustomBrandsActivity.VIEW_ID))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        field.perform(SetCardNumberDividerAction('-'))

        field.check(ViewAssertions.matches(WithCardNumberDividerMatcher('-')))

        field.perform(SetCustomBrandAction(visaCustomCardBrand))

        field.perform(SetTextAction(NUMBER_VISA))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA)))
        field.check(ViewAssertions.matches(withCardBrand(CardType.VISA.name)))

        field.perform(SetTextAction(NUMBER_VISA_OVERRIDE))
        field.check(ViewAssertions.matches(withCardNumberState(NUMBER_VISA_OVERRIDE)))
        field.check(
            ViewAssertions.matches(
                withCardBrand(visaCustomCardBrand.cardBrandName)
            )
        )

        field.perform(SetTextAction(NUMBER_CUSTOM))
        field.check(ViewAssertions.matches(withCardBrand(CardType.UNKNOWN.name)))
    }


    private fun getXMLCardNumberField(): ViewInteraction {
        return Espresso.onView(ViewMatchers.withId(R.id.xmlCardNumber))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    companion object {
        const val NUMBER_VISA = "4242424242424242"
        const val NUMBER_VISA_OVERRIDE = "4111111111111111"
        const val NUMBER_CUSTOM = "777712345678909"
    }
}