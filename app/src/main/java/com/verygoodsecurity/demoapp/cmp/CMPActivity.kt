package com.verygoodsecurity.demoapp.cmp

import android.os.Bundle
import android.view.View
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
import com.verygoodsecurity.vgscollect.util.extension.cardCVC
import com.verygoodsecurity.vgscollect.util.extension.cardExpirationDate
import com.verygoodsecurity.vgscollect.util.extension.cardNumber
import io.github.kbiakov.codeview.adapters.Options
import io.github.kbiakov.codeview.highlight.ColorThemeData
import io.github.kbiakov.codeview.highlight.SyntaxColors
import org.json.JSONObject

class CMPActivity : AppCompatActivity(), VgsCollectResponseListener {

    private lateinit var binding: CmpActivityBinding
    private lateinit var codeExampleBinding: CodeExampleLayoutBinding

    private val collect: VGSCollect by lazy {
        VGSCollect(
            context = this@CMPActivity,
            id = "<VAULT_ID>",
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
        binding.progressBar.visibility = View.GONE
        println(response)
        updateCodeExample(response?.body)
    }

    private fun initViews() {
        initCollectViews()
        initProceedView()
        initCodeExampleView()
        updateCodeExample(null)
    }

    private fun initCollectViews() {
        collect.cardNumber(binding.vgsTiedPan)
        collect.cardExpirationDate(binding.vgsTiedExpiry)
        collect.cardCVC(binding.vgsTiedCvc)

    }

    private fun initProceedView() {
        binding.mbProceed.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            collect.createCard("<ACCESS_TOKEN>")
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