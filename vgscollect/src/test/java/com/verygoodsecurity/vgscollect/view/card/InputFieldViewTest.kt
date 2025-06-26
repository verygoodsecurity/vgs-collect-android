package com.verygoodsecurity.vgscollect.view.card

import android.app.Activity
import android.content.res.ColorStateList
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.TestApplication
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.widget.CardVerificationCodeEditText
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

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
        val internal = view.statePreparer.getView()
        assertNotNull(internal)
    }

    @Test
    fun test_attach_view() {
        view.onAttachedToWindow()

        assertEquals(1, view.childCount)
    }

    @Test
    fun test_field_name() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val fieldName = "card"
        view.setFieldName(fieldName)
        assertEquals(fieldName, view.getFieldName())
        assertEquals(fieldName, (child as BaseInputField).tag)

        val fieldNameId = R.string.sdk_name
        val text = activity.resources.getString(R.string.sdk_name)
        view.setFieldName(fieldNameId)
        assertEquals(text, view.getFieldName())
        assertEquals(text, child.tag)
    }

    @Test
    fun test_enabled() {
        view.isEnabled = false

        assertEquals(false, view.statePreparer.getView().isEnabled)

        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        assertFalse((child as BaseInputField).isEnabled)
    }

    @Test
    fun test_text_size() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setTextSize(12f)

        assertEquals(12f, (child as BaseInputField).textSize)

        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, 12f)

        assertEquals(12f, child.textSize)
    }

    @Test
    fun test_hint() {
        val hint = "hint"
        view.setHint(hint)

        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        assertEquals(hint, (child as BaseInputField).hint)
    }

    @Test
    fun test_text() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val text1 = "text"
        view.setText(text1)
        assertEquals(text1, (child as BaseInputField).text.toString())

        val text2 = "text_2"
        view.setText(text2, TextView.BufferType.SPANNABLE)
        assertEquals(text2, child.text.toString())

        val textId1 = R.string.sdk_name
        val text = activity.resources.getString(R.string.sdk_name)
        view.setText(textId1, TextView.BufferType.SPANNABLE)
        assertEquals(text, child.text.toString())

        view.setText(textId1)
        assertEquals(text, child.text.toString())
    }

    @Test
    fun test_set_hint_text_color() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val myList = ColorStateList(arrayOf(intArrayOf()), intArrayOf(android.R.color.black))
        val color = android.R.color.black
        view.setHintTextColor(myList)
        assertEquals(color, (child as BaseInputField).hintTextColors.defaultColor)

        val color2 = android.R.color.white
        view.setHintTextColor(color2)
        assertEquals(color2, child.hintTextColors.defaultColor)
    }

    @Test
    fun test_text_color() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val color = android.R.color.black
        view.setTextColor(color)
        assertEquals(color, (child as BaseInputField).textColors.defaultColor)
    }

    @Test
    fun test_max_lines() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val maxLines = 12
        view.setMaxLines(maxLines)
        assertEquals(maxLines, (child as BaseInputField).maxLines)
    }

    @Test
    fun test_min_lines() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val minLines = 12
        view.setMinLines(minLines)
        assertEquals(minLines, (child as BaseInputField).minLines)
    }

    @Test
    fun test_ellipsize() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val ellipSize1 = TextUtils.TruncateAt.END
        view.setEllipsize(ellipSize1)
        assertEquals(ellipSize1, (child as BaseInputField).ellipsize)

        val ellipSizeInt = 2
        val ellipSize2 = TextUtils.TruncateAt.MIDDLE
        view.setEllipsize(ellipSizeInt)
        assertEquals(ellipSize2, child.ellipsize)
    }

    @Test
    fun test_gravity() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val gravity = Gravity.CENTER
        view.setGravity(gravity)
        assertEquals(gravity, (child as BaseInputField).gravity)
        assertEquals(gravity, view.getGravity())
    }

    @Test
    fun test_set_cursor_visible() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val visibility = false
        view.setCursorVisible(visibility)
        assertFalse((child as BaseInputField).isCursorVisible)
    }

    @Test
    fun test_set_is_required() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val visibility = true
        view.setIsRequired(visibility)
        assertTrue((child as BaseInputField).isRequired)
        assertTrue(view.isRequired())
    }

    @Test
    fun test_next_focus_down_id() {
        val id = 19011918
        view.nextFocusDownId = id
        val internal = view.statePreparer.getView()

        assertEquals(view.nextFocusDownId, id)
        assertEquals(internal.nextFocusDownId, id)
    }

    @Test
    fun test_next_focus_forward_id() {
        val id = 19011918
        view.nextFocusForwardId = id
        val internal = view.statePreparer.getView()

        assertEquals(view.nextFocusForwardId, id)
        assertEquals(internal.nextFocusForwardId, id)
    }

    @Test
    fun test_next_focus_left_id() {
        val id = 19011918
        view.nextFocusLeftId = id
        val internal = view.statePreparer.getView()

        assertEquals(view.nextFocusLeftId, id)
        assertEquals(internal.nextFocusLeftId, id)
    }

    @Test
    fun test_next_focus_right_id() {
        val id = 19011918
        view.nextFocusRightId = id
        val internal = view.statePreparer.getView()

        assertEquals(view.nextFocusRightId, id)
        assertEquals(internal.nextFocusRightId, id)
    }

    @Test
    fun test_next_focus_up_id() {
        val id = 19011918
        view.nextFocusUpId = id
        val internal = view.statePreparer.getView()

        assertEquals(view.nextFocusUpId, id)
        assertEquals(internal.nextFocusUpId, id)
    }

    @Test
    fun test_set_ime_options() {
        view.setImeOptions(EditorInfo.IME_ACTION_NEXT)

        assertEquals(view.getImeOptions(), EditorInfo.IME_ACTION_NEXT)
    }

    @Test
    fun test_add_on_text_change_listener() {
        val listener = mock(InputFieldView.OnTextChangedListener::class.java)
        view.addOnTextChangeListener(listener)
        view.setText("test")

        verify(listener).onTextChange(view, false)
    }

    @Test
    fun test_remove_on_text_change_listener() {
        val listener = mock(InputFieldView.OnTextChangedListener::class.java)
        view.addOnTextChangeListener(listener)
        view.removeTextChangedListener(listener)
        view.setText("test")

        verify(listener, times(0)).onTextChange(view, false)
    }

    @Test
    fun test_set_is_focusable() {
        view.isFocusable = false

        assertEquals(view.isFocusable, false)
    }

    @Test
    fun tests_request_focus() {
        view.onAttachedToWindow()
        view.requestFocus()

        assertEquals(view.hasFocus(), true)
        assertEquals(view.isFocused, true)
    }

    @Test
    fun test_background() {
        view.background = ContextCompat.getDrawable(activity, android.R.drawable.edit_text)
        assertEquals(view.statePreparer.getView().background, view.background)
    }
}