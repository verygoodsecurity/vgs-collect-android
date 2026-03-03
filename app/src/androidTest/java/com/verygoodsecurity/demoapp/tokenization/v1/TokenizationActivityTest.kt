package com.verygoodsecurity.demoapp.tokenization.v1

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.verygoodsecurity.demoapp.BuildConfig
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.SUCCESS_CODE
import com.verygoodsecurity.demoapp.TEST_CARD_HOLDER
import com.verygoodsecurity.demoapp.TEST_CARD_NUMBER
import com.verygoodsecurity.demoapp.TEST_CVC
import com.verygoodsecurity.demoapp.TEST_ENVIRONMENT
import com.verygoodsecurity.demoapp.TEST_EXPIRATION_DATE
import com.verygoodsecurity.demoapp.start.StartActivity
import com.verygoodsecurity.demoapp.utils.actions.TypeTextAction
import com.verygoodsecurity.demoapp.utils.idling.GlobalIdlingResource
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TokenizationActivityTest {

    @get:Rule
    val rule = activityScenarioRule<TokenizationActivity>(
        Intent(ApplicationProvider.getApplicationContext(), TokenizationActivity::class.java).apply {
            putExtra(StartActivity.KEY_BUNDLE_VAULT_ID, BuildConfig.VAULT_ID)
            putExtra(StartActivity.KEY_BUNDLE_PATH, BuildConfig.PATH)
            putExtra(StartActivity.KEY_BUNDLE_ENVIRONMENT, TEST_ENVIRONMENT)
            putExtra(StartActivity.KEY_BUNDLE_ROUTE_ID, BuildConfig.TOKENIZATION_ROUTE_ID)
        }
    )

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(GlobalIdlingResource.getResource())
    }

    @After
    fun teardown() {
        IdlingRegistry.getInstance().unregister(GlobalIdlingResource.getResource())
    }

    @Test
    fun viewsDisplayed() {
        onView(withId(R.id.scan))
            .check(matches(isDisplayed()))

        onView(withId(R.id.settings))
            .check(matches(isDisplayed()))

        onView(withId(R.id.vgsTilCardHolder))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        onView(withId(R.id.vgsTilCardNumber))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        onView(withId(R.id.vgsTilExpiry))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        onView(withId(R.id.vgsTilCvc))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        onView(withId(R.id.mbTokenize))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        onView(withId(R.id.codeView))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
    }

    @Test
    fun tokenizeSuccess() {
        onView(withId(R.id.vgsTilCardHolder)).perform(scrollTo())
        val cardHolder = onView(withId(R.id.vgsTiedCardHolder))

        onView(withId(R.id.vgsTilCardNumber)).perform(scrollTo())
        val cardNumber = onView(withId(R.id.vgsTiedCardNumber))

        onView(withId(R.id.vgsTilExpiry)).perform(scrollTo())
        val expiry = onView(withId(R.id.vgsTiedExpiry))

        onView(withId(R.id.vgsTilCvc)).perform(scrollTo())
        val cvc = onView(withId(R.id.vgsTiedCvc))

        cardHolder.perform(TypeTextAction(TEST_CARD_HOLDER))
        cardNumber.perform(TypeTextAction(TEST_CARD_NUMBER))
        expiry.perform(TypeTextAction(TEST_EXPIRATION_DATE))
        cvc.perform(TypeTextAction(TEST_CVC))

        onView(withId(R.id.mbTokenize))
            .perform(scrollTo())
            .perform(click())

        onView(withId(R.id.tvResponseCode))
            .check(matches(withText(containsString(SUCCESS_CODE))))
    }
}

