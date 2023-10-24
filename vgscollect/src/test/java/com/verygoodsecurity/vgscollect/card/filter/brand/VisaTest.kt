package com.verygoodsecurity.vgscollect.card.filter.brand

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class VisaTest {

    private var filter: VGSCardFilter = CardBrandFilter()

    @Test
    fun detect() {
        val expectedCardBrand = CardType.VISA.name

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
        "4111111111111111",
        "4007000000027",
        "4012888818888",
        "4005519200000004",
        "4009348888881881",
        "4012000033330026",
        "4012000077777777",
        "4012888888881881",
        "4217651111111119",
        "4500600000000061",
        "4444333322221111",
        "4119862760338320",
        "4012001038443335",
        "4149011500000147",
        "4000007000000031",
        "4462030000000000",
        "4012001037461114",
        "4012001036853337",
        "4012001037484447",
        "4012001036273338",
        "4263970000005262",
        "4484070000000000",
        "4911830000000",
        "4003830171874018",
        "4012001036983332",
        "4012001038488884",
    )
}