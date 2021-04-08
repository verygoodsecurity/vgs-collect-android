package com.verygoodsecurity.vgscollect.card.filter.brand

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class VisaElectronTest {

    private lateinit var filter: VGSCardFilter

    @Before
    fun setupFilter() {
        filter = CardBrandFilter()
    }

    @Test
    fun test_1() {
        val brand = filter.detect("4026")
        Assert.assertEquals(brand.name, CardType.VISA_ELECTRON.name)
    }

    @Test
    fun test_2() {
        val brand = filter.detect("417500")
        Assert.assertEquals(brand.name, CardType.VISA_ELECTRON.name)
    }

    @Test
    fun test_3() {
        val brand = filter.detect("4405")
        Assert.assertEquals(brand.name, CardType.VISA_ELECTRON.name)
    }

    @Test
    fun test_4() {
        val brand = filter.detect("4508")
        Assert.assertEquals(brand.name, CardType.VISA_ELECTRON.name)
    }

    @Test
    fun test_5() {
        val brand = filter.detect("4844")
        Assert.assertEquals(brand.name, CardType.VISA_ELECTRON.name)
    }

    @Test
    fun test_6() {
        val brand = filter.detect("4913")
        Assert.assertEquals(brand.name, CardType.VISA_ELECTRON.name)
    }

    @Test
    fun test_7() {
        val brand = filter.detect("4917")
        Assert.assertEquals(brand.name, CardType.VISA_ELECTRON.name)
    }

    @Test
    fun test_8() {
        val brand = filter.detect("40261")
        Assert.assertEquals(brand.name, CardType.VISA_ELECTRON.name)
    }

    @Test
    fun test_9() {
        val brand = filter.detect("491790")
        Assert.assertEquals(brand.name, CardType.VISA_ELECTRON.name)
    }
}