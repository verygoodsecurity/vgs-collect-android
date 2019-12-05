package com.verygoodsecurity.vgscollect.card.filter

import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.card.CustomCardBrand
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import junit.framework.Assert.assertTrue
import org.junit.Test

class CardBrandFilterTest {

    @Test
    fun detectCustomBrand() {
        val c1 = CustomCardBrand("^123", "VG_Search", drawableResId = R.drawable.amazon)
        val c2 = CustomCardBrand("^777", "VGS", drawableResId = R.drawable.jcb)
        val list = arrayOf(c1, c2)
        val filter = CardBrandFilter(list, null)

        val testItem1 = filter.detect("1234 5611 1233 5412")
        assertTrue(testItem1?.resId == R.drawable.amazon)

        val testItem2 = filter.detect("7773 3423")
        assertTrue(testItem2?.resId == R.drawable.jcb)
    }
}