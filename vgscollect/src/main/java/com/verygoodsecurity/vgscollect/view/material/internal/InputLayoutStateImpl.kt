package com.verygoodsecurity.vgscollect.view.material.internal

import android.content.res.ColorStateList
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.VisibleForTesting
import com.google.android.material.textfield.TextInputLayout

/** @suppress */
internal class InputLayoutStateImpl(
    private val textInputLayout: TextInputLayoutWrapper
) : InputLayoutState {

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

            this.isHintEnabled = this@InputLayoutStateImpl.isHintEnabled
            this.isHintAnimationEnabled = this@InputLayoutStateImpl.isHintAnimationEnabled
            this.hint = this@InputLayoutStateImpl.hint

            textInputLayout.setPadding(
                this@InputLayoutStateImpl.left,
                this@InputLayoutStateImpl.top,
                this@InputLayoutStateImpl.right,
                this@InputLayoutStateImpl.bottom
            )

            this.boxBackgroundMode = this@InputLayoutStateImpl.boxBackgroundMode
            if (this@InputLayoutStateImpl.boxBackgroundMode != TextInputLayout.BOX_BACKGROUND_NONE) {
                this.boxBackgroundColor = this@InputLayoutStateImpl.boxBackgroundColor
                this.boxStrokeColor = this@InputLayoutStateImpl.boxStrokeColor

                val te = this@InputLayoutStateImpl.boxCornerRadiusTopEnd
                val be = this@InputLayoutStateImpl.boxCornerRadiusBottomEnd
                val bs = this@InputLayoutStateImpl.boxCornerRadiusBottomStart
                val ts = this@InputLayoutStateImpl.boxCornerRadiusTopStart
                textInputLayout.setBoxCornerRadii(ts, te, bs, be)
            }
            this.error = this@InputLayoutStateImpl.error
            this.isPasswordVisibilityToggleEnabled = this@InputLayoutStateImpl.isPasswordVisibilityToggleEnabled
            if(this@InputLayoutStateImpl.passwordVisibilityToggleDrawable != 0) {
                this.setPasswordVisibilityToggleDrawable(this@InputLayoutStateImpl.passwordVisibilityToggleDrawable)
            }
            setPasswordVisibilityToggleTintList(this@InputLayoutStateImpl.passwordToggleTint)
        }
    }

    override fun refresh() {
        textInputLayout.restoreState(this)
    }

    internal fun addChildView(child: View?) {
        textInputLayout.addView(child)
    }

    @VisibleForTesting
    internal fun getInternalView() = textInputLayout
}