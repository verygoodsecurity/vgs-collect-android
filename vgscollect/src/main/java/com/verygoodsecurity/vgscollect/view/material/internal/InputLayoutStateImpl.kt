package com.verygoodsecurity.vgscollect.view.material.internal

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.VisibleForTesting
import com.google.android.material.textfield.TextInputLayout
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField

/** @suppress */
internal class InputLayoutStateImpl(
    private val textInputLayout: TextInputLayoutWrapper
) : InputLayoutState {

    internal var helperText: String? = null
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.helperText = value
            }
        }
    internal var counterOverflowTextAppearance: Int = 0
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.setCounterOverflowTextAppearance(value)
            }
        }
    internal var counterTextAppearance: Int = 0
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.setCounterTextAppearance(value)
            }
        }
    internal var helperTextTextAppearance: Int = 0
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.setHelperTextTextAppearance(value)
            }
        }
    internal var hintTextAppearance: Int = 0
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.setHintTextAppearance(value)
            }
        }
    internal var errorTextAppearance: Int = 0
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.setErrorTextAppearance(value)
            }
        }

    internal var typeface: Typeface? = null
        set(value) {
            field = value
            if(isReady() && value != null) {
                textInputLayout.typeface = value
            }
        }
    internal var isCounterEnabled: Boolean = false
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.isCounterEnabled = value
            }
        }
    internal var counterMaxLength: Int = -1
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.counterMaxLength = value
            }
        }
    internal var startIconDrawable: Int = 0
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.setStartIconDrawable(value)
            }
        }

    internal var hintTextColor: ColorStateList? = null
        set(value) {
            field = value
            if(isReady() && value != null) {
                textInputLayout.hintTextColor = value
            }
        }
    internal var startIconTintList: ColorStateList? = null
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.setStartIconTintList(value)
            }
        }
    internal var startIconOnClickListener: View.OnClickListener? = null
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.setStartIconOnClickListener(value)
            }
        }
    internal var endIconDrawable: Int = 0
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.setEndIconDrawable(value)
            }
        }
    internal var endIconTintMode: PorterDuff.Mode? = null
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.setEndIconTintMode(value)
            }
        }
    internal var endIconTintList: ColorStateList? = null
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.setEndIconTintList(value)
            }
        }
    internal var endIconMode: Int = TextInputLayout.END_ICON_NONE
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.endIconMode = value
            }
        }
    internal var endIconOnClickListener: View.OnClickListener? = null
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.setEndIconOnClickListener(value)
            }
        }


    internal var hint: CharSequence? = null
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.hint = value
            }
        }
    internal var isHintAnimationEnabled: Boolean = true
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.isHintAnimationEnabled = value
            }
        }
    internal var isHintEnabled: Boolean = true
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.isHintEnabled = value
            }
        }
    internal var isErrorEnabled: Boolean = false
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.isErrorEnabled = value
            }
        }
    internal var error: CharSequence? = null
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.error = value
            }
        }
    internal var isPasswordVisibilityToggleEnabled: Boolean = false
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.isPasswordVisibilityToggleEnabled = value
            }
        }
    @DrawableRes
    internal var passwordVisibilityToggleDrawable: Int = 0
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.setPasswordVisibilityToggleDrawable(value)
            }
        }
    internal var passwordToggleTint: ColorStateList? = null
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.setPasswordVisibilityToggleTintList(value)
            }
        }
    internal var boxBackgroundMode: Int = TextInputLayout.BOX_BACKGROUND_NONE
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.boxBackgroundMode = value
            }
        }
    internal var boxBackgroundColor: Int = 0
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.boxBackgroundColor = value
            }
        }
    internal var boxStrokeColor: Int = 0
        set(value) {
            field = value
            if(isReady()) {
                textInputLayout.boxStrokeColor = value
            }
        }
    internal var boxStrokeColorStateList: ColorStateList? = null
        set(value) {
            field = value
            if(isReady()) {
                boxStrokeColorStateList?.let { textInputLayout.setBoxStrokeColorStateList(it) }
            }
        }
    internal var boxCornerRadiusBottomEnd: Float = 0f
        set(value) {
            field = value
            if(isReady()) {
                val ts = boxCornerRadiusTopStart
                val te = boxCornerRadiusTopEnd
                val bs = boxCornerRadiusBottomStart
                textInputLayout.setBoxCornerRadii(ts, te, bs, value)
            }
        }
    internal var boxCornerRadiusBottomStart: Float = 0f
        set(value) {
            field = value
            if(isReady()) {
                val ts = boxCornerRadiusTopStart
                val te = boxCornerRadiusTopEnd
                val be = boxCornerRadiusBottomEnd
                textInputLayout.setBoxCornerRadii(ts, te, value, be)
            }
        }
    internal var boxCornerRadiusTopEnd: Float = 0f
        set(value) {
            field = value
            if(isReady()) {
                val ts = boxCornerRadiusTopStart
                val bs = boxCornerRadiusBottomStart
                val be = boxCornerRadiusBottomEnd
                textInputLayout.setBoxCornerRadii(ts, value, bs, be)
            }
        }
    var boxCornerRadiusTopStart: Float = 0f
        set(value) {
            field = value
            if(isReady()) {
                val te = boxCornerRadiusTopEnd
                val bs = boxCornerRadiusBottomStart
                val be = boxCornerRadiusBottomEnd
                textInputLayout.setBoxCornerRadii(value, te, bs, be)
            }
        }
    var left: Int = 0
        set(value) {
            field = value
            if(isReady()) {
                val top = top
                val right = right
                val bottom = bottom
                textInputLayout.setPadding(value, top, right, bottom)
            }
        }
    var start: Int = 0
        set(value) {
            field = value
            if (isReady()) {
                val top = top
                val right = right
                val bottom = bottom
                textInputLayout.setPadding(value, top, right, bottom)
            }
        }
    var top: Int = 0
        set(value) {
            field = value
            if (isReady()) {
                val left = left
                val right = right
                val bottom = bottom
                textInputLayout.setPadding(left, value, right, bottom)
            }
        }
    var right: Int = 0
        set(value) {
            field = value
            if (isReady()) {
                val left = left
                val top = top
                val bottom = bottom
                textInputLayout.setPadding(left, top, value, bottom)
            }
        }
    var end: Int = 0
        set(value) {
            field = value
            if (isReady()) {
                val left = left
                val top = top
                val bottom = bottom
                textInputLayout.setPadding(left, top, value, bottom)
            }
        }
    var bottom: Int = 0
        set(value) {
            field = value
            if (isReady()) {
                val left = left
                val top = top
                val right = right
                textInputLayout.setPadding(left, top, right, value)
            }
        }

    override fun isReady() = textInputLayout.isReady()

    override fun restore(textInputLayoutWrapper: TextInputLayoutWrapper?) {
        textInputLayoutWrapper?.apply {
            if(this@InputLayoutStateImpl.typeface != null) {
                this.typeface = this@InputLayoutStateImpl.typeface
            }
            this.isHintEnabled = this@InputLayoutStateImpl.isHintEnabled
            this.isHintAnimationEnabled = this@InputLayoutStateImpl.isHintAnimationEnabled
            this.hint = this@InputLayoutStateImpl.hint

            textInputLayout.setPadding(
                this@InputLayoutStateImpl.left,
                this@InputLayoutStateImpl.top,
                this@InputLayoutStateImpl.right,
                this@InputLayoutStateImpl.bottom
            )

            this.setHintTextAppearance(this@InputLayoutStateImpl.hintTextAppearance)
            if (this@InputLayoutStateImpl.hintTextColor != null) {
                hintTextColor = this@InputLayoutStateImpl.hintTextColor
            }

            this.boxBackgroundMode = this@InputLayoutStateImpl.boxBackgroundMode
            if (this@InputLayoutStateImpl.boxBackgroundMode != TextInputLayout.BOX_BACKGROUND_NONE) {
                this.boxBackgroundColor = this@InputLayoutStateImpl.boxBackgroundColor
                this.boxStrokeColor = this@InputLayoutStateImpl.boxStrokeColor
                this@InputLayoutStateImpl.boxStrokeColorStateList?.let {
                    this.setBoxStrokeColorStateList(it)
                }

                val te = this@InputLayoutStateImpl.boxCornerRadiusTopEnd
                val be = this@InputLayoutStateImpl.boxCornerRadiusBottomEnd
                val bs = this@InputLayoutStateImpl.boxCornerRadiusBottomStart
                val ts = this@InputLayoutStateImpl.boxCornerRadiusTopStart
                textInputLayout.setBoxCornerRadii(ts, te, bs, be)
            }
            this.isErrorEnabled = this@InputLayoutStateImpl.isErrorEnabled
            this.error = this@InputLayoutStateImpl.error
            this.isPasswordVisibilityToggleEnabled = this@InputLayoutStateImpl.isPasswordVisibilityToggleEnabled

            if(this@InputLayoutStateImpl.passwordVisibilityToggleDrawable != 0) {
                this.setPasswordVisibilityToggleDrawable(this@InputLayoutStateImpl.passwordVisibilityToggleDrawable)
            }
            setPasswordVisibilityToggleTintList(this@InputLayoutStateImpl.passwordToggleTint)

            this.isCounterEnabled = this@InputLayoutStateImpl.isCounterEnabled
            this.counterMaxLength = this@InputLayoutStateImpl.counterMaxLength

            this.setStartIconDrawable(this@InputLayoutStateImpl.startIconDrawable)
            this.setStartIconOnClickListener(this@InputLayoutStateImpl.startIconOnClickListener)
            setStartIconTintList(this@InputLayoutStateImpl.startIconTintList)

            if(this@InputLayoutStateImpl.endIconDrawable != 0) {
                this.setEndIconDrawable(this@InputLayoutStateImpl.endIconDrawable)
            }
            if(isPasswordVisibilityToggleEnabled) {
                this.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            } else {
                this.setEndIconOnClickListener(this@InputLayoutStateImpl.endIconOnClickListener)
                if(this@InputLayoutStateImpl.endIconTintMode != null) {
                    this.setEndIconTintMode(this@InputLayoutStateImpl.endIconTintMode)
                }
                if(this@InputLayoutStateImpl.endIconTintList != null) {
                    setEndIconTintList(this@InputLayoutStateImpl.endIconTintList)
                }
                this.endIconMode = this@InputLayoutStateImpl.endIconMode

                textInputLayout.helperText = this@InputLayoutStateImpl.helperText

                this.setCounterOverflowTextAppearance(this@InputLayoutStateImpl.counterOverflowTextAppearance)
                this.setCounterTextAppearance(this@InputLayoutStateImpl.counterTextAppearance)
                this.setHelperTextTextAppearance(this@InputLayoutStateImpl.helperTextTextAppearance)
                this.setErrorTextAppearance(this@InputLayoutStateImpl.errorTextAppearance)
            }
        }
    }

    override fun refresh() {
        textInputLayout.restoreState(this)
    }

    internal fun addChildView(child: InputFieldView?) {
        (child?.statePreparer?.getView() as? BaseInputField)?.apply {

            val limitations = produceInnerViewPaddingLimitations(boxBackgroundMode, context)
            setMinimumPaddingLimitations(limitations.first, limitations.second)

            if (boxBackgroundMode == TextInputLayout.BOX_BACKGROUND_OUTLINE || boxBackgroundColor != 0) {
                setBackgroundResource(0)
            } else {
                setBackgroundResource(android.R.color.transparent)
            }

            if(this@InputLayoutStateImpl.hint.isNullOrEmpty() && !hint.isNullOrEmpty()) {
                this@InputLayoutStateImpl.hint = hint
            }

            textInputLayout.addView(this)
        }
    }

    private fun produceInnerViewPaddingLimitations(mode: Int, context:Context):Pair<Int, Int> {
        return when(mode) {
            TextInputLayout.BOX_BACKGROUND_OUTLINE -> Pair(
                context.resources.getDimension(R.dimen.f_label_horizontal_field_set_1).toInt(),
                context.resources.getDimension(R.dimen.f_label_vertical_field_set_1).toInt()
            )
            TextInputLayout.BOX_BACKGROUND_FILLED -> Pair(
                context.resources.getDimension(R.dimen.f_label_horizontal_field_set_2).toInt(),
                context.resources.getDimension(R.dimen.f_label_vertical_field_set_2).toInt()
            )
            TextInputLayout.BOX_BACKGROUND_NONE -> Pair(
                context.resources.getDimension(R.dimen.f_label_horizontal_field_set_3).toInt(),
                context.resources.getDimension(R.dimen.f_label_vertical_field_set_3).toInt()
            )
            else -> Pair(0,0)
        }
    }

    @VisibleForTesting
    internal fun getInternalView() = textInputLayout
}