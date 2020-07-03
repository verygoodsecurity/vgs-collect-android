package com.verygoodsecurity.vgscollect.card

import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.card.BrandParams
import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.CardBrand
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class CustomBrandTest {

    @Test
    fun test_custom_brand_params_full() {
        val mask = "### ### ### #####"
        val alg = ChecksumAlgorithm.LUHN
        val rangeNumber = arrayOf(14,16,19)
        val rangeCVC = arrayOf(3,5)

        val params = BrandParams(mask, alg, rangeNumber, rangeCVC)

        assertEquals(alg, params.algorithm)
        assertEquals(mask, params.mask)
        assertArrayEquals(rangeNumber, params.rangeNumber)
        assertArrayEquals(rangeCVC, params.rangeCVV)
    }

    @Test
    fun test_custom_brand_full() {
        val mask = "### ### ### #####"
        val alg = ChecksumAlgorithm.LUHN
        val rangeNumber = arrayOf(14,16,19)
        val rangeCVC = arrayOf(3,5)
        val params = BrandParams(mask, alg, rangeNumber, rangeCVC)

        val regex = "^12333"
        val name = "VG_Search"
        val resId = R.drawable.ic_card_front_preview_dark

        val brand = CardBrand(regex, name, resId, params)

        assertEquals(regex, brand.regex)
        assertEquals(name, brand.cardBrandName)
        assertEquals(resId, brand.drawableResId)

        assertEquals(alg, brand.params.algorithm)
        assertEquals(mask, brand.params.mask)
        assertArrayEquals(rangeNumber, brand.params.rangeNumber)
        assertArrayEquals(rangeCVC, brand.params.rangeCVV)
    }

    @Test
    fun test_custom_brand_default() {
        val regex = "^12333"
        val name = "VG_Search"

        val brand = CardBrand(regex, name)

        assertEquals(regex, brand.regex)
        assertEquals(name, brand.cardBrandName)
        assertEquals("#### #### #### #### ###", brand.params.mask)
        assertEquals(0, brand.drawableResId)
    }

    @Test
    fun test_custom_brand_with_mask() {
        val regex = "^12333"
        val name = "VG_Search"
        val mask = "### ### ### #####"

        val brand = CardBrand(regex, name)

        assertEquals(regex, brand.regex)
        assertEquals(name, brand.cardBrandName)
        assertEquals(0, brand.drawableResId)
    }

    @Test
    fun test_custom_brand_with_res() {
        val regex = "^12333"
        val name = "VG_Search"
        val resId = R.drawable.ic_card_front_preview_dark

        val brand = CardBrand(regex, name, resId)

        assertEquals(regex, brand.regex)
        assertEquals(name, brand.cardBrandName)
        assertEquals("#### #### #### #### ###", brand.params.mask)
        assertEquals(resId, brand.drawableResId)
    }
}