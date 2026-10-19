package com.verygoodsecurity.demoapp.tokenization.v2

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.verygoodsecurity.demoapp.BuildConfig
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.TEST_ENVIRONMENT
import com.verygoodsecurity.demoapp.start.StartActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TokenizationActivityTest {

    @get:Rule
    val composeRule = createEmptyComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var scenario: ActivityScenario<TokenizationActivity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(
            Intent(context, TokenizationActivity::class.java).apply {
                putExtra(StartActivity.KEY_BUNDLE_TENANT_ID, BuildConfig.TENANT_ID)
                putExtra(StartActivity.KEY_BUNDLE_PATH, BuildConfig.PATH)
                putExtra(StartActivity.KEY_BUNDLE_ENVIRONMENT, TEST_ENVIRONMENT)
                putExtra(StartActivity.KEY_BUNDLE_ROUTE_ID, BuildConfig.TOKENIZATION_V2_ROUTE_ID)
            }
        )
    }

    @After
    fun teardown() {
        scenario.close()
    }

    @Test
    fun viewsDisplayed() {
        composeRule.onNodeWithText(context.getString(R.string.start_tokenization_v2_title)).assertIsDisplayed()
        listOf(
            R.string.card_holder_title,
            R.string.card_number_title,
            R.string.expiry_title,
            R.string.cvc_title,
            R.string.create_aliases_btn_title,
        ).forEach {
            composeRule.onNodeWithText(context.getString(it)).performScrollTo().assertIsDisplayed()
        }
    }
}
