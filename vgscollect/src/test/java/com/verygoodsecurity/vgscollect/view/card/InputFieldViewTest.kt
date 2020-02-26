package com.verygoodsecurity.vgscollect.view.card

import android.app.Activity
import android.os.Build
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.widget.VGSEditText
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class InputFieldViewTest {
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
    fun test_field_name() {
        val FIELD_NAME = "card"
        view.setFieldName(FIELD_NAME)

        Assert.assertEquals(FIELD_NAME, view.getFieldName())
    }

    @Test
    fun test_enabled() {
        view.isEnabled = false

        Assert.assertEquals(false, view.getView().isEnabled)
    }

    @Test
    fun test_text_size() {
        view.setTextSize(12f)

        val v = view.getView()
        assertTrue(v is BaseInputField)

        Assert.assertEquals(12f, (v as BaseInputField).textSize)
    }

}