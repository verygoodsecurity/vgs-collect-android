package com.verygoodsecurity.demoapp.tokenization

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textview.MaterialTextView
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.widget.VGSTextInputLayout
import kotlinx.android.synthetic.main.activity_collect_tokenization_demo.*

class VGSCollectTokenizationActivity :
    AppCompatActivity(R.layout.activity_collect_tokenization_demo),
    InputFieldView.OnTextChangedListener {

    private val defaultTextColor by lazy { ContextCompat.getColor(this, R.color.fiord) }
    private val defaultBackgroundColor by lazy { ContextCompat.getColor(this, R.color.fiord_20) }
    private val errorTextColor by lazy { ContextCompat.getColor(this, R.color.brown) }
    private val errorBackgroundColor by lazy { ContextCompat.getColor(this, R.color.vanillaIce) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
    }

    override fun onTextChange(view: InputFieldView, isEmpty: Boolean) {
        val (title, layout) = (when (view.id) {
            R.id.vgsTiedCardHolder -> mtvCardHolder to vgsTilCardHolder
            R.id.vgsTiedCardNumber -> mtvCardNumber to vgsTilCardNumber
            R.id.vgsTiedExpiry -> mtvExpiry to vgsTilExpiry
            R.id.vgsTiedCvc -> mtvCvc to vgsTilCvc
            else -> throw IllegalArgumentException("Not implemented.")
        })
        setInputValid(title, layout)
    }

    private fun initViews() {
        initTextChangeListener()
        mbTokenize?.setOnClickListener { validateInputs() }
    }

    private fun initTextChangeListener() {
        vgsTiedCardHolder?.addOnTextChangeListener(this)
        vgsTiedCardNumber?.addOnTextChangeListener(this)
        vgsTiedExpiry?.addOnTextChangeListener(this)
        vgsTiedCvc?.addOnTextChangeListener(this)
    }

    private fun validateInputs() {
        if (vgsTiedCardHolder.getState()?.isValid == false) {
            setInputInvalid(mtvCardHolder, vgsTilCardHolder)
        }
        if (vgsTiedCardNumber.getState()?.isValid == false) {
            setInputInvalid(mtvCardNumber, vgsTilCardNumber)
        }
        if (vgsTiedExpiry.getState()?.isValid == false) {
            setInputInvalid(mtvExpiry, vgsTilExpiry)
        }
        if (vgsTiedCvc.getState()?.isValid == false) {
            setInputInvalid(mtvCvc, vgsTilCvc)
        }
    }

    private fun setInputValid(title: MaterialTextView, layout: VGSTextInputLayout) {
        title.setTextColor(defaultTextColor)
        layout.setBoxBackgroundColor(defaultBackgroundColor)
    }

    private fun setInputInvalid(title: MaterialTextView, layout: VGSTextInputLayout) {
        title.setTextColor(errorTextColor)
        layout.setBoxBackgroundColor(errorBackgroundColor)
    }
}