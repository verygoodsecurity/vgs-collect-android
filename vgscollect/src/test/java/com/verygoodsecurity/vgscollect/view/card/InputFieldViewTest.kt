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

        val FIELD_NAME = "card"
        view.setFieldName(FIELD_NAME)
        assertEquals(FIELD_NAME, view.getFieldName())
        assertEquals(FIELD_NAME, (child as BaseInputField).tag)

        val FIELD_NAME_ID = R.string.sdk_name
        val TEXT = activity.resources.getString(R.string.sdk_name)
        view.setFieldName(FIELD_NAME_ID)
        assertEquals(TEXT, view.getFieldName())
        assertEquals(TEXT, child.tag)
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
        val HINT = "hint"
        view.setHint(HINT)

        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        assertEquals(HINT, (child as BaseInputField).hint)
    }

    @Test
    fun test_text() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val TEXT1 = "text"
        view.setText(TEXT1)
        assertEquals(TEXT1, (child as BaseInputField).text.toString())

        val TEXT2 = "text_2"
        view.setText(TEXT2, TextView.BufferType.SPANNABLE)
        assertEquals(TEXT2, child.text.toString())

        val TEXT_ID_1 = R.string.sdk_name
        val TEXT = activity.resources.getString(R.string.sdk_name)
        view.setText(TEXT_ID_1, TextView.BufferType.SPANNABLE)
        assertEquals(TEXT, child.text.toString())

        view.setText(TEXT_ID_1)
        assertEquals(TEXT, child.text.toString())
    }

    @Test
    fun test_set_hint_text_color() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val myList = ColorStateList(arrayOf(intArrayOf()), intArrayOf(android.R.color.black))
        val COLOR = android.R.color.black
        view.setHintTextColor(myList)
        assertEquals(COLOR, (child as BaseInputField).hintTextColors.defaultColor)

        val COLOR_2 = android.R.color.white
        view.setHintTextColor(COLOR_2)
        assertEquals(COLOR_2, child.hintTextColors.defaultColor)
    }

    @Test
    fun test_text_color() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val COLOR = android.R.color.black
        view.setTextColor(COLOR)
        assertEquals(COLOR, (child as BaseInputField).textColors.defaultColor)
    }

    @Test
    fun test_max_lines() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val MAX_LINES = 12
        view.setMaxLines(MAX_LINES)
        assertEquals(MAX_LINES, (child as BaseInputField).maxLines)
    }

    @Test
    fun test_min_lines() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val MIN_LINES = 12
        view.setMinLines(MIN_LINES)
        assertEquals(MIN_LINES, (child as BaseInputField).minLines)
    }

    @Test
    fun test_ellipsize() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val ELLIPSIZE_1 = TextUtils.TruncateAt.END
        view.setEllipsize(ELLIPSIZE_1)
        assertEquals(ELLIPSIZE_1, (child as BaseInputField).ellipsize)

        val ELLIPSIZE_INT = 2
        val ELLIPSIZE_2 = TextUtils.TruncateAt.MIDDLE
        view.setEllipsize(ELLIPSIZE_INT)
        assertEquals(ELLIPSIZE_2, child.ellipsize)
    }

    @Test
    fun test_gravity() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val GRAVITY = Gravity.CENTER
        view.setGravity(GRAVITY)
        assertEquals(GRAVITY, (child as BaseInputField).gravity)
        assertEquals(GRAVITY, view.getGravity())
    }

    @Test
    fun test_set_cursor_visible() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val VISIBILITY = false
        view.setCursorVisible(VISIBILITY)
        assertFalse((child as BaseInputField).isCursorVisible)
    }

    @Test
    fun test_set_is_required() {
        val child = view.statePreparer.getView()
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
        view.requestFocus()

        assertEquals(view.hasFocus(), true)
        assertEquals(view.isFocused, true)
    }
}