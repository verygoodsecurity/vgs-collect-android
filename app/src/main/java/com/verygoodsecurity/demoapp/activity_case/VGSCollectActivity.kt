package com.verygoodsecurity.demoapp.activity_case

import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.verygoodsecurity.api.bouncer.ScanActivity
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
import com.verygoodsecurity.vgscollect.view.card.BrandParams
import com.verygoodsecurity.vgscollect.view.card.CardBrand
import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.formatter.CardMaskAdapter
import com.verygoodsecurity.vgscollect.view.card.icon.CardIconAdapter
import com.verygoodsecurity.vgscollect.view.card.validation.payment.ChecksumAlgorithm
import com.verygoodsecurity.vgscollect.view.card.validation.rules.PaymentCardNumberRule
import com.verygoodsecurity.vgscollect.view.card.validation.rules.PersonNameRule
import com.verygoodsecurity.vgscollect.view.date.DatePickerMode
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

        setupCardNumberField()
        setupCVCField()
        setupCardHolderField()
        setupCardExpDateField()

        val staticData = mutableMapOf<String, String>()
        staticData["static_data"] = "static custom data"
//        vgsForm.setCustomData(staticData)
    }

    private fun setupCardExpDateField() {
        vgsForm.bindView(cardExpDateField)
        cardExpDateField?.setOnFieldStateChangeListener(object : OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {
                if(!state.isEmpty && !state.isValid && !state.hasFocus) {
                    cardExpDateFieldLay?.setError("fill it please")
                } else {
                    cardExpDateFieldLay?.setError(null)
                }
            }
        })
    }

    private fun setupCardHolderField() {
        val rule : PersonNameRule = PersonNameRule.ValidationBuilder()
//            .setRegex("^([a-zA-Z]{2,}\\s[a-zA-z]{1,})\$")
            .setAllowableMinLength(3)
            .setAllowableMaxLength(7)
            .build()

        cardHolderField.addRule(rule)

        vgsForm.bindView(cardHolderField)
        cardHolderField?.setOnFieldStateChangeListener(object : OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {
                if(!state.isEmpty && !state.isValid && !state.hasFocus) {
                    cardHolderFieldLay?.setError("fill it please")
                } else {
                    cardHolderFieldLay?.setError(null)
                }
            }
        })
    }

    private fun setupCVCField() {
        vgsForm.bindView(cardCVCField)
        cardCVCField?.setOnFieldStateChangeListener(object : OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {
                if(!state.isEmpty && !state.isValid && !state.hasFocus) {
                    cardCVCFieldLay?.setError("fill it please")
                } else {
                    cardCVCFieldLay?.setError(null)
                }
            }
        })
    }

    private fun setupDefaultBehaviour() {
        val rule : PaymentCardNumberRule = PaymentCardNumberRule.ValidationBuilder()
//            .setAlgorithm(ChecksumAlgorithm.NONE)

            .setAllowableNumberLength(arrayOf(15, 13, 19))

            .setAllowableMinLength(3)
            .setAllowableMaxLength(7)

            .build()

        cardNumberField.addRule(rule)
    }

    private fun addCustomBrands() {
        val params = BrandParams(
            "### ### ### ###",
            ChecksumAlgorithm.LUHN,
            arrayOf(4, 10, 12),
            arrayOf(3, 5)
        )
        val newBrand = CardBrand(
            "^777",
            "NEW BRAND",
            R.drawable.ic_cards,
            params
        )
        cardNumberField.addCardBrand(newBrand)


        val params2 = BrandParams(
            "### ### ### ###### ###",
            ChecksumAlgorithm.LUHN,
            arrayOf(18),
            arrayOf(4)
        )
        val newBrand2 = CardBrand(
            "^878",
            "VGS Brand",
            CardType.MAESTRO.resId,
            params2
        )

        cardNumberField.addCardBrand(newBrand2)
    }

    private fun setupCardNumberField() {
        addCustomBrands()
        setupDefaultBehaviour()

        vgsForm.bindView(cardNumberField)

        cardNumberField.setCardIconAdapter(object : CardIconAdapter(this) {
            override fun getIcon(cardType: CardType, name: String?, resId: Int, r: Rect): Drawable {
                return if(cardType == CardType.VISA) {
                    getDrawable(R.drawable.ic_visa_light)
                } else {
                    super.getIcon(cardType, name, resId, r)
                }
            }
        })

        cardNumberField.setCardMaskAdapter(object : CardMaskAdapter() {
            override fun getMask(
                cardType: CardType,
                name: String,
                bin: String,
                mask: String
            ): String {
                return when(cardType) {
                    CardType.UNKNOWN -> {
                        if (bin == "7771") {
                            "# # # #"
                        } else {
                            mask
                        }
                    }
                    CardType.AMERICAN_EXPRESS -> {
                        if (bin.contains("371233")) {
                            "### # ###### ### ##"
                        } else {
                            mask
                        }
                    }
                    else -> super.getMask(cardType, name, bin, mask)
                }
            }
        })

        cardNumberField?.setOnFieldStateChangeListener(object : OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {
                Log.e("card_number", "$state \n\n ${cardNumberField.getState()} ")
            }
        })
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
        val bndl = with(Bundle()) {
            val scanSettings = hashMapOf<String?, Int>().apply {
                this[cardNumberField?.getFieldName()] = ScanActivity.CARD_NUMBER
                this[cardCVCField?.getFieldName()] = ScanActivity.CARD_CVC
                this[cardHolderField?.getFieldName()] = ScanActivity.CARD_HOLDER
                this[cardExpDateField?.getFieldName()] = ScanActivity.CARD_EXP_DATE
            }

            putSerializable(ScanActivity.SCAN_CONFIGURATION, scanSettings)

            putString(ScanActivity.API_KEY, "<user_bouncer_key>")

            putBoolean(ScanActivity.ENABLE_EXPIRY_EXTRACTION, true)
            putBoolean(ScanActivity.ENABLE_NAME_EXTRACTION, true)
            putBoolean(ScanActivity.DISPLAY_CARD_PAN, true)
            putBoolean(ScanActivity.DISPLAY_CARD_HOLDER_NAME, true)
            putBoolean(ScanActivity.DISPLAY_CARD_SCAN_LOGO, true)
            putBoolean(ScanActivity.ENABLE_DEBUG, true)

            this
        }

        ScanActivity.scan(this, USER_SCAN_REQUEST_CODE, bndl)
    }

    private fun getOnFieldStateChangeListener(): OnFieldStateChangeListener {
        return object : OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {
                Log.e("vgs_collect_state", "$state ")
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
            is VGSResponse.SuccessResponse -> responseContainerView.text = response.toString()
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
//            .setCustomHeader(headers)
//            .setCustomData(customData)
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