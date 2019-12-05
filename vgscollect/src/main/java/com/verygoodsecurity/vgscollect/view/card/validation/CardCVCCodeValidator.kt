package com.verygoodsecurity.vgscollect.view.card.validation

class CardCVCCodeValidator:VGSValidator {
//    private val p = Pattern.compile("^[0-9]{3,4}\$")
    override fun clearRules() {}

    override fun addRule(regex: String) {}

    override fun isValid(content: String?): Boolean {
        val c =  content?.toIntOrNull()
        return c != null && content.length >= 3
//        return p.matcher(content).matches()
    }
}