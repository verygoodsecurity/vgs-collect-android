package com.verygoodsecurity.demoapp.utils

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matcher
import org.junit.Assert

private const val DEFAULT_TIMEOUT = 5_000L

object Utils {

    fun waitForView(matcher: Matcher<View>, timeOut: Long = DEFAULT_TIMEOUT) {
        val retryInterval = 100L
        val endTime = System.currentTimeMillis() + timeOut
        while (System.currentTimeMillis() < endTime) {
            try {
                onView(matcher).check(matches(isDisplayed()))
                return
            } catch (_: Exception) {
                Thread.sleep(retryInterval)
            }
        }
        Assert.fail("View not found: $matcher")
    }
}