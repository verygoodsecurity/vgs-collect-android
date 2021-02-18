package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.storage.DependencyType
import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.conection.InputCardCVCConnection
import com.verygoodsecurity.vgscollect.view.card.text.CVCValidateFilter
import com.verygoodsecurity.vgscollect.view.card.validation.CardCVCCodeValidator
import com.verygoodsecurity.vgscollect.view.internal.CVCInputField.PreviewIconGravity.END
import com.verygoodsecurity.vgscollect.view.internal.CVCInputField.PreviewIconGravity.START
import com.verygoodsecurity.vgscollect.view.internal.CVCInputField.PreviewIconVisibility.*

/** @suppress */
internal class CVCInputField(context: Context) : BaseInputField(context) {

    override var fieldType: FieldType = FieldType.CVC
    private var cvcLength: Array<Int> = arrayOf(3, 4)
    private var cardType: CardType = CardType.UNKNOWN

    private val iconBounds = Rect(
        0,
        0,
        context.resources.getDimension(R.dimen.c_icon_size_w).toInt(),
        context.resources.getDimension(R.dimen.c_icon_size_h).toInt()
    )

    private var defaultPreviewIcon =
        ContextCompat.getDrawable(context, R.drawable.ic_card_front_preview_dark).also {
            it?.bounds = iconBounds
        }
    private var amExPreviewIcon =
        ContextCompat.getDrawable(context, R.drawable.ic_card_front_preview_dark_4).also {
            it?.bounds = iconBounds
        }

    private var previewIconVisibility = NEVER
    private var previewIconGravity = END

    override fun applyFieldType() {
        val validator = CardCVCCodeValidator(cvcLength)
        inputConnection =
            InputCardCVCConnection(
                id,
                validator
            )

        val str = text.toString()
        val stateContent = FieldContent.InfoContent().apply {
            this.data = str
        }
        val state = collectCurrentState(stateContent)

        inputConnection?.setOutput(state)
        inputConnection?.setOutputListener(stateListener)

        applyNewTextWatcher(null)
        applyLengthFilter(cvcLength.last())
        applyInputType()
    }

    private fun applyInputType() {
        if (!isValidInputType(inputType)) {
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        refreshInput()
    }

    private fun isValidInputType(type: Int): Boolean {
        return type == InputType.TYPE_CLASS_NUMBER ||
                type == InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
    }

    override fun setInputType(type: Int) {
        val validType = validateInputType(type)
        super.setInputType(validType)
        refreshInput()
    }

    private fun validateInputType(type: Int): Int {
        return when (type) {
            InputType.TYPE_CLASS_NUMBER -> type
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_NUMBER_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_NUMBER_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD -> type
            else -> InputType.TYPE_CLASS_NUMBER
        }
    }

    override fun dispatchDependencySetting(dependency: Dependency) {
        when (dependency.dependencyType) {
            DependencyType.RANGE -> {
                val cvcLength = dependency.value as Array<Int>
                if (cvcLength.isNotEmpty() && !this.cvcLength.contentEquals(cvcLength)) {
                    this.cvcLength = cvcLength
                    applyLengthFilter(cvcLength.last())

                    (inputConnection as? InputCardCVCConnection)?.runtimeValidator =
                        CardCVCCodeValidator(this.cvcLength)

                    text = text
                }
            }
            DependencyType.CARD_TYPE -> {
                this.cardType = dependency.value as CardType
                refreshIcon()
            }
            else -> super.dispatchDependencySetting(dependency)
        }
    }

    override fun updateTextChanged(str: String) {
        super.updateTextChanged(str)
        refreshIcon()
    }

    private fun applyLengthFilter(length: Int) {
        val filterLength = InputFilter.LengthFilter(length)
        filters = arrayOf(CVCValidateFilter(), filterLength)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (isRTL()) {
            hasRTL = true
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            textDirection = View.TEXT_DIRECTION_LTR
            gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        }
    }

    override fun setupAutofill() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE)
        }
    }

    internal fun setPreviewIconVisibility(mode: Int) {
        this.previewIconVisibility = PreviewIconVisibility.values()[mode]
    }

    private fun refreshIcon() {
        when (previewIconVisibility) {
            ALWAYS -> setIcon(getIcon())
            HAS_CONTENT -> setIcon(if (text.isNullOrEmpty()) null else getIcon())
            IF_BRAND_DETECTED -> setIcon(if (cardType == CardType.UNKNOWN) null else getIcon())
            NEVER -> setIcon(null)
        }
    }

    private fun setIcon(drawable: Drawable?) {
        when (previewIconGravity) {
            START -> setCompoundDrawablesOrNull(start = drawable)
            END -> setCompoundDrawablesOrNull(end = drawable)
        }
    }

    private fun getIcon(): Drawable? = when (cardType) {
        CardType.AMERICAN_EXPRESS -> amExPreviewIcon
        else -> defaultPreviewIcon
    }

    enum class PreviewIconVisibility {

        ALWAYS,
        HAS_CONTENT,
        IF_BRAND_DETECTED,
        NEVER
    }

    enum class PreviewIconGravity {

        START,
        END
    }
}