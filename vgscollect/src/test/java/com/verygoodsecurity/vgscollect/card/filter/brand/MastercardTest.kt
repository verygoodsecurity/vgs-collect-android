package com.verygoodsecurity.vgscollect.card.filter.brand

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MastercardTest {

    private lateinit var filter: VGSCardFilter

    @Before
    fun setupFilter() {
        filter = CardBrandFilter()
    }

    @Test
    fun test_1() {
        val brand = filter.detect("51")
        Assert.assertEquals(brand.name, CardType.MASTERCARD.name)
    }

    @Test
    fun test_2() {
        val brand = filter.detect("52")
        Assert.assertEquals(brand.name, CardType.MASTERCARD.name)
    }

    @Test
    fun test_3() {
        val brand = filter.detect("53")
        Assert.assertEquals(brand.name, CardType.MASTERCARD.name)
    }

    @Test
    fun test_4() {
        val brand = filter.detect("54")
        Assert.assertEquals(brand.name, CardType.MASTERCARD.name)
    }

    @Test
    fun test_5() {
        val brand = filter.detect("55")
        Assert.assertEquals(brand.name, CardType.MASTERCARD.name)
    }

    @Test
    fun test_6() {
        val brand = filter.detect("222123")
        Assert.assertEquals(brand.name, CardType.MASTERCARD.name)
    }

    @Test
    fun test_7() {
        val brand = filter.detect("22223456")
        Assert.assertEquals(brand.name, CardType.MASTERCARD.name)
    }

    @Test
    fun test_8() {
        val brand = filter.detect("271923")
        Assert.assertEquals(brand.name, CardType.MASTERCARD.name)
    }

    @Test
    fun test_9() {
        val brand = filter.detect("272000")
        Assert.assertEquals(brand.name, CardType.MASTERCARD.name)
    }
}