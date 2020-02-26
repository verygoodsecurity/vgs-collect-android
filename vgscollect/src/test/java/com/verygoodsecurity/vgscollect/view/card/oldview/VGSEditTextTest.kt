package com.verygoodsecurity.vgscollect.view.card.oldview

import android.app.Activity
import android.os.Build
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.widget.VGSEditText
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class VGSEditTextTest {
    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var view: VGSEditText

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        view = VGSEditText(activity)
    }

    @Test
    fun test_view() {
        view.onAttachedToWindow()
        val internal = view.getView()
        Assert.assertNotNull(internal)
    }

    @Test
    fun test_attach_view() {
        view.onAttachedToWindow()

        Assert.assertEquals(1, view.childCount)
    }

    @Test
    fun test_card_number() {
        view.applyFieldType(FieldType.CARD_NUMBER)
        Assert.assertEquals(FieldType.CARD_NUMBER, view.getFieldType())
    }

    @Test
    fun test_exp_date() {
        view.applyFieldType(FieldType.CARD_EXPIRATION_DATE)
        Assert.assertEquals(FieldType.CARD_EXPIRATION_DATE, view.getFieldType())
    }

    @Test
    fun test_holder_name() {
        view.applyFieldType(FieldType.CARD_HOLDER_NAME)
        Assert.assertEquals(FieldType.CARD_HOLDER_NAME, view.getFieldType())
    }

    @Test
    fun test_cvc() {
        view.applyFieldType(FieldType.CVC)
        Assert.assertEquals(FieldType.CVC, view.getFieldType())
    }

    @Test
    fun test_info() {
        view.applyFieldType(FieldType.INFO)
        Assert.assertEquals(FieldType.INFO, view.getFieldType())
    }
}