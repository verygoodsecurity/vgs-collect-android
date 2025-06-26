package com.verygoodsecurity.vgscollect.card.filter.brand

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DinnersClubTest {

    private var filter: VGSCardFilter = CardBrandFilter()

    @Test
    fun detect() {
        val expectedCardBrand = CardType.DINCLUB.name

        cards().forEach {
            val actualCardBrand = filter.detect(it).name
            Assert.assertEquals(
                "CARD: $it",
                expectedCardBrand,
                actualCardBrand
            )
        }
    }

    fun cards() = listOf(
        "30569309025904",
        "38520000023237",
        "38000000000006",
        "36256000000725",
        "36256000000998",
        "36256000000634",
        "38865000000705",
        "36700102000000",
        "36148900647913",
    )
}