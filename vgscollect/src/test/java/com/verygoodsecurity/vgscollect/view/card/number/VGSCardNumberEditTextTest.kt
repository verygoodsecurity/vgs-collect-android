package com.verygoodsecurity.vgscollect.view.card.number

import android.app.Activity
import android.os.Build
import android.view.Gravity
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.internal.CardInputField
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class VGSCardNumberEditTextTest {

    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var view: VGSCardNumberEditText


    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        view = VGSCardNumberEditText(activity)
    }

    @Test
    fun test_view() {
        view.onAttachedToWindow()
        val internal = view.getView()
        assertNotNull(internal)
    }

    @Test
    fun test_check_internal_view() {
        val internal = view.getView()
        assertNotNull(internal)

        val child = view.getView()
        Assert.assertTrue(child is CardInputField)
    }

    @Test
    fun test_attach_view() {
        view.onAttachedToWindow()

        assertEquals(1, view.childCount)
    }


    @Test
    fun test_field_type() {
        val type = view.getFieldType()
        Assert.assertEquals(FieldType.CARD_NUMBER, type)
    }


    @Test
    fun test_set_divider() {
        assertNotNull(view)

        view.setDivider(' ')
        assertEquals(' ', view.getDivider())
    }

    @Test
    fun test_set_card_brand_icon_gravity_start() {
        assertNotNull(view)

        view.setCardBrandIconGravity(Gravity.START)
        assertEquals(Gravity.START, view.getCardPreviewIconGravity())
    }

    @Test
    fun test_set_card_brand_icon_gravity_left() {
        assertNotNull(view)

        view.setCardBrandIconGravity(Gravity.LEFT)
        assertEquals(Gravity.LEFT, view.getCardPreviewIconGravity())
    }

    @Test
    fun test_set_card_brand_icon_gravity_end() {
        assertNotNull(view)

        view.setCardBrandIconGravity(Gravity.END)
        assertEquals(Gravity.END, view.getCardPreviewIconGravity())
    }

    @Test
    fun test_set_card_brand_icon_gravity_right() {
        assertNotNull(view)

        view.setCardBrandIconGravity(Gravity.RIGHT)
        assertEquals(Gravity.RIGHT, view.getCardPreviewIconGravity())
    }

    @Test
    fun test_set_card_brand_icon_gravity_wrong() {
        assertNotNull(view)

        view.setCardBrandIconGravity(Gravity.TOP)
        assertEquals(Gravity.END, view.getCardPreviewIconGravity())
        view.setCardBrandIconGravity(Gravity.BOTTOM)
        assertEquals(Gravity.END, view.getCardPreviewIconGravity())
        view.setCardBrandIconGravity(Gravity.NO_GRAVITY)
        assertEquals(Gravity.NO_GRAVITY, view.getCardPreviewIconGravity())
        view.setCardBrandIconGravity(Gravity.CENTER_VERTICAL)
        assertEquals(Gravity.END, view.getCardPreviewIconGravity())
        view.setCardBrandIconGravity(Gravity.CENTER_HORIZONTAL)
        assertEquals(Gravity.END, view.getCardPreviewIconGravity())
    }
}