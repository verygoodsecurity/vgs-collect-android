package com.verygoodsecurity.vgscollect.card.filter.brand

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AmexTest {

    private lateinit var filter: VGSCardFilter

    @Before
    fun setupFilter() {
        filter = CardBrandFilter()
    }

    @Test
    fun test_1() {
        val brand = filter.detect("37")
        Assert.assertEquals(brand.name, CardType.AMERICAN_EXPRESS.name)
    }

    @Test
    fun test_2() {
        val brand = filter.detect("34")
        Assert.assertEquals(brand.name, CardType.AMERICAN_EXPRESS.name)
    }

    @Test
    fun test_3() {
        val brand = filter.detect("341")
        Assert.assertEquals(brand.name, CardType.AMERICAN_EXPRESS.name)
    }

    @Test
    fun test_4() {
        val brand = filter.detect("379")
        Assert.assertEquals(brand.name, CardType.AMERICAN_EXPRESS.name)
    }
}