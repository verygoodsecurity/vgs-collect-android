package com.verygoodsecurity.demoapp.collect_activity

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.verygoodsecurity.api.cardio.ScanActivity
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.StartActivity.Companion.KEY_BUNDLE_ENVIRONMENT
import com.verygoodsecurity.demoapp.StartActivity.Companion.KEY_BUNDLE_PATH
import com.verygoodsecurity.demoapp.StartActivity.Companion.KEY_BUNDLE_VAULT_ID
import com.verygoodsecurity.demoapp.databinding.ActivityCollectBinding
import com.verygoodsecurity.demoapp.databinding.CardInputLayoutBinding
import com.verygoodsecurity.demoapp.databinding.CodeExampleLayoutBinding
import com.verygoodsecurity.demoapp.utils.idling.GlobalIdlingResource
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
import com.verygoodsecurity.vgscollect.view.card.validation.rules.PersonNameRule
import com.verygoodsecurity.vgscollect.view.card.validation.rules.VGSInfoRule
import com.verygoodsecurity.vgscollect.view.core.serializers.VGSExpDateSeparateSerializer
import io.github.kbiakov.codeview.adapters.Options
import io.github.kbiakov.codeview.highlight.ColorThemeData
import io.github.kbiakov.codeview.highlight.SyntaxColors
import org.json.JSONArray
import org.json.JSONObject

class CollectActivity : AppCompatActivity(), VgsCollectResponseListener,
    OnFieldStateChangeListener {

    private val path: String by lazy { getStringExtra(KEY_BUNDLE_PATH) }

    private val collect: VGSCollect by lazy {
        VGSCollect(
            this,
            getStringExtra(KEY_BUNDLE_VAULT_ID, ""),
            getStringExtra(KEY_BUNDLE_ENVIRONMENT, "")
        ).apply {
            addOnResponseListeners(this@CollectActivity)
            addOnFieldStateChangeListener(this@CollectActivity)
        }
    }

    private lateinit var binding: ActivityCollectBinding
    private lateinit var cardBinding: CardInputLayoutBinding
    private lateinit var codeExampleBinding: CodeExampleLayoutBinding

    // Used to start and receive result from scan activity
    private val scanResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            collect.onActivityResult(0, it.resultCode, it.data)
        }

    private var response: String? = null
    private var states: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectBinding.inflate(layoutInflater)
        cardBinding = CardInputLayoutBinding.bind(binding.ccInputsRoot)
        codeExampleBinding = CodeExampleLayoutBinding.bind(binding.ccInputsRoot)
        setContentView(binding.root)
        initViews()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.scan_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.scan_card -> true.also { scan() } // TODO: @Endorf, check this line, what you think?
        else -> super.onOptionsItemSelected(item)
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        collect.onActivityResult(requestCode, resultCode, data)
        updateFilesManageButtonState()
    }

    // Should be called, so VGSCollect can properly release all resources
    override fun onDestroy() {
        super.onDestroy()
        collect.onDestroy()
    }

    // Handle VGSCollect submit responses
    @SuppressLint("SetTextI18n")
    override fun onResponse(response: VGSResponse?) {
        Log.d("Test", "CODE: ${response?.code.toString()}")
        binding.tvResponseCode.text = "CODE: ${response?.code.toString()}"
        setLoading(false)
        this.response = when (response) {
            is VGSResponse.SuccessResponse -> readShortResponse(response.body)
            is VGSResponse.ErrorResponse -> response.body ?: response.localizeMessage
            else -> throw IllegalArgumentException("Not implemented.")
        }
        updateCodeExample()
        GlobalIdlingResource.decrement()
    }

    // Use this callback to track input fields state change
    override fun onStateChange(state: FieldState) {
        updateState()
        updateCodeExample()
    }

    private fun initViews() {
        initCodeExampleView()
        initCardView()
        binding.mbFilesManage.setOnClickListener { handleFileClickedManageButtonClicked() }
        binding.mbSubmit.setOnClickListener { submit() }
        binding.mbGroupCodeExampleType.addOnButtonCheckedListener { _, _, _ -> updateCodeExample() }
        binding.ccInputsRoot.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun initCodeExampleView() {
        val syntaxColor = ContextCompat.getColor(this, R.color.veryLightGray)
        val bgColor = ContextCompat.getColor(this, R.color.blackPearl)
        val lineNumberColor = ContextCompat.getColor(this, R.color.nobel)
        codeExampleBinding.cvResponse.setOptions(
            Options(
                context = this.applicationContext, theme = ColorThemeData(
                    SyntaxColors(
                        string = syntaxColor,
                        punctuation = syntaxColor,
                    ),
                    numColor = lineNumberColor,
                    bgContent = bgColor,
                    bgNum = bgColor,
                    noteColor = syntaxColor,
                )
            )
        )
        codeExampleBinding.cvResponse.alpha = 1f
        codeExampleBinding.cvResponse.findViewById<RecyclerView>(R.id.rv_code_content).isNestedScrollingEnabled =
            false
    }

    private fun initCardView() {
        setupCardHolderNameView()
        setupCardNumberView()
        setupExpiryView()
        setupCvcView()
        setupPostalCodeView()
        setupSsnView()
        bindViewsToCollect()
    }

    // Configure card holder input field behaviour
    private fun setupCardHolderNameView() {
        // Specify card holder name validation rule and error messages
        cardBinding.vgsTiedCardHolder.setRule(
            PersonNameRule.ValidationBuilder()
                .setAllowableMinLength(3, "Card holder name is to short.")
                .setAllowableMaxLength(20, "Card holder name is to long.")
                .build()
        )
        cardBinding.vgsTiedCardHolder.setOnFieldStateChangeListener(object :
            OnFieldStateChangeListener {

            override fun onStateChange(state: FieldState) {
                Log.d(
                    CollectActivity::class.java.simpleName,
                    "onStateChange: ${state.fieldName}, errors: ${state.validationErrors}"
                )
            }
        })
    }

    // Configure card number input field behaviour
    private fun setupCardNumberView() {
        // Set CardIconAdapter to be able to override default card icons
        cardBinding.vgsTiedCardNumber.setCardIconAdapter(object : CardIconAdapter(this) {

            override fun getIcon(
                cardType: CardType,
                name: String?,
                resId: Int,
                r: Rect
            ): Drawable = when (cardType) {
                CardType.VISA -> getDrawable(R.drawable.ic_visa) // Provide your custom icon for VISA cards here
                else -> super.getIcon(cardType, name, resId, r)
            }
        })
        // Set CardMaskAdapter to be able to specify custom card number masks
        cardBinding.vgsTiedCardNumber.setCardMaskAdapter(object : CardMaskAdapter() {

            override fun getMask(
                cardType: CardType,
                name: String,
                bin: String,
                mask: String
            ): String = when (cardType) {
                CardType.VISA -> "## ## #### #### ## ##" // Specify mask for VISA cards here
                else -> super.getMask(cardType, name, bin, mask)
            }
        })
        // Add card brand which is not specified in VGSCollect
        cardBinding.vgsTiedCardNumber.addCardBrand(
            CardBrand(
                "^7777",
                "<BRAND_NAME>",
                R.drawable.ic_custom_brand,
                BrandParams(
                    "#### #### #### ####",
                    ChecksumAlgorithm.LUHN,
                    rangeNumber = arrayOf(16),
                    rangeCVV = arrayOf(3)
                )
            )
        )
        // Add field state change listener
        cardBinding.vgsTiedCardNumber.setOnFieldStateChangeListener(object :
            OnFieldStateChangeListener {

            override fun onStateChange(state: FieldState) {
                Log.d(CollectActivity::class.java.simpleName, "onStateChange: ${state.fieldName}")
            }
        })
    }

    // Configure expiry input field behaviour
    private fun setupExpiryView() {
        // Specify VGSExpDateSeparateSerializer to send expiry month and year separately in json structure
        cardBinding.vgsTiedExpiry.setSerializer(
            VGSExpDateSeparateSerializer(
                "card.expiry.month",
                "card.expiry.year",
            )
        )
        cardBinding.vgsTiedExpiry.setOnFieldStateChangeListener(object :
            OnFieldStateChangeListener {

            override fun onStateChange(state: FieldState) {
                Log.d(CollectActivity::class.java.simpleName, "onStateChange: ${state.fieldName}")
            }
        })
    }

    // Configure cvc input field behaviour
    private fun setupCvcView() {
        cardBinding.vgsTiedCvc.setOnFieldStateChangeListener(object :
            OnFieldStateChangeListener {

            override fun onStateChange(state: FieldState) {
                Log.d(CollectActivity::class.java.simpleName, "onStateChange: ${state.fieldName}")
            }
        })
    }

    // Configure postal code input field behaviour
    private fun setupPostalCodeView() {
        cardBinding.groupAddress.visibility = View.VISIBLE
        // Specify postal code validation rule and error messages
        cardBinding.vgsTiedPostalCode.setRule(
            VGSInfoRule.ValidationBuilder()
                .setRegex("^[0-9]{5}(?:-[0-9]{4})?\$", "Invalid postal code.")
                .build()
        )
    }

    private fun setupSsnView() {
        cardBinding.groupSsn.visibility = View.VISIBLE
    }

    // Bind all view to VGSCollect, otherwise input data not be sent to proxy
    private fun bindViewsToCollect() {
        collect.bindView(cardBinding.vgsTiedCardHolder)
        collect.bindView(cardBinding.vgsTiedCardNumber)
        collect.bindView(cardBinding.vgsTiedExpiry)
        collect.bindView(cardBinding.vgsTiedCvc)
        collect.bindView(cardBinding.vgsTiedCity)
        collect.bindView(cardBinding.vgsTiedPostalCode)
        collect.bindView(cardBinding.vgsTiedSsn)
    }

    private fun handleFileClickedManageButtonClicked() {
        with(collect.getFileProvider()) {
            if (getAttachedFiles().isNotEmpty()) {
                detachAll()
            } else {
                attachFile(this@CollectActivity, "<FILE_NAME>")
            }
        }
        updateFilesManageButtonState()
    }

    // Send data to VGS proxy
    private fun submit() {
        GlobalIdlingResource.increment()
        setLoading(true)
        val request: VGSRequest = VGSRequest.VGSRequestBuilder()
            .setMethod(HTTPMethod.POST)
            .setPath(path)
            .setCustomHeader(mapOf("custom-header-name" to "value"))
            .setCustomData(mapOf("custom_data" to "value"))
            .build()
        collect.asyncSubmit(request)
    }

    // Start scanning process
    private fun scan() {
        scanResultLauncher.launch(Intent(this, ScanActivity::class.java).apply {
            putExtra(
                ScanActivity.SCAN_CONFIGURATION, hashMapOf(
                    // Provide vgs inputs field names, so scan activity can map scanning result to proper input field
                    cardBinding.vgsTiedCardHolder.getFieldName() to ScanActivity.CARD_HOLDER,
                    cardBinding.vgsTiedCardNumber.getFieldName() to ScanActivity.CARD_NUMBER,
                    cardBinding.vgsTiedExpiry.getFieldName() to ScanActivity.CARD_EXP_DATE,
                    cardBinding.vgsTiedCvc.getFieldName() to ScanActivity.CARD_CVC,
                )
            )
        })
    }

    private fun updateFilesManageButtonState() {
        binding.mbFilesManage.text =
            if (collect.getFileProvider().getAttachedFiles().isEmpty()) "Attach" else "Detach"
    }

    private fun updateCodeExample() {
        val example =
            if (binding.mbGroupCodeExampleType.checkedButtonId == R.id.mbInputState) states else response
        codeExampleBinding.cvResponse.setCode(example ?: "")
    }

    private fun updateState() {
        val states = collect.getAllStates()
        val statesJson = JSONObject()
        val statesJsonArray = JSONArray()
        states.forEach {
            statesJsonArray.put(JSONObject().apply {
                put("fieldName", it.fieldName)
                put("hasFocus", it.hasFocus)
                put("contentLength", it.contentLength)
                put("isValid", it.isValid)
            })
        }
        statesJson.put("states", statesJsonArray)
        this.states = statesJson.toString(4)
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.viewDisableTouch.isVisible = isLoading
    }

    private fun readShortResponse(body: String?): String {
        return try {
            JSONObject(body ?: "").getJSONObject("json").toString(4)
        } catch (e: Exception) {
            ""
        }
    }

    private fun AppCompatActivity.getStringExtra(key: String, defaultValue: String = ""): String {
        return intent.extras?.getString(key) ?: defaultValue
    }
}