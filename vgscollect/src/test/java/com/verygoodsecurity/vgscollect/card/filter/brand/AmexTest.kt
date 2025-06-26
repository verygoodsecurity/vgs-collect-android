package com.verygoodsecurity.vgscollect.card.filter.brand

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AmexTest {

    private var filter: VGSCardFilter = CardBrandFilter()

    @Test
    fun detect() {
        val expectedCardBrand = CardType.AMERICAN_EXPRESS.name

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
        "370000000000002",
        "378282246310005",
        "371449635398431",
        "378734493671000",
        "374111111111111",
        "373953192351004",
        "346018484777573",
        "374101000000608",
        "376525000000010",
        "375425000000907",
        "343452000000306",
        "372349000000852",
    )
}