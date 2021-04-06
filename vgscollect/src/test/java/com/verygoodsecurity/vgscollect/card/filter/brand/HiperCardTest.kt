package com.verygoodsecurity.vgscollect.card.filter.brand

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class HiperCardTest {

    private lateinit var filter: VGSCardFilter

    @Before
    fun setupFilter() {
        filter = CardBrandFilter()
    }

    @Test
    fun test_1() {
        val brand = filter.detect("60")
        Assert.assertEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_2() {
        val brand = filter.detect("606")
        Assert.assertEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_3() {
        val brand = filter.detect("384100")
        Assert.assertEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_4() {
        val brand = filter.detect("384140")
        Assert.assertEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_5() {
        val brand = filter.detect("384160")
        Assert.assertEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_6() {
        val brand = filter.detect("637095")
        Assert.assertEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_7() {
        val brand = filter.detect("637568")
        Assert.assertEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_8() {
        val brand = filter.detect("60211")
        Assert.assertEquals(brand.name, CardType.HIPERCARD.name)
        val brand2 = filter.detect("60311")
        Assert.assertEquals(brand2.name, CardType.HIPERCARD.name)
        val brand3 = filter.detect("60411")
        Assert.assertEquals(brand3.name, CardType.HIPERCARD.name)
        val brand4 = filter.detect("60511")
        Assert.assertEquals(brand4.name, CardType.HIPERCARD.name)
        val brand5 = filter.detect("60611")
        Assert.assertEquals(brand5.name, CardType.HIPERCARD.name)
        val brand6 = filter.detect("60711")
        Assert.assertEquals(brand6.name, CardType.HIPERCARD.name)
        val brand7 = filter.detect("60811")
        Assert.assertEquals(brand7.name, CardType.HIPERCARD.name)
        val brand8 = filter.detect("60911")
        Assert.assertEquals(brand8.name, CardType.HIPERCARD.name)
    }
}