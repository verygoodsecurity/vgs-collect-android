package com.verygoodsecurity.demoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder

class MainActivity : AppCompatActivity(), VgsCollectResponseListener, View.OnClickListener {

    val vgsForm = VGSCollect("tntxrsfgxcn")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sendPost?.setOnClickListener(this)
        sendGet?.setOnClickListener(this)

        vgsForm.onResponseListener = this

        vgsForm.addOnFieldStateChangeListener(getOnFieldStateChangeListener())

        vgsForm.bindView(cardNumberField)
        vgsForm.bindView(cardCVVField)
        vgsForm.bindView(cardHolderField)
        vgsForm.bindView(cardExpDateField)
        brokeViewMethodTest(cardNumberFieldLay)
    }

    override fun onDestroy() {
        vgsForm.onDestroy()
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        progressBar?.visibility = View.VISIBLE
        when (v?.id) {
            R.id.sendPost -> vgsForm.asyncSubmit(this@MainActivity, "/post", HTTPMethod.POST, null)
            R.id.sendGet -> brokeViewMethodTest(cardNumberFieldLay)//vgsForm.asyncSubmit(this@MainActivity, "/get", HTTPMethod.GET, null)
        }
    }


    private fun getOnFieldStateChangeListener(): OnFieldStateChangeListener {
        return object : OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {
                val states = vgsForm.getAllStates()
                val builder = StringBuilder()
                states.forEach {
                    builder.append(it.alias).append("\n")
                        .append("   hasFocus: ").append(it.hasFocus).append("\n")
                        .append("   isValid: ").append(it.isValid).append("\n")
                        .append("   isEmpty: ").append(it.isEmpty).append("\n")
                        .append("   isRequired: ").append(it.isRequired).append("\n")
                    if (it is FieldState.CardNumberState) {
                        builder.append("    type: ").append(it.cardType).append("\n")
                            .append("       last4: ").append(it.last4).append("\n")
                            .append("       bin: ").append(it.bin).append("\n")
                    }

                    builder.append("\n")
                }
                responseView?.text = builder.toString()
            }
        }
    }

    override fun onResponse(response: VGSResponse?) {
        progressBar?.visibility = View.INVISIBLE
        when (response) {
            is VGSResponse.SuccessResponse -> {
                val builder = StringBuilder("CODE: ")
                    .append(response.code.toString()).append("\n\n")
                response.response?.forEach {
                    builder.append(it.key).append(": ").append(it.value).append("\n")
                }
                responseView.text = builder.toString()
            }
            is VGSResponse.ErrorResponse -> responseView.text =
                "CODE: ${response.errorCode} \n\n ${response.localizeMessage}"
        }
    }
}