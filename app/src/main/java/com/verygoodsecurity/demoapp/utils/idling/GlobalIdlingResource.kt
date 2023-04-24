package com.verygoodsecurity.demoapp.utils.idling

import androidx.test.espresso.IdlingResource

object GlobalIdlingResource {

    private val resource = CountingIdlingResource(this::class.java.simpleName)

    fun increment() {
        resource.increment()
    }

    fun decrement() {
        resource.decrement()
    }

    fun getResource(): IdlingResource = resource
}