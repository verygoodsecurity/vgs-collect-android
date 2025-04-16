package com.verygoodsecurity.demoapp.cmp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.databinding.CmpActivityBinding
import com.verygoodsecurity.demoapp.databinding.CodeExampleLayoutBinding
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.Environment
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.view.core.serializers.VGSExpDateSeparateSerializer
import io.github.kbiakov.codeview.adapters.Options
import io.github.kbiakov.codeview.highlight.ColorThemeData
import io.github.kbiakov.codeview.highlight.SyntaxColors
import org.json.JSONObject

class CMPActivity : AppCompatActivity(), VgsCollectResponseListener {

    private lateinit var binding: CmpActivityBinding
    private lateinit var codeExampleBinding: CodeExampleLayoutBinding

    private val collectCMP: VGSCollect by lazy {
        VGSCollect.initCMP(
            context = this@CMPActivity,
            environment = Environment.SANDBOX
        ).apply {
            addOnResponseListeners(this@CMPActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CmpActivityBinding.inflate(layoutInflater)
        codeExampleBinding = CodeExampleLayoutBinding.bind(binding.root)
        setContentView(binding.root)
        VGSCollectLogger.logLevel = VGSCollectLogger.Level.DEBUG
        initViews()
    }

    override fun onResponse(response: VGSResponse?) {
        println(response)
    }

    private fun initViews() {
        initPanView()
        initExpiryView()
        initProceedView()
        initCodeExampleView()
        updateCodeExample(null)
    }

    private fun initProceedView() {
        binding.mbProceed.setOnClickListener {
            collectCMP.createCard(accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI1RVhBTnFldTBIbUNGVHZDUGs1c21iQjNjcVo1c0FLUzNKWFRjeHdTWjY4In0.eyJleHAiOjE3NDQ4MTI0NzAsImlhdCI6MTc0NDgxMTI3MCwianRpIjoiZDc2NTAyYzctMjE0OS00NDgyLThkZGUtYWJmYWU0ODU1NTgxIiwiaXNzIjoiaHR0cHM6Ly9hdXRoLnZlcnlnb29kc2VjdXJpdHkuY29tL2F1dGgvcmVhbG1zL3ZncyIsImF1ZCI6InZhdWx0LWFwaSIsInN1YiI6IjcxNzdiZTU1LTM4MDItNDczNy04YjA5LTFkYWQ0OGQyYjkyYiIsInR5cCI6IkJlYXJlciIsImF6cCI6IkFDazRGYW1mRi1BbGlhc2VzUE9DLWZUSWNSIiwicmVzb3VyY2VfYWNjZXNzIjp7InZhdWx0LWFwaSI6eyJyb2xlcyI6WyJhbGlhc2VzOndyaXRlIl19fSwic2NvcGUiOiJhbGlhc2VzOndyaXRlIHVzZXJfaWQgc2VydmljZS1hY2NvdW50Iiwic2VydmljZV9hY2NvdW50Ijp0cnVlLCJjbGllbnRIb3N0IjoiNDUuODkuOTAuMTgyIiwiY2xpZW50QW5ub3RhdGlvbnMiOnsidmdzLmlvL3ZhdWx0LWlkIjoidG50aHU1eWl6bmQifSwiY2xpZW50QWRkcmVzcyI6IjQ1Ljg5LjkwLjE4MiIsImNsaWVudF9pZCI6IkFDazRGYW1mRi1BbGlhc2VzUE9DLWZUSWNSIn0.e9rdaJZxH88hB9-mnHdFMI0pCHtGt4B0W7l5eHHV45qLTSNdyvwPzR2REax6UujhVFoGargYl0V5UNssANkM0ZyyFNg3fJnFCMLpYJXHahT48DIqdeh-iYRRTNWliZbRWKlCWvyvFwYp1tz0Si3wSGSDHeuXqo5_H6nNryFRhGP7MQjqSeYD-LhtykojJSVE4-FlANJLTFaSwqboqXFf1bjIOoX7PQsIFgvAZQvvpw58pdcmpXYSxZ7wGs3HvSyUdZ7b_CmKmIcCcOQlQtDgRtVp3G2Sxw3xVpKbduLJr6VoOo_EG9RfIQqymTyihscpicgZIzqqD1zjj6HWLJUhsA")
        }
    }


    private fun initPanView() {
        collectCMP.bindView(binding.vgsTiedPan)
    }

    private fun initExpiryView() {
        binding.vgsTiedExpiry.setSerializer(
            VGSExpDateSeparateSerializer(
                monthFieldName = "data.attributes.exp_month",
                yearFieldName = "data.attributes.exp_year"
            )
        )
        collectCMP.bindView(binding.vgsTiedExpiry)
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
    }

    private fun updateCodeExample(response: String?) {
        val json = try {
            JSONObject(response ?: "").toString(4)
        } catch (_: Exception) {
            ""
        }
        codeExampleBinding.cvResponse.setCode(json)
    }
}