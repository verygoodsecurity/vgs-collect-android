package com.verygoodsecurity.demoapp.date_range_activity

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.StartActivity.Companion.KEY_BUNDLE_ENVIRONMENT
import com.verygoodsecurity.demoapp.StartActivity.Companion.KEY_BUNDLE_PATH
import com.verygoodsecurity.demoapp.StartActivity.Companion.KEY_BUNDLE_VAULT_ID
import com.verygoodsecurity.demoapp.collect_activity.CollectActivity
import com.verygoodsecurity.demoapp.databinding.ActivityDateRangeBinding
import com.verygoodsecurity.demoapp.databinding.CodeExampleLayoutBinding
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.VGSDate
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import io.github.kbiakov.codeview.adapters.Options
import io.github.kbiakov.codeview.highlight.ColorThemeData
import io.github.kbiakov.codeview.highlight.SyntaxColors
import org.json.JSONArray
import org.json.JSONObject

class DateRangeActivity : AppCompatActivity(), VgsCollectResponseListener, OnFieldStateChangeListener {

    private val collect: VGSCollect by lazy {
        VGSCollect(
            this,
            getStringExtra(KEY_BUNDLE_VAULT_ID, ""),
            getStringExtra(KEY_BUNDLE_ENVIRONMENT, "")
        ).apply {
            addOnResponseListeners(this@DateRangeActivity)
            addOnFieldStateChangeListener(this@DateRangeActivity)
        }
    }

    private val path: String by lazy { getStringExtra(KEY_BUNDLE_PATH) }
    private lateinit var binding: ActivityDateRangeBinding
    private lateinit var codeExampleBinding: CodeExampleLayoutBinding

    private var response: String? = null
    private var states: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDateRangeBinding.inflate(layoutInflater)
        codeExampleBinding = CodeExampleLayoutBinding.bind(binding.ccInputsRoot)
        setContentView(binding.root)
        initViews()
    }

    // Should be called, so VGSCollect can properly release all resources
    override fun onDestroy() {
        super.onDestroy()
        collect.onDestroy()
    }

    private fun AppCompatActivity.getStringExtra(key: String, defaultValue: String = ""): String {
        return intent.extras?.getString(key) ?: defaultValue
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
    }

    private fun readShortResponse(body: String?): String {
        return try {
            JSONObject(body ?: "").getJSONObject("json").toString(4)
        } catch (e: Exception) {
            ""
        }
    }

    // Use this callback to track input fields state change
    override fun onStateChange(state: FieldState) {
        updateState()
        updateCodeExample()
    }

    private fun initViews() {
        initCodeExampleView()
        setupDateRangesView()
        binding.mbSubmit.setOnClickListener { submit() }
        binding.mbGroupCodeExampleType.addOnButtonCheckedListener { _, _, _ -> updateCodeExample() }
        binding.ccInputsRoot.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        collect.bindView(binding.vgsTiedDateRange)
        collect.bindView(binding.cardExpDateField)

        VGSDate.create(10, 10, 2020)?.let {
            binding.vgsTiedDateRange.setMinDate(it)
        }
        VGSDate.create(10, 10, 2026)?.let {
            binding.vgsTiedDateRange.setMaxDate(it)
        }
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
                    noteColor = syntaxColor
                )
            )
        )
        codeExampleBinding.cvResponse.alpha = 1f
        codeExampleBinding.cvResponse.findViewById<RecyclerView>(R.id.rv_code_content).isNestedScrollingEnabled = false
    }

    private fun setupDateRangesView() {
        // Specify VGSExpDateSeparateSerializer to send expiry month and year separately in json structure
//        binding.vgsTiedDateRange.setSerializer(
//            VGSDateRangeSeparateSerializer(
//                "date.day",
//                "date.month",
//                "date.year"
//            )
//        )
        binding.vgsTiedDateRange.setOnFieldStateChangeListener(object : OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {
                Log.d(CollectActivity::class.java.simpleName, "onStateChange: ${state.fieldName}")
            }
        })
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

    private fun updateCodeExample() {
        val example = if (binding.mbGroupCodeExampleType.checkedButtonId == R.id.mbInputState) states else response
        codeExampleBinding.cvResponse.setCode(example ?: "")
    }

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

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.viewDisableTouch.isVisible = isLoading
    }
}