package com.verygoodsecurity.demoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.api.cardio.ScanActivity
import com.verygoodsecurity.vgscollect.core.Environment
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import kotlinx.android.synthetic.main.activity_main.*

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
        VGSCollect(this, vault_id, env)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        retrieveSettings()

        showVaultId()

        submitBtn?.setOnClickListener(this)
        attachFileBtn?.setOnClickListener(this)
        scanCardIOBtn?.setOnClickListener(this)

        vgsForm.addOnResponseListeners(this)

        vgsForm.addOnFieldStateChangeListener(getOnFieldStateChangeListener())

        vgsForm.bindView(cardNumberField)
        vgsForm.bindView(cardCVCField)
        vgsForm.bindView(cardHolderField)
        vgsForm.bindView(cardExpDateField)

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
            R.id.attachFileBtn -> attachFile()
            R.id.scanCardIOBtn -> scanData()
        }
    }

    private fun attachFile() {
        vgsForm.getFileProvider().attachFile("files.somePic")
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
//            .ignoreFields(true) //setup of list ignored fields?
//            .ignoreFiles(true) //setup ignoreTypes [FILES, FIELDS, ETC., SPECIFIC_FIELD] ?
            .build()

        vgsForm.asyncSubmit(request)
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
                val str = builder.toString()
                responseView.text = str
            }
            is VGSResponse.ErrorResponse -> responseView.text =
                "CODE: ${response.errorCode} \n\n ${response.localizeMessage}"
        }
    }

}