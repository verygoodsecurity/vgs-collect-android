package com.verygoodsecurity.vgscollect.view.card.validation

class CompositeValidator {

    private val validators = mutableListOf<VGSValidator>()

    fun addRule(validator: VGSValidator) {
        validators.add(validator)
    }

    fun removeRule(validator: VGSValidator) {
        validators.remove(validator)
    }

    fun clearRules() {
        validators.clear()
    }

    fun validate(content: String): List<String> {
        return if (validators.isEmpty()) {
            listOf(ERROR_MSG)
        } else {
            validators.mapNotNull { if (!it.isValid(content)) it.errorMsg else null }
        }
    }

    private companion object {

        private const val ERROR_MSG = "NO_VALIDATION_RULES_ATTACHED"
    }
}