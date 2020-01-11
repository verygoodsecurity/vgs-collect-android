package com.verygoodsecurity.demoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.verygoodsecurity.api.ScanActivity
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder

class MainActivity : AppCompatActivity(), VgsCollectResponseListener, View.OnClickListener {

    companion object {
        const val USER_SCAN_REQUEST_CODE = 0x7
    }

    val vgsForm = VGSCollect("tntxrsfgxcn")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        submitBtn?.setOnClickListener(this)
        scanCardIOBtn?.setOnClickListener(this)
        scanGetBouncerBtn?.setOnClickListener(this)

        vgsForm.onResponseListener = this

        vgsForm.addOnFieldStateChangeListener(getOnFieldStateChangeListener())

        vgsForm.bindView(cardNumberField)
        vgsForm.bindView(cardCVCField)
        vgsForm.bindView(cardHolderField)
        vgsForm.bindView(cardExpDateField)
    }

    override fun onDestroy() {
        vgsForm.onDestroy()
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        progressBar?.visibility = View.VISIBLE
        when (v?.id) {
            R.id.submitBtn -> vgsForm.asyncSubmit(this@MainActivity, "/post", HTTPMethod.POST, null)
            R.id.scanCardIOBtn -> scanByCardIOCard()
            R.id.scanGetBouncerBtn -> scanByGetBouncerCard()
        }
    }

    private fun scanByCardIOCard() {
        progressBar?.visibility = View.INVISIBLE
        val intent = Intent(this, ScanActivity::class.java)

        val scanSettings = hashMapOf<String?, Int>().apply {
            this[cardNumberField?.getFieldName()] = ScanActivity.CARD_NUMBER
            this[cardCVCField?.getFieldName()] = ScanActivity.CARD_CVC
            this[cardHolderField?.getFieldName()] = ScanActivity.CARD_HOLDER
            this[cardExpDateField?.getFieldName()] = ScanActivity.CARD_EXP_DATE
        }

        intent.putExtra(ScanActivity.SCAN_CONFIGURATION, scanSettings)

        startActivityForResult(intent, USER_SCAN_REQUEST_CODE)
    }


    private fun scanByGetBouncerCard() {
        progressBar?.visibility = View.INVISIBLE
        /*val intent = Intent(this, ScanActivity::class.java)

        val scanSettings = hashMapOf<String?, Int>().apply {
            this[cardNumberField?.getFieldName()] = ScanActivity.CARD_NUMBER
            this[cardCVCField?.getFieldName()] = ScanActivity.CARD_CVC
            this[cardHolderField?.getFieldName()] = ScanActivity.CARD_HOLDER
            this[cardExpDateField?.getFieldName()] = ScanActivity.CARD_EXP_DATE
        }

        intent.putExtra(ScanActivity.SCAN_CONFIGURATION, scanSettings)

        startActivityForResult(intent, USER_SCAN_REQUEST_CODE)*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        vgsForm.onActivityResult(requestCode, resultCode, data)
    }

    private fun getOnFieldStateChangeListener(): OnFieldStateChangeListener {
        return object : OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {
                titleHeader?.text = "STATE:"
                val states = vgsForm.getAllStates()
                val builder = StringBuilder()
                states.forEach {
                    builder.append(it.fieldName).append("\n")
                        .append("   hasFocus: ").append(it.hasFocus).append("\n")
                        .append("   isValid: ").append(it.isValid).append("\n")
                        .append("   isEmpty: ").append(it.isEmpty).append("\n")
                        .append("   isRequired: ").append(it.isRequired).append("\n")
                    if (it is FieldState.CardNumberState) {
                        builder.append("    type: ").append(it.cardBrand).append("\n")
                            .append("       end: ").append(it.last).append("\n")
                            .append("       bin: ").append(it.bin).append("\n")
                            .append(it.number).append("\n")
                    }

                    builder.append("\n")
                }
                responseView?.text = builder.toString()
            }
        }
    }

    override fun onResponse(response: VGSResponse?) {
        progressBar?.visibility = View.INVISIBLE

        titleHeader?.text = "RESPONSE:"
        when (response) {
            is VGSResponse.SuccessResponse -> {
                val builder = StringBuilder("CODE: ")
                    .append(response.code.toString()).append("\n\n")
                response.response?.forEach {
                    builder.append(it.key).append(": ").append(it.value).append("\n\n")
                }
                responseView.text = builder.toString()
            }
            is VGSResponse.ErrorResponse -> responseView.text =
                "CODE: ${response.errorCode} \n\n ${response.localizeMessage}"
        }
    }
}