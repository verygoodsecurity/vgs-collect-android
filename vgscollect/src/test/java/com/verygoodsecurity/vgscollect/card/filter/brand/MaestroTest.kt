package com.verygoodsecurity.vgscollect.card.filter.brand

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MaestroTest {

    private var filter: VGSCardFilter = CardBrandFilter()

    @Test
    fun detect() {
        val expectedCardBrand = CardType.MAESTRO.name

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
        "6771798021000008",
        "6771830999991239",
        "6759649826438453",
        "5020620000000000",
        "6304000000000000",
        "5611111111111113",
        "5711111111111112",
        "5811111111111111",
        "6723680000000000008",
    )
}