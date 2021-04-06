package com.verygoodsecurity.vgscollect.card.filter.brand

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class VisaTest {

    private lateinit var filter: VGSCardFilter

    @Before
    fun setupFilter() {
        filter = CardBrandFilter()
    }

    @Test
    fun test_1() {
        val brand = filter.detect("4")
        Assert.assertEquals(brand.name, CardType.VISA.name)
    }

    @Test
    fun test_2() {
        val brand = filter.detect("41")
        Assert.assertEquals(brand.name, CardType.VISA.name)
    }

    @Test
    fun test_3() {
        val brand = filter.detect("40")
        Assert.assertEquals(brand.name, CardType.VISA.name)
    }

    @Test
    fun test_4() {
        val brand = filter.detect("49")
        Assert.assertEquals(brand.name, CardType.VISA.name)
    }
}