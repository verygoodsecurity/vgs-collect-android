package com.verygoodsecurity.vgscollect.view.card.material

import android.app.Application
import com.verygoodsecurity.vgscollect.R

internal class TestApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.AppTheme)
    }

}