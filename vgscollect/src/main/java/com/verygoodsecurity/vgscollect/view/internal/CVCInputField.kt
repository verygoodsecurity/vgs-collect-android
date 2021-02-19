package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.os.Build
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.view.View
import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.storage.DependencyType
import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.conection.InputCardCVCConnection
import com.verygoodsecurity.vgscollect.view.card.text.CVCValidateFilter
import com.verygoodsecurity.vgscollect.view.card.validation.CardCVCCodeValidator
import com.verygoodsecurity.vgscollect.view.cvc.CvcIconAdapter
import com.verygoodsecurity.vgscollect.view.internal.CVCInputField.PreviewIconGravity.END
import com.verygoodsecurity.vgscollect.view.internal.CVCInputField.PreviewIconGravity.START
import com.verygoodsecurity.vgscollect.view.internal.CVCInputField.PreviewIconVisibility.*

/** @suppress */
internal class CVCInputField(context: Context) : BaseInputField(context) {

    override var fieldType: FieldType = FieldType.CVC
    private var cvcLength: Array<Int> = arrayOf(3, 4)
    private var cardType: CardType = CardType.UNKNOWN

    private var iconAdapter = CvcIconAdapter(context)

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
            DependencyType.RANGE -> handleRangeDependency(dependency.value as Array<Int>)
            DependencyType.CARD_TYPE -> handleCardTypeDependency(dependency.value as CardType)
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

    internal fun setPreviewIconGravity(gravity: Int) {
        this.previewIconGravity = PreviewIconGravity.values()[gravity]
    }

    internal fun setPreviewIconAdapter(adapter: CvcIconAdapter?) {
        this.iconAdapter = adapter ?: CvcIconAdapter(context)
    }

    private fun handleRangeDependency(cvcLength: Array<Int>) {
        if (cvcLength.isNotEmpty() && !this.cvcLength.contentEquals(cvcLength)) {
            this.cvcLength = cvcLength
            applyLengthFilter(cvcLength.last())

            (inputConnection as? InputCardCVCConnection)?.runtimeValidator =
                CardCVCCodeValidator(this.cvcLength)

            text = text
        }
    }

    private fun handleCardTypeDependency(cardType: CardType) {
        this.cardType = cardType
        refreshIcon()
    }

    private fun refreshIcon() {
        when (previewIconVisibility) {
            ALWAYS -> setIcon()
            HAS_CONTENT -> if (text.isNullOrEmpty()) removeIcon() else setIcon()
            IF_BRAND_DETECTED -> if (cardType == CardType.UNKNOWN) removeIcon() else setIcon()
            NEVER -> removeIcon()
        }
    }

    private fun setIcon() {
        val icon = iconAdapter.getItem(cardType, cvcLength.last(), localVisibleRect)
        when (previewIconGravity) {
            START -> setCompoundDrawablesOrNull(start = icon)
            END -> setCompoundDrawablesOrNull(end = icon)
        }
    }

    private fun removeIcon() {
        setCompoundDrawablesOrNull()
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