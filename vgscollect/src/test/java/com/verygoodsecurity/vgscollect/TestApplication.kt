package com.verygoodsecurity.vgscollect

import android.app.Application
import org.mockito.Mockito

internal class TestApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.AppTheme)
    }
}

fun <T> any(): T = Mockito.any<T>()
