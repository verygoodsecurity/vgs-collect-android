package com.verygoodsecurity.demoapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.api.nfc.VGSNFCAdapter
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.app.mapper.VGSDataMapper
import com.verygoodsecurity.vgscollect.core.Environment
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import kotlinx.android.synthetic.main.activity_debug_demo.*

class DebugActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var vgsForm: VGSCollect

    private lateinit var nfcCardAdapter: VGSNFCAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug_demo)

        clearBtn?.setOnClickListener(this)
        scanBtn?.setOnClickListener(this)

        setupCollect()

        setupNFCCardAdapter()
        vgsForm.addDataAdapter(nfcCardAdapter)
    }

    private fun setupCollect() {
        VGSCollectLogger.logLevel = VGSCollectLogger.Level.DEBUG

        vgsForm = VGSCollect.Builder(this, "vault_id")
            .setEnvironment(Environment.SANDBOX)
            .create()

        vgsForm.bindView(cardNumberField)
        vgsForm.bindView(cardExpDateField)

        vgsForm.addOnFieldStateChangeListener(object : OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {
                validateFields()
            }
        })
    }

    private fun validateFields() {
        var isValid = true

        vgsForm.getAllStates().forEach {
            if (!it.isValid) {
                isValid = !it.isValid
            }
        }
        if (isValid) {
            nfcCardAdapter.disableForegroundDispatch()
            progressBar?.visibility = View.INVISIBLE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.clearBtn -> clearFields()
            R.id.scanBtn -> startScanning()
        }
    }

    private fun startScanning() {
        progressBar?.visibility = View.VISIBLE
        nfcCardAdapter.enableForegroundDispatch()
    }

    private fun clearFields() {
        cardExpDateField?.setText("")
        cardNumberField?.setText("")
    }

    private fun setupNFCCardAdapter() {
        val mapper = VGSDataMapper.Builder()
            .setCardNumber(cardNumberField?.getFieldName())
            .setExpirationDate(cardExpDateField?.getFieldName())
            .build()

        nfcCardAdapter = VGSNFCAdapter(this, mapper)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        nfcCardAdapter.onNewIntent(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        vgsForm.onActivityResult(requestCode, resultCode, data)
    }

}