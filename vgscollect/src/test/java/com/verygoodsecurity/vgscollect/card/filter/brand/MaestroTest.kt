package com.verygoodsecurity.vgscollect.card.filter.brand

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.filter.DefaultCardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MaestroTest {

    private lateinit var filter: VGSCardFilter

    @Before
    fun setupFilter() {
        filter = DefaultCardBrandFilter(CardType.values())
    }

    @Test
    fun test_1() {
        val brand = filter.detect("670123")
        Assert.assertEquals(brand?.name, CardType.MAESTRO.name)
    }

    @Test
    fun test_2() {
        val brand = filter.detect("679123")
        Assert.assertEquals(brand?.name, CardType.MAESTRO.name)
    }

    @Test
    fun test_3() {
        val brand = filter.detect("5018123")
        Assert.assertEquals(brand?.name, CardType.MAESTRO.name)
    }

    @Test
    fun test_4() {
        val brand = filter.detect("5020123")
        Assert.assertEquals(brand?.name, CardType.MAESTRO.name)
    }

    @Test
    fun test_5() {
        val brand = filter.detect("5038123")
        Assert.assertEquals(brand?.name, CardType.MAESTRO.name)
    }

    @Test
    fun test_6() {
        val brand = filter.detect("63900123")
        Assert.assertEquals(brand?.name, CardType.MAESTRO.name)
    }

    @Test
    fun test_7() {
        val brand = filter.detect("6390912")
        Assert.assertEquals(brand?.name, CardType.MAESTRO.name)
    }

    @Test
    fun test_8() {
        val brand = filter.detect("50181")
        Assert.assertEquals(brand?.name, CardType.MAESTRO.name)
    }

    @Test
    fun test_9() {
        val brand = filter.detect("50201232")
        Assert.assertEquals(brand?.name, CardType.MAESTRO.name)
    }
}