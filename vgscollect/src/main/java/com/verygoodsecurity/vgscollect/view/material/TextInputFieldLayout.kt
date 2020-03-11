package com.verygoodsecurity.vgscollect.view.material

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.material.internal.InputLayoutStateImpl
import com.verygoodsecurity.vgscollect.view.material.internal.TextInputLayoutWrapper

/**
 * An abstract class that provide floating label when
 * the hint is hidden due to user inputting text.
 *
 * @version 1.0.0
 */
abstract class TextInputFieldLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var isAttachPermitted = true

    private val fieldState: InputLayoutStateImpl

    init {
        val textInputLayout = TextInputLayoutWrapper(
            context
        ).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        fieldState = InputLayoutStateImpl(textInputLayout)

        addView(textInputLayout)
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        fieldState.left = left
        fieldState.top = top
        fieldState.right = right
        fieldState.bottom = bottom
        super.setPadding(0,0,0,0)
    }

    override fun getPaddingBottom(): Int {
        return fieldState.bottom
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getPaddingEnd(): Int {
        return fieldState.end
    }

    override fun getPaddingLeft(): Int {
        return fieldState.left
    }

    override fun getPaddingRight(): Int {
        return fieldState.right
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun getPaddingStart(): Int {
        return fieldState.start
    }

    override fun getPaddingTop(): Int {
        return fieldState.top
    }

    override fun attachViewToParent(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if(isAttachPermitted) {
            super.attachViewToParent(child, index, params)
        }
    }

    override fun addOnAttachStateChangeListener(listener: OnAttachStateChangeListener?) {
        if(isAttachPermitted) {
            super.addOnAttachStateChangeListener(listener)
        }
    }

    override fun addView(child: View?) {
        val v = handleNewChild(child)
        super.addView(v)
        fieldState.refresh()
    }

    override fun addView(child: View?, index: Int) {
        handleNewChild(child)?.let { v ->
            super.addView(v, index)
        }
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        handleNewChild(child)?.let { v ->
            super.addView(v, params)
        }
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        handleNewChild(child)?.let { v ->
            super.addView(v, index, params)
        }
    }

    override fun addView(child: View?, width: Int, height: Int) {
        handleNewChild(child)?.let { v ->
            super.addView(v, width, height)
        }
    }

    override fun onAttachedToWindow() {
        if(isAttachPermitted) {
            super.onAttachedToWindow()
            isAttachPermitted = false
        }
    }

    private fun handleNewChild(child: View?): View? {
        return child?.run {
            when(this) {
                is TextInputLayoutWrapper -> this
                is InputFieldView -> {
                    val editText = child as InputFieldView
                    attachViewToParent(editText, childCount, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

                    fieldState.addChildView(child)
                    null
                }
                else -> null
            }
        }
    }

    open fun setError(errorText:CharSequence?) {
        fieldState.error = errorText
    }

    open fun setError(resId:Int) {
        fieldState.error = context.resources.getString(resId)
    }

    open fun getHint() = fieldState.hint

    open fun setHint(text:String?) {
        fieldState.hint = text
    }

    open fun setHint(resId:Int) {
        fieldState.hint = context.resources.getString(resId)
    }

    open fun setPasswordToggleEnabled(isEnabled:Boolean) {
        fieldState.isPasswordVisibilityToggleEnabled = isEnabled
    }

    open fun setPasswordVisibilityToggleDrawable(@DrawableRes resId:Int) {
        fieldState.passwordVisibilityToggleDrawable = resId
    }

    open fun setPasswordVisibilityToggleTintList(tintList: ColorStateList?) {
        fieldState.passwordToggleTint = tintList
    }

    open fun setBoxCornerRadius(boxCornerRadiusTopStart:Float, boxCornerRadiusTopEnd:Float, boxCornerRadiusBottomStart:Float, boxCornerRadiusBottomEnd:Float) {
        fieldState.boxCornerRadiusTopStart = boxCornerRadiusTopStart
        fieldState.boxCornerRadiusTopEnd = boxCornerRadiusTopEnd
        fieldState.boxCornerRadiusBottomStart = boxCornerRadiusBottomStart
        fieldState.boxCornerRadiusBottomEnd = boxCornerRadiusBottomEnd
    }

    open fun setBoxBackgroundMode(style:Int) {
        fieldState.boxBackgroundMode = style
    }

    open fun setBoxBackgroundColor(c:Int) {
        fieldState.boxBackgroundColor = c
    }

    open fun setBoxStrokeColor(c:Int) {
        fieldState.boxStrokeColor = c
    }

    open fun setHintEnabled(state:Boolean) {
        fieldState.isHintEnabled = state
    }

    open fun setHintAnimationEnabled(state:Boolean) {
        fieldState.isHintAnimationEnabled = state
    }

    @VisibleForTesting
    internal fun getFieldState():InputLayoutStateImpl {
        return fieldState
    }

}