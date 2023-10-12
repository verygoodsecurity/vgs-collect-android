package com.verygoodsecurity.demoapp.instrumented

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.demoapp.databinding.InstrumentedActivityVgsEdittextBinding
import com.verygoodsecurity.vgscollect.view.card.validation.rules.VGSInfoRule

class VGSEditTextActivity : AppCompatActivity() {

    private var isValidationEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = InstrumentedActivityVgsEdittextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.maxBtn.setOnClickListener { binding.vgsEditText.setMaxLength(7) }
        binding.disableValidationBtn.setOnClickListener {
            isValidationEnabled = !isValidationEnabled
            binding.vgsEditText.enableValidation(isValidationEnabled)
        }
        binding.regBtn.setOnClickListener {
            val rule = VGSInfoRule.ValidationBuilder()
                .setAllowableMaxLength(7)
                .setAllowableMinLength(2)
                .setRegex("^([a-zA-Z]{2,}\\s[a-zA-Z]{1,}'?-?[a-zA-Z]{2,}\\s?([a-zA-Z]{1,})?)")
                .build()
            binding.vgsEditText.setRule(rule)
        }
    }
}