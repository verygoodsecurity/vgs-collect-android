package com.verygoodsecurity.demoapp.utils.idling

import androidx.test.espresso.IdlingResource

object GlobalIdlingResource {

    private val resource = CountingIdlingResource(this::class.java.simpleName)

    fun increment(skipCounter: Boolean = false) {
        if (!skipCounter)
        resource.increment()
    }

    fun decrement(skipCounter: Boolean = false) {
        if (!skipCounter)
        resource.decrement()
    }

    fun getResource(): IdlingResource = resource
}