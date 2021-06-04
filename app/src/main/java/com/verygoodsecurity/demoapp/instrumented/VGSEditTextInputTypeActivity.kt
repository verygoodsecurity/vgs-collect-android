package com.verygoodsecurity.demoapp.instrumented

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.demoapp.R
import kotlinx.android.synthetic.main.instrumented_activity_vgs_edittext.*

class VGSEditTextInputTypeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.instrumented_activity_vgs_edittext_input_type)
    }
}