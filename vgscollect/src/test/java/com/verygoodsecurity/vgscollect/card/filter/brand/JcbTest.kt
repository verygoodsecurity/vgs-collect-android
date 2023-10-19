package com.verygoodsecurity.vgscollect.card.filter.brand

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class JcbTest {

    private var filter: VGSCardFilter = CardBrandFilter()

    @Test
    fun detect() {
        val expectedCardBrand = CardType.JCB.name

        cards().forEach {
            val actualCardBrand = filter.detect(it).name
            Assert.assertEquals(
                "CARD: $it",
                expectedCardBrand,
                actualCardBrand
            )
        }
    }

    private fun cards() = listOf(
        "3566002020360505",
        "3530111333300000",
        "3566111111111113",
        "3566000000000000",
        "3566000000001016",
        "3566000000001024",
        "3566000000001032",
        "3566000000002006",
        "3569990000000009",
        "3528000700000000",
        "2131000000000016",
        "1800000000000018",
    )
}