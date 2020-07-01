package com.verygoodsecurity.vgscollect.view.card.validation

class CompositeValidator : MutableValidator {
    private val validators = mutableListOf<VGSValidator>()

    override fun clearRules() {
        validators.clear()
    }

    override fun addRule(validator: VGSValidator) {
        validators.add(validator)
    }

    override fun isValid(content: String?): Boolean {
        return if(validators.isEmpty()) {
            false
        } else {
            var isValid = true
            for (validator in validators) {
                isValid = isValid && validator.isValid(content)
            }
            isValid
        }
    }
}