package com.verygoodsecurity.vgscollect.card.validator.luhn

import com.verygoodsecurity.vgscollect.view.card.validation.card.brand.DinersClubDelegate
import org.junit.Assert.assertTrue
import org.junit.Test

class DinersClubDelegateTest {

    @Test
    fun detectDinersClub14() {
        val dinClub = DinersClubDelegate()

        val state = dinClub.isValid("30043277253249")

        assertTrue(state)
    }
}