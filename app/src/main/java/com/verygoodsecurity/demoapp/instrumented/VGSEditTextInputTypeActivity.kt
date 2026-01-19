package com.verygoodsecurity.demoapp.instrumented

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.verygoodsecurity.demoapp.R

class VGSEditTextInputTypeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.instrumented_activity_vgs_edittext_input_type)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root)) { v, windowInsets ->
            val bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = bars.top,
                bottom = bars.bottom,
            )
            WindowInsetsCompat.CONSUMED
        }
    }
}