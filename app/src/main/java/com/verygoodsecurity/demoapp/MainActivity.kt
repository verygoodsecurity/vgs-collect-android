package com.verygoodsecurity.demoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.verygoodsecurity.vgscollect.core.Environment
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
        setContentView(R.layout.activity_main)

        retrieveSettings()

        showVaultId()

        submitBtn?.setOnClickListener(this)

        vgsForm.addOnResponseListeners(this)

        vgsForm.addOnFieldStateChangeListener(getOnFieldStateChangeListener())

        vgsForm.bindView(cardNumberField)
        vgsForm.bindView(cardCVCField)
        vgsForm.bindView(cardHolderField)
        vgsForm.bindView(cardExpDateField)
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

        Log.e("test", "$vault_id $path $env")

    }

    override fun onDestroy() {
        vgsForm.onDestroy()
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        progressBar?.visibility = View.VISIBLE
        when (v?.id) {
            R.id.submitBtn -> submitData()
        }
    }

    private fun submitData() {
        vgsForm.resetCustomData()
        vgsForm.resetCustomHeaders()

        val data = HashMap<String, String>()
        data["nonSDKValue"] = "some additional data"
        vgsForm.setCustomData(data)

        val headers = HashMap<String, String>()
        headers["CUSTOMHEADER"] = "value"
        vgsForm.setCustomHeaders(headers)

        vgsForm.asyncSubmit(this@MainActivity, path, HTTPMethod.POST)
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
                Log.e("response", str)
                responseView.text = str
            }
            is VGSResponse.ErrorResponse -> responseView.text =
                "CODE: ${response.errorCode} \n\n ${response.localizeMessage}"
        }
    }
}