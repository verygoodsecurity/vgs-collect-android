package com.verygoodsecurity.demoapp.activity_case

import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
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
import com.verygoodsecurity.vgscollect.view.card.*
import com.verygoodsecurity.vgscollect.view.card.formatter.CardMaskAdapter
import com.verygoodsecurity.vgscollect.view.card.icon.CardIconAdapter
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
        vgsForm.setCustomData(staticData)
    }

    private fun setupCardExpDateField() {
        vgsForm.bindView(cardExpDateField)
        cardExpDateField?.setOnFieldStateChangeListener(object : OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {
                Log.e("card_exp", "$state \n\n ${cardExpDateField.getState()} ")
                if(!state.isEmpty && !state.isValid && !state.hasFocus) {
                    cardExpDateFieldLay?.setError("fill it please")
                } else {
                    cardExpDateFieldLay?.setError(null)
                }
            }
        })
    }

    private fun setupCardHolderField() {
        vgsForm.bindView(cardHolderField)
        cardHolderField?.setOnFieldStateChangeListener(object : OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {
                Log.e("card_holder", "$state \n\n ${cardHolderField.getState()} ")
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
                Log.e("card_cvc", "$state \n\n ${cardCVCField.getState()} ")
                if(!state.isEmpty && !state.isValid && !state.hasFocus) {
                    cardCVCFieldLay?.setError("fill it please")
                } else {
                    cardCVCFieldLay?.setError(null)
                }
            }
        })
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

    private fun setupDefaultBehaviour() {
        val rule = Rule.RuleBuilder()
            .setAlgorithm(ChecksumAlgorithm.LUHN)
            .setLength(arrayOf())
            .build()
        cardNumberField.addValidationRule(rule)
    }

    private fun addCustomBrands() {
        val params = BrandParams(
            "### ### ### ####",
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




//        RulePARAMS {
//        -  ChecksumAlgorithm.LUHN, arrayOf(12..19), eerorMesage
//          - ignore
//          - LUHN
//
//    }

//        UNDEFINED == VISA

//        customBrand
//        customValidation
//        ignore rules
//

//        defined ( default + custom )  -> ( length + brand ) + checkSUM
//        undefined


//        cardNumberField.addValidationRule(CardType.UNDEFINED, ChecksumAlgorithm.LUHN, arrayOf(10..19), eerorMesage)



//        enum RUles {
//            Luhn,
//            Length
//        }

//        val arrayRules = { LuhnRule}
//
//
//        CheckeSum ( aka Luhn + Some )
//
//


//        enum CARDs {
//                DEFINED,    // custom, default
//                UNDEFINED  // unknown
//        }

//        UNDEFINED_CardNumberValidation_Luhn_LENGTH = ValidationBuilder().setGroup(CARDs.DEFINED)
//                    .addRule(RUles.Luhn)
//                    .addRule(RUles.Length(3..19))
//                    .build()

//        DATACALSS = ValidationBuilder().setGroup(CARDs.UNDEFINED)
//                    .addRule(RUles.Luhn)
//                    .addRule(RUles.Length(3..19))
//                    .build()

//        IOS
//        cardNumberField.addValidationRule( UNDEFINED_CardNumberValidation_Luhn_LENGTH() )
//        cardNumberField.addValidationRule( DEFINED_CardNumberValidation_NONE_CHECKSUM() )


//        ANDROID
//        cardNumberField.addValidationRule(CARDs.DEFINED, ChecksumAlgorithm.NONE )
//        cardNumberField.addValidationRule(CARDs.UNDEFINED, arrayRules(  RUles.Length(4..19)   ))


//   123 ##### 123
//6 ..13
//
//> 11

//        cardNumberField.addValidationRule(

//              RUles.Length(4..19) ,

//              ChecksumAlgorithm.ANY,
//              ChecksumAlgorithm.Luhn,
//              ChecksumAlgorithm.Some

//        )



//        cardNumberField.addValidationRule( ChecksumAlgorithm.Luhn, RUles.Length(16..19) )   - wolt case





//        cardNumberField.setFlow_Unknown(CARDs.Flow_1)

//        app:validation_1_for_default_card="checkSum"
//
//        app:validation_2="checkSum|Length"




// UNDEFINED_CardNumberValidation_Luhn_SomeCheck
// CardNumberValidationLuhn
// CardNumberValidationSome

// CardNumberValidationLength

//    fun addValidationRule(type, array) {
//        val list = List()
//        when {
//            array.idEmpty -> list.add(CardNumberValidationAllTrue)//none
//            array == Luhn -> list.add(CardNumberValidationLuhn)
//            array == Some -> list.add(CardNumberValidationSome)
//            array == Length = list.add(CardNumberValidationLength)
//        }
//
//        return list //CardNumberValidationLuhn, CardNumberValidationLength
//    }
//        val isLuhnValid: Boolean = validateCheckSum(card.algorithm, rawStr)
//        val isLengthAppropriate = checkLength(card.numberLength, rawStr.length)



//        cardNumberField.ignore(CardType.MAESTRO)



//        cardNumberField.addValidationRule(CardType.ALL, ChecksumAlgorithm.LUHN)













//              Brand                                    Unknown
//        |                  |                              |
//    Custom             Default                            |
//
// - Length(x)          - Length                     - Length (4..19)
// - cvcLength(x)       - cvcLength                  - cvcLength (3, 4)
// - algorithm          - algorithm                  - [ Luhn, Judas, ..., etc.]
//
//
//


