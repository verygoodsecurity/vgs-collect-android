package com.verygoodsecurity.vgscollect.card.filter.brand

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DiscoverTest {

    private lateinit var filter: VGSCardFilter

    @Before
    fun setupFilter() {
        filter = CardBrandFilter()
    }

    @Test
    fun test_1() {
        val brand = filter.detect("6011")
        Assert.assertEquals(brand.name, CardType.DISCOVER.name)
    }

    @Test
    fun test_2() {
        val brand = filter.detect("65")
        Assert.assertEquals(brand.name, CardType.DISCOVER.name)
    }

    @Test
    fun test_3() {
        val brand = filter.detect("644")
        Assert.assertEquals(brand.name, CardType.DISCOVER.name)
    }

    @Test
    fun test_4() {
        val brand = filter.detect("645")
        Assert.assertEquals(brand.name, CardType.DISCOVER.name)
    }

    @Test
    fun test_5() {
        val brand = filter.detect("646")
        Assert.assertEquals(brand.name, CardType.DISCOVER.name)
    }

    @Test
    fun test_6() {
        val brand = filter.detect("647")
        Assert.assertEquals(brand.name, CardType.DISCOVER.name)
    }

    @Test
    fun test_7() {
        val brand = filter.detect("648")
        Assert.assertEquals(brand.name, CardType.DISCOVER.name)
    }

    @Test
    fun test_8() {
        val brand = filter.detect("649")
        Assert.assertEquals(brand.name, CardType.DISCOVER.name)
    }

    @Test
    fun test_9() {
        val brand = filter.detect("622")
        Assert.assertEquals(brand.name, CardType.DISCOVER.name)
    }

    @Test
    fun test_10() {
        val brand = filter.detect("60110")
        Assert.assertEquals(brand.name, CardType.DISCOVER.name)
    }

    @Test
    fun test_11() {
        val brand = filter.detect("659")
        Assert.assertEquals(brand.name, CardType.DISCOVER.name)
    }

    @Test
    fun test_12() {
        val brand = filter.detect("6456")
        Assert.assertEquals(brand.name, CardType.DISCOVER.name)
    }
}