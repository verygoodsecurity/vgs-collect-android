package com.verygoodsecurity.vgscollect.card

import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.card.CustomCardBrand
import org.junit.Assert
import org.junit.Test

class CustomBrandTest {

    @Test
    fun test_custom_brand_full() {
        val regex = "^12333"
        val name = "VG_Search"
        val resId = R.drawable.ic_card_front_preview_dark
        val mask = "### ### ### #####"

        val brand = CustomCardBrand(regex, name, resId, mask)

        Assert.assertEquals(regex, brand.regex)
        Assert.assertEquals(name, brand.cardBrandName)
        Assert.assertEquals(mask, brand.mask)
        Assert.assertEquals(resId, brand.drawableResId)
    }

    @Test
    fun test_custom_brand_default() {
        val regex = "^12333"
        val name = "VG_Search"

        val brand = CustomCardBrand(regex, name)

        Assert.assertEquals(regex, brand.regex)
        Assert.assertEquals(name, brand.cardBrandName)
        Assert.assertEquals("#### #### #### #### ###", brand.mask)
        Assert.assertEquals(0, brand.drawableResId)
    }

    @Test
    fun test_custom_brand_with_mask() {
        val regex = "^12333"
        val name = "VG_Search"
        val mask = "### ### ### #####"

        val brand = CustomCardBrand(regex, name, mask = mask)

        Assert.assertEquals(regex, brand.regex)
        Assert.assertEquals(name, brand.cardBrandName)
        Assert.assertEquals(mask, brand.mask)
        Assert.assertEquals(0, brand.drawableResId)
    }

    @Test
    fun test_custom_brand_with_res() {
        val regex = "^12333"
        val name = "VG_Search"
        val resId = R.drawable.ic_card_front_preview_dark

        val brand = CustomCardBrand(regex, name, resId)

        Assert.assertEquals(regex, brand.regex)
        Assert.assertEquals(name, brand.cardBrandName)
        Assert.assertEquals("#### #### #### #### ###", brand.mask)
        Assert.assertEquals(resId, brand.drawableResId)
    }
}