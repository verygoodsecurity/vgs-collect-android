package com.verygoodsecurity.vgscollect.view.card

import android.app.Activity
import android.content.res.ColorStateList
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.TestApplication
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.widget.CardVerificationCodeEditText
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@Ignore
@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class InputFieldViewTest {
    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var view: InputFieldView

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        view = CardVerificationCodeEditText(activity)
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
        val child = view.getView()
        assertTrue(child is BaseInputField)

        val FIELD_NAME = "card"
        view.setFieldName(FIELD_NAME)
        Assert.assertEquals(FIELD_NAME, view.getFieldName())
        Assert.assertEquals(FIELD_NAME, (child as BaseInputField).tag)

        val FIELD_NAME_ID = R.string.sdk_name
        var TEXT = activity.resources.getString(R.string.sdk_name)
        view.setFieldName(FIELD_NAME_ID)
        Assert.assertEquals(TEXT, view.getFieldName())
        Assert.assertEquals(TEXT, child.tag)
    }

    @Test
    fun test_enabled() {
        view.isEnabled = false

        Assert.assertEquals(false, view.getView().isEnabled)

        val child = view.getView()
        assertTrue(child is BaseInputField)

        assertFalse((child as BaseInputField).isEnabled)
    }

    @Test
    fun test_text_size() {
        val child = view.getView()
        assertTrue(child is BaseInputField)

        view.setTextSize(12f)

        Assert.assertEquals(12f, (child as BaseInputField).textSize)

        view.setTextSize(TypedValue.COMPLEX_UNIT_PX,12f)

        Assert.assertEquals(12f, child.textSize)
    }

    @Test
    fun test_hint() {
        val HINT = "hint"
        view.setHint(HINT)

        val child = view.getView()
        assertTrue(child is BaseInputField)

        Assert.assertEquals(HINT, (child as BaseInputField).hint)
    }

    @Test
    fun test_text() {
        val child = view.getView()
        assertTrue(child is BaseInputField)

        var TEXT1 = "text"
        view.setText(TEXT1)
        Assert.assertEquals(TEXT1, (child as BaseInputField).text.toString())

        var TEXT2 = "text_2"
        view.setText(TEXT2, TextView.BufferType.SPANNABLE)
        Assert.assertEquals(TEXT2, child.text.toString())

        var TEXT_ID_1 = R.string.sdk_name
        var TEXT = activity.resources.getString(R.string.sdk_name)
        view.setText(TEXT_ID_1, TextView.BufferType.SPANNABLE)
        Assert.assertEquals(TEXT, child.text.toString())

        view.setText(TEXT_ID_1)
        Assert.assertEquals(TEXT, child.text.toString())
    }

    @Test
    fun test_set_hint_text_color() {
        val child = view.getView()
        assertTrue(child is BaseInputField)

        val myList =  ColorStateList(arrayOf(intArrayOf()), intArrayOf(android.R.color.black))
        val COLOR = android.R.color.black
        view.setHintTextColor(myList)
        Assert.assertEquals(COLOR, (child as BaseInputField).hintTextColors.defaultColor)

        val COLOR_2 = android.R.color.white
        view.setHintTextColor(COLOR_2)
        Assert.assertEquals(COLOR_2, child.hintTextColors.defaultColor)
    }

    @Test
    fun test_text_color() {
        val child = view.getView()
        assertTrue(child is BaseInputField)

        val COLOR = android.R.color.black
        view.setTextColor(COLOR)
        Assert.assertEquals(COLOR, (child as BaseInputField).textColors.defaultColor)
    }

    @Test
    fun test_max_lines() {
        val child = view.getView()
        assertTrue(child is BaseInputField)

        val MAX_LINES = 12
        view.setMaxLines(MAX_LINES)
        Assert.assertEquals(MAX_LINES, (child as BaseInputField).maxLines)
    }

    @Test
    fun test_min_lines() {
        val child = view.getView()
        assertTrue(child is BaseInputField)

        val MIN_LINES = 12
        view.setMinLines(MIN_LINES)
        Assert.assertEquals(MIN_LINES, (child as BaseInputField).minLines)
    }

    @Test
    fun test_ellipsize() {
        val child = view.getView()
        assertTrue(child is BaseInputField)

        val ELLIPSIZE_1 = TextUtils.TruncateAt.END
        view.setEllipsize(ELLIPSIZE_1)
        Assert.assertEquals(ELLIPSIZE_1, (child as BaseInputField).ellipsize)

        val ELLIPSIZE_INT = 2
        val ELLIPSIZE_2 = TextUtils.TruncateAt.MIDDLE
        view.setEllipsize(ELLIPSIZE_INT)
        Assert.assertEquals(ELLIPSIZE_2, child.ellipsize)
    }

    @Test
    fun test_gravity() {
        val child = view.getView()
        assertTrue(child is BaseInputField)

        val GRAVITY = Gravity.CENTER
        view.setGravity(GRAVITY)
        Assert.assertEquals(GRAVITY, (child as BaseInputField).gravity)
        Assert.assertEquals(GRAVITY, view.getGravity())
    }

    @Test
    fun test_set_cursor_visible() {
        val child = view.getView()
        assertTrue(child is BaseInputField)

        val VISIBILITY = false
        view.setCursorVisible(VISIBILITY)
        assertFalse((child as BaseInputField).isCursorVisible)
    }

    @Test
    fun test_set_is_required() {
        val child = view.getView()
        assertTrue(child is BaseInputField)

        val VISIBILITY = true
        view.setIsRequired(VISIBILITY)
        assertTrue((child as BaseInputField).isRequired)
        assertTrue(view.isRequired())
    }

    @Test
    fun test_next_focus_down_id() {
        val id = 19011918
        view.nextFocusDownId = id
        val internal = view.getView()

        Assert.assertEquals(view.nextFocusDownId, id)
        Assert.assertEquals(internal.nextFocusDownId, id)
    }

    @Test
    fun test_next_focus_forward_id() {
        val id = 19011918
        view.nextFocusForwardId = id
        val internal = view.getView()

        Assert.assertEquals(view.nextFocusForwardId, id)
        Assert.assertEquals(internal.nextFocusForwardId, id)
    }

    @Test
    fun test_next_focus_left_id() {
        val id = 19011918
        view.nextFocusLeftId = id
        val internal = view.getView()

        Assert.assertEquals(view.nextFocusLeftId, id)
        Assert.assertEquals(internal.nextFocusLeftId, id)
    }

    @Test
    fun test_next_focus_right_id() {
        val id = 19011918
        view.nextFocusRightId = id
        val internal = view.getView()

        Assert.assertEquals(view.nextFocusRightId, id)
        Assert.assertEquals(internal.nextFocusRightId, id)
    }

    @Test
    fun test_next_focus_up_id() {
        val id = 19011918
        view.nextFocusUpId = id
        val internal = view.getView()

        Assert.assertEquals(view.nextFocusUpId, id)
        Assert.assertEquals(internal.nextFocusUpId, id)
    }

    @Test
    fun test_set_ime_options() {
        view.setImeOptions(EditorInfo.IME_ACTION_NEXT)

        Assert.assertEquals(view.getImeOptions(), EditorInfo.IME_ACTION_NEXT)
    }
}