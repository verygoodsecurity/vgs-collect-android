package com.verygoodsecurity.vgscollect.view.card.validation.card

import com.verygoodsecurity.vgscollect.view.card.validation.MuttableValidator
import java.util.regex.Pattern

class CardNumberValidator(
    private val divider:String? = " "
) : MuttableValidator {
    private val rules = ArrayList<String>()

    override fun clearRules() {
        rules.clear()
    }

    override fun addRule(regex: String) {
        rules.add(regex)
    }

    override fun isValid(content: String?): Boolean {
        val preparedStr = content?.trim()?.replace(divider?:" ", "")
        for(i in rules.indices) {
            val rule = rules[i]
            val m = Pattern.compile(rule).matcher(preparedStr)
            while (m.find()) {
                return true
            }
        }
        return false
    }
}