package com.verygoodsecurity.vgscollect.view.card.validation

class CompositeValidator {

    private val validators = mutableListOf<VGSValidator>()

    fun addRule(validator: VGSValidator) {
        validators.add(validator)
    }

    fun clearRules() {
        validators.clear()
    }

    fun isValid(content: String): Boolean {
        return if (validators.isEmpty()) {
            false
        } else {
            var isValid = true
            for (validator in validators) {
                isValid = validator.isValid(content) && isValid
            }
            isValid
        }
    }
}