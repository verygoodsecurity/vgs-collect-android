package com.verygoodsecurity.demoapp.utils

import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.getStringExtra(key: String, defaultValue: String = ""): String {
    return intent.extras?.getString(key) ?: defaultValue
}