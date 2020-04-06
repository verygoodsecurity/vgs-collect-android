package com.verygoodsecurity.demoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.verygoodsecurity.vgscollect.core.Environment
import com.verygoodsecurity.api.cardio.ScanActivity
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.progressBar
import kotlinx.android.synthetic.main.activity_main.responseView
import kotlinx.android.synthetic.main.activity_main.scanCardIOBtn
import kotlinx.android.synthetic.main.activity_main.submitBtn
import kotlinx.android.synthetic.main.activity_main.titleHeader
import java.lang.StringBuilder

class MainActivity : AppCompatActivity(), VgsCollectResponseListener, View.OnClickListener {

    companion object {
        const val USER_SCAN_REQUEST_CODE = 0x7

        const val VAULT_ID = "user_vault_id"
        const val ENVIROMENT = "user_env"
        const val PATH = "user_path"
    }

    private lateinit var vault_id:String
    private lateinit var path:String
    private lateinit var env:Environment

    private val vgsForm:VGSCollect by lazy {
        VGSCollect(vault_id, env)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.input_layout_card_num_test)

        retrieveSettings()

        showVaultId()

        submitBtn?.setOnClickListener(this)
        scanCardIOBtn?.setOnClickListener(this)

        vgsForm.addOnResponseListeners(this)

        vgsForm.addOnFieldStateChangeListener(getOnFieldStateChangeListener())

//        vgsForm.bindView(cardNumberField)
//        vgsForm.bindView(cardCVCField)
//        vgsForm.bindView(cardHolderField)
//        vgsForm.bindView(cardExpDateField)

//        cardNumberFieldLay.setCounterEnabled(true)
//        cardNumberFieldLay.setCounterMaxLength(22)

//        cardNumberFieldLay.setStartIconDrawable(R.drawable.ic_scan_test)
//        cardNumberFieldLay.setStartIconDrawableTintList(ContextCompat.getColorStateList(this, R.color.colorAccent))
//        cardNumberFieldLay.setStartIconOnClickListener(View.OnClickListener {
//            Toast.makeText(this, "StartIcon Action", Toast.LENGTH_LONG).show()
//        })
//
//        cardNumberFieldLay.setEndIconDrawable(R.drawable.ic_scan_test)
//        cardNumberFieldLay.setEndIconDrawableTintList(ContextCompat.getColorStateList(this, R.color.black))
//        cardNumberFieldLay.setEndIconMode(TextInputLayout.END_ICON_CUSTOM)
//        cardNumberFieldLay.setEndIconOnClickListener(View.OnClickListener {
//            Toast.makeText(this, "EndIcon Action", Toast.LENGTH_LONG).show()
//        })

        val customData = HashMap<String, Any>()
        customData["nonSDKValue"] = "all time data"

        vgsForm.setCustomData(customData)

        val headers = HashMap<String, String>()
        headers["CUSTOM-HEADER"] = "all time header"
        vgsForm.setCustomHeaders(headers)

    }

    private fun showVaultId() {
        val message:String = resources.getString(R.string.user_vault_id_hint)+": "+vault_id
        userVault?.setText(message)
    }

    private fun retrieveSettings() {
        val bndl = intent?.extras
        vault_id = bndl?.getString(VAULT_ID, "")?:""
        path = bndl?.getString(PATH,"/")?.run {
            if(this.first() == '/') {
                this
            } else {
                "/$this"
            }
        }?:"/"

        val envId = bndl?.getInt(ENVIROMENT, 0)?:0
        env = Environment.values()[envId]
    }

    override fun onDestroy() {
        vgsForm.onDestroy()
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        progressBar?.visibility = View.VISIBLE
        when (v?.id) {
            R.id.submitBtn -> submitData()
            R.id.scanCardIOBtn -> scanData()
        }
    }

    private fun scanData() {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        vgsForm.onActivityResult(requestCode, resultCode, data)
    }

    private fun submitData() {
        val customData = HashMap<String, Any>()
        customData.put("nickname", "Taras")

        val headers = HashMap<String, String>()
        headers["some-headers"] = "custom-header"

        val request: VGSRequest = VGSRequest.VGSRequestBuilder()
            .setMethod(HTTPMethod.POST)
            .setPath(path)
            .setCustomHeader(headers)
            .setCustomData(customData)
            .build()

        vgsForm.asyncSubmit(this@MainActivity, request)
    }

    private fun getOnFieldStateChangeListener(): OnFieldStateChangeListener {
        return object : OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {
//                titleHeader?.text = "STATE:"
//                val states = vgsForm.getAllStates()
//                val builder = StringBuilder()
//                states.forEach {
//                    builder.append(it.fieldName).append("\n")
//                        .append("   hasFocus: ").append(it.hasFocus).append("\n")
//                        .append("   isValid: ").append(it.isValid).append("\n")
//                        .append("   isEmpty: ").append(it.isEmpty).append("\n")
//                        .append("   isRequired: ").append(it.isRequired).append("\n")
//                    if (it is FieldState.CardNumberState) {
//                        builder.append("    type: ").append(it.cardBrand).append("\n")
//                            .append("       end: ").append(it.last).append("\n")
//                            .append("       bin: ").append(it.bin).append("\n")
//                            .append(it.number).append("\n")
//                    }
//
//                    builder.append("\n")
//                }
//                responseView?.text = builder.toString()
            }
        }
    }

    override fun onResponse(response: VGSResponse?) {
//        progressBar?.visibility = View.INVISIBLE
//
//        titleHeader?.text = "RESPONSE:"
//        when (response) {
//            is VGSResponse.SuccessResponse -> {
//                val builder = StringBuilder("CODE: ")
//                    .append(response.code.toString()).append("\n\n")
//                response.response?.forEach {
//                    builder.append(it.key).append(": ").append(it.value).append("\n\n")
//                }
//                val str = builder.toString()
//                responseView.text = str
//            }
//            is VGSResponse.ErrorResponse -> responseView.text =
//                "CODE: ${response.errorCode} \n\n ${response.localizeMessage}"
//        }
    }
}