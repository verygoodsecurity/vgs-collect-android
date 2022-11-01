package com.verygoodsecurity.demoapp.activity_case

import android.animation.LayoutTransition
import android.content.Intent
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
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
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
    override fun onResponse(response: VGSResponse?) {
        setLoading(false)
        this.response = when (response) {
            is VGSResponse.SuccessResponse -> createShortResponse(response.body)
            is VGSResponse.ErrorResponse -> response.body ?: response.localizeMessage
            else -> throw IllegalArgumentException("Not implemented.")
        }
        updateCodeExample()
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
        cardBinding.groupAddress.visibility = View.VISIBLE
        bindViewsToCollect()
    }

    // Bind all view to VGSCollect, otherwise they not be sent to proxy
    private fun bindViewsToCollect() {
        collect.bindView(cardBinding.vgsTiedCardHolder)
        collect.bindView(cardBinding.vgsTiedCardNumber)
        collect.bindView(cardBinding.vgsTiedExpiry)
        collect.bindView(cardBinding.vgsTiedCvc)
        collect.bindView(cardBinding.vgsTiedCity)
        collect.bindView(cardBinding.vgsTiedPostalCode)
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
        Log.d("Test", example.toString())
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

    private fun createShortResponse(body: String?): String {
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