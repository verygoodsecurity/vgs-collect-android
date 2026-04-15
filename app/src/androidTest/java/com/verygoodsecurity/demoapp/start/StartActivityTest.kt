package com.verygoodsecurity.demoapp.start

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.cmp.CMPActivity
import com.verygoodsecurity.demoapp.collect.compose.CollectComposeActivity
import com.verygoodsecurity.demoapp.collect.views.CollectViewsActivity
import com.verygoodsecurity.demoapp.google_pay.GooglePayActivity
import com.verygoodsecurity.demoapp.payopt.PaymentOptimizationActivity
import com.verygoodsecurity.demoapp.tokenization.v1.TokenizationActivity as TokenizationActivityV1
import com.verygoodsecurity.demoapp.tokenization.v2.TokenizationActivity as TokenizationActivityV2
import org.hamcrest.Matchers.isEmptyString
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartActivityTest {

    @get:Rule
    val rule = activityScenarioRule<StartActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun teardown() {
        Intents.release()
    }

    @Test
    fun startScreen_displaysInputsAndFlowList() {
        onView(withId(R.id.tiedVaultId))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        onView(withId(R.id.tiedPath))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        onView(withId(R.id.mbGroupEnvironment))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        onView(withId(R.id.rvFlows))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        onView(withId(R.id.rvFlows))
            .perform(scrollToLastItem())
    }

    @Test
    fun defaultValues_arePopulatedAndSandboxSelected() {
        onView(withId(R.id.tiedVaultId))
            .perform(scrollTo())
            .check(matches(withText(not(isEmptyString()))))

        onView(withId(R.id.tiedPath))
            .perform(scrollTo())
            .check(matches(withText(not(isEmptyString()))))

        onView(withId(R.id.mbSandbox))
            .perform(scrollTo())
            .check(matches(isChecked()))
    }

    @Test
    fun switchingEnvironment_selectsLive() {
        onView(withId(R.id.mbLive))
            .perform(scrollTo(), click())
            .check(matches(isChecked()))
    }

    @Test
    fun flowsList_hasItems() {
        onView(withId(R.id.rvFlows))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
            .check(hasItemCountAtLeast(1))
    }

    @Test
    fun clickCollectViewsFlow_launchesCollectViewsActivityWithExtras() {
        onView(withId(R.id.rvFlows))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.start_collect_views_title)),
                    click()
                )
            )

        intended(hasComponent(CollectViewsActivity::class.java.name))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_VAULT_ID))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_PATH))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_ENVIRONMENT))
    }

    @Test
    fun clickCollectComposeFlow_launchesCollectViewsActivityWithExtras() {
        onView(withId(R.id.rvFlows))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.start_collect_compose_title)),
                    click()
                )
            )

        intended(hasComponent(CollectComposeActivity::class.java.name))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_VAULT_ID))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_PATH))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_ENVIRONMENT))
    }

    @Test
    fun clickPayoptFlow_launchesCollectViewsActivityWithExtras() {
        onView(withId(R.id.rvFlows))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.start_payopt_title)),
                    click()
                )
            )

        intended(hasComponent(PaymentOptimizationActivity::class.java.name))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_VAULT_ID))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_PATH))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_ENVIRONMENT))
    }

    @Test
    fun clickTokenizationV1Flow_launchesCollectViewsActivityWithExtras() {
        onView(withId(R.id.rvFlows))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.start_tokenization_title)),
                    click()
                )
            )

        intended(hasComponent(TokenizationActivityV1::class.java.name))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_VAULT_ID))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_PATH))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_ENVIRONMENT))
    }

    @Test
    fun clickTokenizationV2Flow_launchesCollectViewsActivityWithExtras() {
        onView(withId(R.id.rvFlows))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.start_tokenization_v2_title)),
                    click()
                )
            )

        intended(hasComponent(TokenizationActivityV2::class.java.name))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_VAULT_ID))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_PATH))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_ENVIRONMENT))
    }

    @Test
    fun clickCMPFlow_launchesCollectViewsActivityWithExtras() {
        onView(withId(R.id.rvFlows))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.start_cmp_title)),
                    click()
                )
            )

        intended(hasComponent(CMPActivity::class.java.name))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_VAULT_ID))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_PATH))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_ENVIRONMENT))
    }

    @Test
    fun clickOnGooglePayFlow_launchesGooglePayActivity() {
        onView(withId(R.id.rvFlows))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(R.string.start_google_pay_title)),
                    click()
                )
            )

        intended(hasComponent(GooglePayActivity::class.java.name))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_VAULT_ID))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_PATH))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_ENVIRONMENT))
    }

    private fun scrollToLastItem(): ViewAction {
        return object : ViewAction {
            override fun getConstraints() = isAssignableFrom(RecyclerView::class.java)

            override fun getDescription() = "Scroll RecyclerView to last adapter position"

            override fun perform(uiController: UiController, view: android.view.View) {
                val recyclerView = view as RecyclerView
                val lastIndex = (recyclerView.adapter?.itemCount ?: 0) - 1
                if (lastIndex >= 0) {
                    recyclerView.scrollToPosition(lastIndex)
                    uiController.loopMainThreadUntilIdle()
                }
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun hasItemCountAtLeast(minCount: Int): ViewAssertion {
        return ViewAssertion { view, noViewFoundException ->
            if (noViewFoundException != null) throw noViewFoundException
            val recyclerView = view as RecyclerView
            val count = recyclerView.adapter?.itemCount ?: 0
            if (count < minCount) {
                throw AssertionError("RecyclerView itemCount was $count, expected at least $minCount")
            }
        }
    }
}
