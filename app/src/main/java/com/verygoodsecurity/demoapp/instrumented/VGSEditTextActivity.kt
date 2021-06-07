package com.verygoodsecurity.demoapp.instrumented

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.vgscollect.view.card.validation.rules.VGSInfoRule
import kotlinx.android.synthetic.main.instrumented_activity_vgs_edittext.*

class VGSEditTextActivity:AppCompatActivity() {

    private var isValidationEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.instrumented_activity_vgs_edittext)

        maxBtn?.setOnClickListener { vgsEditText?.setMaxLength(7) }
        disableValidationBtn?.setOnClickListener {
            isValidationEnabled = !isValidationEnabled
            vgsEditText?.enableValidation(isValidationEnabled)
        }
        regBtn?.setOnClickListener {
            val rule = VGSInfoRule.ValidationBuilder()
                .setAllowableMaxLength(7)
                .setAllowableMinLength(2)
                .setRegex("^([a-zA-Z]{2,}\\s[a-zA-Z]{1,}'?-?[a-zA-Z]{2,}\\s?([a-zA-Z]{1,})?)")
                .build()
            vgsEditText?.addRule(rule)
        }
    }
}