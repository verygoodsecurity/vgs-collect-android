package com.verygoodsecurity.vgscollect.view.card.validation

class CompositeValidator {

    private val validators = mutableListOf<VGSValidator>()

    fun addRule(validator: VGSValidator) {
        validators.add(validator)
    }

    fun clearRules() {
        validators.clear()
    }

    fun validate(content: String): List<String> {
        return if (validators.isEmpty()) {
            listOf(ERROR_MSG)
        } else {
            validators.mapNotNull { it.isValid(content) }
        }
    }

    private companion object {

        private const val ERROR_MSG = "NO_VALIDATION_RULES_ATTACHED_ATTACHED"
    }
}