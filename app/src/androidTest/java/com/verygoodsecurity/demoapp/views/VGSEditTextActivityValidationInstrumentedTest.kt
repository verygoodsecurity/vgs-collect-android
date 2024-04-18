package com.verygoodsecurity.demoapp.views

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.utils.actions.SetTextAction
import com.verygoodsecurity.demoapp.instrumented.VGSEditTextActivity
import com.verygoodsecurity.demoapp.utils.matchers.validation.WithValidationStateMatcher
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VGSEditTextActivityValidationInstrumentedTest {

    @get:Rule
    val rule = activityScenarioRule<VGSEditTextActivity>()

    private lateinit var device: UiDevice

    @Before
    fun prepareDevice() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun test_editText_set_max_length() {
        Espresso.onView(ViewMatchers.withId(R.id.regBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.click())

        val field = Espresso.onView(ViewMatchers.withId(R.id.vgsEditText))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        field.perform(SetTextAction(NAME_WRONG_1))
        field.check(ViewAssertions.matches(CoreMatchers.not(WithValidationStateMatcher())))
        field.perform(SetTextAction(NAME_WRONG_2))
        field.check(ViewAssertions.matches(CoreMatchers.not(WithValidationStateMatcher())))
        field.perform(SetTextAction(NAME_CORRECT))
        field.check(ViewAssertions.matches(WithValidationStateMatcher()))
    }

    @Test
    fun test_editText_disable_validation() {
        val field = Espresso.onView(ViewMatchers.withId(R.id.vgsEditText))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        field.check(ViewAssertions.matches(CoreMatchers.not(WithValidationStateMatcher())))

        Espresso.onView(ViewMatchers.withId(R.id.disableValidationBtn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.click())

        field.perform(SetTextAction(NAME_CORRECT))
        field.check(ViewAssertions.matches(WithValidationStateMatcher()))
    }

    companion object {
        private const val NAME_CORRECT = "Ji Gal"
        private const val NAME_WRONG_1 = "Di"
        private const val NAME_WRONG_2 = "John Galt"
    }
}