package com.verygoodsecurity.demoapp.tests.views

import android.view.KeyEvent
import android.view.accessibility.AccessibilityWindowInfo
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
import com.verygoodsecurity.demoapp.instrumented.VGSEditTextInputTypeActivity
import com.verygoodsecurity.demoapp.matchers.withEditTextState
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VGSEditTextActivityInputTypeInstrumentedTest {

    @get:Rule
    val rule = activityScenarioRule<VGSEditTextInputTypeActivity>()

    private lateinit var device: UiDevice

    @Before
    fun prepareDevice() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        pauseTestFor(500)
    }

    @Test
    fun test_editText_number_inputType() {
        val field = Espresso.onView(ViewMatchers.withId(R.id.vgsEditTextNumber))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        openKeyboard(field)

        typeText(device)
        field.check(ViewAssertions.matches(withEditTextState(EMPTY_STRING)))

        typeNumber(device)
        field.check(ViewAssertions.matches(withEditTextState(NUMBER)))
        device.pressBack()
    }

    @Test
    fun test_editText_number_password_inputType() {
        val field = Espresso.onView(ViewMatchers.withId(R.id.vgsEditTextNumberPassword))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        openKeyboard(field)

        typeText(device)
        field.check(ViewAssertions.matches(withEditTextState(EMPTY_STRING)))

        typeNumber(device)
        field.check(ViewAssertions.matches(withEditTextState(NUMBER)))

    }

    @Test
    fun test_editText_text_inputType() {
        val field = Espresso.onView(ViewMatchers.withId(R.id.vgsEditTextText))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        openKeyboard(field)

        typeText(device)
        field.check(ViewAssertions.matches(withEditTextState(TEXT)))

        typeNumber(device)
        field.check(ViewAssertions.matches(withEditTextState(TEXT + NUMBER)))

    }

    @Test
    fun test_editText_text_password_inputType() {
        val field = Espresso.onView(ViewMatchers.withId(R.id.vgsEditTextTextPassword))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        openKeyboard(field)

        typeText(device)
        field.check(ViewAssertions.matches(withEditTextState(TEXT)))

        typeNumber(device)
        field.check(ViewAssertions.matches(withEditTextState(TEXT + NUMBER)))

    }

    @Test
    fun test_editText_date_inputType() {
        val field = Espresso.onView(ViewMatchers.withId(R.id.vgsEditTextDate))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        openKeyboard(field)

        typeText(device)
        field.check(ViewAssertions.matches(withEditTextState("a")))

        typeNumber(device)
        field.check(ViewAssertions.matches(withEditTextState("a$NUMBER")))

    }

    private fun openKeyboard(field: ViewInteraction) {
        field.perform(click())
        pauseTestFor(300)
        check(isKeyboardOpened())
        pauseTestFor(300)
    }

    private fun pauseTestFor(milliseconds: Long) {
        try {
            Thread.sleep(milliseconds)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val EMPTY_STRING = ""
        private const val NUMBER = "1230"
        private fun typeNumber(device: UiDevice) {
            device.pressKeyCode(KeyEvent.KEYCODE_1)
            device.pressKeyCode(KeyEvent.KEYCODE_2)
            device.pressKeyCode(KeyEvent.KEYCODE_3)
            device.pressKeyCode(KeyEvent.KEYCODE_0)
        }

        private const val TEXT = "Android"
        private fun typeText(device: UiDevice) {
            device.pressKeyCode(KeyEvent.KEYCODE_A, KeyEvent.META_SHIFT_ON)
            device.pressKeyCode(KeyEvent.KEYCODE_N)
            device.pressKeyCode(KeyEvent.KEYCODE_D)
            device.pressKeyCode(KeyEvent.KEYCODE_R)
            device.pressKeyCode(KeyEvent.KEYCODE_O)
            device.pressKeyCode(KeyEvent.KEYCODE_I)
            device.pressKeyCode(KeyEvent.KEYCODE_D)
        }

        private fun isKeyboardOpened(): Boolean {
            for (window in InstrumentationRegistry.getInstrumentation().uiAutomation.windows) {
                if (window.type == AccessibilityWindowInfo.TYPE_INPUT_METHOD) {
                    return true
                }
            }
            return false
        }
    }


}