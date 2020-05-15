package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.view.View
import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.storage.DependencyType
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.InputCardCVCConnection
import com.verygoodsecurity.vgscollect.view.card.text.CVCValidateFilter
import com.verygoodsecurity.vgscollect.view.card.validation.CardCVCCodeValidator

/** @suppress */
internal class CVCInputField(context: Context): BaseInputField(context) {

    override var fieldType: FieldType = FieldType.CVC

    override fun applyFieldType() {
        val validator = CardCVCCodeValidator()
        inputConnection = InputCardCVCConnection(id, validator)

        val str = text.toString()
        val stateContent = FieldContent.InfoContent().apply {
            this.data = str
        }
        val state = collectCurrentState(stateContent)

        inputConnection?.setOutput(state)
        inputConnection?.setOutputListener(stateListener)

        applyNewTextWatcher(null)
        val filterLength = InputFilter.LengthFilter(4)
        filters = arrayOf(CVCValidateFilter(), filterLength)
        applyInputType()
    }

    private fun applyInputType() {
        if(!isValidInputType(inputType)) {
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        refreshInput()
    }

    private fun isValidInputType(type: Int):Boolean {
        return type == InputType.TYPE_CLASS_NUMBER ||
                type == InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
    }

    override fun setInputType(type: Int) {
        val validType = validateInputType(type)
        super.setInputType(validType)
        refreshInput()
    }

    private fun validateInputType(type: Int):Int {
        return when(type) {
            InputType.TYPE_CLASS_NUMBER -> type
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_NUMBER_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_NUMBER_VARIATION_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD -> type
            else -> InputType.TYPE_CLASS_NUMBER
        }
    }

    private var limit:Int = 4
    override fun dispatchDependencySetting(dependency: Dependency) {
        if(dependency.dependencyType == DependencyType.LENGTH) {
            if(limit != dependency.value as Int) {
                limit = dependency.value
                val filterLength = InputFilter.LengthFilter(dependency.value)
                filters = arrayOf(CVCValidateFilter(), filterLength)
                (inputConnection as? InputCardCVCConnection)?.runtimeValidator =
                    CardCVCCodeValidator(dependency.value)
                text = text
            }
        } else {
            super.dispatchDependencySetting(dependency)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if(isRTL()) {
            hasRTL = true
            layoutDirection = View.LAYOUT_DIRECTION_LTR
            textDirection = View.TEXT_DIRECTION_LTR
            gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        }
    }

}