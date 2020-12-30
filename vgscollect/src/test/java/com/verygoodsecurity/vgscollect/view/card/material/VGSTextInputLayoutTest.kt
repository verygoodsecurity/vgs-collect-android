package com.verygoodsecurity.vgscollect.view.card.material

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.TestApplication
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.widget.CardVerificationCodeEditText
import com.verygoodsecurity.vgscollect.widget.VGSTextInputLayout
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class VGSTextInputLayoutTest {
    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var textInputLayout: VGSTextInputLayout

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        textInputLayout = VGSTextInputLayout(activity)
    }

    private fun getVGSTextView(): InputFieldView {
        return CardVerificationCodeEditText(activity).apply {
            this.setBackgroundResource(0)
        }
    }

    private fun attachView() {
        val textView = getVGSTextView()
        textInputLayout.addView(textView)
    }

    @Test
    fun test_is_ready_true() {
        attachView()

        val state = textInputLayout.getFieldState()

        assertTrue(state.isReady())
    }

    @Test
    fun test_wrong_view_is_ready_false() {
        textInputLayout = VGSTextInputLayout(activity)
        val textView = EditText(activity)
        textInputLayout.addView(textView)

        val state = textInputLayout.getFieldState()

        assertFalse(state.isReady())
    }

    @Test
    fun test_no_view_is_ready_false() {
        textInputLayout = VGSTextInputLayout(activity)

        val state = textInputLayout.getFieldState()

        assertFalse(state.isReady())
    }

    @Test
    fun test_attach_first_set_error_enabled() {
        attachView()

        textInputLayout.setErrorEnabled(true)
        val state = textInputLayout.getFieldState()

        assertEquals(true, state.isErrorEnabled)
        assertEquals(true, state.getInternalView().isErrorEnabled)
    }

    @Test
    fun test_attach_first_set_error_string() {
        attachView()

        val TAG = "test"
        textInputLayout.setError(TAG)
        val state = textInputLayout.getFieldState()

        assertEquals(TAG, state.error)
        assertEquals(TAG, state.getInternalView().error)
    }

    @Test
    fun test_attach_first_set_error_res() {
        attachView()

        val TAG = "VGSCollect"
        textInputLayout.setError(R.string.sdk_name)
        val state = textInputLayout.getFieldState()

        assertEquals(TAG, state.error)
        assertEquals(TAG, state.getInternalView().error)
    }

    @Test
    fun test_attach_first_set_hint_string() {
        attachView()

        val TAG = "test"
        textInputLayout.setHint(TAG)
        val state = textInputLayout.getFieldState()

        assertEquals(TAG, state.hint)
        assertEquals(TAG, state.getInternalView().hint)
    }

    @Test
    fun test_attach_first_set_hint_res() {
        attachView()

        val TAG = "VGSCollect"
        textInputLayout.setHint(R.string.sdk_name)
        val state = textInputLayout.getFieldState()

        assertEquals(TAG, state.hint)
        assertEquals(TAG, state.getInternalView().hint)
    }

    @Test
    fun test_attach_first_set_is_hint_animation_enabled() {
        attachView()

        textInputLayout.setHintAnimationEnabled(true)
        val state1 = textInputLayout.getFieldState()

        assertTrue(state1.isHintAnimationEnabled)
        assertTrue(state1.getInternalView().isHintAnimationEnabled)

        textInputLayout.setHintAnimationEnabled(false)
        val state2 = textInputLayout.getFieldState()

        assertFalse(state2.isHintAnimationEnabled)
        assertFalse(state2.getInternalView().isHintAnimationEnabled)
    }

    @Test
    fun test_attach_first_set_is_hint_enabled() {
        attachView()

        textInputLayout.setHintEnabled(true)
        val state1 = textInputLayout.getFieldState()

        assertTrue(state1.isHintEnabled)
        assertTrue(state1.getInternalView().isHintEnabled)

        textInputLayout.setHintEnabled(false)
        val state2 = textInputLayout.getFieldState()

        assertFalse(state2.isHintEnabled)
        assertFalse(state2.getInternalView().isHintEnabled)
    }

    @Test
    fun test_attach_first_set_password_toggle_enabled() {
        attachView()

        textInputLayout.setPasswordToggleEnabled(true)
        val state1 = textInputLayout.getFieldState()

        assertTrue(state1.isPasswordVisibilityToggleEnabled)
        assertTrue(state1.getInternalView().isPasswordVisibilityToggleEnabled)


        textInputLayout.setPasswordToggleEnabled(false)
        val state2 = textInputLayout.getFieldState()

        assertFalse(state2.isPasswordVisibilityToggleEnabled)
        assertFalse(state2.getInternalView().isPasswordVisibilityToggleEnabled)
    }

    @Test
    fun test_attach_first_set_password_visibility_toggle_tint_list() {
        attachView()

        val myList =  ColorStateList(arrayOf(intArrayOf()), intArrayOf(android.R.color.black))
        val COLOR = android.R.color.black

        textInputLayout.setPasswordVisibilityToggleTintList(myList)
        val state = textInputLayout.getFieldState()

        assertEquals(myList, state.passwordToggleTint)
    }

    @Test
    fun test_attach_first_set_password_visibility_toggle_drawable() {
        attachView()

        val DRAWABLE = R.drawable.ic_amex_dark
        textInputLayout.setPasswordVisibilityToggleDrawable(DRAWABLE)
        val state = textInputLayout.getFieldState()

        assertEquals(DRAWABLE, state.passwordVisibilityToggleDrawable)
    }

    @Test
    fun test_attach_first_box_background_mode() {
        attachView()

        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE)
        val state1 = textInputLayout.getFieldState()
        assertEquals(state1.getInternalView().boxBackgroundMode, state1.boxBackgroundMode)
        assertEquals(TextInputLayout.BOX_BACKGROUND_OUTLINE, state1.boxBackgroundMode)

        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_FILLED)
        val state2 = textInputLayout.getFieldState()
        assertEquals(state2.getInternalView().boxBackgroundMode, state2.boxBackgroundMode)
        assertEquals(TextInputLayout.BOX_BACKGROUND_FILLED, state2.boxBackgroundMode)

        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_NONE)
        val state3 = textInputLayout.getFieldState()
        assertEquals(state3.getInternalView().boxBackgroundMode, state3.boxBackgroundMode)
        assertEquals(TextInputLayout.BOX_BACKGROUND_NONE, state3.boxBackgroundMode)
    }

    @Test
    fun test_attach_first_box_background_color() {
        attachView()

        val COLOR = Color.BLUE
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE)
        textInputLayout.setBoxBackgroundColor(COLOR)
        val state = textInputLayout.getFieldState()
        assertEquals(state.getInternalView().boxBackgroundColor, state.boxBackgroundColor)
        assertEquals(COLOR, state.boxBackgroundColor)
    }

    @Test
    fun test_attach_first_box_stroke_color() {
        attachView()

        val COLOR = Color.BLUE
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE)
        textInputLayout.setBoxStrokeColor(COLOR)
        val state = textInputLayout.getFieldState()
        assertEquals(state.getInternalView().boxStrokeColor, state.boxStrokeColor)
        assertEquals(COLOR, state.boxStrokeColor)
    }

    @Test
    fun test_attach_first_set_box_corner_radius() {
        attachView()

        val TopStart = 10f
        val TopEnd = 20f
        val BottomStart = 30f
        val BottomEnd = 40f
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE)
        textInputLayout.setBoxCornerRadius(TopStart, TopEnd, BottomStart, BottomEnd)

        val state = textInputLayout.getFieldState()
        val iv = state.getInternalView()
        val a1 = iv.boxCornerRadiusTopStart
        val a2 = iv.boxCornerRadiusTopEnd
        val a3 = iv.boxCornerRadiusBottomStart
        val a4 = iv.boxCornerRadiusBottomEnd

        //for some reason in 'com.google.android.material:material:1.1.0' lib after set value to boxCornerRadiusBottomStart
        // later this value will return from boxCornerRadiusBottomEnd instead
        //getBoxCornerRadiusBottomEnd return getBottomLeftCornerResolvedSize
        //getBoxCornerRadiusBottomStart return getBottomRightCornerResolvedSize

        assertEquals(iv.boxCornerRadiusBottomEnd, state.boxCornerRadiusBottomStart)
        assertEquals(BottomStart, state.boxCornerRadiusBottomStart)

        assertEquals(iv.boxCornerRadiusBottomStart, state.boxCornerRadiusBottomEnd)
        assertEquals(BottomEnd, state.boxCornerRadiusBottomEnd)

        assertEquals(iv.boxCornerRadiusTopStart, state.boxCornerRadiusTopStart)
        assertEquals(TopStart, state.boxCornerRadiusTopStart)

        assertEquals(iv.boxCornerRadiusTopEnd, state.boxCornerRadiusTopEnd)
        assertEquals(TopEnd, state.boxCornerRadiusTopEnd)
    }

    @Test
    fun test_attach_first_set_padding() {
        attachView()

        val Start = 10
        val Top = 20
        val Bottom = 30
        val End = 40
        textInputLayout.setPadding(Start, Top, End, Bottom)

        val state = textInputLayout.getFieldState()
        val iv = state.getInternalView()

        assertEquals(iv.paddingTop, state.top)
        assertEquals(Top, state.top)

        assertEquals(iv.paddingLeft, state.left)
        assertEquals(Start, state.left)

        assertEquals(iv.paddingRight, state.right)
        assertEquals(End, state.right)

        assertEquals(iv.paddingBottom, state.bottom)
        assertEquals(Bottom, state.bottom)
    }





    @Test
    fun test_attach_last_set_error_enabled() {
        textInputLayout.setErrorEnabled(true)
        attachView()

        val state = textInputLayout.getFieldState()

        assertEquals(true, state.isErrorEnabled)
        assertEquals(true, state.getInternalView().isErrorEnabled)
    }

    @Test
    fun test_attach_last_set_error_string() {
        val TAG = "test"
        textInputLayout.setError(TAG)
        attachView()

        val state = textInputLayout.getFieldState()

        assertEquals(TAG, state.error)
        assertEquals(TAG, state.getInternalView().error)
    }

    @Test
    fun test_attach_last_set_error_res() {
        val TAG = "VGSCollect"
        textInputLayout.setError(R.string.sdk_name)
        attachView()

        val state = textInputLayout.getFieldState()

        assertEquals(TAG, state.error)
        assertEquals(TAG, state.getInternalView().error)
    }

    @Test
    fun test_attach_last_set_hint_string() {
        val TAG = "test"
        textInputLayout.setHint(TAG)
        attachView()

        val state = textInputLayout.getFieldState()

        assertEquals(TAG, state.hint)
        assertEquals(TAG, state.getInternalView().hint)
    }

    @Test
    fun test_attach_last_set_hint_res() {
        val TAG = "VGSCollect"
        textInputLayout.setHint(R.string.sdk_name)
        attachView()

        val state = textInputLayout.getFieldState()

        assertEquals(TAG, state.hint)
        assertEquals(TAG, state.getInternalView().hint)
    }

    @Test
    fun test_attach_last_set_is_hint_animation_enabled() {
        textInputLayout.setHintAnimationEnabled(true)
        attachView()

        val state1 = textInputLayout.getFieldState()

        assertTrue(state1.isHintAnimationEnabled)
        assertTrue(state1.getInternalView().isHintAnimationEnabled)
    }

    @Test
    fun test_attach_last_set_is_hint_enabled() {
        textInputLayout.setHintEnabled(true)
        attachView()

        val state1 = textInputLayout.getFieldState()

        assertTrue(state1.isHintEnabled)
        assertTrue(state1.getInternalView().isHintEnabled)
    }

    @Test
    fun test_attach_last_set_password_toggle_enabled() {
        textInputLayout.setPasswordToggleEnabled(true)
        attachView()

        val state1 = textInputLayout.getFieldState()

        assertTrue(state1.isPasswordVisibilityToggleEnabled)
        assertTrue(state1.getInternalView().isPasswordVisibilityToggleEnabled)
    }

    @Test
    fun test_attach_last_set_password_visibility_toggle_tint_list() {
        val myList =  ColorStateList(arrayOf(intArrayOf()), intArrayOf(android.R.color.black))
        val COLOR = android.R.color.black

        textInputLayout.setPasswordVisibilityToggleTintList(myList)
        attachView()

        val state = textInputLayout.getFieldState()

        assertEquals(myList, state.passwordToggleTint)
    }

    @Test
    fun test_attach_last_set_password_visibility_toggle_drawable() {
        val DRAWABLE = R.drawable.ic_amex_dark
        textInputLayout.setPasswordVisibilityToggleDrawable(DRAWABLE)
        attachView()

        val state = textInputLayout.getFieldState()

        assertEquals(DRAWABLE, state.passwordVisibilityToggleDrawable)
    }

    @Test
    fun test_attach_last_box_background_mode() {
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE)

        attachView()

        val state1 = textInputLayout.getFieldState()
        assertEquals(state1.getInternalView().boxBackgroundMode, state1.boxBackgroundMode)
        assertEquals(TextInputLayout.BOX_BACKGROUND_OUTLINE, state1.boxBackgroundMode)
    }

    @Test
    fun test_attach_last_box_background_color() {
        val COLOR = Color.BLUE
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE)
        textInputLayout.setBoxBackgroundColor(COLOR)
        attachView()

        val state = textInputLayout.getFieldState()
        assertEquals(state.getInternalView().boxBackgroundColor, state.boxBackgroundColor)
        assertEquals(COLOR, state.boxBackgroundColor)
    }

    @Test
    fun test_attach_last_box_stroke_color() {
        val COLOR = Color.BLUE
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE)
        textInputLayout.setBoxStrokeColor(COLOR)
        attachView()

        val state = textInputLayout.getFieldState()
        assertEquals(state.getInternalView().boxStrokeColor, state.boxStrokeColor)
        assertEquals(COLOR, state.boxStrokeColor)
    }

    @Test
    fun test_attach_last_set_box_corner_radius() {
        val TopStart = 10f
        val TopEnd = 20f
        val BottomStart = 30f
        val BottomEnd = 40f
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE)
        textInputLayout.setBoxCornerRadius(TopStart, TopEnd, BottomStart, BottomEnd)

        attachView()

        val state = textInputLayout.getFieldState()
        val iv = state.getInternalView()
        val a1 = iv.boxCornerRadiusTopStart
        val a2 = iv.boxCornerRadiusTopEnd
        val a3 = iv.boxCornerRadiusBottomStart
        val a4 = iv.boxCornerRadiusBottomEnd

        //for some reason in 'com.google.android.material:material:1.1.0' lib after set value to boxCornerRadiusBottomStart
        // later this value will return from boxCornerRadiusBottomEnd instead
        //getBoxCornerRadiusBottomEnd return getBottomLeftCornerResolvedSize
        //getBoxCornerRadiusBottomStart return getBottomRightCornerResolvedSize

        assertEquals(iv.boxCornerRadiusBottomEnd, state.boxCornerRadiusBottomStart)
        assertEquals(BottomStart, state.boxCornerRadiusBottomStart)

        assertEquals(iv.boxCornerRadiusBottomStart, state.boxCornerRadiusBottomEnd)
        assertEquals(BottomEnd, state.boxCornerRadiusBottomEnd)

        assertEquals(iv.boxCornerRadiusTopStart, state.boxCornerRadiusTopStart)
        assertEquals(TopStart, state.boxCornerRadiusTopStart)

        assertEquals(iv.boxCornerRadiusTopEnd, state.boxCornerRadiusTopEnd)
        assertEquals(TopEnd, state.boxCornerRadiusTopEnd)
    }

    @Test
    fun test_attach_end_set_padding() {
        val Start = 10
        val Top = 20
        val Bottom = 30
        val End = 40
        textInputLayout.setPadding(Start, Top, End, Bottom)
        attachView()

        val state = textInputLayout.getFieldState()
        val iv = state.getInternalView()

        assertEquals(iv.paddingTop, state.top)
        assertEquals(Top, state.top)

        assertEquals(iv.paddingLeft, state.left)
        assertEquals(Start, state.left)

        assertEquals(iv.paddingRight, state.right)
        assertEquals(End, state.right)

        assertEquals(iv.paddingBottom, state.bottom)
        assertEquals(Bottom, state.bottom)
    }


    @Test
    fun test_empty_container_set_error_string() {
        val TAG = "test"
        textInputLayout.setError(TAG)
        val state = textInputLayout.getFieldState()

        assertEquals(TAG, state.error)
    }

    @Test
    fun test_empty_container_set_error_res() {
        val TAG = "VGSCollect"
        textInputLayout.setError(R.string.sdk_name)
        val state = textInputLayout.getFieldState()

        assertEquals(TAG, state.error)
    }

    @Test
    fun test_empty_container_set_hint_string() {
        val TAG = "test"
        textInputLayout.setHint(TAG)
        val state = textInputLayout.getFieldState()

        assertEquals(TAG, state.hint)
    }

    @Test
    fun test_empty_container_set_hint_res() {
        val TAG = "VGSCollect"
        textInputLayout.setHint(R.string.sdk_name)
        val state = textInputLayout.getFieldState()

        assertEquals(TAG, state.hint)
    }

    @Test
    fun test_empty_container_set_is_hint_animation_enabled() {
        textInputLayout.setHintAnimationEnabled(true)
        val state1 = textInputLayout.getFieldState()

        assertTrue(state1.isHintAnimationEnabled)

        textInputLayout.setHintAnimationEnabled(false)
        val state2 = textInputLayout.getFieldState()

        assertFalse(state2.isHintAnimationEnabled)
    }

    @Test
    fun test_empty_container_set_is_hint_enabled() {
        textInputLayout.setHintEnabled(true)
        val state1 = textInputLayout.getFieldState()

        assertTrue(state1.isHintEnabled)

        textInputLayout.setHintEnabled(false)
        val state2 = textInputLayout.getFieldState()

        assertFalse(state2.isHintEnabled)
    }

    @Test
    fun test_empty_container_set_password_toggle_enabled() {
        textInputLayout.setPasswordToggleEnabled(true)
        val state1 = textInputLayout.getFieldState()

        assertTrue(state1.isPasswordVisibilityToggleEnabled)

        textInputLayout.setPasswordToggleEnabled(false)
        val state2 = textInputLayout.getFieldState()

        assertFalse(state2.isPasswordVisibilityToggleEnabled)
    }

    @Test
    fun test_empty_container_set_password_visibility_toggle_tint_list() {
        val myList =  ColorStateList(arrayOf(intArrayOf()), intArrayOf(android.R.color.black))
        val COLOR = android.R.color.black

        textInputLayout.setPasswordVisibilityToggleTintList(myList)
        val state = textInputLayout.getFieldState()

        assertEquals(myList, state.passwordToggleTint)
    }

    @Test
    fun test_empty_container_set_password_visibility_toggle_drawable() {
        val DRAWABLE = R.drawable.ic_amex_dark
        textInputLayout.setPasswordVisibilityToggleDrawable(DRAWABLE)
        val state = textInputLayout.getFieldState()

        assertEquals(DRAWABLE, state.passwordVisibilityToggleDrawable)
    }

    @Test
    fun test_empty_container_box_background_mode() {
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE)
        val state1 = textInputLayout.getFieldState()
        assertEquals(TextInputLayout.BOX_BACKGROUND_OUTLINE, state1.boxBackgroundMode)

        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_FILLED)
        val state2 = textInputLayout.getFieldState()
        assertEquals(TextInputLayout.BOX_BACKGROUND_FILLED, state2.boxBackgroundMode)

        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_NONE)
        val state3 = textInputLayout.getFieldState()
        assertEquals(TextInputLayout.BOX_BACKGROUND_NONE, state3.boxBackgroundMode)
    }

    @Test
    fun test_empty_container_box_background_color() {
        val COLOR = Color.BLUE
        textInputLayout.setBoxBackgroundColor(COLOR)
        val state = textInputLayout.getFieldState()
        assertEquals(COLOR, state.boxBackgroundColor)
    }

    @Test
    fun test_empty_container_box_stroke_color() {
        val COLOR = Color.BLUE
        textInputLayout.setBoxStrokeColor(COLOR)
        val state = textInputLayout.getFieldState()
        assertEquals(COLOR, state.boxStrokeColor)
    }

    @Test
    fun test_empty_container_set_box_corner_radius() {
        val dimensStart = 22f
        val dimensEnd = 14f
        textInputLayout.setBoxCornerRadius(dimensStart, dimensEnd, dimensStart, dimensEnd)
        val state = textInputLayout.getFieldState()
        assertEquals(dimensStart, state.boxCornerRadiusTopStart)
        assertEquals(dimensEnd, state.boxCornerRadiusBottomEnd)
        assertEquals(dimensStart, state.boxCornerRadiusBottomStart)
        assertEquals(dimensEnd, state.boxCornerRadiusBottomEnd)
    }

    @Test
    fun test_empty_container_set_padding() {
        val Start = 10
        val Top = 20
        val Bottom = 30
        val End = 40
        textInputLayout.setPadding(Start, Top, End, Bottom)

        val state = textInputLayout.getFieldState()

        assertEquals(Top, state.top)
        assertEquals(Start, state.left)
        assertEquals(End, state.right)
        assertEquals(Bottom, state.bottom)
    }

    @Test
    fun test_set_start_icon_drawable() {
        val resID = R.drawable.ic_amex_dark
        textInputLayout.setStartIconDrawable(resID)
        val state1 = textInputLayout.getFieldState()

        assertEquals(resID, state1.startIconDrawable)
    }

    @Test
    fun test_set_start_drawable_tint_list() {
        val myList =  ColorStateList(arrayOf(intArrayOf()), intArrayOf(android.R.color.black))
        textInputLayout.setStartIconDrawableTintList(myList)
        attachView()

        val state = textInputLayout.getFieldState()

        assertEquals(myList, state.startIconTintList)
    }

    @Test
    fun test_set_start_icon_OnClickListener() {
        val listener = mock(View.OnClickListener::class.java)
        textInputLayout.setStartIconOnClickListener(listener)
        attachView()

        val state = textInputLayout.getFieldState()

        assertEquals(listener, state.startIconOnClickListener)
    }

    @Test
    fun test_set_end_icon_drawable() {
        val resID = R.drawable.ic_amex_dark
        textInputLayout.setEndIconDrawable(resID)
        val state1 = textInputLayout.getFieldState()

        assertEquals(resID, state1.endIconDrawable)
    }

    @Test
    fun test_set_end_drawable_tint_list() {
        val myList =  ColorStateList(arrayOf(intArrayOf()), intArrayOf(android.R.color.black))
        textInputLayout.setEndIconDrawableTintList(myList)
        attachView()

        val state = textInputLayout.getFieldState()

        assertEquals(myList, state.endIconTintList)
    }

    @Test
    fun test_set_hint_text_color() {
        val myList =  ColorStateList.valueOf(Color.GREEN)
        textInputLayout.setHintTextColor(myList)
        attachView()

        val state = textInputLayout.getFieldState()

        assertEquals(myList, state.hintTextColor)
    }

    @Test
    fun test_set_end_drawable_mode() {
        textInputLayout.setEndIconMode(VGSTextInputLayout.END_ICON_CLEAR_TEXT)
        attachView()

        val state = textInputLayout.getFieldState()

        assertEquals(VGSTextInputLayout.END_ICON_CLEAR_TEXT, state.endIconMode)
    }

    @Test
    fun test_set_end_icon_OnClickListener() {
        val listener = mock(View.OnClickListener::class.java)
        textInputLayout.setEndIconOnClickListener(listener)
        attachView()

        val state = textInputLayout.getFieldState()

        assertEquals(listener, state.endIconOnClickListener)
    }

    @Test
    fun test_set_counter_enabled() {
        textInputLayout.setCounterEnabled(true)
        attachView()

        val state = textInputLayout.getFieldState()

        assertTrue(state.isCounterEnabled)
    }

    @Test
    fun test_set_counter_max() {
        val value = 123
        textInputLayout.setCounterMaxLength(value)
        attachView()

        val state = textInputLayout.getFieldState()

        assertEquals(value, state.counterMaxLength)
    }


    @Test
    fun test_set_typeface() {
        val typeface = Typeface.DEFAULT_BOLD
        textInputLayout.setTypeface(typeface)
        attachView()

        val state = textInputLayout.getFieldState()

        assertEquals(typeface, textInputLayout.getTypeface())
        assertEquals(typeface, state.typeface)
    }

    @Test
    fun test_set_hint_text_appearance() {
        val styleResID = 7332

        textInputLayout.setHintTextAppearance(styleResID)

        val state = textInputLayout.getFieldState()

        assertEquals(styleResID, state.hintTextAppearance)
    }

    @Test
    fun test_set_error_text_appearance() {
        val styleResID = 7332

        textInputLayout.setErrorTextAppearance(styleResID)

        val state = textInputLayout.getFieldState()

        assertEquals(styleResID, state.errorTextAppearance)
    }

    @Test
    fun test_set_counter_overflow_text_appearance() {
        val styleResID = 7332

        textInputLayout.setCounterOverflowTextAppearance(styleResID)

        val state = textInputLayout.getFieldState()

        assertEquals(styleResID, state.counterOverflowTextAppearance)
    }

    @Test
    fun test_set_counter_text_appearance() {
        val styleResID = 7332

        textInputLayout.setCounterTextAppearance(styleResID)

        val state = textInputLayout.getFieldState()

        assertEquals(styleResID, state.counterTextAppearance)
    }

    @Test
    fun test_set_helper_text_appearance() {
        val styleResID = 7332

        textInputLayout.setHelperTextTextAppearance(styleResID)

        val state = textInputLayout.getFieldState()

        assertEquals(styleResID, state.helperTextTextAppearance)
    }

    @Test
    fun test_set_helper_text() {
        val helper = "7332"

        textInputLayout.setHelperText(helper)

        val state = textInputLayout.getFieldState()

        assertEquals(helper, state.helperText)
    }

}