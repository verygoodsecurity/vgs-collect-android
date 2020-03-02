package com.verygoodsecurity.vgscollect.view.card.date

import android.app.Activity
import android.os.Build
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
import com.verygoodsecurity.vgscollect.view.internal.DateInputField
import com.verygoodsecurity.vgscollect.widget.ExpirationDateEditText
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
class ExpirationDateEditTextTest {

    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var view: ExpirationDateEditText

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        view = ExpirationDateEditText(activity)
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
    fun test_check_internal_view() {
        val internal = view.getView()
        Assert.assertNotNull(internal)

        val child = view.getView()
        Assert.assertTrue(child is DateInputField)
    }

    @Test
    fun test_field_type() {
        val type = view.getFieldType()
        Assert.assertEquals(FieldType.CARD_EXPIRATION_DATE, type)
    }

    @Test
    fun test_dd_MM_yy() {
        view.setDateRegex("dd/MM/yy")
        Assert.assertEquals("dd/MM/yy", view.getDateRegex())

        view.setDateRegex("dd MMMM yyyy")
        Assert.assertEquals("dd MMMM yyyy", view.getDateRegex())
    }

    @Test
    fun test_date_picker_mode() {
        view.setDatePickerMode(DatePickerMode.SPINNER)
        Assert.assertEquals(DatePickerMode.SPINNER, view.getDatePickerMode())

        view.setDatePickerMode(DatePickerMode.CALENDAR)
        Assert.assertEquals(DatePickerMode.CALENDAR, view.getDatePickerMode())

        view.setDatePickerMode(DatePickerMode.INPUT)
        Assert.assertEquals(DatePickerMode.INPUT, view.getDatePickerMode())
    }

    @Test
    fun test_input_picker_mode_failure() {
        val DEFAULT_PATTERN = "MM/yyyy"

        view.setDateRegex("HH:mm dd/yy")
        Assert.assertEquals("HH:mm dd/yy", view.getDateRegex())

        view.setDatePickerMode(DatePickerMode.INPUT)
        Assert.assertEquals(DatePickerMode.INPUT, view.getDatePickerMode())

        Assert.assertEquals(DEFAULT_PATTERN, view.getDateRegex())
    }

        @Test
    fun test_input_picker_mode_failure2() {
        val DEFAULT_PATTERN = "MM/yyyy"

        view.setDatePickerMode(DatePickerMode.INPUT)
        Assert.assertEquals(DatePickerMode.INPUT, view.getDatePickerMode())

        view.setDateRegex("dd/MMMM/yy")
        Assert.assertEquals(DEFAULT_PATTERN, view.getDateRegex())

        view.setDateRegex("dd/mm/yy")
        Assert.assertEquals(DEFAULT_PATTERN, view.getDateRegex())

        view.setDateRegex("dd/mmmm/yy")
        Assert.assertEquals(DEFAULT_PATTERN, view.getDateRegex())

        view.setDateRegex("dd0mmTyy")
        Assert.assertEquals(DEFAULT_PATTERN, view.getDateRegex())

        view.setDateRegex("dd mm/yy'T'")
        Assert.assertEquals(DEFAULT_PATTERN, view.getDateRegex())

        view.setDateRegex("dd mm/yy ")
        Assert.assertEquals(DEFAULT_PATTERN, view.getDateRegex())

        view.setDateRegex("dd mm/yyy")
        Assert.assertEquals(DEFAULT_PATTERN, view.getDateRegex())
    }

    @Test
    fun test_input_picker_mode_right() {
        view.setDatePickerMode(DatePickerMode.INPUT)
        Assert.assertEquals(DatePickerMode.INPUT, view.getDatePickerMode())

        view.setDateRegex("dd/MM/yy")
        Assert.assertEquals("dd/MM/yy", view.getDateRegex())

        view.setDateRegex("dd/MM/yyyy")
        Assert.assertEquals("dd/MM/yyyy", view.getDateRegex())

        view.setDateRegex("MM/yy")
        Assert.assertEquals("MM/yy", view.getDateRegex())

        view.setDateRegex("MM/yy")
        Assert.assertEquals("MM/yy", view.getDateRegex())
    }

    @Test
    fun test_calendar_picker_mode_right() {
        view.setDatePickerMode(DatePickerMode.CALENDAR)
        Assert.assertEquals(DatePickerMode.CALENDAR, view.getDatePickerMode())

        view.setDateRegex("HH:mm dd/MMMM/yy")
        Assert.assertEquals("HH:mm dd/MMMM/yy", view.getDateRegex())
    }

    @Test
    fun test_spinner_picker_mode_right() {
        view.setDatePickerMode(DatePickerMode.SPINNER)
        Assert.assertEquals(DatePickerMode.SPINNER, view.getDatePickerMode())

        view.setDateRegex("HH:mm dd/MMMM/yy")
        Assert.assertEquals("HH:mm dd/MMMM/yy", view.getDateRegex())
    }
}