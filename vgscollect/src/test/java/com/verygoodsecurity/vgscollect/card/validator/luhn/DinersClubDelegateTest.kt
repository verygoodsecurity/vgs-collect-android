package com.verygoodsecurity.vgscollect.card.validator.luhn

import com.verygoodsecurity.vgscollect.view.card.validation.payment.brand.LuhnCheckSumValidator
import org.junit.Assert.assertTrue
import org.junit.Test

class DinersClubDelegateTest {

    @Test
    fun detectDinersClub_1() {
        val dinClub = LuhnCheckSumValidator()

        val state = dinClub.isValid("30569309025904")

        assertTrue(state)
    }

    @Test
    fun detectDinersClub_2() {
        val dinClub = LuhnCheckSumValidator()

        val state = dinClub.isValid("38520000023237")

        assertTrue(state)
    }

    @Test
    fun detectDinersClub_3() {
        val dinClub = LuhnCheckSumValidator()

        val state = dinClub.isValid("38000000000006")

        assertTrue(state)
    }

    @Test
    fun detectDinersClub_4() {
        val dinClub = LuhnCheckSumValidator()

        val state = dinClub.isValid("36256000000725")

        assertTrue(state)
    }

    @Test
    fun detectDinersClub_5() {
        val dinClub = LuhnCheckSumValidator()

        val state = dinClub.isValid("36256000000998")

        assertTrue(state)
    }

    @Test
    fun detectDinersClub_6() {
        val dinClub = LuhnCheckSumValidator()

        val state = dinClub.isValid("36256000000634")

        assertTrue(state)
    }

    @Test
    fun detectDinersClub_7() {
        val dinClub = LuhnCheckSumValidator()

        val state = dinClub.isValid("38865000000705")

        assertTrue(state)
    }

    @Test
    fun detectDinersClub_8() {
        val dinClub = LuhnCheckSumValidator()

        val state = dinClub.isValid("30450000000985")

        assertTrue(state)
    }

    @Test
    fun detectDinersClub_9() {
        val dinClub = LuhnCheckSumValidator()

        val state = dinClub.isValid("36700102000000")

        assertTrue(state)
    }

    @Test
    fun detectDinersClub_10() {
        val dinClub = LuhnCheckSumValidator()

        val state = dinClub.isValid("36148900647913")

        assertTrue(state)
    }
}