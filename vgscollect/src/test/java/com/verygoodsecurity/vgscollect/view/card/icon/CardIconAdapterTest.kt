package com.verygoodsecurity.vgscollect.view.card.icon

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.card.CardType
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Ignore
@RunWith(RobolectricTestRunner::class)
class CardIconAdapterTest {

    private lateinit var adapter:TestAdapter
    private lateinit var context: Context

    @Before
    fun setupAdapter() {
        val activityController = Robolectric.buildActivity(Activity::class.java)
        context = activityController.get()
        adapter = TestAdapter(context)
    }

    @Test
    fun test_get_card_brand() {
        val cardType = CardType.DINCLUB
        val icon = adapter.getItem(cardType, cardType.name, cardType.resId, Rect())

        val cb = adapter.getCardBrand()

        Assert.assertEquals(cardType.resId, cb!!.resId)
        Assert.assertEquals(cardType.name, cb.name)
    }

    @Test
    fun test_bounds() {
        val cardType = CardType.DINCLUB

        val w = context.resources.getDimension(R.dimen.c_icon_size_w).toInt()
        val h = context.resources.getDimension(R.dimen.c_icon_size_h).toInt()

        val item = adapter.getItem(cardType, cardType.name, cardType.resId, Rect())
        val bounds = item.bounds

        Assert.assertEquals(0, bounds.left)
        Assert.assertEquals(0, bounds.top)
        Assert.assertEquals(w, bounds.right)
        Assert.assertEquals(h, bounds.bottom)
    }

    @Test
    fun test_override_icon_with_bounds() {
        val cardType = CardType.AMERICAN_EXPRESS

        val w = context.resources.getDimension(R.dimen.vgsfield_padding).toInt()
        val h = context.resources.getDimension(R.dimen.vgsfield_padding).toInt()

        val item = adapter.getItem(cardType, cardType.name, cardType.resId, Rect())
        val bounds = item.bounds

        val cb = adapter.getCardBrand()
        Assert.assertNotEquals(cardType.resId, cb!!.resId)
        Assert.assertEquals(R.drawable.ic_amex_dark, cb.resId)
        Assert.assertEquals(cardType.name, cb.name)

        Assert.assertEquals(0, bounds.left)
        Assert.assertEquals(0, bounds.top)
        Assert.assertEquals(w, bounds.right)
        Assert.assertEquals(h, bounds.bottom)
    }


    @Test
    fun test_override_icon_without_bounds() {
        val cardType = CardType.VISA

        val w = context.resources.getDimension(R.dimen.c_icon_size_w).toInt()
        val h = context.resources.getDimension(R.dimen.c_icon_size_h).toInt()

        val item = adapter.getItem(cardType, cardType.name, cardType.resId, Rect())
        val bounds = item.bounds

        val cb = adapter.getCardBrand()
        Assert.assertNotEquals(cardType.resId, cb!!.resId)
        Assert.assertEquals(R.drawable.ic_visa_light, cb.resId)
        Assert.assertEquals(cardType.name, cb.name)

        Assert.assertEquals(0, bounds.left)
        Assert.assertEquals(0, bounds.top)
        Assert.assertEquals(w, bounds.right)
        Assert.assertEquals(h, bounds.bottom)
    }

    class TestAdapter(
        val context: Context
    ):CardIconAdapter(context) {

        private var brand:CardBrand? = null

        override fun getIcon(cardType: CardType, name: String?, resId: Int, r:Rect): Drawable {
            return when(cardType) {
                CardType.AMERICAN_EXPRESS -> {
                    brand = CardBrand(name, R.drawable.ic_amex_dark)
                    handleAmex()
                }
                CardType.VISA -> {
                    brand = CardBrand(name, R.drawable.ic_visa_light)
                    getDrawable(R.drawable.ic_visa_light)
                }
                else -> {
                    brand = CardBrand(name, resId)
                    super.getIcon(cardType, name, resId, r)
                }
            }
        }

        private fun handleAmex(): Drawable {
            val c_icon_size_w = context.resources.getDimension(R.dimen.vgsfield_padding).toInt()
            val c_icon_size_h = context.resources.getDimension(R.dimen.vgsfield_padding).toInt()
            val d = getDrawable(R.drawable.ic_amex_dark)
            d.setBounds(0,0,c_icon_size_w, c_icon_size_h)
            return d
        }

        internal fun getCardBrand():CardBrand? {
            return brand
        }

        data class CardBrand(val name:String?, val resId:Int)
    }
}