package com.verygoodsecurity.vgscollect.view

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.text.InputFilter
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.autofill.AutofillId
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.AnalyticsHandler
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultStorageType
import com.verygoodsecurity.vgscollect.core.storage.DependencyListener
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.CardBrand
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.formatter.CardMaskAdapter
import com.verygoodsecurity.vgscollect.view.card.icon.CardIconAdapter
import com.verygoodsecurity.vgscollect.view.card.validation.rules.ValidationRule
import com.verygoodsecurity.vgscollect.view.core.serializers.FieldDataSerializer
import com.verygoodsecurity.vgscollect.view.cvc.CVCIconAdapter
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.view.internal.CVCInputField
import com.verygoodsecurity.vgscollect.view.internal.CardInputField
import com.verygoodsecurity.vgscollect.view.internal.InfoInputField
import com.verygoodsecurity.vgscollect.view.internal.PersonNameInputField
import com.verygoodsecurity.vgscollect.view.internal.SSNInputField
import com.verygoodsecurity.vgscollect.view.internal.core.DateInputField
import com.verygoodsecurity.vgscollect.view.material.TextInputFieldLayout
import com.verygoodsecurity.vgscollect.widget.core.VisibilityChangeListener

/**
 * An abstract class that provides a basis for all VGS input fields.
 */
abstract class InputFieldView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var isAttachPermitted = true

    private lateinit var notifier: DependencyNotifier
    private var imeOptions: Int = 0
    private var textAppearance: Int = 0
    private var fontFamily: Typeface? = null
    private var enableValidation: Boolean? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.InputFieldView,
            0, 0
        ).apply {
            try {
                for (i in 0 until indexCount) {
                    val attr = getIndex(i)
                    when (attr) {
                        R.styleable.InputFieldView_textAppearance -> setupAppearance(this)
                        R.styleable.InputFieldView_imeOptions -> setupImeOptions(this)
                        R.styleable.InputFieldView_enableValidation -> setupEnableValidation(this)
                        R.styleable.InputFieldView_fontFamily -> setupFont(this)
                    }
                }

                val leftP = paddingLeft
                val topP = paddingTop
                val rightP = paddingRight
                val bottomP = paddingBottom
                setPadding(leftP, topP, rightP, bottomP)
            } finally {
                recycle()
            }
        }
    }

    private fun setupImeOptions(typedArray: TypedArray) {
        imeOptions =
            typedArray.getInt(R.styleable.InputFieldView_imeOptions, EditorInfo.IME_ACTION_DONE)
    }

    private fun setupAppearance(typedArray: TypedArray) {
        textAppearance =
            typedArray.getResourceId(R.styleable.InputFieldView_textAppearance, 0)
    }

    private fun setupEnableValidation(typedArray: TypedArray) {
        enableValidation =
            typedArray.getBoolean(R.styleable.InputFieldView_enableValidation, false)
    }

    private fun setupFont(attrs: TypedArray) {
        fontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            attrs.getFont(R.styleable.InputFieldView_fontFamily)
        } else {
            val s = attrs.getString(R.styleable.InputFieldView_fontFamily)
            if (s.isNullOrEmpty()) {
                null
            } else {
                Typeface.create(s, Typeface.NORMAL)
            }
        }
    }

    /**
     * A delegate class that helps deliver new requirements between related input fields.
     *
     * @param notifier The listener that emits new dependencies to apply.
     */
    internal class DependencyNotifier(notifier: DependencyListener) : DependencyListener by notifier

    private lateinit var fieldType: FieldType

    private lateinit var inputField: BaseInputField

    protected fun setupViewType(type: FieldType) {
        with(type) {
            fieldType = this
            inputField = BaseInputField.getInputField(context, this@InputFieldView)

            syncInputState()
        }
    }

    internal val statePreparer: AccessibilityStatePreparer = StatePreparer()

    internal inner class StatePreparer : AccessibilityStatePreparer {

        override fun getId(): Int = inputField.id

        override fun getView(): View {
            return inputField
        }

        override fun getDependencyListener(): DependencyListener = notifier

        override fun setAnalyticHandler(handler: AnalyticsHandler) {
            inputField.analyticsHandler = handler
        }

        override fun unsubscribe() {
            inputField.stateListener = null
        }
    }

    override fun onDetachedFromWindow() {
        if (hasChildren()) removeAllViews()
        bgDraw = null
        super.onDetachedFromWindow()
    }

    override fun addView(child: View?) {
        if (!hasChildren() && child is BaseInputField) {
            super.addView(child)
        }
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        if (isAttachPermitted) {
            super.addView(child, params)
        }
    }

    override fun addView(child: View?, index: Int) {
        if (isAttachPermitted) {
            super.addView(child, index)
        }
    }

    override fun addView(child: View?, width: Int, height: Int) {
        if (isAttachPermitted) {
            super.addView(child, width, height)
        }
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (isAttachPermitted) {
            super.addView(child, index, params)
        }
    }

    override fun attachViewToParent(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (isAttachPermitted) {
            super.attachViewToParent(child, index, params)
        }
    }

    override fun addOnAttachStateChangeListener(listener: OnAttachStateChangeListener?) {
        if (isAttachPermitted) {
            super.addOnAttachStateChangeListener(listener)
        }
    }

    /**
     * Sets the padding. The view may add to the space required to display the scrollbars,
     * depending on the style and visibility of the scrollbars. So the values returned from
     * getPaddingLeft(), getPaddingTop(), getPaddingRight(), and getPaddingBottom() may be different
     * from the values set in this call.
     *
     * @param left The left padding in pixels.
     * @param top The top padding in pixels.
     * @param right The right padding in pixels.
     * @param bottom The bottom padding in pixels.
     */
    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        this.leftP = left
        this.topP = top
        this.rightP = right
        this.bottomP = bottom
        super.setPadding(0, 0, 0, 0)
    }

    /**
     * Returns the bottom padding of this view. If there are inset and enabled scrollbars,
     * this value may include the space required to display the scrollbars as well.
     *
     * @return The bottom padding in pixels.
     */
    override fun getPaddingBottom(): Int {
        return if (isAttachPermitted) {
            super.getPaddingBottom()
        } else {
            inputField.paddingBottom
        }
    }

    /**
     * Returns the end padding of this view depending on its resolved layout direction.
     * If there are inset and enabled scrollbars, this value may include the space required
     * to display the scrollbars as well.
     *
     * @return The end padding in pixels.
     */
    override fun getPaddingEnd(): Int {
        return if (isAttachPermitted) {
            super.getPaddingEnd()
        } else {
            inputField.paddingEnd
        }
    }

    /**
     * Returns the left padding of this view. If there are inset and enabled scrollbars,
     * this value may include the space required to display the scrollbars as well.
     *
     * @return The left padding in pixels.
     */
    override fun getPaddingLeft(): Int {
        return if (isAttachPermitted) {
            super.getPaddingLeft()
        } else {
            inputField.paddingLeft
        }
    }

    /**
     * Returns the right padding of this view. If there are inset and enabled scrollbars,
     * this value may include the space required to display the scrollbars as well.
     *
     * @return The right padding in pixels.
     */
    override fun getPaddingRight(): Int {
        return if (isAttachPermitted) {
            super.getPaddingRight()
        } else {
            inputField.paddingRight
        }
    }

    /**
     * Returns the start padding of this view depending on its resolved layout direction.
     * If there are inset and enabled scrollbars, this value may include the space required
     * to display the scrollbars as well.
     *
     * @return The start padding in pixels.
     */
    override fun getPaddingStart(): Int {
        return if (isAttachPermitted) {
            super.getPaddingStart()
        } else {
            inputField.paddingStart
        }
    }

    /**
     * Returns the top padding of this view.
     *
     * @return The top padding in pixels.
     */
    override fun getPaddingTop(): Int {
        return if (isAttachPermitted) {
            super.getPaddingTop()
        } else {
            inputField.paddingTop
        }
    }

    private var leftP: Int = 0
    private var topP: Int = 0
    private var rightP: Int = 0
    private var bottomP: Int = 0

    public override fun onAttachedToWindow() {
        if (isAttachPermitted) {
            super.onAttachedToWindow()
            if (parent !is TextInputFieldLayout) {
                setAddStatesFromChildren(true)
                inputField.setMinimumPaddingLimitations(
                    resources.getDimension(R.dimen.default_horizontal_field).toInt(),
                    resources.getDimension(R.dimen.default_vertical_field).toInt()
                )
                applyLayoutParams(inputField)
                addView(inputField)
            }
            inputField.setPadding(leftP, topP, rightP, bottomP)
            inputField.contentDescription = this.contentDescription
            inputField.importantForAccessibility = this.importantForAccessibility
            isAttachPermitted = false
        }
    }

    private fun applyLayoutParams(v: TextView?) {
        v?.apply {
            var currentGravity = v.gravity

            if (currentGravity and Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK == 0) {
                currentGravity = currentGravity or Gravity.START
            }
            if (currentGravity and Gravity.VERTICAL_GRAVITY_MASK == 0) {
                currentGravity = currentGravity or Gravity.TOP
            }

            val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            lp.weight = 1.0f
            lp.setMargins(0, 0, 0, 0)
            if (lp.gravity == -1) {
                lp.gravity = Gravity.CENTER_VERTICAL
            }
            layoutParams = lp

            this.gravity = currentGravity
        }
    }

    /**
     * Returns the type of the editable content.
     *
     * @return The input type.
     */
    open fun getInputType(): Int {
        return inputField.inputType
    }

    /**
     * Sets the type of the content.
     *
     * @param inputType The input type.
     */
    open fun setInputType(inputType: Int) {
        inputField.inputType = inputType
    }

    /**
     * Sets the field name for data transfer to the VGS proxy.
     *
     * @param fieldName The name of the field.
     */
    open fun setFieldName(fieldName: String?) {
        inputField.tag = fieldName
    }

    /**
     * Returns the field name used for data transfer to the VGS proxy.
     *
     * @return The field name.
     */
    open fun getFieldName(): String? = inputField.tag as String?

    /**
     * Sets the field name for data transfer to the VGS proxy.
     *
     * @param resId The resource ID of the field name.
     */
    open fun setFieldName(resId: Int) {
        inputField.tag = resources.getString(resId, "")
    }

    /**
     * Causes words in the text that are longer than the view's width to be ellipsized
     * instead of broken in the middle.
     *
     * @param type The type of truncation to use.
     */
    open fun setEllipsize(type: Int) {
        val ellipsize = when (type) {
            1 -> TextUtils.TruncateAt.START
            2 -> TextUtils.TruncateAt.MIDDLE
            3 -> TextUtils.TruncateAt.END
            4 -> TextUtils.TruncateAt.MARQUEE
            else -> null
        }
        inputField.ellipsize = ellipsize
    }

    /**
     * Causes words in the text that are longer than the view's width to be ellipsized
     * instead of broken in the middle.
     *
     * @param ellipsis The type of truncation to use.
     */
    open fun setEllipsize(ellipsis: TextUtils.TruncateAt) {
        inputField.ellipsize = ellipsis
    }

    /**
     * Sets the height of the TextView to be at least minLines tall.
     *
     * @param lines The minimum height in terms of number of lines.
     */
    open fun setMinLines(lines: Int) {
        inputField.minLines = lines
    }

    /**
     * Sets the height of the TextView to be at most maxLines tall.
     *
     * @param lines The maximum height in terms of number of lines.
     */
    open fun setMaxLines(lines: Int) {
        inputField.maxLines = lines
    }

    /**
     * If true, sets the properties of this field to be for a single-line input.
     *
     * @param singleLine Whether to enable single-line mode.
     */
    open fun setSingleLine(singleLine: Boolean) {
        inputField.isSingleLine = singleLine
    }

    /**
     * Returns true if this view has focus, false otherwise.
     *
     * @return True if this view has focus, false otherwise.
     */
    override fun isFocused(): Boolean {
        return if (hasChildren()) {
            inputField.isFocused
        } else {
            super.isFocused()
        }
    }

    /**
     * Finds the view in the hierarchy rooted at this view that currently has focus.
     *
     * @return The view that currently has focus, or null if no focused view can be found.
     */
    override fun findFocus(): View? = inputField.findFocus()

    /**
     * Sets whether this view can receive focus.
     *
     * @param focusable If true, this view can receive focus.
     */
    override fun setFocusable(focusable: Boolean) {
        inputField.isFocusable = focusable
    }

    /**
     * Sets whether this view can receive focus.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun setFocusable(focusable: Int) {
        inputField.focusable = focusable
    }

    /**
     * Returns true if this view has focus itself, or is the ancestor of the view that has focus.
     *
     * @return True if this view has or contains focus, false otherwise.
     */
    override fun hasFocus(): Boolean {
        return super.hasFocus().takeIf {
            ::inputField.isInitialized.not()
        } ?: inputField.hasFocus()
    }

    /**
     * Sets whether this view can receive focus while in touch mode.
     *
     * @param focusableInTouchMode If true, this view can receive focus while in touch mode.
     */
    override fun setFocusableInTouchMode(focusableInTouchMode: Boolean) {
        super.setFocusableInTouchMode(focusableInTouchMode)
        inputField.isFocusableInTouchMode = focusableInTouchMode
    }

    /**
     * Sets the text to be displayed when the text of the TextView is empty.
     *
     * @param text The hint text.
     */
    open fun setHint(text: String?) {
        inputField.hint = text
    }

    /**
     * Sets the color of the hint text.
     *
     * @param colors The color state list.
     */
    open fun setHintTextColor(colors: ColorStateList) {
        inputField.setHintTextColor(colors)
    }

    /**
     * Sets the color of the hint text.
     *
     * @param color The color.
     */
    open fun setHintTextColor(color: Int) {
        inputField.setHintTextColor(color)
    }

    /**
     * Sets whether the text should be allowed to be wider than the view.
     *
     * @param canScroll Whether the text can scroll horizontally.
     */
    open fun canScrollHorizontally(canScroll: Boolean) {
        inputField.setHorizontallyScrolling(canScroll)
    }

    /**
     * Sets the horizontal and vertical alignment of the text.
     *
     * @param gravity The gravity.
     */
    open fun setGravity(gravity: Int) {
        inputField.gravity = gravity
    }

    /**
     * Returns the horizontal and vertical alignment of this TextView.
     *
     * @return The gravity.
     */
    open fun getGravity() = inputField.gravity

    /**
     * Sets whether the cursor is visible.
     *
     * @param isVisible Whether the cursor is visible.
     */
    open fun setCursorVisible(isVisible: Boolean) {
        inputField.isCursorVisible = isVisible
    }

    /**
     * Sets the text appearance from the specified style resource.
     *
     * @param context The context.
     * @param resId The resource ID of the style.
     */
    @Deprecated("deprecated")
    open fun setTextAppearance(context: Context, resId: Int) {
        inputField.setTextAppearance(context, resId)
    }

    /**
     * Sets the text appearance from the specified style resource.
     *
     * @param resId The resource ID of the style.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    open fun setTextAppearance(resId: Int) {
        inputField.setTextAppearance(resId)
    }

    /**
     * Returns the current typeface used to style the text.
     *
     * @return The current typeface.
     */
    open fun getTypeface(): Typeface? {
        return inputField.typeface
    }

    /**
     * Sets the typeface and style in which the text should be displayed.
     *
     * @param typeface The typeface.
     */
    open fun setTypeface(typeface: Typeface) {
        inputField.typeface = typeface
    }

    /**
     * Sets the typeface and style in which the text should be displayed.
     *
     * @param tf The typeface.
     * @param style The style.
     */
    open fun setTypeface(tf: Typeface?, style: Int) {
        when (style) {
            -1 -> inputField.typeface = fontFamily
            0 -> inputField.typeface = Typeface.DEFAULT
            1 -> inputField.setTypeface(tf, Typeface.BOLD)
            2 -> inputField.setTypeface(tf, Typeface.ITALIC)
            3 -> inputField.setTypeface(tf, Typeface.BOLD_ITALIC)
        }
    }

    /**
     * Sets the text to be displayed from a string resource.
     *
     * @param resId The resource ID of the string.
     */
    open fun setText(resId: Int) {
        inputField.setText(resId)
    }

    /**
     * Sets the text to be displayed from a string resource.
     *
     * @param resId The resource ID of the string.
     * @param type The buffer type.
     */
    open fun setText(resId: Int, type: TextView.BufferType) {
        inputField.setText(resId, type)
    }

    /**
     * Sets the text to be displayed.
     *
     * @param text The text to be displayed.
     */
    open fun setText(text: CharSequence?) {
        inputField.setText(text)
    }

    /**
     * Sets the text to be displayed.
     *
     * @param text The text to be displayed.
     * @param type The buffer type.
     */
    open fun setText(text: CharSequence?, type: TextView.BufferType) {
        inputField.setText(text, type)
    }

    /**
     * Sets the text size in scaled pixels.
     *
     * @param size The text size.
     */
    open fun setTextSize(size: Float) {
        inputField.textSize = size
    }

    /**
     * Sets the text size to a given unit and value.
     *
     * @param unit The desired dimension unit.
     * @param size The desired size in the given units.
     */
    open fun setTextSize(unit: Int, size: Float) {
        inputField.setTextSize(unit, size)
    }

    /**
     * Sets the text color.
     *
     * @param color The text color.
     */
    open fun setTextColor(color: Int) {
        inputField.setTextColor(color)
    }

    /**
     * Specifies whether the field is required to be filled before sending.
     *
     * @param state If true, the field is required.
     */
    open fun setIsRequired(state: Boolean) {
        inputField.isRequired = state
    }

    /**
     * Returns whether the field is required to be filled before sending.
     *
     * @return True if the field is required, false otherwise.
     */
    open fun isRequired(): Boolean {
        return inputField.isRequired
    }

    /**
     * Sets how to determine whether this view is important for accessibility.
     *
     * @param mode How to determine whether this view is important for accessibility.
     */
    override fun setImportantForAccessibility(mode: Int) {
        if (::inputField.isInitialized) {
            this.inputField.importantForAccessibility = mode
        }
        super.setImportantForAccessibility(mode)
    }

    /**
     * Sets the content description of the view.
     *
     * @param contentDescription The content description.
     */
    override fun setContentDescription(contentDescription: CharSequence?) {
        if (::inputField.isInitialized) {
            this.inputField.contentDescription = contentDescription
        }
        super.setContentDescription(contentDescription)
    }

    /**
     * Returns the field type.
     *
     * @return The field type.
     */
    fun getFieldType(): FieldType {
        return fieldType
    }

    protected fun applyMaxLength(length: Int) {
        when (inputField) {
            is CardInputField -> (inputField as CardInputField).setMaxLength(length)
            is InfoInputField -> inputField.filters = arrayOf(InputFilter.LengthFilter(length))
        }
    }

    internal fun getFontFamily(): Typeface? {
        return fontFamily
    }

    protected fun applyStorageType(storage: VGSVaultStorageType) {
        inputField.vaultStorage = storage
    }

    protected fun applyAliasFormat(format: VGSVaultAliasFormat) {
        inputField.vaultAliasFormat = format
    }

    protected fun enableTokenization(isEnabled: Boolean) {
        inputField.isEnabledTokenization = isEnabled
    }

    protected fun hasChildren(): Boolean = childCount > 0

    private fun syncInputState() {
        notifier = DependencyNotifier(inputField)

        inputField.nextFocusDownId = nextFocusDownId
        inputField.nextFocusForwardId = nextFocusForwardId
        inputField.nextFocusUpId = nextFocusUpId
        inputField.nextFocusLeftId = nextFocusLeftId
        inputField.nextFocusRightId = nextFocusRightId
        inputField.imeOptions = imeOptions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            inputField.importantForAutofill = importantForAutofill
        }

        enableValidation?.let {
            inputField.enableValidation = it
        }

        if (fontFamily != null) {
            inputField.typeface = fontFamily
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            inputField.setTextAppearance(textAppearance)
        } else {
            inputField.setTextAppearance(context, textAppearance)
        }

        background?.constantState?.newDrawable()?.apply {
            setBackgroundColor(Color.TRANSPARENT)
            bgDraw = this
            inputField.background = this
        }
    }

    private var bgDraw: Drawable? = null

    override fun setBackgroundColor(color: Int) {
        background = ColorDrawable(color)
    }

    override fun setBackground(background: Drawable?) {
        when {
            ::inputField.isInitialized -> {
                bgDraw = background
                inputField.background = background
                super.setBackground(ContextCompat.getDrawable(context, android.R.color.transparent))
            }
            bgDraw != null -> {
                bgDraw = background
                super.setBackground(ContextCompat.getDrawable(context, android.R.color.transparent))
            }
            else -> super.setBackground(background)
        }
    }

    override fun getBackground(): Drawable? {
        return bgDraw ?: super.getBackground()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setImportantForAutofill(mode: Int) {
        super.setImportantForAutofill(mode)
        if (::inputField.isInitialized) {
            inputField.importantForAutofill = mode
        }
    }

    internal fun addStateListener(stateListener: OnVgsViewStateChangeListener) {
        inputField.stateListener = stateListener
    }

    protected fun applyPreviewIconGravity(gravity: Int) {
        if (fieldType == FieldType.CARD_NUMBER) {
            (inputField as? CardInputField)?.setCardPreviewIconGravity(gravity)
        } else if (fieldType == FieldType.CVC) {
            (inputField as? CVCInputField)?.setPreviewIconGravity(gravity)
        }
    }

    protected fun applyPreviewIconMode(mode: Int) {
        if (fieldType == FieldType.CARD_NUMBER) {
            (inputField as? CardInputField)?.setPreviewIconMode(mode)
        } else if (fieldType == FieldType.CVC) {
            (inputField as? CVCInputField)?.setPreviewIconVisibility(mode)
        }
    }

    protected fun getCardIconGravity(): Int {
        return if (fieldType == FieldType.CARD_NUMBER) {
            (inputField as? CardInputField)?.getCardPreviewIconGravity() ?: -1
        } else {
            -1
        }
    }

    protected fun applyCardBrand(c: CardBrand) {
        if (fieldType == FieldType.CARD_NUMBER) {
            (inputField as? CardInputField)?.setCardBrand(c)
        }
    }

    protected fun setValidCardBrands(cardBrands: List<CardBrand>) {
        if (fieldType == FieldType.CARD_NUMBER) {
            (inputField as? CardInputField)?.setValidCardBrands(cardBrands)
        }
    }

    protected fun setNumberDivider(divider: String?) {
        if (fieldType == FieldType.CARD_NUMBER) {
            (inputField as? CardInputField)?.setNumberDivider(divider)
        } else if (fieldType == FieldType.SSN) {
            (inputField as? SSNInputField)?.setNumberDivider(divider)
        }
    }

    protected fun getNumberDivider(): Char? {
        return when (fieldType) {
            FieldType.CARD_NUMBER -> (inputField as? CardInputField)?.getNumberDivider()
            FieldType.SSN -> (inputField as? SSNInputField)?.getNumberDivider()
            else -> null
        }
    }

    protected fun setOutputNumberDivider(divider: String?) {
        if (fieldType == FieldType.CARD_NUMBER) {
            (inputField as? CardInputField)?.setOutputNumberDivider(divider)
        } else if (fieldType == FieldType.SSN) {
            (inputField as? SSNInputField)?.setOutputNumberDivider(divider)
        }
    }

    protected fun getOutputNumberDivider(): Char? {
        return when (fieldType) {
            FieldType.CARD_NUMBER -> (inputField as? CardInputField)?.getOutputDivider()
            FieldType.SSN -> (inputField as? SSNInputField)?.getOutputDivider()
            else -> null
        }
    }


    /**
     * Returns the base paint used for the text.
     */
    fun getPaint(): TextPaint? = inputField.paint

    /**
     * Hook allowing a view to generate a representation of its internal state that can later be used to
     * create a new instance with that same state.
     */
    override fun onSaveInstanceState(): Parcelable? {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.text = inputField.text.toString()
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            setText(state.text)
            super.onRestoreInstanceState(state.superState)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    protected class SavedState : BaseSavedState {
        var text: CharSequence = ""

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState {
                    return SavedState(source)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }

        constructor(superState: Parcelable?) : super(superState)

        constructor(`in`: Parcel) : super(`in`) {
            text = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(`in`)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            TextUtils.writeToParcel(text, out, flags)
        }
    }

    /**
     * Sets the enabled state of this view.
     *
     * @param enabled True if this view is enabled, false otherwise.
     */
    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        inputField.isEnabled = enabled
    }

    protected fun setDatePattern(pattern: String?) {
        if (fieldType == FieldType.DATE_RANGE || fieldType == FieldType.CARD_EXPIRATION_DATE) {
            (inputField as? DateInputField)?.setDatePattern(pattern)
        }
    }

    protected fun setOutputPattern(pattern: String?) {
        if (fieldType == FieldType.DATE_RANGE || fieldType == FieldType.CARD_EXPIRATION_DATE) {
            (inputField as? DateInputField)?.setOutputPattern(pattern)
        }
    }

    protected fun getDatePattern(): String? {
        return if (fieldType == FieldType.DATE_RANGE || fieldType == FieldType.CARD_EXPIRATION_DATE) {
            (inputField as? DateInputField)?.getDatePattern()
        } else {
            null
        }
    }

    protected fun setDatePickerMode(type: Int) {
        if (fieldType == FieldType.DATE_RANGE || fieldType == FieldType.CARD_EXPIRATION_DATE) {
            (inputField as? DateInputField)?.setDatePickerMode(type)
        }
    }

    protected fun getDateMode(): DatePickerMode? {
        return (inputField as? DateInputField)?.getDatePickerMode()
    }

    protected fun setMinDate(date: Long) {
        if (fieldType == FieldType.DATE_RANGE || fieldType == FieldType.CARD_EXPIRATION_DATE) {
            (inputField as? DateInputField)?.minDate = date
        }
    }

    protected fun setMaxDate(date: Long) {
        if (fieldType == FieldType.DATE_RANGE || fieldType == FieldType.CARD_EXPIRATION_DATE) {
            (inputField as? DateInputField)?.maxDate = date
        }
    }

    protected fun showPickerDialog(
        dialogMode: DatePickerMode,
        ignoreFieldMode: Boolean
    ) {
        if (fieldType == FieldType.DATE_RANGE || fieldType == FieldType.CARD_EXPIRATION_DATE) {
            (inputField as? DateInputField)?.showDatePickerDialog(dialogMode, ignoreFieldMode)
        }
    }

    protected fun setDatePickerVisibilityListener(l: VisibilityChangeListener?) {
        if (fieldType == FieldType.DATE_RANGE || fieldType == FieldType.CARD_EXPIRATION_DATE) {
            (inputField as? DateInputField)?.setDatePickerVisibilityListener(l)
        }
    }

    protected fun setFieldDataSerializers(serializers: List<FieldDataSerializer<*, *>>?) {
        if (fieldType == FieldType.DATE_RANGE || fieldType == FieldType.CARD_EXPIRATION_DATE) {
            (inputField as? DateInputField)?.setFieldDataSerializers(serializers)
        }
    }

    /**
     * Sets the ID of the view to use when the next focus is FOCUS_FORWARD.
     *
     * @param nextFocusForwardId The next focus ID.
     */
    override fun setNextFocusForwardId(nextFocusForwardId: Int) {
        inputField.nextFocusForwardId = nextFocusForwardId
        super.setNextFocusForwardId(nextFocusForwardId)
    }

    /**
     * Sets the ID of the view to use when the next focus is FOCUS_LEFT.
     *
     * @param nextFocusLeftId The next focus ID.
     */
    override fun setNextFocusLeftId(nextFocusLeftId: Int) {
        inputField.nextFocusLeftId = nextFocusLeftId
        super.setNextFocusLeftId(nextFocusLeftId)
    }

    /**
     * Sets the ID of the view to use when the next focus is FOCUS_RIGHT.
     *
     * @param nextFocusRightId The next focus ID.
     */
    override fun setNextFocusRightId(nextFocusRightId: Int) {
        inputField.nextFocusRightId = nextFocusRightId
        super.setNextFocusRightId(nextFocusRightId)
    }

    /**
     * Sets the ID of the view to use when the next focus is FOCUS_UP.
     *
     * @param nextFocusUpId The next focus ID.
     */
    override fun setNextFocusUpId(nextFocusUpId: Int) {
        inputField.nextFocusUpId = nextFocusUpId
        super.setNextFocusUpId(nextFocusUpId)
    }

    /**
     * Sets the ID of the view to use when the next focus is FOCUS_DOWN.
     *
     * @param nextFocusDownId The next focus ID.
     */
    override fun setNextFocusDownId(nextFocusDownId: Int) {
        inputField.nextFocusDownId = nextFocusDownId
        super.setNextFocusDownId(nextFocusDownId)
    }

    /**
     * Call this to try to give focus to a specific view or to one of its descendants.
     *
     * @param direction The direction of the focus.
     * @param previouslyFocusedRect The previously focused rectangle.
     *
     * @return Whether this view or one of its descendants actually took focus.
     */
    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        return inputField.requestFocus(direction, previouslyFocusedRect)
    }

    /**
     * Moves the cursor to the specified offset in the text.
     *
     * @param index The position for the cursor.
     */
    fun setSelection(index: Int) {
        inputField.setSelection(index)
    }

    /**
     * Called when this view wants to give up focus.
     */
    override fun clearFocus() {
        inputField.clearFocus()
    }

    /**
     * Changes the editor type integer associated with the text view.
     */
    fun setImeOptions(imeOptions: Int) {
        inputField.imeOptions = imeOptions
    }

    /**
     * Returns the type of the Input Method Editor (IME).
     *
     * @return The type of the IME.
     */
    fun getImeOptions(): Int {
        return inputField.imeOptions
    }

    /**
     * Sets a listener to be notified when the field state changes.
     *
     * @param onFieldStateChangeListener The listener.
     */
    fun setOnFieldStateChangeListener(onFieldStateChangeListener: OnFieldStateChangeListener?) {
        inputField.setOnFieldStateChangeListener(onFieldStateChangeListener)
    }

    /**
     * Sets a listener to be invoked when the focus of this view changes.
     *
     * @param l The listener.
     */
    override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        inputField.setOnFocusChangeListener(l, true)
    }

    /**
     * A listener for editor actions.
     */
    interface OnEditorActionListener {
        /**
         * Called when an action is being performed.
         *
         * @param v The view that was clicked.
         * @param actionId The ID of the action.
         * @param event The key event.
         *
         * @return True if you have consumed the action, false otherwise.
         */
        fun onEditorAction(
            v: View?,
            actionId: Int,
            event: KeyEvent?
        ): Boolean
    }

    /**
     * Sets a listener to be called when an action is performed on the text view.
     *
     * @param l The listener.
     */
    fun setOnEditorActionListener(l: OnEditorActionListener?) {
        inputField.setEditorActionListener(l)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setAutofillHints(vararg autofillHints: String?) {
        inputField.setAutofillHints(*autofillHints)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun setAutofillId(id: AutofillId?) {
        inputField.autofillId = id
    }

    protected fun setCardBrandIconAdapter(adapter: CardIconAdapter?) {
        if (fieldType == FieldType.CARD_NUMBER) {
            (inputField as? CardInputField)?.setCardBrandAdapter(adapter)
        }
    }

    protected fun setCardBrandMaskAdapter(adapter: CardMaskAdapter) {
        if (fieldType == FieldType.CARD_NUMBER) {
            (inputField as? CardInputField)?.setCardBrandMaskAdapter(adapter)
        }
    }

    protected fun getCardNumberState(): FieldState.CardNumberState? {
        return if (fieldType == FieldType.CARD_NUMBER) {
            (inputField as? CardInputField)?.getState() as? FieldState.CardNumberState
        } else {
            null
        }
    }

    protected fun getSSNState(): FieldState.SSNNumberState? {
        return if (fieldType == FieldType.SSN) {
            (inputField as? SSNInputField)?.getState() as? FieldState.SSNNumberState
        } else {
            null
        }
    }

    protected fun setCVCPreviewIconAdapter(adapter: CVCIconAdapter?) {
        if (fieldType == FieldType.CVC) {
            (inputField as? CVCInputField)?.setPreviewIconAdapter(adapter)
        }
    }

    protected fun getCVCState(): FieldState.CVCState? {
        return if (fieldType == FieldType.CVC) {
            (inputField as? CVCInputField)?.getState() as? FieldState.CVCState
        } else {
            null
        }
    }

    protected fun getCardHolderName(): FieldState.CardHolderNameState? {
        return if (fieldType == FieldType.CARD_HOLDER_NAME) {
            (inputField as? PersonNameInputField)?.getState() as? FieldState.CardHolderNameState
        } else {
            null
        }
    }

    protected fun getDateState(): FieldState.DateState? {
        return if (fieldType == FieldType.DATE_RANGE || fieldType == FieldType.CARD_EXPIRATION_DATE) {
            (inputField as? DateInputField)?.getState() as? FieldState.DateState
        } else {
            null
        }
    }

    protected fun getInfoState(): FieldState.InfoState? {
        return (inputField as? InfoInputField)?.getState() as? FieldState.InfoState
    }

    /**
     * Sets whether validation is enabled for this view.
     *
     * @param isEnabled True if validation is enabled, false otherwise.
     */
    fun enableValidation(isEnabled: Boolean) {
        inputField.enableValidation = isEnabled
    }

    protected fun isValidationPredefined(): Boolean {
        return enableValidation != null
    }

    /**
     * Returns whether validation is enabled for this view.
     *
     * @return True if validation is enabled, false otherwise.
     */
    fun isValidationEnabled(): Boolean = inputField.enableValidation

    /**
     * Returns true if the content of this view is the same as the content of the other view.
     *
     * @param view The other view.
     *
     * @return True if the content is the same, false otherwise.
     */
    fun isContentEquals(view: InputFieldView): Boolean = inputField.isContentEquals(view.inputField)

    protected fun applyValidationRule(rule: ValidationRule) {
        inputField.applyValidationRules(listOf(rule))
    }

    protected fun applyValidationRules(rules: List<ValidationRule>) {
        inputField.applyValidationRules(rules)
    }

    protected fun appendValidationRule(rule: ValidationRule) {
        inputField.appendValidationRule(rule)
    }

    override fun performClick(): Boolean {
        return inputField.performClick()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    internal fun setFormatterMode(mode: Int) {
        if (fieldType == FieldType.DATE_RANGE || fieldType == FieldType.CARD_EXPIRATION_DATE) {
            (inputField as? DateInputField)?.setFormatterMode(mode)
        }
    }

    internal fun getFormatterMode(): Int {
        return if (fieldType == FieldType.DATE_RANGE || fieldType == FieldType.CARD_EXPIRATION_DATE) {
            (inputField as? DateInputField)?.getFormatterMode() ?: -1
        } else {
            -1
        }
    }

    /**
     * A listener for text changes.
     */
    interface OnTextChangedListener {

        /**
         * This method is called to notify you that the text has changed.
         *
         * @param view The view that was clicked.
         * @param isEmpty If true, then field is have no revealed data.
         */
        fun onTextChange(view: InputFieldView, isEmpty: Boolean)
    }

    private val textChangeListeners = mutableListOf<OnTextChangedListener>()

    internal fun notifyOnTextChanged(isEmpty: Boolean) {
        textChangeListeners.forEach { it.onTextChange(this, isEmpty) }
    }

    /**
     * Adds a listener for text changes.
     *
     * @param listener The listener.
     */
    fun addOnTextChangeListener(listener: OnTextChangedListener?) {
        listener?.let { textChangeListeners.add(listener) }
    }

    /**
     * Removes a listener for text changes.
     *
     * @param listener The listener to remove.
     */
    fun removeTextChangedListener(listener: OnTextChangedListener?) {
        listener?.let { textChangeListeners.remove(listener) }
    }

    /**
     * Sets a listener to be invoked when a key is pressed in this view.
     *
     * @param l The listener.
     */
    override fun setOnKeyListener(l: OnKeyListener?) {
        inputField.setOnKeyListener(l)
    }

    /**
     * Shows the soft keyboard.
     */
    fun showKeyboard() {
        if (::inputField.isInitialized) {
            val im = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.showSoftInput(inputField, 0)
        }
    }

    /**
     * Hides the soft keyboard.
     */
    fun hideKeyboard() {
        if (::inputField.isInitialized) {
            val im = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(inputField.windowToken, 0)
        }
    }

    companion object {
        internal val TAG: String = InputFieldView::class.simpleName.toString()
    }
}