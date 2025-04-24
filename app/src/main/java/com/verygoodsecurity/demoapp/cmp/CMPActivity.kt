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
import com.verygoodsecurity.vgscollect.core.cmp.VGSCardManager
import com.verygoodsecurity.vgscollect.core.cmp.VGSCardManagerResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.view.core.serializers.VGSExpDateSeparateSerializer
import io.github.kbiakov.codeview.adapters.Options
import io.github.kbiakov.codeview.highlight.ColorThemeData
import io.github.kbiakov.codeview.highlight.SyntaxColors
import org.json.JSONObject

class CMPActivity : AppCompatActivity(), VGSCardManagerResponseListener {

    private lateinit var binding: CmpActivityBinding
    private lateinit var codeExampleBinding: CodeExampleLayoutBinding

    private val collectCMP: VGSCardManager by lazy {
        VGSCardManager(
            context = this@CMPActivity,
            accountId = "test",
            environment = Environment.SANDBOX.rawValue
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
        initPanView()
        initExpiryView()
        initProceedView()
        initCodeExampleView()
        updateCodeExample(null)
    }

    private fun initProceedView() {
        binding.mbProceed.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            collectCMP.createCard(accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI1RVhBTnFldTBIbUNGVHZDUGs1c21iQjNjcVo1c0FLUzNKWFRjeHdTWjY4In0.eyJleHAiOjE3NDU1MDQ2NTUsImlhdCI6MTc0NTUwMzQ1NSwianRpIjoiMjBhNGUwOWItZTJiNS00ODNiLWI0NTctZWQ5NzJhNGFlYzU5IiwiaXNzIjoiaHR0cHM6Ly9hdXRoLnZlcnlnb29kc2VjdXJpdHkuY29tL2F1dGgvcmVhbG1zL3ZncyIsImF1ZCI6WyJjYWxtLXNhbmRib3giLCJjYWxtLWxpdmUiXSwic3ViIjoiMGExNzc1M2UtMGU5My00MDQyLTlkMmQtMTMxMzUzMjA0MDY1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiQUNrNEZhbWZGLUNyZWF0ZUNhcmQtVmRTOWQiLCJyZXNvdXJjZV9hY2Nlc3MiOnsiY2FsbS1zYW5kYm94Ijp7InJvbGVzIjpbImNhcmRzOnJlYWQiLCJhY2NvdW50czp3cml0ZSIsImNhcmRzOndyaXRlIiwibmV0d29yay10b2tlbnM6d3JpdGUiLCJhY2NvdW50czpyZWFkIiwibmV0d29yay10b2tlbnM6cmVhZCIsIm1lcmNoYW50czp3cml0ZSJdfSwiY2FsbS1saXZlIjp7InJvbGVzIjpbImNhcmRzOnJlYWQiLCJhY2NvdW50czp3cml0ZSIsImNhcmRzOndyaXRlIiwibmV0d29yay10b2tlbnM6d3JpdGUiLCJhY2NvdW50czpyZWFkIiwibmV0d29yay10b2tlbnM6cmVhZCIsIm1lcmNoYW50czp3cml0ZSJdfX0sInNjb3BlIjoiYWNjb3VudHM6d3JpdGUgY2FyZHM6cmVhZCBhY2NvdW50czpyZWFkIG1lcmNoYW50czp3cml0ZSBzZXJ2aWNlLWFjY291bnQgY2FyZHM6d3JpdGUgdXNlcl9pZCIsInNlcnZpY2VfYWNjb3VudCI6dHJ1ZSwiY2xpZW50SG9zdCI6IjQ1Ljg5LjkwLjE4MiIsImNsaWVudEFubm90YXRpb25zIjp7InZncy5pby92YXVsdC1pZCI6InRudGNheGRrYm1pIn0sImNsaWVudEFkZHJlc3MiOiI0NS44OS45MC4xODIiLCJjbGllbnRfaWQiOiJBQ2s0RmFtZkYtQ3JlYXRlQ2FyZC1WZFM5ZCJ9.hOTl4waX7yOuzRWXtcTtBVAdXXkEGqA78kiZ9KtmnOFkPWdwGkxAXMoVnwaUJ-ePa-EWdglLJpui11TmYmU7T4NIVP_R24EdAJAsfq4DhS_OHWd-vOomJYh7w8FNMWWbepE-G6WuZnf1H7WwMwMalfPuJ3cDEhYecYY9FIyfiMxRoQxKRpfmYcZMx76OSY_gg4gqVGFAJ1Qxuf8KNqUxXF8MVNNuYoeusAb0GztNAb0FC3vPcOJHctbs9WVPW0rvNBhMzh-Tm0AWT_pJ2cR0U3Q54hmzu6mFnuP05xXYvIHMK0GJQmKkNCb2hVQ31ICyAf_-Jt6Ct7Hy0to215bBHw")
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