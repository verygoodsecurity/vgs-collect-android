package com.verygoodsecurity.vgscollect.card.filter.brand

import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import org.junit.Assert
import org.junit.Test

class MastercardTest {

    private var filter: VGSCardFilter = CardBrandFilter()

    @Test
    fun detect() {
        val expectedCardBrand = CardType.MASTERCARD.name

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
        "5555555555554444",
        "5454545454545454",
        "5105105105105100",
        "5399999999999999",
        "5232569007637831",
        "5556011778787485",
        "2720992593319364",
        "2222420000001113",
        "2222630000001125",
        "5185540810000019",
        "5420923878724339",
        "5111010030175156",
        "5200828282828210",
        "5204230080000017",
        "5425230000004415",
        "5114610000004778",
        "5114630000009791",
        "5121220000006921",
        "5135020000005871",
        "5100000000000131",
        "5301250070000050",
        "5454609899026213",
        "5123456789012346",
        "5133333333333338",
        "5111111111111118",
        "2223000000000023",
        "5413000000000000",
        "5404000000000068",
        "5404000000000084",
        "5404000000000043",
        "5496198584584769",
    )
}