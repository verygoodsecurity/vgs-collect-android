package com.verygoodsecurity.demoapp.cmp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.StartActivity.Companion.KEY_BUNDLE_ENVIRONMENT
import com.verygoodsecurity.demoapp.StartActivity.Companion.KEY_BUNDLE_VAULT_ID
import com.verygoodsecurity.demoapp.databinding.ActivityCmpBinding
import com.verygoodsecurity.demoapp.databinding.CodeExampleLayoutBinding
import com.verygoodsecurity.demoapp.getStringExtra
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.api.VGSHttpBodyFormat
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.core.serializers.VGSExpDateSeparateSerializer
import io.github.kbiakov.codeview.adapters.Options
import io.github.kbiakov.codeview.highlight.ColorThemeData
import io.github.kbiakov.codeview.highlight.SyntaxColors
import org.json.JSONArray
import org.json.JSONObject

class CMPActivity : AppCompatActivity(), VgsCollectResponseListener, OnFieldStateChangeListener {

    private lateinit var binding: ActivityCmpBinding
    private lateinit var codeExampleBinding: CodeExampleLayoutBinding

    private val collect: VGSCollect by lazy {
        VGSCollect(
            this,
            getStringExtra(KEY_BUNDLE_VAULT_ID, ""),
            getStringExtra(KEY_BUNDLE_ENVIRONMENT, "")
        ).apply {
            addOnResponseListeners(this@CMPActivity)
            addOnFieldStateChangeListener(this@CMPActivity)
        }
    }

    private var response: String? = null
    private var states: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCmpBinding.inflate(layoutInflater)
        codeExampleBinding = CodeExampleLayoutBinding.bind(binding.ccInputsRoot)
        setContentView(binding.root)
        initViews()
    }

    // Handle VGSCollect submit responses
    @SuppressLint("SetTextI18n")
    override fun onResponse(response: VGSResponse?) {
        Log.d("CMPActivity", "CODE: ${response?.code.toString()}")
        binding.tvResponseCode.text = "CODE: ${response?.code.toString()}"
        setLoading(false)
        this.response = when (response) {
            is VGSResponse.SuccessResponse -> formatResponse(response.body)
            is VGSResponse.ErrorResponse -> response.body ?: response.localizeMessage
            else -> throw IllegalArgumentException("Not implemented.")
        }
        updateCodeExample()
        binding.mbGroupCodeExampleType.check(R.id.mbResponse)
    }

    // Use this callback to track input fields state change
    override fun onStateChange(state: FieldState) {
        updateState()
        updateCodeExample()
    }

    private fun initViews() {
        collect.bindView(
            binding.vgsTiedExpiry,
            binding.vgsTiedPan,
            binding.vgsTiedCvc,
        )
        initExpiryView()
        initCodeExampleView()
        binding.mbGroupCodeExampleType.addOnButtonCheckedListener { _, _, _ -> updateCodeExample() }
        binding.mbSubmit.setOnClickListener { submit() }
    }

    private fun initExpiryView() {
        binding.vgsTiedExpiry.setSerializer(
            VGSExpDateSeparateSerializer(
                monthFieldName = "data.attributes.exp_month",
                yearFieldName = "data.attributes.exp_year"
            )
        )
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

    private fun submit() {
        setLoading(true)

        collect.asyncSubmit(
            VGSRequest.VGSRequestBuilder()
                .setPath("/cards")
                .setCustomHeader(
                    mapOf(
                        "Authorization" to "Bearer <ACCESS_TOKEN>",
                    )
                )
                .setFormat(VGSHttpBodyFormat.API_JSON)
                .build()
        )
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.viewDisableTouch.isVisible = isLoading
    }

    private fun formatResponse(body: String?): String {
        return try {
            JSONObject(body ?: "").toString(4)
        } catch (_: Exception) {
            ""
        }
    }
}