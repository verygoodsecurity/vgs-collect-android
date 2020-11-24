package com.verygoodsecurity.vgscollect.core.api.analityc.action

class HostNameValidationAction(
    val params:Map<String, Any>
): Action {

    override fun getAttributes(): MutableMap<String, Any> {
        return with(mutableMapOf<String, Any>()) {
            putAll(params)
            put(EVENT, INIT)

            this
        }
    }

    companion object {
        private const val EVENT = "type"
        private const val INIT = "HostNameValidation"
    }
}