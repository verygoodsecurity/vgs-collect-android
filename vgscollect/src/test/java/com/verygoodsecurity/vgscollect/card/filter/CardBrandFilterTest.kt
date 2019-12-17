package com.verygoodsecurity.vgscollect.card.filter

import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.card.CustomCardBrand
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import junit.framework.Assert.assertTrue
import org.junit.Test

class CardBrandFilterTest {

    @Test
    fun detectCustomBrand() {
        val c1 = CustomCardBrand("^123", "VG_Search", drawableResId = R.mipmap.amazon)
        val c2 = CustomCardBrand("^777", "VGS", drawableResId = R.mipmap.jcb)

        val filter = CardBrandFilter()
        filter.add(c1)
        filter.add(c2)

        val testItem1 = filter.detect("1234 5611 1233 5412")
        assertTrue(testItem1?.resId == R.mipmap.amazon)
    }

    @Test
    fun userCustomBrandDivider() {
        val c1 = CustomCardBrand("^123", "VG_Search", drawableResId = R.mipmap.amazon)
        val c2 = CustomCardBrand("^777", "VGS", drawableResId = R.mipmap.jcb)

        val filter = CardBrandFilter(divider = "-")
        filter.add(c1)
        filter.add(c2)

        val testItem1 = filter.detect("1234-5611-1233-5412")
        assertTrue(testItem1?.resId == R.mipmap.amazon)
    }
}