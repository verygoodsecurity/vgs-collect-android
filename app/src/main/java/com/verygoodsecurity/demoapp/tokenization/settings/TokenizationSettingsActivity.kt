package com.verygoodsecurity.demoapp.tokenization.settings

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.verygoodsecurity.demoapp.ui.DemoTheme

/** Hosts [TokenizationSettingsScreen]. */
class TokenizationSettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The top bar is a dark gradient, so the status bar needs light icons.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        setContent {
            DemoTheme {
                TokenizationSettingsScreen(onBack = ::finish)
            }
        }
    }

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, TokenizationSettingsActivity::class.java))
        }
    }
}
