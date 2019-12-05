package com.verygoodsecurity.vgscollect.card.filter

import com.verygoodsecurity.vgscollect.view.card.filter.DefaultCardBrandFilter
import com.verygoodsecurity.vgscollect.view.text.validation.card.CardType
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultCardBrandFilterTest {
    val filter = DefaultCardBrandFilter(CardType.values(), null)

    @Test
    fun detectEloBrand() {
        val brand = filter.detect("6362970000457013")
        assertTrue(brand?.name == CardType.ELO.name)
    }

    @Test
    fun detectVisaElectronBrand() {
        val brand = filter.detect("4917300800000000")
        assertTrue(brand?.name == CardType.VISA_ELECTRON.name)
    }

    @Test
    fun detectMaestroBrand() {
        val brand = filter.detect("6759649826438453")
        assertTrue(brand?.name == CardType.MAESTRO.name)
    }

    @Test
    fun detectForbrugsforeningenBrand() {
        val brand = filter.detect("600722")
        assertTrue(brand?.name == CardType.FORBRUGSFORENINGEN.name)
    }

    @Test
    fun detectDankortBrand() {
        val brand = filter.detect("5019717010103742")
        assertTrue(brand?.name == CardType.DANKORT.name)
    }

    @Test
    fun detectVisaBrand() {
        val brand = filter.detect("4111111111111111")
        assertTrue(brand?.name == CardType.VISA.name)
    }

    @Test
    fun detectMastercardBrand() {
        val brand = filter.detect("5555555555555555")
        assertTrue(brand?.name == CardType.MASTERCARD.name)
    }

    @Test
    fun detectAmericanExpressBrand() {
        val brand = filter.detect("378282246310005")
        assertTrue(brand?.name == CardType.AMERICAN_EXPRESS.name)
    }

    @Test
    fun detectHipercardBrand() {
        val brand = filter.detect("6062826786276634")
        assertTrue(brand?.name == CardType.HIPERCARD.name)
    }

    @Test
    fun detectDinClubBrand() {
        val brand = filter.detect("30043277253249")
        assertTrue(brand?.name == CardType.DINCLUB.name)
    }

    @Test
    fun detectDiscoverBrand() {
        val brand = filter.detect("6011000000000004")
        assertTrue(brand?.name == CardType.DISCOVER.name)
    }

    @Test
    fun detectJcbBrand() {
        val brand = filter.detect("3566002020360505")
        assertTrue(brand?.name == CardType.JCB.name)
    }

    @Test
    fun detectUnionPayBrand() {
        val brand = filter.detect("6212345678901232")
        assertTrue(brand?.name == CardType.UNIONPAY.name)
    }

    @Test
    fun detectLaserBrand() {
        val brand = filter.detect("670676038979126821")
        assertTrue(brand?.name == CardType.LASER.name)
    }
}