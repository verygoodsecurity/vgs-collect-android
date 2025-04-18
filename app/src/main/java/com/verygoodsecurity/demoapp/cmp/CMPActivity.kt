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
            collectCMP.createCard(accessToken = "")
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