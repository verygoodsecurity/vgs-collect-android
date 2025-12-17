package com.verygoodsecurity.vgscollect.view.card.validation

/**
 * A validator that is composed of other validators.
 */
class CompositeValidator {

    private val validators = mutableListOf<VGSValidator>()

    /**
     * Adds a validation rule to the composite validator.
     *
     * @param validator The validator to add.
     */
    fun addRule(validator: VGSValidator) {
        validators.add(validator)
    }

    /**
     * Removes a validation rule from the composite validator.
     *
     * @param validator The validator to remove.
     */
    fun removeRule(validator: VGSValidator) {
        validators.remove(validator)
    }

    /**
     * Removes all validation rules from the composite validator.
     */
    fun clearRules() {
        validators.clear()
    }

    /**
     * Validates the given content against all the validation rules.
     *
     * @param content The content to validate.
     * @return A list of error messages, or an empty list if the content is valid.
     */
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