package com.verygoodsecurity.demoapp.flows

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import com.google.android.material.textfield.TextInputLayout
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.date_range_activity.DateRangeActivity
import com.verygoodsecurity.demoapp.utils.idling.GlobalIdlingResource
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DateRangeActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(DateRangeActivity::class.java)

    private lateinit var device: UiDevice

    @Before
    fun prepareDevice() {
        init()
        device = UiDevice.getInstance(getInstrumentation())
        device.setOrientationNatural()
        IdlingRegistry.getInstance().register(GlobalIdlingResource.getResource())
    }

    @After
    fun teardown() {
        release()
        IdlingRegistry.getInstance().unregister(GlobalIdlingResource.getResource())
    }

    @Test
    fun nextButton_onCardNumberField_opensDatePicker() {
        val cardNumberFieldId = interactWithCardNumber()
        val datePickerDoneButtonText = "DONE"

        onView(withId(R.id.viewDisableTouch))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))

        cardNumberFieldId.perform(replaceText("4242424242424242"))

        cardNumberFieldId.perform(pressImeActionButton())

        onView(withText(datePickerDoneButtonText)).check(matches(isDisplayed()))
    }

    @Test
    fun datePickerShowed_afterFieldClick() {
        val datePickerDoneButtonText = "DONE"

        onView(withId(R.id.viewDisableTouch))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))

        onView(withId(R.id.vgsTilDateRange)).perform(scrollTo())

        interactWithDateRange().perform(ViewActions.click())

        onView(withText(datePickerDoneButtonText))
            .check(matches(isDisplayed()))
    }

    @Test
    fun test_dateRange_validDate_noErrors() {
        onView(withId(R.id.vgsTilDateRange)).perform(scrollTo())
        interactWithDateRange().perform(replaceText("05/25"))

        onView(withId(R.id.vgsTilDateRange)).check(matches(not(hasError())))
    }

    private fun hasError(): Matcher<View> {
        return object : TypeSafeMatcher<View>() {

            override fun describeTo(description: Description) {
                description.appendText("has error")
            }

            override fun matchesSafely(item: View): Boolean {
                // 1. Перевіряємо, чи є наш View екземпляром TextInputLayout
                if (item !is TextInputLayout) {
                    return false // Якщо ні, то він не може мати помилку в стилі Material
                }
                // 2. Якщо так, перевіряємо, чи встановлено текст помилки
                return item.error != null
            }
        }
    }

    private fun interactWithDateRange(): ViewInteraction {
        return onView(
            allOf(
                ViewMatchers.isDescendantOfA(withId(R.id.vgsTilDateRange)),
                ViewMatchers.isAssignableFrom(android.widget.EditText::class.java)
            )
        )
    }

    private fun interactWithCardNumber(): ViewInteraction {
        return onView(
            allOf(
                ViewMatchers.isDescendantOfA(withId(R.id.vgsTilCardNumber)),
                ViewMatchers.isAssignableFrom(android.widget.EditText::class.java)
            )
        )
    }
}