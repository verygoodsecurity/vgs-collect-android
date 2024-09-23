package com.verygoodsecurity.vgscollect.core.api.analityc.action

data class InitAction(
    val fieldType: String,
    val isCompose: Boolean,
): Action {

    override fun getAttributes(): MutableMap<String, Any> {
        return with(mutableMapOf<String, Any>()) {
            put(FIELD_TYPE_KEY, fieldType)
            put(UI_KEY,if (isCompose) UI_COMPOSE else UI_XML)
            put(EVENT, INIT)

            this
        }
    }

    companion object {
        private const val EVENT = "type"
        private const val INIT = "Init"
        private const val FIELD_TYPE_KEY = "field"
        private const val UI_KEY = "ui"
        private const val UI_COMPOSE = "compose"
        private const val UI_XML = "xml"
    }
}