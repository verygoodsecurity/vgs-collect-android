package com.verygoodsecurity.demoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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

        vgsForm.onFieldStateChangeListener = getOnFieldStateChangeListener()

        vgsForm.bindView(cardNumberField)
        vgsForm.bindView(cardCVVField)
        vgsForm.bindView(cardHolderField)
        vgsForm.bindView(cardExpDateField)

//        val allStates = vgsForm.getAllStates()
    }

    override fun onDestroy() {
        vgsForm.onDestroy()
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        progressBar?.visibility = View.VISIBLE
        when(v?.id) {
            R.id.sendPost -> vgsForm.asyncSubmit(this@MainActivity, "/post", HTTPMethod.POST)
            R.id.sendGet -> vgsForm.asyncSubmit(this@MainActivity, "/get", HTTPMethod.GET)
        }
    }










    private fun getOnFieldStateChangeListener():OnFieldStateChangeListener {
        return object :OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {}

            //todo remove for release
            override fun onStateChange(states: Collection<FieldState>) {
                val builder = StringBuilder()
                states.forEach {
                    builder.append(it.alias).append("\n")
                        .append("   hasFocus: ").append(it.hasFocus).append("\n")
                        .append("   isValid: ").append(it.isValid).append("\n")
                        .append("   isEmpty: ").append(it.isEmpty).append("\n")
                        .append("   isRequired: ").append(it.isRequired).append("\n")
                    if(it is FieldState.CardNumberState) {
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
        when(response) {
            is VGSResponse.SuccessResponse -> response.response?.values?.forEach {
                responseView.text = "CODE: ${response.successCode} \n\n $it"
            }
            is VGSResponse.ErrorResponse -> responseView.text = "CODE: ${response.errorCode} \n\n ${response.localizeMessage}"
        }
    }


}
