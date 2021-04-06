package com.verygoodsecurity.vgscollect.card.filter.brand

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EloTest {

    private lateinit var filter: VGSCardFilter

    @Before
    fun setupFilter() {
        filter = CardBrandFilter()
    }

    @Test
    fun test_elo1() {
        val brand = filter.detect("6362 97")
        Assert.assertEquals(brand.name, CardType.ELO.name)
    }

    @Test
    fun test_elo2() {
        val brand = filter.detect("5066 99")
        Assert.assertEquals(brand.name, CardType.ELO.name)
    }

    @Test
    fun test_elo3() {
        val brand = filter.detect("6362 97")
        Assert.assertEquals(brand.name, CardType.ELO.name)
    }

    @Test
    fun test_elo4() {
        val brand = filter.detect("5067 31")
        Assert.assertEquals(brand.name, CardType.ELO.name)
    }

}