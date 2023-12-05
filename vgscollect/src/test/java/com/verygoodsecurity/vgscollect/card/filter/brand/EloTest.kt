package com.verygoodsecurity.vgscollect.card.filter.brand

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import org.junit.Assert
import org.junit.Test

class EloTest {

    private var filter: VGSCardFilter = CardBrandFilter()

    @Test
    fun detect() {
        val expectedCardBrand = CardType.ELO.name

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
        "5066991111111118",
        "6362970000457013",
        "5067310000000010",
        "5067312520593847"
    )
}