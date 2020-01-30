package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.google.android.material.textfield.TextInputEditText
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.InputRunnable
import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator
import androidx.core.widget.addTextChangedListener
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.core.storage.DependencyListener
import com.verygoodsecurity.vgscollect.util.Logger

internal abstract class BaseInputField(context: Context) : TextInputEditText(context),
    DependencyListener {

    protected abstract val fieldType: FieldType

    protected var inputConnection: InputRunnable? = null
    protected var validator: VGSValidator? = null

    protected var stateListener: OnVgsViewStateChangeListener? = null
        protected set(value) {
            field = value
            inputConnection?.setOutputListener(value)
        }

    protected var isListeningPermitted = true
    private var isBackgroundVisible = true

    protected var hasRTL = false

    protected var isRequired:Boolean = true
        set(value) {
            field = value
            inputConnection?.getOutput()?.isRequired = value
            inputConnection?.run()
        }

    private var activeTextWatcher: TextWatcher? = null

    init {
        isListeningPermitted = true
        setupFocusChangeListener()
        setupInputConnectionListener()
        isListeningPermitted = false

        setupViewAttributes()
    }

    private fun setupViewAttributes() {
        id = ViewCompat.generateViewId()

        compoundDrawablePadding = resources.getDimension(R.dimen.half_default_padding).toInt()
    }

    private fun setupFocusChangeListener() {
        onFocusChangeListener = OnFocusChangeListener { _, f ->
            inputConnection?.getOutput()?.isFocusable = f
            inputConnection?.run()
        }
    }

    private fun setupInputConnectionListener() {
        val handler = Handler(Looper.getMainLooper())
        addTextChangedListener {
            inputConnection?.getOutput()?.content?.data =  it.toString()

            handler.removeCallbacks(inputConnection)
            handler.postDelayed(inputConnection, 300)
        }
    }


    override fun onAttachedToWindow() {
        isListeningPermitted = true
        applyFieldType()
        super.onAttachedToWindow()
        isListeningPermitted = false
    }

    protected abstract fun applyFieldType()

    protected fun applyNewTextWatcher(textWatcher: TextWatcher?) {
        activeTextWatcher?.let { removeTextChangedListener(activeTextWatcher) }
        textWatcher?.let { addTextChangedListener(textWatcher) }
        activeTextWatcher = textWatcher
    }


    protected fun collectCurrentState(stateContent: FieldContent): VGSFieldState {
        val state = VGSFieldState().apply {
            isRequired = this@BaseInputField.isRequired
            isFocusable = this@BaseInputField.hasFocus()
            type = this@BaseInputField.fieldType
            content = stateContent

            fieldName = this@BaseInputField.tag as? String
        }

        return state
    }

    internal fun setHasBackground(state:Boolean) {
        isBackgroundVisible = state
        if(isBackgroundVisible) {
            setBackgroundResource(android.R.color.transparent)
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        setSelection(text?.length?:0)
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        if(isRTL()
//            && (fieldType == FieldType.CARD_NUMBER
//                    || fieldType == FieldType.CVC
//                    || fieldType == FieldType.CARD_EXPIRATION_DATE)) {
//            hasRTL = true
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                layoutDirection = View.LAYOUT_DIRECTION_LTR
//                textDirection = View.TEXT_DIRECTION_LTR
//            }
//            gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
//        }
//    }

    protected fun isRTL():Boolean {
        val direction = getResolvedLayoutDirection()
        return direction == View.LAYOUT_DIRECTION_RTL
                || direction == View.TEXT_DIRECTION_ANY_RTL
                || direction == View.TEXT_DIRECTION_FIRST_STRONG_RTL
                || direction == View.TEXT_DIRECTION_RTL
    }

    private fun getResolvedLayoutDirection():Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutDirection
        } else {
            View.LAYOUT_DIRECTION_LTR
        }
    }

    override fun setTag(tag: Any?) {
        tag?.run {
            super.setTag(tag)
            inputConnection?.getOutput()?.fieldName = this as String
        }
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {
        if(isListeningPermitted) {
            super.addTextChangedListener(watcher)
        }
    }

    internal fun setCursorDrawableColor(color: Int) {
        try {
            val cursorDrawableResField = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            cursorDrawableResField.isAccessible = true
            val cursorDrawableRes = cursorDrawableResField.getInt(this)
            val editorField = TextView::class.java.getDeclaredField("mEditor")
            editorField.isAccessible = true
            val editor = editorField.get(this)
            val clazz = editor.javaClass
            val res = context.resources
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val drawableForCursorField = clazz.getDeclaredField("mDrawableForCursor")
                drawableForCursorField.isAccessible = true
                val drawable = res.getDrawable(cursorDrawableRes)
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                drawableForCursorField.set(editor, drawable)
            } else {
                val cursorDrawableField = clazz.getDeclaredField("mCursorDrawable")
                cursorDrawableField.isAccessible = true
                val drawables = arrayOfNulls<Drawable>(2)
                drawables[0] = res.getDrawable(cursorDrawableRes)
                drawables[0]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                drawables[1] = res.getDrawable(cursorDrawableRes)
                drawables[1]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                cursorDrawableField.set(editor, drawables)
            }
        } catch (t: Throwable) {
            Logger.i("VGSEditText", "Can't apply color on cursor")
        }
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        val minPaddingV = resources.getDimension(R.dimen.default_vertical_field).toInt()
        val minPaddingH = resources.getDimension(R.dimen.default_horizontal_field).toInt()
        val l = if(left < minPaddingH) minPaddingH else left
        val t = if(top < minPaddingV) minPaddingV else top
        val r = if(right < minPaddingH) minPaddingH else right
        val b = if(bottom < minPaddingV) minPaddingV else bottom
        super.setPadding(l, t, r, b)
    }

    override fun setCompoundDrawables(
        left: Drawable?,
        top: Drawable?,
        right: Drawable?,
        bottom: Drawable?
    ) {
        if(isRTL()) {
            super.setCompoundDrawables(right, top, left, bottom)
        } else {
            super.setCompoundDrawables(left, top, right, bottom)
        }
    }

    override fun dispatchDependencySetting(dependency: Dependency) {}
}