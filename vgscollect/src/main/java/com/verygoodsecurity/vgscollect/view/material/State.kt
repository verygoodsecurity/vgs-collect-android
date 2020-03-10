package com.verygoodsecurity.vgscollect.view.material

import android.content.res.ColorStateList
import android.view.View
import androidx.annotation.DrawableRes
import com.google.android.material.textfield.TextInputLayout
import com.verygoodsecurity.vgscollect.view.material.internal.InputLayoutState
import com.verygoodsecurity.vgscollect.view.material.internal.TextInputLayoutWrapper

internal class State(
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
                val te = textInputLayout.boxCornerRadiusTopEnd
                val bs = textInputLayout.boxCornerRadiusBottomStart
                val ts = textInputLayout.boxCornerRadiusTopStart
                textInputLayout.setBoxCornerRadii(ts, te, bs, value)
            }
        }
    internal var boxCornerRadiusBottomStart: Float = 0f
        set(value) {
            field = value
            if(isReady()) {
                val te = textInputLayout.boxCornerRadiusTopEnd
                val be = textInputLayout.boxCornerRadiusBottomEnd
                val ts = textInputLayout.boxCornerRadiusTopStart
                textInputLayout.setBoxCornerRadii(ts, te, value, be)
            }
        }
    internal var boxCornerRadiusTopEnd: Float = 0f
        set(value) {
            field = value
            if(isReady()) {
                val be = textInputLayout.boxCornerRadiusBottomEnd
                val bs = textInputLayout.boxCornerRadiusBottomStart
                val ts = textInputLayout.boxCornerRadiusTopStart
                textInputLayout.setBoxCornerRadii(ts, value, bs, be)
            }
        }
    var boxCornerRadiusTopStart: Float = 0f
        set(value) {
            field = value
            if(isReady()) {
                val te = textInputLayout.boxCornerRadiusTopEnd
                val be = textInputLayout.boxCornerRadiusBottomEnd
                val bs = textInputLayout.boxCornerRadiusBottomStart
                val ts = textInputLayout.boxCornerRadiusTopStart
                textInputLayout.setBoxCornerRadii(value, te, bs, be)
            }
        }
    var left: Int = 0
        set(value) {
            field = value
            if(isReady()) {
                val top = textInputLayout.top
                val right = textInputLayout.right
                val bottom = textInputLayout.bottom
                textInputLayout.setPadding(value, top, right, bottom)
            }
        }
    var start: Int = 0
        set(value) {
            field = value
            if (isReady()) {
                val top = textInputLayout.top
                val right = textInputLayout.right
                val bottom = textInputLayout.bottom
                textInputLayout.setPadding(value, top, right, bottom)
            }
        }
    var top: Int = 0
        set(value) {
            field = value
            if (isReady()) {
                val left = textInputLayout.left
                val right = textInputLayout.right
                val bottom = textInputLayout.bottom
                textInputLayout.setPadding(left, value, right, bottom)
            }
        }
    var right: Int = 0
        set(value) {
            field = value
            if (isReady()) {
                val left = textInputLayout.left
                val top = textInputLayout.top
                val bottom = textInputLayout.bottom
                textInputLayout.setPadding(left, top, value, bottom)
            }
        }
    var end: Int = 0
        set(value) {
            field = value
            if (isReady()) {
                val left = textInputLayout.left
                val top = textInputLayout.top
                val bottom = textInputLayout.bottom
                textInputLayout.setPadding(left, top, value, bottom)
            }
        }
    var bottom: Int = 0
        set(value) {
            field = value
            if (isReady()) {
                val left = textInputLayout.left
                val top = textInputLayout.top
                val right = textInputLayout.right
                textInputLayout.setPadding(left, top, right, value)
            }
        }

    override fun isReady() = textInputLayout.isReady()

    override fun restore(textInputLayoutWrapper: TextInputLayoutWrapper?) {
        textInputLayoutWrapper?.apply {

            this.isHintEnabled = this@State.isHintEnabled
            this.isHintAnimationEnabled = this@State.isHintAnimationEnabled
            this.hint = this@State.hint

            this.boxBackgroundMode = this@State.boxBackgroundMode
            if (this@State.boxBackgroundMode != TextInputLayout.BOX_BACKGROUND_NONE) {
                this.boxBackgroundColor = this@State.boxBackgroundColor
                this.boxStrokeColor = this@State.boxStrokeColor

                val te = this@State.boxCornerRadiusTopEnd
                val be = this@State.boxCornerRadiusBottomEnd
                val bs = this@State.boxCornerRadiusBottomStart
                val ts = this@State.boxCornerRadiusTopStart
                textInputLayout.setBoxCornerRadii(ts, te, bs, be)
            }
            this.error = this@State.error
            this.isPasswordVisibilityToggleEnabled = this@State.isPasswordVisibilityToggleEnabled
            if(this@State.passwordVisibilityToggleDrawable != 0) {
                this.setPasswordVisibilityToggleDrawable(this@State.passwordVisibilityToggleDrawable)
            }
            setPasswordVisibilityToggleTintList(this@State.passwordToggleTint)
        }
    }

    override fun refresh() {
        textInputLayout.restoreState(this)
    }

    internal fun addChildView(child: View?) {
        textInputLayout.addView(child)
    }
}