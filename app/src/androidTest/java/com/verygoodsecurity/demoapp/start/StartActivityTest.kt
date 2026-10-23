package com.verygoodsecurity.demoapp.start

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.cmp.CMPActivity
import com.verygoodsecurity.demoapp.collect.compose.CollectComposeActivity
import com.verygoodsecurity.demoapp.collect.views.CollectViewsActivity
import com.verygoodsecurity.demoapp.google_pay.GooglePayActivity
import com.verygoodsecurity.demoapp.tokenization.v1.TokenizationActivity as TokenizationActivityV1
import com.verygoodsecurity.demoapp.tokenization.v2.TokenizationActivity as TokenizationActivityV2
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StartActivityTest {

    @get:Rule
    val rule = createAndroidComposeRule<StartActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun teardown() {
        Intents.release()
    }

    @Test
    fun startScreen_displaysInputsAndEnvironment() {
        rule.onNodeWithTag(TAG_TENANT_ID).performScrollTo().assertIsDisplayed()
        rule.onNodeWithTag(TAG_PATH).performScrollTo().assertIsDisplayed()
        rule.onNodeWithText(string(R.string.start_activity_environment_title)).assertIsDisplayed()
    }

    @Test
    fun defaultValues_arePopulatedAndSandboxSelected() {
        rule.onNodeWithTag(TAG_TENANT_ID).assertTextContains(StartConfig().tenantId)
        rule.onNodeWithTag(TAG_PATH).assertTextContains(StartConfig().path)
        segment(R.string.start_activity_environment_sandbox).assertIsSelected()
    }

    @Test
    fun switchingEnvironment_selectsLive() {
        segment(R.string.start_activity_environment_Live).performClick()
        segment(R.string.start_activity_environment_Live).assertIsSelected()
    }

    @Test
    fun clickCollectViewsFlow_launchesCollectViewsActivityWithExtras() =
        assertFlowLaunches(R.string.start_collect_views_title, CollectViewsActivity::class.java)

    @Test
    fun clickCollectComposeFlow_launchesCollectComposeActivityWithExtras() =
        assertFlowLaunches(R.string.start_collect_compose_title, CollectComposeActivity::class.java)

    @Test
    fun clickTokenizationV1Flow_launchesTokenizationActivityWithExtras() =
        assertFlowLaunches(R.string.start_tokenization_title, TokenizationActivityV1::class.java)

    @Test
    fun clickTokenizationV2Flow_launchesTokenizationV2ActivityWithExtras() =
        assertFlowLaunches(R.string.start_tokenization_v2_title, TokenizationActivityV2::class.java)

    @Test
    fun clickCMPFlow_launchesCMPActivityWithExtras() =
        assertFlowLaunches(R.string.start_cmp_title, CMPActivity::class.java)

    @Test
    fun clickOnGooglePayFlow_launchesGooglePayActivityWithExtras() =
        assertFlowLaunches(R.string.start_google_pay_title, GooglePayActivity::class.java)

    private fun assertFlowLaunches(@StringRes title: Int, activity: Class<*>) {
        rule.onNodeWithText(string(title)).performScrollTo().performClick()

        intended(hasComponent(activity.name))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_TENANT_ID))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_PATH))
        intended(hasExtraWithKey(StartActivity.KEY_BUNDLE_ENVIRONMENT))
    }

    // The header chip repeats the environment name, so match the selectable segment only.
    private fun segment(@StringRes id: Int) = rule.onNode(hasText(string(id)) and isSelectable())

    private fun string(@StringRes id: Int) = rule.activity.getString(id)
}
