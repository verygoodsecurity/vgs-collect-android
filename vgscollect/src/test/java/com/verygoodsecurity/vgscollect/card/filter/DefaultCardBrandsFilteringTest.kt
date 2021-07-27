package com.verygoodsecurity.vgscollect.card.filter

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class DefaultCardBrandsFilteringTest {

    private lateinit var filter: VGSCardFilter

    @Before
    fun setupFilter() {
        filter = CardBrandFilter()
    }

    @Test
    fun test_elo() {
        val brand = filter.detect("6362 9700 0045 7013")
        assertEquals(brand.name, CardType.ELO.name)
    }

    @Test
    fun test_elo_divider() {
        (filter as CardBrandFilter).setDivider("-")

        val brand = filter.detect("6362-9700-0045-7013")
        assertEquals(brand.name, CardType.ELO.name)
    }

    @Test
    fun test_elo_divider_false() {
        (filter as CardBrandFilter).setDivider("-")

        val brand = filter.detect("4011 7800 0045 7013")
        assertNotEquals(brand.name, CardType.ELO.name)
    }

    @Test
    fun test_visa_electron() {
        val brand = filter.detect("4917 3008 0000 0000")
        assertEquals(brand.name, CardType.VISA_ELECTRON.name)
    }

    @Test
    fun test_visa_electron_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("4917-3008-0000-0000")
        assertEquals(brand.name, CardType.VISA_ELECTRON.name)
    }

    @Test
    fun test_visa_electron_divider_false() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("4175 0008 0000 0000")
        assertNotEquals(brand.name, CardType.VISA_ELECTRON.name)
    }

    @Test
    fun test_maestro_13() {
        val brand = filter.detect("6759 6498 2643 8")
        assertEquals(brand.name, CardType.MAESTRO.name)
    }

    @Test
    fun test_maestro_13_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("6759-6498-2643-8")
        assertEquals(brand.name, CardType.MAESTRO.name)
    }

    @Test
    fun test_maestro_14() {
        val brand = filter.detect("6759 6498 2643 84")
        assertEquals(brand.name, CardType.MAESTRO.name)
    }

    @Test
    fun test_maestro_14_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("6759-6498-2643-84")
        assertEquals(brand.name, CardType.MAESTRO.name)
    }

    @Test
    fun test_maestro_15() {
        val brand = filter.detect("6759 6498 2643 845")
        assertEquals(brand.name, CardType.MAESTRO.name)
    }

    @Test
    fun test_maestro_15_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("6759-6498-2643-845")
        assertEquals(brand.name, CardType.MAESTRO.name)
    }

    @Test
    fun test_maestro_16() {
        val brand = filter.detect("6759 6498 2643 8453")
        assertEquals(brand.name, CardType.MAESTRO.name)
    }

    @Test
    fun test_maestro_16_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("6759-6498-2643-8453")
        assertEquals(brand.name, CardType.MAESTRO.name)
    }

    @Test
    fun test_forbrugsforeningen() {
        val brand = filter.detect("6007 2234 3434 3434")
        assertEquals(brand.name, CardType.FORBRUGSFORENINGEN.name)
    }

    @Test
    fun test_forbrugsforeningen_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("6007-2234-3434-3434")
        assertEquals(brand.name, CardType.FORBRUGSFORENINGEN.name)
    }

    @Test
    fun test_dankort() {
        val brand = filter.detect("5019 7170 1010 3742")
        assertEquals(brand.name, CardType.DANKORT.name)
    }

    @Test
    fun test_dankort_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("5019 7170 1010 3742")
        assertEquals(brand.name, CardType.DANKORT.name)
    }

    @Test
    fun test_visa_13() {
        val brand = filter.detect("4111 1111 1111 1")
        assertEquals(brand.name, CardType.VISA.name)
    }

    @Test
    fun test_visa_13_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("4111-1111-1111-1")
        assertEquals(brand.name, CardType.VISA.name)
    }

    @Test
    fun test_visa_16() {
        val brand = filter.detect("4111 1111 1111 1111")
        assertEquals(brand.name, CardType.VISA.name)
    }

    @Test
    fun test_visa_16_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("4111-1111-1111-1111")
        assertEquals(brand.name, CardType.VISA.name)
    }

    @Test
    fun test_visa_19() {
        val brand = filter.detect("4111 1111 1111 1111 111")
        assertEquals(brand.name, CardType.VISA.name)
    }

    @Test
    fun test_visa_19_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("4111-1111-1111-1111-111")
        assertEquals(brand.name, CardType.VISA.name)
    }

    @Test
    fun test_mastercard() {
        val brand = filter.detect("5555 5555 5555 5555")
        assertEquals(brand.name, CardType.MASTERCARD.name)
    }

    @Test
    fun test_mastercard_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("5555-5555-5555-5555")
        assertEquals(brand.name, CardType.MASTERCARD.name)
    }

    @Test
    fun test_american_express() {
        val brand = filter.detect("3782 822463 10005")
        assertEquals(brand.name, CardType.AMERICAN_EXPRESS.name)
    }

    @Test
    fun test_american_express_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("3782-822463-10005")
        assertEquals(brand.name, CardType.AMERICAN_EXPRESS.name)
    }

    @Test
    fun test_hipercard_14() {
        val brand = filter.detect("3841 0067 8627 66")
        assertEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_hipercard_14_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("3841-0067-8627-66")
        assertEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_hipercard_14_divider_false() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("3841 0067 8627 66")
        assertNotEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_hipercard_15() {
        val brand = filter.detect("3841 0067 8627 662")
        assertEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_hipercard_15_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("3841-0067-8627-662")
        assertEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_hipercard_15_divider_false() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("3841 0067 8627 662")
        assertNotEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_hipercard_16() {
        val brand = filter.detect("3841 0067 8627 6623")
        assertEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_hipercard_16_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("3841-0067-8627-6623")
        assertEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_hipercard_16_divider_false() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("3841 0067 8627 6623")
        assertNotEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_hipercard_17() {
        val brand = filter.detect("3841 0067 8627 6623 7")
        assertEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_hipercard_17_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("3841-0067-8627-6623-7")
        assertEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_hipercard_17_divider_false() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("3841 0067 8627 6623 7")
        assertNotEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_hipercard_18() {
        val brand = filter.detect("3841 0067 8627 6623 72")
        assertEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_hipercard_18_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("3841-0067-8627-6623-72")
        assertEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_hipercard_18_divider_false() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("3841 0067 8627 6623 72")
        assertNotEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_hipercard_19() {
        val brand = filter.detect("3841 0067 8627 6623 721")
        assertEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_hipercard_19_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("3841-0067-8627-6623-721")
        assertEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_hipercard_19_divider_false() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("3841 0067 8627 6623 721")
        assertNotEquals(brand.name, CardType.HIPERCARD.name)
    }

    @Test
    fun test_dinclub_14() {
        val brand = filter.detect("3004-327725-3249")
        assertEquals(brand.name, CardType.DINCLUB.name)
    }

    @Test
    fun test_dinclub_divider_14() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("3004-327725-3249")
        assertEquals(brand.name, CardType.DINCLUB.name)
    }

    @Test
    fun test_dinclub_16() {
        val brand = filter.detect("3004-327725-324912")
        assertEquals(brand.name, CardType.DINCLUB.name)
    }

    @Test
    fun test_dinclub_divider_16() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("3004-327725-324912")
        assertEquals(brand.name, CardType.DINCLUB.name)
    }

    @Test
    fun test_discover() {
        val brand = filter.detect("6011 0000 0000 0004")
        assertEquals(brand.name, CardType.DISCOVER.name)
    }

    @Test
    fun test_discover_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("6011-0000-0000-0004")
        assertEquals(brand.name, CardType.DISCOVER.name)
    }

    @Test
    fun test_jcb_16() {
        val brand = filter.detect("3566 0020 2036 0505")
        assertEquals(brand.name, CardType.JCB.name)
    }

    @Test
    fun test_jcb_16_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("3566-0020-2036-0505")
        assertEquals(brand.name, CardType.JCB.name)
    }

    @Test
    fun test_jcb_17() {
        val brand = filter.detect("3566 0020 2036 0505 1")
        assertEquals(brand.name, CardType.JCB.name)
    }

    @Test
    fun test_jcb_17_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("3566-0020-2036-0505-1")
        assertEquals(brand.name, CardType.JCB.name)
    }

    @Test
    fun test_jcb_18() {
        val brand = filter.detect("3566 0020 2036 0505 12")
        assertEquals(brand.name, CardType.JCB.name)
    }

    @Test
    fun test_jcb_18_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("3566-0020-2036-0505-12")
        assertEquals(brand.name, CardType.JCB.name)
    }

    @Test
    fun test_jcb_19() {
        val brand = filter.detect("3566 0020 2036 0505 125")
        assertEquals(brand.name, CardType.JCB.name)
    }

    @Test
    fun test_jcb_19_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("3566-0020-2036-0505-125")
        assertEquals(brand.name, CardType.JCB.name)
    }

    @Test
    fun test_unionpay_16() {
        val brand = filter.detect("6212 3456 7890 1232")
        assertEquals(brand.name, CardType.UNIONPAY.name)
    }

    @Test
    fun test_unionpay_16_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("6212-3456-7890-1232")
        assertEquals(brand.name, CardType.UNIONPAY.name)
    }

    @Test
    fun test_unionpay_17() {
        val brand = filter.detect("6212 3456 7890 1232 7")
        assertEquals(brand.name, CardType.UNIONPAY.name)
    }

    @Test
    fun test_unionpay_17_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("6212-3456-7890-1232-7")
        assertEquals(brand.name, CardType.UNIONPAY.name)
    }

    @Test
    fun test_unionpay_18() {
        val brand = filter.detect("6212 3456 7890 1232 78")
        assertEquals(brand.name, CardType.UNIONPAY.name)
    }

    @Test
    fun test_unionpay_18_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("6212-3456-7890-1232-78")
        assertEquals(brand.name, CardType.UNIONPAY.name)
    }

    @Test
    fun test_unionpay_19() {
        val brand = filter.detect("6212 3456 7890 1232 123")
        assertEquals(brand.name, CardType.UNIONPAY.name)
    }

    @Test
    fun test_unionpay_19_divider() {
        (filter as CardBrandFilter).setDivider("-")
        val brand = filter.detect("6212-3456-7890-1232-123")
        assertEquals(brand.name, CardType.UNIONPAY.name)
    }
}