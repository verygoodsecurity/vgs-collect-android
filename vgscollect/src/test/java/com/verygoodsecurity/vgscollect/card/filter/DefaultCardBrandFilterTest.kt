package com.verygoodsecurity.vgscollect.card.filter

import com.verygoodsecurity.vgscollect.view.card.filter.DefaultCardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.CardType
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultCardBrandFilterTest {
    val filter = DefaultCardBrandFilter(CardType.values(), null)


    @Test
    fun detectBrandWithCustomDivider() {
        val filter = DefaultCardBrandFilter(CardType.values(), null, "-")

        val brand = filter.detect("4111-1111-1111-1111")
        assertTrue(brand?.name == CardType.VISA.name)
    }

    @Test
    fun detectEloBrand() {
        val brand = filter.detect("6362 9700 0045 7013")
        assertTrue(brand?.name == CardType.ELO.name)
    }

    @Test
    fun detectVisaElectronBrand() {
        val brand = filter.detect("4917 3008 0000 0000")
        assertTrue(brand?.name == CardType.VISA_ELECTRON.name)
    }

    @Test
    fun detectMaestroBrand() {
        val brand = filter.detect("6759 6498 2643 8453")
        assertTrue(brand?.name == CardType.MAESTRO.name)
    }

    @Test
    fun detectForbrugsforeningenBrand() {
        val brand = filter.detect("6007 2234 3434 3434")
        assertTrue(brand?.name == CardType.FORBRUGSFORENINGEN.name)
    }

    @Test
    fun detectDankortBrand() {
        val brand = filter.detect("5019 7170 1010 3742")
        assertTrue(brand?.name == CardType.DANKORT.name)
    }

    @Test
    fun detectVisaBrand() {
        val brand = filter.detect("4111 1111 1111 1111")
        assertTrue(brand?.name == CardType.VISA.name)
    }

    @Test
    fun detectMastercardBrand() {
        val brand = filter.detect("5555 5555 5555 5555")
        assertTrue(brand?.name == CardType.MASTERCARD.name)
    }

    @Test
    fun detectAmericanExpressBrand() {
        val brand = filter.detect("3782 8224 6310 005")
        assertTrue(brand?.name == CardType.AMERICAN_EXPRESS.name)
    }

    @Test
    fun detectHipercardBrand() {
        val brand = filter.detect("6062826786276634")
        assertTrue(brand?.name == CardType.HIPERCARD.name)
    }

    @Test
    fun detectDinClubBrand() {
        val brand = filter.detect("3004 3277 2532 49")
        assertTrue(brand?.name == CardType.DINCLUB.name)
    }

    @Test
    fun detectDiscoverBrand() {
        val brand = filter.detect("6011 0000 0000 0004")
        assertTrue(brand?.name == CardType.DISCOVER.name)
    }

    @Test
    fun detectJcbBrand() {
        val brand = filter.detect("3566 0020 2036 0505")
        assertTrue(brand?.name == CardType.JCB.name)
    }

    @Test
    fun detectUnionPayBrand() {
        val brand = filter.detect("6212 3456 7890 1232")
        assertTrue(brand?.name == CardType.UNIONPAY.name)
    }

    @Test
    fun detectLaserBrand() {
        val brand = filter.detect("6706 7603 8979 1268 21")
        assertTrue(brand?.name == CardType.LASER.name)
    }

    @Test
    fun detectNoneBrandType() {
        val brand = filter.detect("0292 7603 8979 1268")
        assertTrue(brand?.name == CardType.NONE.name)
    }
}