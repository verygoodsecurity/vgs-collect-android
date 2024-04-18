package com.verygoodsecurity.demoapp.views

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.utils.actions.SetTextAction
import com.verygoodsecurity.demoapp.instrumented.VGSEditTextActivity
import com.verygoodsecurity.demoapp.utils.matchers.withEditTextState
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VGSEditTextActivityMaxLengthInstrumentedTest {

    @get:Rule
    val rule = activityScenarioRule<VGSEditTextActivity>()

    private lateinit var device: UiDevice

    @Before
    fun prepareDevice() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun test_editText_set_max_length() {
        Espresso.onView(ViewMatchers.withId(R.id.maxBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(click())

        val field = Espresso.onView(ViewMatchers.withId(R.id.vgsEditText))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        field.perform(SetTextAction(NUMBER_WRONG))
        field.check(ViewAssertions.matches(withEditTextState(NUMBER_CORRECT)))
    }

    companion object {
        private const val NUMBER_CORRECT = "1234567"
        private const val NUMBER_WRONG = "1234567890"
    }
}