package com.verygoodsecurity.vgscollect.card.filter.brand

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DinnersClubTest {

    private lateinit var filter: VGSCardFilter

    @Before
    fun setupFilter() {
        filter = CardBrandFilter()
    }

    @Test
    fun test_1() {
        val brand = filter.detect("305")
        Assert.assertEquals(brand.name, CardType.DINCLUB.name)
    }

    @Test
    fun test_2() {
        val brand = filter.detect("3056")
        Assert.assertEquals(brand.name, CardType.DINCLUB.name)
    }

    @Test
    fun test_3() {
        val brand = filter.detect("38")
        Assert.assertEquals(brand.name, CardType.DINCLUB.name)
    }

    @Test
    fun test_4() {
        val brand = filter.detect("36")
        Assert.assertEquals(brand.name, CardType.DINCLUB.name)
    }

    @Test
    fun test_5() {
        val brand = filter.detect("367")
        Assert.assertEquals(brand.name, CardType.DINCLUB.name)
    }
}