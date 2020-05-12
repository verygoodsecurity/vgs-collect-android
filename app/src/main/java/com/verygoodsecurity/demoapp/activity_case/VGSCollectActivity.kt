package com.verygoodsecurity.demoapp.activity_case

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.verygoodsecurity.api.cardio.ScanActivity
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.StartActivity
import com.verygoodsecurity.vgscollect.core.Environment
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.CardType
import kotlinx.android.synthetic.main.activity_collect_demo.*

class VGSCollectActivity: AppCompatActivity(), VgsCollectResponseListener, View.OnClickListener  {

    companion object {
        const val USER_SCAN_REQUEST_CODE = 0x7
    }

    private lateinit var vault_id:String
    private lateinit var path:String
    private lateinit var env:Environment

    private lateinit var vgsForm: VGSCollect

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect_demo)

        retrieveSettings()

        submitBtn?.setOnClickListener(this)
        attachBtn?.setOnClickListener(this)

        vgsForm.addOnResponseListeners(this)
        vgsForm.addOnFieldStateChangeListener(getOnFieldStateChangeListener())

        vgsForm.bindView(cardNumberField)
        vgsForm.bindView(cardCVCField)
        vgsForm.bindView(cardHolderField)
        vgsForm.bindView(cardExpDateField)

        val staticData = mutableMapOf<String, String>()
        staticData["static_data"] = "static custom data"
        vgsForm.setCustomData(staticData)
    }

    private fun retrieveSettings() {
        val bndl = intent?.extras

        vault_id = bndl?.getString(StartActivity.VAULT_ID, "")?:""
        path = bndl?.getString(StartActivity.PATH,"/")?:""

        val envId = bndl?.getInt(StartActivity.ENVIROMENT, 0)?:0
        env = Environment.values()[envId]

        vgsForm = VGSCollect(this, vault_id, env)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.scan_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if(item.itemId == R.id.scan_card) {
            scanCard()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun scanCard() {
        val intent = Intent(this, ScanActivity::class.java)

        val scanSettings = hashMapOf<String?, Int>().apply {
            this[cardNumberField?.getFieldName()] = ScanActivity.CARD_NUMBER
            this[cardCVCField?.getFieldName()] = ScanActivity.CARD_CVC
            this[cardHolderField?.getFieldName()] = ScanActivity.CARD_HOLDER
            this[cardExpDateField?.getFieldName()] = ScanActivity.CARD_EXP_DATE
        }

        intent.putExtra(ScanActivity.SCAN_CONFIGURATION, scanSettings)

        startActivityForResult(intent,
            USER_SCAN_REQUEST_CODE
        )
    }

    private fun getOnFieldStateChangeListener(): OnFieldStateChangeListener {
        return object : OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {
                when(state) {
                    is FieldState.CardNumberState -> handleCardNumberState(state)
                }
                refreshAllStates()
            }
        }
    }

    private fun handleCardNumberState(state: FieldState.CardNumberState) {
        previewCardNumber?.text = state.number
        if(state.cardBrand == CardType.VISA.name) {
            previewCardBrand?.setImageResource(R.drawable.ic_custom_visa)
        } else {
            previewCardBrand?.setImageResource(state.drawableBrandResId)
        }
    }

    private fun refreshAllStates() {
        val states = vgsForm.getAllStates()
        val builder = StringBuilder()
        states.forEach {
            builder.append(it.toString()).append("\n\n")
        }
        stateContainerView?.text = builder.toString()
    }

    override fun onDestroy() {
        vgsForm.onDestroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        vgsForm.onActivityResult(requestCode, resultCode, data)
        checkAttachedFiles()
    }

    private fun checkAttachedFiles() {
        if(vgsForm.getFileProvider().getAttachedFiles().isEmpty()) {
            attachBtn?.text = getString(R.string.collect_activity_attach_btn)
        } else {
            attachBtn?.text = getString(R.string.collect_activity_detach_btn)
        }
    }

    override fun onResponse(response: VGSResponse?) {
        setEnabledResponseHeader(true)
        setStateLoading(false)

        when (response) {
            is VGSResponse.SuccessResponse -> {
                responseContainerView.text = response.toString()
            }
            is VGSResponse.ErrorResponse -> responseContainerView.text = response.toString()
        }
    }

    private fun setStateLoading(state:Boolean) {
        if(state) {
            progressBar?.visibility = View.VISIBLE
            submitBtn?.isEnabled = false
            attachBtn?.isEnabled = false
        } else {
            progressBar?.visibility = View.INVISIBLE
            submitBtn?.isEnabled = true
            attachBtn?.isEnabled = true
        }
    }

    private fun setEnabledResponseHeader(isEnabled:Boolean) {
        if(isEnabled) {
            attachBtn.setTextColor(ContextCompat.getColor(this,
                R.color.state_active
            ))
        } else {
            responseContainerView.text = ""
            attachBtn.setTextColor(ContextCompat.getColor(this,
                R.color.state_unactive
            ))
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.submitBtn -> submitData()
            R.id.attachBtn -> attachFile()
        }
    }

    private fun submitData() {
        setEnabledResponseHeader(false)
        setStateLoading(true)

        val customData = HashMap<String, Any>()
        customData["nickname"] = "Taras"

        val headers = HashMap<String, String>()
        headers["some-headers"] = "dynamic-header"

        val request: VGSRequest = VGSRequest.VGSRequestBuilder()
            .setMethod(HTTPMethod.POST)
            .setPath(path)
            .setCustomHeader(headers)
            .setCustomData(customData)
            .build()

        vgsForm.asyncSubmit(request)
    }

    private fun attachFile() {
        if(vgsForm.getFileProvider().getAttachedFiles().isEmpty()) {
            vgsForm.getFileProvider().attachFile("attachments.file")
        } else {
            vgsForm.getFileProvider().detachAll()
        }
        checkAttachedFiles()
    }
}