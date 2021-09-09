package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.conection.InputInfoConnection

/** @suppress */
internal class InfoInputField(context: Context) : BaseInputField(context) {

    override var fieldType: FieldType = FieldType.INFO

    override fun applyFieldType() {
        inputConnection = InputInfoConnection(id, validator)

        val str = text.toString()
        val stateContent = FieldContent.InfoContent().apply {
            this.data = str
        }
        val state = collectCurrentState(stateContent)

        inputConnection?.setOutput(state)
        inputConnection?.setOutputListener(stateListener)

        applyNewTextWatcher(null)
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
    }
}